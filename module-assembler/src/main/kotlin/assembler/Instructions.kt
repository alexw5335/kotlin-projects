package assembler

import assembler.Mnemonic.*
import assembler.OperandsEncoding.*
import assembler.Operands.*



enum class OperandsEncoding {
	I,
	MI,
	RM,
	MR;
}



enum class OpcodeType {
	SINGLE,
	DOUBLE,
	TRIPLE38,
	TRIPLE3A;
}



data class InstructionEncoding(
	val opcode     : Int,
	val opcodeType : OpcodeType,
	val extension  : Int,
	val operands   : Operands,
	val encoding   : OperandsEncoding
)



class InstructionGroup(
	val list                : List<InstructionEncoding>,
	val encodingFlags       : Long,
	val specialisationFlags : Int
)



enum class Specialisation {

	NONE,
	A_IMM,
	R_IMM8,
	M_IMM8;

	val bit = 1 shl (ordinal - 1)

	fun inFlags(flags: Int) = flags and bit != 0

}



/*@JvmInline
value class Specialisations(val value: Int) {

	operator fun contains(other: Specialisations) = value and other.value == other.value

	infix fun or(other: Specialisations) = Specialisations(value or other.value)

	companion object {
		val NONE = Specialisations(0)
		val A_IMM = Specialisations(1)
		val R_IMM8 = Specialisations(2)
		val M_IMM8 = Specialisations(4)
	}

}*/


object Instructions {


	private val rawMap = HashMap<Mnemonic, MutableList<InstructionEncoding>>()

	private val map = HashMap<Mnemonic, InstructionGroup>()

	fun get(mnemonic: Mnemonic) = map[mnemonic]

	private fun add(
		opcode     : Int,
		opcodeType : OpcodeType,
		mnemonic   : Mnemonic,
		extension  : Int,
		operands   : Operands,
		encoding   : OperandsEncoding
	) = rawMap.getOrPut(mnemonic, ::ArrayList).add(InstructionEncoding(
		opcode,
		opcodeType,
		extension,
		operands,
		encoding
	))

	private fun single(
		opcode    : Int,
		mnemonic  : Mnemonic,
		operands  : Operands,
		encoding  : OperandsEncoding,
		extension : Int = 0
	) = add(opcode, OpcodeType.SINGLE, mnemonic, 0, operands, encoding)



	init {
		single(0x00, ADD, R8_R8,     MR)
		single(0x00, ADD, M8_R8,     MR)
		single(0x01, ADD, R16_R16,   MR)
		single(0x01, ADD, M16_R16,   MR)
		single(0x01, ADD, R32_R32,   MR)
		single(0x01, ADD, M32_R32,   MR)
		single(0x01, ADD, R64_R64,   MR)
		single(0x01, ADD, M64_R64,   MR)
		single(0x02, ADD, R8_M8,     RM)
		single(0x03, ADD, R16_M16,   RM)
		single(0x03, ADD, R32_M32,   RM)
		single(0x03, ADD, R64_M64,   RM)
		single(0x04, ADD, AL_IMM8,   I)
		single(0x05, ADD, AX_IMM16,  I)
		single(0x05, ADD, EAX_IMM32, I)
		single(0x05, ADD, RAX_IMM32, I)
		single(0x80, ADD, R8_IMM8,   I,  extension = 0)
		single(0x81, ADD, R16_IMM16, I,  extension = 0)
		single(0x81, ADD, R32_IMM32, I,  extension = 0)
		single(0x81, ADD, R64_IMM32, I,  extension = 0)
		single(0x83, ADD, R16_IMM8,  MI, extension = 0)
		single(0x83, ADD, M16_IMM8,  MI, extension = 0)
		single(0x83, ADD, R32_IMM8,  MI, extension = 0)
		single(0x83, ADD, M32_IMM8,  MI, extension = 0)
		single(0x83, ADD, R64_IMM8,  MI, extension = 0)
		single(0x83, ADD, M64_IMM8,  MI, extension = 0)

		for((mnemonic, encodings) in rawMap) {
			val list = encodings.sortedBy { it.operands }
			var encodingFlags = 0L
			var specialisationFlags = 0

			for(encoding in encodings) {
				encodingFlags = encodingFlags or encoding.operands.bit
				specialisationFlags = specialisationFlags or encoding.operands.specialisation.bit
			}

			map[mnemonic] = InstructionGroup(list, encodingFlags, specialisationFlags)
		}
	}


}