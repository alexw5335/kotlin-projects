package assembler

import assembler.Mnemonic.*
import assembler.OpType.*
import assembler.Operands.*

object Instructions {


	private fun single(
		opcode    : Int,
		operands  : Operands,
		extension : Int = -1
	) = Instruction(
		SINGLE,
		opcode,
		extension,
		operands,
		custom = false,
		oso = false,
		rexw = false
	)



	val map = mapOf(
		ADD to listOf(
			single(0x04, AL_IMM8),
			single(0x05, A_IMM),
			single(0x80, RM8_IMM8, extension = 0),
			single(0x83, RM_IMM8, extension = 0),
			single(0x81, RM_IMM, extension = 0),
			single(0x00, RM8_R8),
			single(0x01, RM_R),
			single(0x02, R8_RM8),
			single(0x03, R_RM)
		)
	)


}