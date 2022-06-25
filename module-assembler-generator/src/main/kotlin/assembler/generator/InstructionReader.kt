package assembler.generator

import core.LexerBase

class InstructionReader(chars: CharArray) : LexerBase(chars) {


	private var currentMnemonic = ""

	private val mnemonics = ArrayList<String>()

	private val operandMap = Operand.values().associateBy(Operand::name)

	private val instructions = ArrayList<Instruction>()



	fun readInstructions(): List<Instruction> {
		while(pos < chars.size) {
			val char = chars[pos]

			if(char.isWhitespace()) {
				pos++
				continue
			}

			if(char == ';') {
				skipLine()
				continue
			}

			if(char == '#') {
				pos++
				skipWhitespace()
				currentMnemonic = readWhile { it.isLetterOrDigit() }
				mnemonics.add(currentMnemonic)
				skipLine()
				continue
			}

			instructions.add(readInstruction().also(::println))
		}

		return instructions
	}



	private fun readOperandEncoding(): Operand? {
		skipWhile { it.isWhitespace() && it != '\n' && it != ';' }
		if(pos >= chars.size || chars[pos] == '\n' || chars[pos] == ';')
			return null
		val string = readUntil { it.isWhitespace() || it == ';' }
		return operandMap[string] ?: error("Unrecognised operand: $string")
	}



	private fun readInstruction(): Instruction {
		var hex = readHex2()
		var mandatoryPrefix = -1

		if(hex == 0x66 || hex == 0xF2 || hex == 0xF3) {
			mandatoryPrefix = hex
			skipWhitespace()
			hex = readHex2()
		}

		var opcode = hex
		var optype = OpType.SINGLE

		if(hex == 0x0F) {
			skipSpaces()
			opcode = readHex2()
			if(opcode == 0x38) {
				skipSpaces()
				opcode = readHex2()
				optype = OpType.TRIP38
			} else if(opcode == 0x3A) {
				skipSpaces()
				opcode = readHex2()
				optype = OpType.TRIP3A
			}
		}

		skipSpaces()

		val extension = if(chars[pos] == '/') {
			pos++
			chars[pos++] - '0'
		} else -1

		skipSpaces()

		val operand1 = readOperandEncoding()
		val operand2 = if(operand1 != null) readOperandEncoding() else null
		val operand3 = if(operand2 != null) readOperandEncoding() else null
		val operand4 = if(operand3 != null) readOperandEncoding() else null

		return Instruction(
			currentMnemonic,
			opcode,
			optype,
			operand1,
			operand2,
			operand3,
			operand4,
			extension,
			mandatoryPrefix
		)
	}


}