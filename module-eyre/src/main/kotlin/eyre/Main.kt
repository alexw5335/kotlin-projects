package eyre

import java.nio.file.Paths

fun main() {
	val srcDir = Paths.get("test/src")

	Compiler.create(srcDir, listOf("vulkan", "main")).compile()
}