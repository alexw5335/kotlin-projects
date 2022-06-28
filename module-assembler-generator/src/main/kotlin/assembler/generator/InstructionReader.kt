package assembler.generator

import core.ReaderBase

class InstructionReader(chars: CharArray) : ReaderBase(chars) {


	private val instructions = ArrayList<Instruction>()



	fun readInstructions(): List<Instruction> {
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

		return instructions
	}



	private fun readOperandEncoding(): String? {
		skipSpaces()
		if(pos >= chars.size || chars[pos] == '\n' || chars[pos] == '#' || chars[pos] == '(')
			return null
		return readWhile { it.isLetterOrDigit() || it == '_' }
	}



	private fun readInstruction() {
		var hex = readHex2()
		var mandatoryPrefix = -1

		if(hex == 0x66 || hex == 0xF2 || hex == 0xF3) {
			mandatoryPrefix = hex
			skipSpaces()
			hex = readHex2()
		}

		var opcode = hex
		var opType = OpType.SINGLE

		if(hex == 0x0F) {
			skipSpaces()
			opcode = readHex2()
			when(opcode) {
				0x38 -> { skipSpaces(); opcode = readHex2(); opType = OpType.TRIP38 }
				0x3A -> { skipSpaces(); opcode = readHex2(); opType = OpType.TRIP3A }
				else -> { opType = OpType.DOUBLE }
			}
		}

		skipSpaces()

		val extension = if(chars[pos] == '/') {
			pos++
			chars[pos++] - '0'
		} else -1

		skipSpaces()

		val mnemonic = readUntil { it.isWhitespace() }

		skipSpaces()

		val operand1 = readOperandEncoding()
		val operand2 = if(operand1 != null) readOperandEncoding() else null
		val operand3 = if(operand2 != null) readOperandEncoding() else null
		val operand4 = if(operand3 != null) readOperandEncoding() else null

		skipSpaces()

		var rexw = false
		var oso = false

		while(pos < chars.size && chars[pos] == '(') {
			pos++
			when(readWhile { it.isLetterOrDigit() }) {
				"OSO"    -> oso = true
				"REX.W"  -> rexw = true
			}
			skipSpaces()
			if(chars[pos++] != ')') error("Expecting '(")
			skipSpaces()
		}
	}


}