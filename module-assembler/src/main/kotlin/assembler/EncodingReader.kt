package assembler

class EncodingReader(private val chars: CharArray) {


	private var pos = 0

	private val encodings = ArrayList<InstructionEncoding>()

	private val mnemonics = ArrayList<String>()

	private var currentMnemonic = ""

	private val operandEncodingMap = OperandEncoding.values().associateBy { it.name }

	private val mnemonicMap = Mnemonic.values().associateBy { it.name }



	private fun read(length: Int) = String(CharArray(length) { chars[pos++ ]})

	private inline fun readUntil(block: (Char) -> Boolean) = buildString {
		while(pos < chars.size && !block(chars[pos]))
			append(chars[pos++])
	}

	private inline fun readWhile(block: (Char) -> Boolean) = buildString {
		while(pos < chars.size && block(chars[pos]))
			append(chars[pos++])
	}

	private inline fun skipUntil(block: (Char) -> Boolean) {
		while(pos < chars.size && !block(chars[pos]))
			pos++
	}

	private inline fun skipWhile(block: (Char) -> Boolean) {
		while(pos < chars.size && block(chars[pos]))
			pos++
	}

	private inline fun skipTo(block: (Char) -> Boolean) {
		while(pos < chars.size && !block(chars[pos++])) Unit
	}

	fun skipWhitespace() = skipWhile { it.isWhitespace() }

	fun skipLine() = skipTo { it == '\n' }



	fun read(): List<InstructionEncoding> {
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
				currentMnemonic = readUntil { it.isWhitespace() }
				mnemonics.add(currentMnemonic)
				skipLine()
				continue
			}

			encodings.add(readEncoding().also(::println))
		}

		return encodings
	}



	private fun readOperandEncoding(): OperandEncoding? {
		skipWhile { it.isWhitespace() && it != '\n' && it != ';' }
		if(pos >= chars.size || chars[pos] == '\n' || chars[pos] == ';')
			return null
		val string = readUntil { it.isWhitespace() || it == ';' }
		return operandEncodingMap[string]
			?: error("Unrecognised operand encoding: $string")
	}



	private fun readEncoding(): InstructionEncoding {
		var hex = read(2).toInt(16)

		var mandatoryPrefix = -1

		if(hex == 0x66 || hex == 0xF2 || hex == 0xF3) {
			mandatoryPrefix = hex
			skipWhitespace()
			hex = read(2).toInt(16)
		}

		var opcode = hex
		var optype = OpType.SINGLE

		if(hex == 0x0F) {
			skipWhitespace()
			opcode = read(2).toInt(16)
			optype = OpType.DOUBLE
			if(opcode == 0x38) {
				skipWhitespace()
				opcode = read(2).toInt(16)
				optype = OpType.TRIP38
			} else if(opcode == 0x3A) {
				skipWhitespace()
				opcode = read(2).toInt(16)
				optype = OpType.TRIP3A
			}
		}

		skipWhile { it.isWhitespace() && it != '\n' }

		val extension = if(chars[pos] == '/') {
			pos++
			chars[pos++] - '0'
		} else -1

		skipWhile { it.isWhitespace() && it != '\n' }

		val operand1 = readOperandEncoding()
		val operand2 = if(operand1 != null) readOperandEncoding() else null
		val operand3 = if(operand2 != null) readOperandEncoding() else null
		val operand4 = if(operand3 != null) readOperandEncoding() else null

		return InstructionEncoding(
			mnemonicMap[currentMnemonic] ?: Mnemonic.NONE,
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