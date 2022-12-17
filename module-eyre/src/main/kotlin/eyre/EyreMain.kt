package eyre

fun main() {
	Compiler.createFromResources("/samples", "window.eyre").run()
}