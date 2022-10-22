package macro

import core.Core
import core.memory.Unsafe
import core.memory.Unsafe.calloc
import java.nio.file.Files
import java.nio.file.Paths



enum class MessageType(val value: Int) {
	INPUT(0x00FF),
	KEY_DOWN(0x0100),
	KEY_UP(0x0101),
	DESTROY(0x0002);
}

enum class RawMouseInput(val value: Int) {
	LEFT_DOWN(1),
	LEFT_UP(2),
	RIGHT_DOWN(4),
	RIGHT_UP(8),
	SCROLL(0x400);
}

enum class MouseEvent(val value: Int, val button: MouseButton? = null, val release: Boolean = false) {
	MOVE(0x001),
	LEFT_DOWN(0x002, MouseButton.LEFT),
	LEFT_UP(0x004, MouseButton.LEFT, true),
	RIGHT_DOWN(0x008, MouseButton.RIGHT),
	RIGHT_UP(0x010, MouseButton.RIGHT, true),
	MIDDLE_DOWN(0x020, MouseButton.MIDDLE),
	MIDDLE_UP(0x040, MouseButton.MIDDLE, true),
	SCROLL(0x800);
}

enum class MouseButton {

	LEFT,
	RIGHT,
	MIDDLE;

}




private val rawInput = RawInputHeader(calloc(SIZEOF_RAWINPUT))
private val pcbSize = calloc(4).also { Unsafe.setInt(it, SIZEOF_RAWINPUT) }
private val keyboardInput = KeyboardInput(calloc(SIZEOF_INPUT))
private val mouseInput = MouseInput(calloc(SIZEOF_INPUT))



sealed interface Action {
	val time: Int
}

data class KeyAction(override val time: Int, val key: Key, val release: Boolean) : Action

data class MouseAction(override val time: Int, val event: MouseEvent) : Action

data class MoveAction(override val time: Int, val dx: Int, val dy: Int) : Action

data class ScrollAction(override val time: Int, val dy: Int) : Action

val actions = ArrayList<Action>()

var startTime = 0L

val pressed = HashSet<Int>()



private fun handleMessageInput(lParam: Long) {
	val currentTime = System.currentTimeMillis()

	if(startTime == 0L) startTime = currentTime

	val time = (currentTime - startTime).toInt()

	Natives.getRawInputData(lParam, 0x10000003, rawInput.address, pcbSize, 24)

	if(rawInput.type == 1) {
		val input = RawInputKeyboard(rawInput.address)
		val released = input.flags and 1 != 0
		val scanCode = input.scanCode

		if(released)
			pressed.remove(scanCode)
		else if(pressed.contains(scanCode))
			return
		else
			pressed.add(scanCode)

		println(input.scanCode)

		val key = Key.codeMap[scanCode] ?: return
		actions.add(KeyAction(time, key, released))
	} else if(rawInput.type == 0) {
		val input = RawInputMouse(rawInput.address)
		if(input.flags != 0) error("Mouse input was not relative")
		val flags = input.buttonFlags
		val dx = input.lastX
		val dy = input.lastY

		if(dx != 0 && dy != 0)
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



private fun windowProc(hwnd: Long, msg: Int, wParam: Long, lParam: Long): Boolean {
	when(msg) {
		MessageType.DESTROY.value -> Natives.postQuitMessage(wParam.toInt())
		MessageType.INPUT.value   -> handleMessageInput(lParam)
		else                      -> return false
	}

	return true
}



private fun createDevices(hwnd: Long) {
	val pointer = calloc(SIZEOF_RAWINPUTDEVICE * 2)

	RawInputDevice(pointer).let {
		it.usagePage = 1
		it.usage     = 6
		it.flags     = 0x100
		it.hwnd      = hwnd
	}

	RawInputDevice(pointer + SIZEOF_RAWINPUTDEVICE).let {
		it.usagePage = 1
		it.usage     = 2
		it.flags     = 0x100
		it.hwnd      = hwnd
	}

	Natives.registerRawInputDevices(pointer, 2, SIZEOF_RAWINPUTDEVICE)
}



private fun playAction(action: Action) = when(action) {
	is KeyAction    -> sendKeyboardInput(action.key.scanCode, action.release)
	is MoveAction   -> sendMouseInput(action.dx, action.dy, 0, MouseEvent.MOVE.value)
	is ScrollAction -> sendMouseInput(0, 0, action.dy, MouseEvent.SCROLL.value)
	is MouseAction  -> sendMouseInput(0, 0, 0, action.event.value)

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



private fun loadActions(): List<Action> {
	val actions = ArrayList<Action>()

	val lines = Files.readAllLines(Paths.get("output.txt"))

	for(line in lines) {
		val parts = line.split(' ')
		if(parts[0] == "PRESS") {
			if(parts[1] == "LEFT_MOUSE")
				actions.add(MouseAction(parts[2].toInt(), MouseEvent.LEFT_DOWN))
			else if(parts[1] == "RIGHT_MOUSE")
				actions.add(MouseAction(parts[2].toInt(), MouseEvent.RIGHT_DOWN))
			else if(parts[2] == "MIDDLE_MOUSE")
				actions.add(MouseAction(parts[2].toInt(), MouseEvent.MIDDLE_DOWN))
			else
				actions.add(KeyAction(parts[2].toInt(), Key.nameMap[parts[1]]!!, false))
		} else if(parts[0] == "RELEASE") {
			if(parts[1] == "LEFT_MOUSE")
				actions.add(MouseAction(parts[2].toInt(), MouseEvent.LEFT_UP))
			else if(parts[1] == "RIGHT_MOUSE")
				actions.add(MouseAction(parts[2].toInt(), MouseEvent.RIGHT_UP))
			else if(parts[2] == "MIDDLE_MOUSE")
				actions.add(MouseAction(parts[2].toInt(), MouseEvent.MIDDLE_UP))
			else
				actions.add(KeyAction(parts[2].toInt(), Key.nameMap[parts[1]]!!, true))
		} else if(parts[0] == "MOVE") {
			actions.add(MoveAction(parts[3].toInt(), parts[1].toInt(), parts[2].toInt()))
		} else if(parts[0] == "SCROLL") {
			actions.add(ScrollAction(parts[2].toInt(), parts[1].toInt()))
		}
	}

	return actions
}



private fun play(actions: List<Action>) {
	val startTime = System.currentTimeMillis()
	var index = 0

	while(index < actions.size) {
		val currentTime = System.currentTimeMillis()
		val time = currentTime - startTime

		while(index < actions.size) {
			if(actions[index].time > time) break
			playAction(actions[index++])
		}

		Thread.sleep(1)
	}
}



fun main() {
	Core.loadResource("/natives.dll")
	//Thread.sleep(3000); play(loadActions())
	record()
}



private fun record() {
	Natives.proc = ::windowProc
	val msg = Message(calloc(SIZEOF_MSG))
	val window = Natives.createWindow()
	Natives.showWindow(window, 5)
	createDevices(window)

	while(true) {
		val result = Natives.getMessage(msg.address, 0, 0, 0)
		Natives.translateMessage(msg.address)
		Natives.dispatchMessage(msg.address)
		if(result == 0) break
	}

	val output = buildString {
		for(action in actions) {
			when(action) {
				is KeyAction -> if(action.release)
					appendLine("RELEASE ${action.key} ${action.time}")
				else
					appendLine("PRESS ${action.key} ${action.time}")

				is MoveAction -> appendLine("MOVE ${action.dx} ${action.dy} ${action.time}")

				is ScrollAction -> appendLine("SCROLL ${action.dy} ${action.time}")

				is MouseAction -> if(action.event.release)
					appendLine("RELEASE ${action.event.button}_MOUSE ${action.time}")
				else
					appendLine("PRESS ${action.event.button}_MOUSE ${action.time}")
			}
		}
	}

	Files.writeString(Paths.get("output.txt"), output)

	println("Finished")
}