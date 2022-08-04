package assembler

@JvmInline
value class OperandType(val value: Int) {


	infix fun or(other: OperandType) = OperandType(value or other.value)

	operator fun contains(other: OperandType) = value and other.value == other.value



	/*
	InstructionNode produces one flag bits (most specific), which is then matched against multiple flag bits when
	determining the instruction encoding.
	E.g. InstructionNode produces AL, matched against AL | R8
	 */


	
	override fun toString() = buildString {
		append('{')

		if(value == 0) {
			append("EMPTY}")
			return@buildString
		}
		
		var count = 0
		fun appendBit(name: String) { if(value and (1 shl count++) != 0) append("$name, ") }
		appendBit("R8")
		appendBit("AL")
		appendBit("CL")
		appendBit("R16")
		appendBit("AX")
		appendBit("DX")
		appendBit("R32")
		appendBit("EAX")
		appendBit("R64")
		appendBit("RAX")
		appendBit("MN")
		appendBit("M8")
		appendBit("M16")
		appendBit("M32")
		appendBit("M64")
		appendBit("IMMN")
		appendBit("IMM8")
		appendBit("IMM16")
		appendBit("IMM32")
		appendBit("IMM64")
		appendBit("ONE")
		appendBit("ZERO")
		setLength(length - 2)
		append('}')
	}



	companion object {

		private var count = 0
		private fun new() = OperandType(1 shl (count++))

		val NONE = OperandType(0)
		val R8 = new()
		val AL = new()
		val CL = new()
		val R16 = new()
		val AX = new()
		val DX = new()
		val R32 = new()
		val EAX = new()
		val R64 = new()
		val RAX = new()
		val MN = new() // Memory operand of unspecified width
		val M8 = new()
		val M16 = new()
		val M32 = new()
		val M64 = new()
		val IMMN = new() // Immediate of indeterminable width
		val IMM8 = new()
		val IMM16 = new()
		val IMM32 = new()
		val IMM64 = new()
		val ONE = new()
		val ZERO = new()

		val IMM = IMMN or IMM8 or IMM16 or IMM32 or IMM64 or ONE or ZERO
		val M = MN or M8 or M16 or M32 or M64

	}


}



/*

Hierarchy for target bits

R
	R8
		AL
		CL
	R16
		AX
		DX
	R32
		EAX
	R64
		RAX
M
	Mn
	M8
	M16
	M32
	M64
	M128
IMM
	IMM64
		IMM32
			IMM16
				IMM8
					ONE
					ZERO

 */