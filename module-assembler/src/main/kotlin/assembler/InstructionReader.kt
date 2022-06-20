package assembler

class InstructionReader(private val chars: CharArray) {


/*
	InstructionReader::class.java
		.getResourceAsStream("/instructions.txt")!!
		.bufferedReader()
		.readText()
		.toCharArray()
		.let(::InstructionReader)
		.read()
*/



/*
	loop over all encodings of add, finding the most suitable one.

	add al, 10 could satisfy either of:
	add al, imm8
	add rm8, imm8

	encodings are sorted by their specificity.
 */


}