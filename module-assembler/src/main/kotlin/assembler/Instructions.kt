package assembler

import assembler.Mnemonic.*
import assembler.OperandEncoding.*

object Instructions {


	private fun Mnemonic.single(
		opcode    : Int,
		operand1  : OperandEncoding? = null,
		operand2  : OperandEncoding? = null,
		operand3  : OperandEncoding? = null,
		operand4  : OperandEncoding? = null,
		extension : Int = -1
	) = InstructionEncoding(
		this,
		opcode,
		OpType.SINGLE,
		operand1,
		operand2,
		operand3,
		operand4,
		extension,
		-1
	)



	val all = listOf(
		ADD.single(0x04, AL,  IMM),
		ADD.single(0x05, A,   IMM),
		ADD.single(0x80, RM8, IMM8, extension = 0),
		ADD.single(0x81, RM,  IMM,  extension = 0),
		ADD.single(0x83, RM,  IMM8, extension = 0),
		ADD.single(0x00, RM8, R8),
		ADD.single(0x01, RM,  R),
		ADD.single(0x02, R8,  RM8),
		ADD.single(0x03, R,   RM),

		OR.single(0x0C, AL,  IMM),
		OR.single(0x0D, A,   IMM),
		OR.single(0x80, RM8, IMM8, extension = 1),
		OR.single(0x81, RM,  IMM,  extension = 1),
		OR.single(0x83, RM,  IMM8, extension = 1),
		OR.single(0x08, RM8, R8),
		OR.single(0x09, RM,  R),
		OR.single(0x0A, R8,  RM8),
		OR.single(0x0B, R,   RM),

		ADC.single(0x14, AL,  IMM),
		ADC.single(0x15, A,   IMM),
		ADC.single(0x80, RM8, IMM8, extension = 2),
		ADC.single(0x81, RM,  IMM,  extension = 2),
		ADC.single(0x83, RM,  IMM8, extension = 2),
		ADC.single(0x10, RM8, R8),
		ADC.single(0x11, RM,  R),
		ADC.single(0x12, R8,  RM8),
		ADC.single(0x13, R,   RM),

		SBB.single(0x1C, AL,  IMM),
		SBB.single(0x1D, A,   IMM),
		SBB.single(0x80, RM8, IMM8, extension = 2),
		SBB.single(0x81, RM,  IMM,  extension = 2),
		SBB.single(0x83, RM,  IMM8, extension = 2),
		SBB.single(0x18, RM8, R8),
		SBB.single(0x19, RM,  R),
		SBB.single(0x1A, R8,  RM8),
		SBB.single(0x1B, R,   RM),

		AND.single(0x24, AL,  IMM),
		AND.single(0x25, A,   IMM),
		AND.single(0x80, RM8, IMM8, extension = 2),
		AND.single(0x81, RM,  IMM,  extension = 2),
		AND.single(0x83, RM,  IMM8, extension = 2),
		AND.single(0x20, RM8, R8),
		AND.single(0x21, RM,  R),
		AND.single(0x22, R8,  RM8),
		AND.single(0x23, R,   RM),

		SUB.single(0x2C, AL,  IMM),
		SUB.single(0x2D, A,   IMM),
		SUB.single(0x80, RM8, IMM8, extension = 2),
		SUB.single(0x81, RM,  IMM,  extension = 2),
		SUB.single(0x83, RM,  IMM8, extension = 2),
		SUB.single(0x28, RM8, R8),
		SUB.single(0x29, RM,  R),
		SUB.single(0x2A, R8,  RM8),
		SUB.single(0x2B, R,   RM),

		XOR.single(0x34, AL,  IMM),
		XOR.single(0x35, A,   IMM),
		XOR.single(0x80, RM8, IMM8, extension = 2),
		XOR.single(0x81, RM,  IMM,  extension = 2),
		XOR.single(0x83, RM,  IMM8, extension = 2),
		XOR.single(0x30, RM8, R8),
		XOR.single(0x31, RM,  R),
		XOR.single(0x32, R8,  RM8),
		XOR.single(0x33, R,   RM),

		CMP.single(0x3C, AL,  IMM),
		CMP.single(0x3D, A,   IMM),
		CMP.single(0x80, RM8, IMM8, extension = 2),
		CMP.single(0x81, RM,  IMM,  extension = 2),
		CMP.single(0x83, RM,  IMM8, extension = 2),
		CMP.single(0x38, RM8, R8),
		CMP.single(0x39, RM,  R),
		CMP.single(0x3A, R8,  RM8),
		CMP.single(0x3B, R,   RM),
	)



	private val mnemonicMap = HashMap<Mnemonic, MutableList<InstructionEncoding>>()



	init {
		for(encoding in all)
			mnemonicMap
				.getOrPut(encoding.mnemonic, ::ArrayList)
				.add(encoding)
	}


}