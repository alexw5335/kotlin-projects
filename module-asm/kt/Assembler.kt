import core.Core


@Suppress("unused")
object NasmMain

@Suppress("unused")
object AsmMain



fun main() {
	//Core.nasmRun("module-asm", "program")
	Core.nasmPrint("""
		mov eax, [qword label]
		label:
	""".trimIndent())
}