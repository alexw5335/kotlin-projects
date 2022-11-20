package eyre

import java.nio.file.Paths

fun main() {
	Compiler.create(Paths.get("test/src")).compile()
}