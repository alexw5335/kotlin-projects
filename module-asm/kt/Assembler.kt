import core.Core



object NasmMain

object AsmMain



fun main() {
	//Core.nasmRun("module-asm", "assembler")
	Core.nasmAssemble("""
		.xmm0:
			mov rax, rax
	""".trimIndent())
}