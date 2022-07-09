import core.Core

fun main() {
	//Core.nasmRun("module-asm", "assembler")
	Core.nasmAssemble("""
		label:
			mov byte [label2 - label - label - label], 0xA
		label2:
	""".trimIndent())
}