package macro

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

enum class MouseEvent(val value: Int) {
	MOVE(0x001),
	LEFT_DOWN(0x002),
	LEFT_UP(0x004),
	RIGHT_DOWN(0x008),
	RIGHT_UP(0x010),
	MIDDLE_DOWN(0x020E),
	MIDDLE_UP(0x040),
	SCROLL(0x800);

	companion object {
		val nameMap = entries.associateBy { it.name }
	}
}