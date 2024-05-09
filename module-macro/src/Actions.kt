package macro



sealed interface Action {
	val time: Int
}



data class KeyPressAction(override val time: Int, val key: Key) : Action



data class KeyReleaseAction(override val time: Int, val key: Key) : Action



data class MouseAction(override val time: Int, val event: MouseEvent) : Action



data class MoveAction(override val time: Int, val dx: Int, val dy: Int) : Action



data class ScrollAction(override val time: Int, val dy: Int) : Action