package assembler.generator

fun main() {
	val instructions = InstructionReader::class.java
		.getResourceAsStream("/instructions.txt")!!
		.reader()
		.readText()
		.toCharArray()
		.let(::InstructionReader)
		.readInstructions()

	instructions.forEach(::println)
}