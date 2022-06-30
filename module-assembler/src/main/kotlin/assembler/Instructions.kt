package assembler

import assembler.Operands.*

object Instructions {


	private fun Int.single(operands: Operands) = InstructionEncoding(this, operands)
	private fun Int.single(extension: Int, operands: Operands) = InstructionEncoding(this, operands, extension)


	val ADD = listOf(
		0x04.single(AL_IMM8),
		0x05.single(A_IMM),
		0x80.single(0, RM8_IMM8),
		0x81.single(0, RM_IMM),
		0x83.single(0, RM_IMM8),
		0x00.single(RM8_R8),
		0x01.single(RM_R),
		0x02.single(R8_RM8),
		0x03.single(R_RM)
	)

}