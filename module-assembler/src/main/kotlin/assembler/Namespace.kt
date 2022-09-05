package assembler

class Namespace(val components: List<String>) {

	override fun hashCode() = components.hashCode()

	override fun equals(other: Any?) = other is Namespace && other.components == components
}