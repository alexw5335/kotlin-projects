package macro

import core.Core
import core.memory.Unsafe
import core.memory.Unsafe.calloc
import java.nio.file.Files
import java.nio.file.Paths



const val WM_INPUT    = 0x00FF
const val WM_KEY_DOWN = 0x0100
const val WM_KEY_UP   = 0x0101
const val WM_DESTROY  = 0x0002
const val RI_M1_DOWN = 1
const val RI_M1_UP = 2
const val RI_M2_DOWN = 4
const val RI_M2_UP = 8
const val RI_SCROLL = 0x400

enum class MouseEvent(val value: Int) {
	MOVE(0x001),
	LEFT_DOWN(0x002),
	LEFT_UP(0x004),
	RIGHT_DOWN(0x008),
	RIGHT_UP(0x010),
	MIDDLE_DOWN(0x020),
	MIDDLE_UP(0x040),
	X_DOWN(0x080),
	X_UP(0x100),
	SCROLL(0x800)
}




private val rawInput = RawInputHeader(calloc(SIZEOF_RAWINPUT))
private val pcbSize = calloc(4).also { Unsafe.setInt(it, SIZEOF_RAWINPUT) }
private val keyboardInput = KeyboardInput(calloc(SIZEOF_INPUT))
private val mouseInput = MouseInput(calloc(SIZEOF_INPUT))



sealed interface Action {
	val time: Int
}

data class ButtonAction(override val time: Int, val button: InputButton, val release: Boolean) : Action

data class MouseAction(override val time: Int, val dx: Int, val dy: Int) : Action

data class ScrollAction(override val time: Int, val dy: Int) : Action

val actions = ArrayList<Action>()

var startTime = 0L

val pressed = HashSet<InputButton>()



private fun handleMessageInput(lParam: Long) {
	val currentTime = System.currentTimeMillis()

	if(startTime == 0L) startTime = currentTime

	val time = (currentTime - startTime).toInt()

	Natives.getRawInputData(lParam, 0x10000003, rawInput.address, pcbSize, 24)

	if(rawInput.type == 1) {
		val input = RawInputKeyboard(rawInput.address)
		val code = input.virtualCode
		val released = input.flags == 1
		val button = InputButton.map[code] ?: return

		println(released)

		if(released)
			pressed.remove(button)
		else if(pressed.contains(button))
			return
		else
			pressed.add(button)

		actions.add(ButtonAction(time, button, released))
	} else if(rawInput.type == 0) {
		val input = RawInputMouse(rawInput.address)
		if(input.flags != 0) error("Mouse input was not relative")
		val flags = input.buttonFlags
		val dx = input.lastX
		val dy = input.lastY
		if(dx != 0 && dy != 0) actions.add(MouseAction(time, dx, dy))
		if(RI_M1_DOWN and flags != 0) actions.add(ButtonAction(time, InputButton.LEFT_MOUSE, false))
		if(RI_M1_UP and flags != 0) actions.add(ButtonAction(time, InputButton.LEFT_MOUSE, true))
		if(RI_M2_DOWN and flags != 0) actions.add(ButtonAction(time, InputButton.RIGHT_MOUSE, false))
		if(RI_M2_UP and flags != 0) actions.add(ButtonAction(time, InputButton.RIGHT_MOUSE, true))
		if(RI_SCROLL and flags != 0) actions.add(ScrollAction(time, input.buttonData))
	}
}



private fun windowProc(hwnd: Long, msg: Int, wParam: Long, lParam: Long): Boolean {
	when(msg) {
		WM_DESTROY -> Natives.postQuitMessage(wParam.toInt())
		WM_INPUT   -> handleMessageInput(lParam)
		else       -> return false
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
	is ButtonAction -> playButtonAction(action)
	is MouseAction -> playMouseAction(action)
	is ScrollAction -> playScrollAction(action)
}



private fun playMouseAction(action: MouseAction) {
	sendMouseInput(action.dx, action.dy, 0, MouseEvent.MOVE.value)
}



private fun playScrollAction(action: ScrollAction) {
	sendMouseInput(0, 0, action.dy, MouseEvent.SCROLL.value)
}



private fun playButtonAction(action: ButtonAction) {
	if(action.button == InputButton.LEFT_MOUSE) {
		if(action.release)
			sendMouseInput(0, 0, 0, MouseEvent.LEFT_UP.value)
		else
			sendMouseInput(0, 0, 0, MouseEvent.LEFT_DOWN.value)
		return
	}

	if(action.button == InputButton.RIGHT_MOUSE) {
		if(action.release)
			sendMouseInput(0, 0, 0, MouseEvent.RIGHT_UP.value)
		else
			sendMouseInput(0, 0, 0, MouseEvent.RIGHT_DOWN.value)
		return
	}

	if(action.button.scanCode == 0) error("Missing scanCode for button '${action.button}'")

	sendKeyboardInput(action.button.scanCode, action.release)
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
			actions.add(ButtonAction(parts[2].toInt(), InputButton.nameMap[parts[1]]!!, false))
		} else if(parts[0] == "RELEASE") {
			actions.add(ButtonAction(parts[2].toInt(), InputButton.nameMap[parts[1]]!!, true))
		} else if(parts[0] == "MOVE") {
			actions.add(MouseAction(parts[3].toInt(), parts[1].toInt(), parts[2].toInt()))
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
				is ButtonAction -> if(action.release)
					appendLine("RELEASE ${action.button} ${action.time}")
				else
					appendLine("PRESS ${action.button} ${action.time}")

				is MouseAction -> appendLine("MOVE ${action.dx} ${action.dy} ${action.time}")

				is ScrollAction -> appendLine("SCROLL ${action.dy} ${action.time}")
			}
		}
	}

	Files.writeString(Paths.get("output.txt"), output)
}



/*
val input = KeyboardInput(Unsafe.calloc(40))
input.type = 1
input.scanCode = 0x1E
input.flags = 8
Thread.sleep(3000)
Natives.sendInput(1, input.address, 40)
input.flags = 10
Thread.sleep(1000)
Natives.sendInput(1, input.address, 40)

fun sendKeyboardInput(virtualCode: Int, scanCode: Int, release: Boolean, useScanCode: Boolean) {
	val input = KeyboardInput(calloc(SIZEOF_INPUT))
	input.type = 1
	input.virtualCode = virtualCode
	input.scanCode = scanCode
	if(release) input.flags = input.flags or 2
	if(useScanCode) input.flags = input.flags or 8
	Natives.sendInput(1, input.address, 40)
}
*/