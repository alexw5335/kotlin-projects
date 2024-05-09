#ifndef UNICODE
#define UNICODE
#endif



#include <windows.h>
#include <jni.h>



WNDCLASS class = { };

int initialised = 0;

JNIEnv* globalEnv;

jclass globalClass;

jmethodID methodID;



LRESULT CALLBACK windowProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam) {
	if(globalEnv == NULL)
		return DefWindowProc(hwnd, uMsg, wParam, lParam);

	int handled = (*globalEnv)->CallBooleanMethod(
		globalEnv,
		globalClass,
		methodID,
		hwnd,
		uMsg,
		wParam,
		lParam
	);

	if(!handled)
		return DefWindowProc(hwnd, uMsg, wParam, lParam);
}



HWND createWindow(const wchar_t* title, int x, int y, int width, int height) {
	if(!initialised) {
		class.lpszClassName = L"JNI";
		class.lpfnWndProc = windowProc;
		class.hCursor = LoadCursor(NULL, IDC_ARROW);
		RegisterClass(&class);
		initialised = TRUE;
	}

	HWND hwnd = CreateWindow(
		class.lpszClassName,
		title,
		WS_OVERLAPPEDWINDOW,
		x,
		y,
		width,
		height,
		NULL,
		NULL,
		NULL,
		NULL
	);

	return hwnd;
}




JNIEXPORT jint JNICALL Java_macro_Natives_sendInput(
	JNIEnv* env,
	jobject obj,
	jint param1,
	jlong param2,
	jint param3
) {
	return (jint) SendInput(param1, (LPINPUT) param2, param3);
}



JNIEXPORT jint JNICALL Java_macro_Natives_getLastError(
	JNIEnv* env,
	jobject obj
) {
	return (jint) GetLastError();
}



JNIEXPORT HWND JNICALL Java_macro_Natives_createWindow(
	JNIEnv* env,
	jobject obj,
	const wchar_t* title,
	jint x,
	jint y,
	jint width,
	jint height
) {
	globalEnv = env;
	globalClass = (*env)->GetObjectClass(env, obj);
	methodID = (*env)->GetMethodID(env, globalClass, "windowProc", "(JIJJ)Z");
	return createWindow(title, x, y, width, height);
}



JNIEXPORT jboolean JNICALL Java_macro_Natives_destroyWindow(
	JNIEnv* env,
	jobject obj,
	HWND hwnd
) {
	return DestroyWindow(hwnd);
}



JNIEXPORT jboolean JNICALL Java_macro_Natives_showWindow(
	JNIEnv* env,
	jobject obj,
	HWND hwnd,
	jint code
) {
	return (jboolean) ShowWindow(hwnd, code);
}



JNIEXPORT jint JNICALL Java_macro_Natives_getSystemMetrics(
	JNIEnv* env,
	jobject obj,
	jint code
) {
	return GetSystemMetrics(code);
}



JNIEXPORT jboolean JNICALL Java_macro_Natives_peekMessage(
	JNIEnv* env,
	jobject obj,
	jlong msg
) {
	return (jboolean) PeekMessage((MSG*) msg, 0, 0, 0, PM_REMOVE);
}



JNIEXPORT jboolean JNICALL Java_macro_Natives_translateMessage(
	JNIEnv* env,
	jobject obj,
	MSG* msg
) {
	return (jboolean) TranslateMessage(msg);
}



JNIEXPORT jint JNICALL Java_macro_Natives_dispatchMessage(
	JNIEnv* env,
	jobject obj,
	MSG* msg
) {
	return (jint) DispatchMessage(msg);
}



JNIEXPORT void JNICALL Java_macro_Natives_updateRect(
	JNIEnv* env,
	jobject obj,
	HWND hwnd,
	RECT* rect
) {
	GetWindowRect(hwnd, rect);
}



JNIEXPORT void JNICALL Java_macro_Natives_updateClientRect(
	JNIEnv* env,
	jobject obj,
	HWND hwnd,
	RECT* rect
) {
	GetClientRect(hwnd, rect);
}



JNIEXPORT jint JNICALL Java_macro_Natives_getCursorX(
	JNIEnv* env,
	jobject obj,
	HWND hwnd
) {
	POINT point = { };
	GetCursorPos(&point);
	ScreenToClient(hwnd, &point);
	return point.x;
}



JNIEXPORT int JNICALL Java_macro_Natives_getCursorY(
	JNIEnv* env,
	jobject obj,
	HWND hwnd
) {
	POINT point = { };
	GetCursorPos(&point);
	ScreenToClient(hwnd, &point);
	return point.y;
}



JNIEXPORT jint JNICALL Java_macro_Natives_getKeyState(
	JNIEnv* env,
	jobject obj,
	jint virtualKey
) {
	return (jint) GetKeyState(virtualKey);
}



JNIEXPORT jlong JNICALL Java_macro_Natives_getFocussedWindow(
	JNIEnv* env,
	jobject obj
) {
	return (jlong) GetFocus();
}



JNIEXPORT void JNICALL Java_macro_Natives_getWindowRect(
	JNIEnv* env,
	jobject obj,
	HWND hwnd,
	RECT* rect
) {
	GetWindowRect(hwnd, rect);
}



JNIEXPORT void JNICALL Java_macro_Natives_getClientRect(
	JNIEnv* env,
	jobject obj,
	HWND hwnd,
	RECT* rect
) {
	GetClientRect(hwnd, rect);
}



JNIEXPORT void JNICALL Java_macro_Natives_adjustWindowRect(
	JNIEnv* env,
	jobject obj,
	RECT* rect,
	jint style,
	jboolean menu
) {
	AdjustWindowRect(rect, style, menu);
}



JNIEXPORT jint JNICALL Java_macro_Natives_getMessage(
	JNIEnv* env,
	jobject obj,
	LPMSG msg,
	HWND hwnd,
	jint min,
	jint max
) {
	return GetMessage(msg, hwnd, min, max);
}



JNIEXPORT void JNICALL Java_macro_Natives_postQuitMessage(
	JNIEnv* env,
	jobject obj,
	jint exitCode
) {
	PostQuitMessage(exitCode);
}



JNIEXPORT jboolean JNICALL Java_macro_Natives_registerRawInputDevices(
	JNIEnv* env,
	jobject obj,
	PCRAWINPUTDEVICE devices,
	jint numDevices,
	jint structSize
) {
	return RegisterRawInputDevices(devices, numDevices, structSize);
}



JNIEXPORT jint JNICALL Java_macro_Natives_getRawInputData(
	JNIEnv* env,
	jobject obj,
	HRAWINPUT rawInput,
	jint uiCommand,
	LPVOID data,
	PUINT size,
	UINT sizeHeader
) {
	return GetRawInputData(rawInput, uiCommand, data, size, sizeHeader);
}



JNIEXPORT jint JNICALL Java_macro_Natives_mapVirtualKey(
	JNIEnv* env,
	jobject obj,
	jint code,
	jint type

) {
	return MapVirtualKey(code, type);
}