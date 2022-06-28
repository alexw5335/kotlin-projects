package assembler

import assembler.Mnemonic.*
import assembler.OpType.*
import assembler.Operand.*

object Instructions {


	private fun single(
		opcode    : Int,
		operand1  : Operand,
		operand2  : Operand,
		extension : Int = -1
	) = Instruction(
		SINGLE,
		opcode,
		extension,
		operand1,
		operand2,
		Operand.NONE,
		Operand.NONE,
		oso = false,
		rexw = false
	)



	val map = mapOf(
		ADD to listOf(
			single(0x04, AL, IMM8),
			single(0x05, A, IMM),
			single(0x80, RM8, IMM8, extension = 0),
			single(0x81, RM, IMM, extension = 0),
			single(0x83, RM, IMM8, extension = 0),
			single(0x00, RM8, R8),
			single(0x01, RM, R),
			single(0x02, R8, RM8),
			single(0x03, R, RM)
		)
	)


}