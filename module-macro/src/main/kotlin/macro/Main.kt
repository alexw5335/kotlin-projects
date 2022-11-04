package macro

import core.Core
import core.memory.Unsafe
import core.memory.Unsafe.calloc
import java.nio.file.Files
import java.nio.file.Paths



private val rawInput = RawInputHeader(calloc(SIZEOF_RAWINPUT))

private val pcbSize = calloc(4).also { Unsafe.setInt(it, SIZEOF_RAWINPUT) }

private val keyboardInput = KeyboardInput(calloc(SIZEOF_INPUT))

private val mouseInput = MouseInput(calloc(SIZEOF_INPUT))



val actions = ArrayList<Action>()

var startTime = 0L

val pressed = HashSet<Int>()

var recording = false

var playing = false



private val recordStartKey = Key.COMMA

private val recordEndKey = Key.PERIOD

private val playKey = Key.M

private val loadKey = Key.SLASH

private val continualPlayKey = Key.N



private fun handleKeyPress(key: Key): Boolean {
	if(key == continualPlayKey) {
		if(recording) return false
		playing = !playing
		if(playing) playContinualThreaded()
		return true
	}

	if(key == playKey) {
		if(recording) return false
		playing = !playing
		if(playing) playThreaded()
		return true
	}

	if(key == recordStartKey) {
		if(recording || playing) return false
		println("Recording start")
		actions.clear()
		recording = true
		startTime = 0L
		return true
	}

	if(key == recordEndKey) {
		if(!recording || playing) return false
		println("Recording end")
		recording = false
		startTime = 0L
		saveToFile()
		return true
	}

	if(key == loadKey) {
		if(recording || playing) return false
		println("Loading from file")
		actions.clear()
		actions.addAll(loadFromFile())
		return true
	}

	return false
}



private fun handleMessageInput(lParam: Long) {
	val currentTime = System.currentTimeMillis()

	Natives.getRawInputData(lParam, 0x10000003, rawInput.address, pcbSize, 24)

	if(rawInput.type == 1) {
		val input    = RawInputKeyboard(rawInput.address)
		val released = input.flags and 1 != 0
		val code     = input.virtualCode
		val key      = Key.codeMap[code] ?: return

		if(released && recording && key == recordStartKey && startTime == 0L) return

		if(!released && handleKeyPress(key)) return

		if(!recording) return

		if(startTime == 0L) startTime = currentTime
		val time = (currentTime - startTime).toInt()

		if(released) {
			pressed.remove(code)
			actions.add(KeyReleaseAction(time, key))
		} else if(!pressed.contains(code)) {
			pressed.add(code)
			actions.add(KeyPressAction(time, key))
		}
	} else if(rawInput.type == 0) {
		if(!recording) return

		if(startTime == 0L) startTime = currentTime
		val time = (currentTime - startTime).toInt()

		val input = RawInputMouse(rawInput.address)

		if(input.flags != 0)
			error("Mouse input was not relative")

		val flags = input.buttonFlags
		val dx = input.lastX
		val dy = input.lastY

		if(dx != 0 || dy != 0)
			actions.add(MoveAction(time, dx, dy))

		if(RawMouseInput.LEFT_DOWN.value and flags != 0)
			actions.add(MouseAction(time, MouseEvent.LEFT_DOWN))

		if(RawMouseInput.LEFT_UP.value and flags != 0)
			actions.add(MouseAction(time, MouseEvent.LEFT_UP))

		if(RawMouseInput.RIGHT_DOWN.value and flags != 0)
			actions.add(MouseAction(time, MouseEvent.RIGHT_DOWN))

		if(RawMouseInput.RIGHT_UP.value and flags != 0)
			actions.add(MouseAction(time, MouseEvent.RIGHT_UP))

		if(RawMouseInput.SCROLL.value and flags != 0)
			actions.add(ScrollAction(time, input.buttonData))
	}
}



/*
Playback
 */



private fun playContinualThreaded() = Thread {
	println("Continual playback start")
	while(playing) {
		println("Playback start")
		play(actions)
		println("Playback end")
		Thread.sleep(3000)
	}
	println("Continual playback end")
}.start()



private fun playThreaded() = Thread {
	println("Playback start")
	play(actions)
	println("Playback end")
	playing = false
}.start()



private fun play(actions: List<Action>) {
	val startTime = System.currentTimeMillis()
	var index = 0

	while(index < actions.size && playing) {
		val currentTime = System.currentTimeMillis()
		val time = currentTime - startTime

		while(index < actions.size && playing) {
			if(actions[index].time > time) break
			play(actions[index++])
		}

		Thread.sleep(1)
	}
}



private fun play(action: Action) = when(action) {
	is KeyPressAction   -> sendKeyboardInput(Natives.mapVirtualCodeToScanCode(action.key.code), false)
	is KeyReleaseAction -> sendKeyboardInput(Natives.mapVirtualCodeToScanCode(action.key.code), true)
	is MoveAction       -> sendMouseInput(action.dx, action.dy, 0, MouseEvent.MOVE.value)
	is ScrollAction     -> sendMouseInput(0, 0, action.dy, MouseEvent.SCROLL.value)
	is MouseAction      -> sendMouseInput(0, 0, 0, action.event.value)

}



private fun sendMouseInput(dx: Int, dy: Int, data: Int, flags: Int) {
	Unsafe.zero(mouseInput.address, SIZEOF_INPUT)
	mouseInput.type = 0
	mouseInput.dx = dx
	mouseInput.dy = dy
	mouseInput.data = data
	mouseInput.flags = flags
	Natives.sendInput(1, mouseInput.address, SIZEOF_INPUT)
}



private fun sendKeyboardInput(scanCode: Int, release: Boolean) {
	Unsafe.zero(keyboardInput.address, SIZEOF_INPUT)
	keyboardInput.type = 1
	keyboardInput.virtualCode = 0
	keyboardInput.scanCode = scanCode
	keyboardInput.flags = if(release) 10 else 8
	Natives.sendInput(1, keyboardInput.address, SIZEOF_INPUT)
}



/*
Serialisation
 */



private fun loadFromFile() = Files.lines(Paths.get("output.txt")).toList().map {
	val parts = it.split(' ')

	when(parts[0]) {
		"PRESS"   -> KeyPressAction(parts[2].toInt(), Key.nameMap[parts[1]]!!)
		"RELEASE" -> KeyReleaseAction(parts[2].toInt(), Key.nameMap[parts[1]]!!)
		"MOUSE"   -> MouseAction(parts[2].toInt(), MouseEvent.nameMap[parts[1]]!!)
		"SCROLL"  -> ScrollAction(parts[2].toInt(), parts[1].toInt())
		"MOVE"    -> MoveAction(parts[3].toInt(), parts[1].toInt(), parts[2].toInt())
		else      -> error("Invalid action: $it")
	}
}



private fun saveToFile() {
	Files.delete(Paths.get("output.txt"))
	Files.write(Paths.get("output.txt"), actions.map {
		when(it) {
			is KeyPressAction   -> "PRESS ${it.key} ${it.time}"
			is KeyReleaseAction -> "RELEASE ${it.key} ${it.time}"
			is MoveAction       -> "MOVE ${it.dx} ${it.dy} ${it.time}"
			is ScrollAction     -> "SCROLL ${it.dy} ${it.time}"
			is MouseAction      -> "MOUSE ${it.event} ${it.time}"
		}
	})
}



/*
Main
 */



fun main() {
	start()
}



@Suppress("UNUSED_PARAMETER")
private fun windowProc(hwnd: Long, msg: Int, wParam: Long, lParam: Long): Boolean {
	when(msg) {
		MessageType.DESTROY.value -> Natives.postQuitMessage(wParam.toInt())
		MessageType.INPUT.value   -> handleMessageInput(lParam)
		else                      -> return false
	}

	return true
}



private fun start() {
	Core.loadResource("/natives.dll")
	Natives.proc = ::windowProc
	val msg = Message(calloc(SIZEOF_MSG))
	val window = Natives.createWindow()
	Natives.showWindow(window, 5)

	val pointer = calloc(SIZEOF_RAWINPUTDEVICE * 2)

	RawInputDevice(pointer).let {
		it.usagePage = 1
		it.usage     = 6
		it.flags     = 0x100
		it.hwnd      = window
	}

	RawInputDevice(pointer + SIZEOF_RAWINPUTDEVICE).let {
		it.usagePage = 1
		it.usage     = 2
		it.flags     = 0x100
		it.hwnd      = window
	}

	Natives.registerRawInputDevices(pointer, 2, SIZEOF_RAWINPUTDEVICE)

	while(true) {
		val result = Natives.getMessage(msg.address, 0, 0, 0)
		Natives.translateMessage(msg.address)
		Natives.dispatchMessage(msg.address)
		if(result == 0) break
	}

	println("Finished")
}