import core.Core


@Suppress("unused")
object NasmMain

@Suppress("unused")
object AsmMain



fun main() {
	//Core.nasmRun("module-asm", "program")
	Core.nasmPrint("""
		l0:
		add eax, [1000 * 2 - (300 - rax)]
		l1:
	""".trimIndent())
}