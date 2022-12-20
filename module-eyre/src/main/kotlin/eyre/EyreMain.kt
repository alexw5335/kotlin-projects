package eyre

fun main() {
	val compiler = Compiler.createFromResources("/samples", "main.eyre")
	compiler.compile()
}