package macro



const val SIZEOF_RAWINPUTDEVICE = 16
const val SIZEOF_RAWINPUTHEADER = 24
const val SIZEOF_RAWINPUT = 48
const val SIZEOF_INPUT = 40
const val SIZEOF_MSG = 48



class RawInputDevice(override val address: Long) : Struct {
	var usagePage by short(0)
	var usage     by short(2)
	var flags     by int(4)
	var hwnd      by long(8)
}



class RawInputHeader(override val address: Long) : Struct {
	var type   by int(0)
	var size   by int(4)
	var handle by long(8)
	var wParam by long(16)
}



class RawInputKeyboard(override val address: Long) : Struct {
	var type        by int(0)
	var size        by int(4)
	var handle      by long(8)
	var wParam      by long(16)
	var scanCode    by short(24)
	var flags       by short(26)
	var virtualCode by short(30)
	var message     by int(32)
}



class RawInputMouse(override val address: Long) : Struct {
	var type        by int(0)
	var size        by int(4)
	var handle      by long(8)
	var wParam      by long(16)
	var flags       by short(24)
	var buttonFlags by short(28)
	var buttonData  by short(30)
	var rawButtons  by int(32)
	var lastX       by int(36)
	var lastY       by int(40)
	var extra       by int(44)
}



class Message(override val address: Long) : Struct {
	var hwnd    by long(0)
	var message by int(8)
	var wparam  by long(16)
	var lparam  by long(24)
	var time    by int(32)
	var cursorX by int(36)
	var cursorY by int(40)
}



class KeyboardInput(override val address: Long) : Struct {
	var type        by int(0)
	var virtualCode by short(8)
	var scanCode    by short(10)
	var flags       by int(12)
	var time        by int(12)
	var extra       by long(16)
}



class MouseInput(override val address: Long) : Struct {
	var type  by int(0)
	var dx    by int(8)
	var dy    by int(12)
	var data  by int(16)
	var flags by int(20)
	var time  by int(24)
}

