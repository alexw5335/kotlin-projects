package generator

/*
import assembler.Mnemonic
import assembler.OpcodeType
import assembler.Operands
import core.ReaderBase


class InstructionReader(chars: CharArray) : ReaderBase(chars) {


	enum class Operand {

		NONE,
		R,
		RM,
		IMM,
		IMM8,
		R8,
		R16,
		R32,
		R64,
		M8,
		M16,
		M32,
		M64,
		RM8;

		val isNone get() = this == NONE

		companion object {
			val map = values().associateBy { it.name }
		}

	}




	fun expandOperands(
		op1: Operand,
		op2: Operand,
		op3: Operand,
		op4: Operand
	) : List<Operands> {
		if(op1.isNone)
			return listOf(Operands.NONE)
		if(op2.isNone) {
			return when(op1) {
				Operand.R8 -> listOf(Operands.R8)
				Operand.R16 -> listOf(Operands.R16)
				Operand.R32 -> listOf(Operands.R32)
				Operand.R64 -> listOf(Operands.R64)
 			}
		}
	}



	data class Instruction(
		val mnemonic: Mnemonic,
		val opcode: Int,
		val opcodeType: OpcodeType
	)



	fun read() {
		while(pos < chars.size) {
			val char = chars[pos]

			if(char.isWhitespace()) {
				pos++
				continue
			}

			if(char == '#') {
				skipLine()
				continue
			}

			readInstruction()
		}
	}



	private fun readOperand(): Operand {
		skipSpaces()
		if(pos >= chars.size || chars[pos] == '\n' || chars[pos] == '#' || chars[pos] == '(')
			return Operand.NONE
		val string = readWhile { it.isLetterOrDigit() || it == '_' }
		return Operand.map[string] ?: error("Unrecognised operand: $string")
	}



	private fun readInstruction() {
		var hex = readHex2()
		var prefix = -1

		if(hex == 0x66 || hex == 0xF2 || hex == 0xF3) {
			prefix = hex
			skipSpaces()
			hex = readHex2()
		}

		var opcode = hex
		var opcodeType = OpcodeType.SINGLE

		if(hex == 0x0F) {
			skipSpaces()
			opcode = readHex2()
			opcodeType = OpcodeType.DOUBLE

			if(opcode == 0x38) {
				skipSpaces()
				opcode = readHex2()
				opcodeType = OpcodeType.TRIPLE38
			} else if(opcode == 0x3A) {
				skipSpaces()
				opcode = readHex2()
				opcodeType = OpcodeType.TRIPLE3A
			}
		}

		skipSpaces()

		val extension = if(advanceIfAt('/')) chars[pos++] - '0' else -1

		val op1 = readOperand()
		val op2 = if(!op1.isNone) readOperand() else Operand.NONE
		val op3 = if(!op2.isNone) readOperand() else Operand.NONE
		val op4 = if(!op3.isNone) readOperand() else Operand.NONE

		skipLine()
	}


}*/
