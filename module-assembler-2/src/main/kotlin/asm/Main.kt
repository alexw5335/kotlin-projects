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
			RegisterNode(Register.AL),
			ImmediateNode(IntNode(1)),
			null,
			null
		)
	)

	val bytes = Assembler(nodes, emptyMap()).assemble()
	Files.write(Paths.get("test.bin"), bytes)
	Core.run("ndisasm test.bin -b64")
	Core.nasmPrint("add al, 1")
}