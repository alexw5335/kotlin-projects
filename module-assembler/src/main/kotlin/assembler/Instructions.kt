package assembler

import assembler.Mnemonic.*
import assembler.Operand.*
import assembler.OperandEncoding.*

object Instructions {


	private val map = HashMap<Mnemonic, ArrayList<InstructionEncoding>>()

	fun get(mnemonic: Mnemonic) = map[mnemonic]



	private fun Mnemonic.add(
		opcode : Int,
		opEnc  : OperandEncoding,
		op1    : Operand? = null,
		op2    : Operand? = null,
		op3    : Operand? = null,
		op4    : Operand? = null
	) {
		val list = map.getOrPut(this, ::ArrayList)
		list.add(InstructionEncoding(this, opcode, opEnc, op1, op2, op3, op4))
	}



	init {
		ADD.add(0x00, MR, R8,  R8)
		ADD.add(0x00, MR, M8,  R8)
		ADD.add(0x01, MR, R16, R16)
		ADD.add(0x01, MR, M16, R16)
		ADD.add(0x01, MR, R32, R32)
		ADD.add(0x01, MR, M32, M32)
		ADD.add(0x01, MR, R64, R64)
		ADD.add(0x01, MR, M64, R64)

		ADD.add(0x02, RM, R8,  M8)
		ADD.add(0x03, RM, R16, M16)
		ADD.add(0x03, RM, R32, M32)
		ADD.add(0x03, RM, R64, M64)
		ADD.add(0x04, I,  AL,  IMM8)

		ADD.add(0x83, MI, R16, IMM8)
		ADD.add(0x83, MI, M16, IMM8)
		ADD.add(0x83, MI, R32, IMM8)
		ADD.add(0x83, MI, M32, IMM8)
		ADD.add(0x83, MI, R64, IMM8)
		ADD.add(0x83, MI, M64, IMM8)

		ADD.add(0x05, I,  AX,  IMM16)
		ADD.add(0x05, I,  EAX, IMM32)
		ADD.add(0x05, I,  RAX, IMM32)

		ADD.add(0x80, MI, R8,  IMM8)
		ADD.add(0x80, MI, M8,  IMM8)
		ADD.add(0x81, MI, R16, IMM16)
		ADD.add(0x81, MI, M16, IMM16)
		ADD.add(0x81, MI, R32, IMM32)
		ADD.add(0x81, MI, M32, IMM32)
		ADD.add(0x81, MI, R64, IMM32)
		ADD.add(0x81, MI, M64, IMM32)
	}


}