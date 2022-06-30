package assembler

import assembler.Operands.*

object Instructions {


	private fun Int.single(operands: Operands) = InstructionEncoding(this, operands)
	private fun Int.single(extension: Int, operands: Operands) = InstructionEncoding(this, operands, extension)

}