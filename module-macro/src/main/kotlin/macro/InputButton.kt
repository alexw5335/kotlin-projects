package macro

/**
 * Codes are based on WinApi codes.
 */
enum class InputButton(
	val code     : Int,
	val scanCode : Int = 0,
	val type     : Type = Type.KEY
) {


	NONE(0x00),

	LEFT_MOUSE(0x01,  type = Type.MOUSE),
	RIGHT_MOUSE(0x02,  type = Type.MOUSE),
	MIDDLE_MOUSE(0x04, type = Type.MOUSE),
	X1_MOUSE(0x05,     type = Type.MOUSE),
	X2_MOUSE(0x06,     type = Type.MOUSE),

	BACKSPACE(0x08, 15),
	TAB(0x09, 16),
	CLEAR(0x0C),
	ENTER(0x0D, 43),
	SHIFT(0x10),
	CTRL(0x11),
	ALT(0x12),
	PAUSE(0x13, 126),
	CAPS_LOCK(0x14, 30),
	ESCAPE(0x1B, 110),
	SPACEBAR(0x20, 61),
	PAGE_UP(0x21, 85),
	PAGE_DOWN(0x22, 86),
	END(0x23, 81),
	HOME(0x24, 80),
	LEFT(0x25, 75),
	UP(0x26, 72),
	RIGHT(0x27, 77),
	DOWN(0x28, 80),
	SELECT(0x29),
	PRINT(0x2A),
	EXECUTE(0x2B),
	PRINT_SCREEN(0x2C, 124),
	INSERT(0x2D, 75),
	DELETE(0x2E, 76),
	HELP(0x2F),

	ZERO(0x30, 11),
	ONE(0x31, 2),
	TWO(0x32, 3),
	THREE(0x33, 4),
	FOUR(0x34, 5),
	FIVE(0x35, 6),
	SIX(0x36, 7),
	SEVEN(0x37, 8),
	EIGHT(0x38, 9),
	NINE(0x39, 10),

	A(0x41, 31),
	B(0x42, 50),
	C(0x43, 48),
	D(0x44, 33),
	E(0x45, 19),
	F(0x46, 34),
	G(0x47, 35),
	H(0x48, 36),
	I(0x49, 24),
	J(0x4A, 37),
	K(0x4B, 38),
	L(0x4C, 39),
	M(0x4D, 52),
	N(0x4E, 51),
	O(0x4F, 25),
	P(0x50, 26),
	Q(0x51, 17),
	R(0x52, 20),
	S(0x53, 32),
	T(0x54, 21),
	U(0x55, 23),
	V(0x56, 49),
	W(0x57, 18),
	X(0x58, 47),
	Y(0x59, 22),
	Z(0x5A, 46),

	LEFT_WINDOWS(0x5B),
	RIGHT_WINDOWS(0x5C),
	APPLICATIONS(0x5D),
	SLEEP(0x5F),

	NUMPAD_ZERO(0x60),
	NUMPAD_ONE(0x61),
	NUMPAD_TWO(0x62),
	NUMPAD_THREE(0x63),
	NUMPAD_FOUR(0x64),
	NUMPAD_FIVE(0x65),
	NUMPAD_SIX(0x66),
	NUMPAD_SEVEN(0x67),
	NUMPAD_EIGHT(0x68),
	NUMPAD_NINE(0x69),
	MULTIPLY(0x6A),

	ADD(0x6B),
	SEPARATOR(0x6C),
	SUBTRACT(0x6D),
	DECIMAL(0x6E),
	DIVIDE(0x6F),

	F1(0x70, 112),
	F2(0x71, 113),
	F3(0x72, 114),
	F4(0x73, 115),
	F5(0x74, 116),
	F6(0x75, 117),
	F7(0x76, 118),
	F8(0x77, 119),
	F9(0x78, 120),
	F10(0x79, 121),
	F11(0x7A, 122),
	F12(0x7B, 123),
	F13(0x7C),
	F14(0x7D),
	F15(0x7E),
	F16(0x7F),
	F17(0x80),
	F18(0x81),
	F19(0x82),
	F20(0x83),
	F21(0x84),
	F22(0x85),
	F23(0x86),
	F24(0x87),

	NUM_LOCK(0x90, 90),
	SCROLL_LOCK(0x91, 125),
	LEFT_SHIFT(0xA0, 44),
	RIGHT_SHIFT(0xA1, 57),
	LEFT_CTRL(0xA2, 58),
	RIGHT_CTRL(0xA3, 64),
	LEFT_ALT(0xA4, 60),
	RIGHT_ALT(0xA5, 62),


	BROWSER_BACK(0xA6),
	BROWSER_FORWARD(0xA7),
	BROWSER_REFRESH(0xA8),
	BROWSER_STOP(0xA9),
	BROWSER_SEARCH(0xAA),
	BROWSER_FAVOURITES(0xAB),
	BROWSER_HOME(0xAC),
	VOLUME_MUTE(0xAD),
	VOLUME_DOWN(0xAE),
	VOLUME_UP(0xAF),
	MEDIA_NEXT(0xB0),
	MEDIA_PREV(0xB1),
	MEDIA_STOP(0xB2),
	MEDIA_PLAY_PAUSE(0xB3),
	LAUNCH_MAIL(0xB4),
	LAUNCH_MEDIA_SELECT(0xB5),
	LAUNCH_APP_ONE(0xB6),
	LAUNCH_APP_TWO(0xB7),

	// ;:
	OEM_ONE(0xBA, 40),
	OEM_SEMICOLON(OEM_ONE.code, OEM_ONE.scanCode),

	// +=
	OEM_PLUS(0xBB, 13),

	// ,<
	OEM_COMMA(0xBC, 53),

	// -_
	OEM_MINUS(0xBD, 12),

	// .>
	OEM_PERIOD(0xBE, 54),

	// /?
	OEM_TWO(0xBF, 55),
	OEM_FORWARD_SLASH(OEM_TWO.code, OEM_TWO.scanCode),

	// tilde
	OEM_THREE(0xC0, 1),
	TILDE(OEM_THREE.code, OEM_THREE.scanCode),

	// [{
	OEM_FOUR(0xDB, 27),
	LEFT_BRACKET(OEM_FOUR.code, OEM_FOUR.scanCode),
	LEFT_BRACE(OEM_FOUR.code, OEM_FOUR.scanCode),

	// \|
	OEM_FIVE(0xDC, 29),
	BACK_SLASH(OEM_FIVE.code, OEM_FIVE.scanCode),

	// ]}
	OEM_SIX(0xDD, 28),
	OEM_RIGHT_BRACKET(OEM_SIX.code, OEM_SIX.scanCode),

	// '"
	OEM_SEVEN(0xDE, 41),
	OEM_QUOTE(OEM_SEVEN.code, OEM_SEVEN.scanCode),

	OEM_EIGHT(0xDF),

	PACKET(0xE7),

	PLAY(0xFA),

	ZOOM(0xFB);



	enum class Type { KEY, MOUSE; }



	companion object {

		val values = values()

		val map = values.associateBy { it.code }

		val nameMap = values.associateBy { it.name }

	}


}