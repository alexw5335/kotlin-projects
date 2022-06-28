package assembler.generator

import core.Core

fun main() {
	InstructionReader(Core.readResourceText("/instructions.txt")).readInstructions()
}