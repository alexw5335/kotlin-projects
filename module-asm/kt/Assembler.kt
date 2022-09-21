import core.Core


@Suppress("unused")
object NasmMain

@Suppress("unused")
object AsmMain



fun main() {
	Core.nasmRun("module-asm", "program")
	Core.nasmPrint("""
		add eax, [rax + 1]
	""".trimIndent())
}