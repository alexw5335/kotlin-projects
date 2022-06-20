package assembler

import assembler.Mnemonic.*
import assembler.OperandsEncoding.*

object Instructions {


	private fun Mnemonic.single(
		opcode: Int,
		operands: OperandsEncoding,
		extension: Int = -1
	) = InstructionEncoding(
		this,
		opcode,
		Optype.SINGLE,
		operands,
		extension
	)



	private fun Mnemonic.double(
		opcode: Int,
		operands: OperandsEncoding,
		extension: Int = -1
	) = InstructionEncoding(
		this,
		opcode,
		Optype.DOUBLE,
		operands,
		extension
	)



	val unordered = listOf(
		ADD.single(0x04, AL_IMM8),
		ADD.single(0x05, A_IMM),
		ADD.single(0x80, RM8_IMM8, 0),
		ADD.single(0x81, RM_IMM, 0),
		ADD.single(0x83, RM_IMM8, 0),
		ADD.single(0x00, RM8_R8),
		ADD.single(0x01, RM_R),
		ADD.single(0x02, R8_RM8),
		ADD.single(0x03, R_RM),

		OR.single(0x0C, AL_IMM8),
		OR.single(0x0D, A_IMM),
		OR.single(0x80, RM8_IMM8, 1),
		OR.single(0x81, RM_IMM, 1),
		OR.single(0x83, RM_IMM8, 1),
		OR.single(0x08, RM8_R8),
		OR.single(0x09, RM_R),
		OR.single(0x0A, R8_RM8),
		OR.single(0x0B, R_RM)
	)



	private val mnemonicMap = HashMap<Mnemonic, MutableList<InstructionEncoding>>()



	init {
		for(encoding in unordered)
			mnemonicMap
				.getOrPut(encoding.mnemonic, ::ArrayList)
				.add(encoding)
	}


}