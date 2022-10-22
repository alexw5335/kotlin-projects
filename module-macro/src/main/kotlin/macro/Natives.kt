package macro

object Natives {


	external fun sendInput(inputCount: Int, pInputs: Long, inputSize: Int): Int

	external fun getLastError(): Int

	external fun createWindow(title: Long, x: Int, y: Int, width: Int, height: Int): Long

	external fun destroyWindow(hwnd: Long): Boolean

	external fun showWindow(hwnd: Long, code: Int): Boolean

	external fun getSystemMetrics(code: Int): Int

	external fun peekMessage(msg: Long): Boolean

	external fun translateMessage(msg: Long): Boolean

	external fun dispatchMessage(msg: Long): Int

	external fun getWindowRect(hwnd: Long, rect: Long)

	external fun getClientRect(hwnd: Long, rect: Long)

	external fun getCursorPos(hwnd: Long, point: Long)

	external fun getKeyState(virtualKey: Int): Int

	external fun getFocussedWindow(): Long

	external fun adjustWindowRect(rect: Long, style: Int, menu: Boolean)

	external fun getMessage(msg: Long, hwnd: Long, min: Int, max: Int) : Int

	external fun postQuitMessage(exitCode: Int)

	external fun registerRawInputDevices(devices: Long, count: Int, size: Int): Boolean

	external fun getRawInputData(rawInput: Long, uiCommand: Int, data: Long, size: Long, sizeHeader: Int): Int

	external fun mapVirtualKey(code: Int, type: Int): Int



	fun createWindow() = createWindow(0, 200, 200, 200, 200)

	var proc: (hwnd: Long, msg: Int, wparam: Long, lparam: Long) -> Boolean = { _,_,_,_ -> false}



	@Suppress("unused")
	fun windowProc(hwnd: Long, msg: Int, wparam: Long, lparam: Long) =
		proc(hwnd, msg, wparam, lparam)


}