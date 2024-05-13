package core.mem

class MemStackLazy<T>(private val initialiser: MemStack.() -> T): Lazy<T> {

	private var _value: T? = null

	override val value: T get() = _value ?: MemStack.current().get(initialiser).also { _value = it }

	override fun isInitialized() = _value != null

}