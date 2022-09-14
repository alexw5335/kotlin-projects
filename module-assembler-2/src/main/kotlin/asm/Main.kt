package asm

import core.Core
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Deque
import java.util.LinkedList

fun main() {
	val nodes = listOf(
		InstructionNode(
			Mnemonic.ADD,
			op2 = ImmediateNode(IntNode(0x1000)),
			op1 = MemoryNode(Width.BIT16, false, Register.RAX, Register.RCX, 8, IntNode(0x1000)),
			op3 = null,
			op4 = null
		)
	)

	val bytes = Assembler(nodes, emptyMap()).assemble()
	Files.write(Paths.get("test.bin"), bytes)
	Core.run("ndisasm test.bin -b64")
	Core.nasmPrint("add dword [rax + rcx * 8 + 0x1000], 0x1000")
}