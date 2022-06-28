package assembler

import assembler.Mnemonic.*
import assembler.OpType.*
import assembler.Operands.*

object Instructions {


	private fun single(opcode: Int, operands: Operands) = Instruction(
		-1,
		SINGLE,
		opcode,
		-1,
		operands,
		oso = false,
		rexw = false
	)



	val map = mapOf(
		ADD to listOf(
			single(0x00, R_R),
			single(0x00, R_MEM),
			single(0x01, R_R),
			single(0x01, MEM_R)
		)
	)


}