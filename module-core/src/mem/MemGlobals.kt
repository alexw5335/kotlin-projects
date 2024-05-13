package core.mem


inline fun stack(block: MemStack.() -> Unit) {
	val stack = MemStack.current()
	val pointer = stack.pointer
	block(stack)
	stack.pointer = pointer
}



inline fun<T> stackGet(block: MemStack.() -> T): T {
	val stack = MemStack.current()
	val pointer = stack.pointer
	val result = block(stack)
	stack.pointer = pointer
	return result
}



fun <T> stackLazy(initialiser: MemStack.() -> T) = MemStackLazy(initialiser)



val Addressable?.addressOrNULL get() = this?.address ?: 0L