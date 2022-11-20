package eyre

class Qualified(val components: Array<Intern>) {

	override fun equals(other: Any?) = other is Qualified && components.contentEquals(other.components)

	override fun hashCode() = components.contentHashCode()

}