package assembler.generator

class Parser(private val tokens: List<Token>) {

	private var pos = 0



	fun parse() {
		while(pos < tokens.size) {
			while(tokens[pos] == SymbolToken.NEWLINE) pos++
			parseInstruction()
		}
	}



	private fun hex2(): Int {
		val token = tokens[pos++]
		fun error(): Nothing = error("Expected two-digit hex integer, found $token")
		if(token !is IdToken) error()
		return token.value.toIntOrNull(16) ?: error()
	}



	private fun readOperand(): KeywordToken? {
		val token = tokens[pos]
		if(token == SymbolToken.LPAREN || token == SymbolToken.NEWLINE)
			return null
		if(token !is KeywordToken)
			error("Expecting operand, found: $token")
		pos++
		return token
	}



	private fun parseInstruction() {
		var value = hex2()
		var prefix = -1

		if(value == 0x66 || value == 0xF2 || value == 0xF3) {
			prefix = value
			value = hex2()
		}

		var opcode = value
		var opType = OpType.SINGLE

		if(opcode == 0x0F) {
			opcode = hex2()
			when(opcode) {
				0x38 -> { opType = OpType.TRIP38; opcode = hex2() }
				0x3A -> { opType = OpType.TRIP3A; opcode = hex2() }
				else -> { opType = OpType.DOUBLE }
			}
		}

		val extension = if(tokens[pos] == SymbolToken.SLASH) {
			pos++
			hex2()
		} else -1

		val mnemonic = (tokens[pos++] as IdToken).value

		val operand1 = readOperand()
		val operand2 = if(operand1 != null) readOperand() else null
		val operand3 = if(operand2 != null) readOperand() else null
		val operand4 = if(operand3 != null) readOperand() else null

		while(pos < tokens.size && tokens[pos] == SymbolToken.LPAREN) {
			pos++
			val token = tokens[pos++]
			if(token !is KeywordToken) error("Expecting keyword")
			if(tokens[pos++] != SymbolToken.RPAREN) error("Expecting ')'")
		}

		println("$opType $opcode $extension $prefix $mnemonic $operand1 $operand2 $operand3 $operand4")

		if(pos < tokens.size && tokens[pos++] != SymbolToken.NEWLINE)
			error("Expected newline, found: ${tokens[pos - 1]}")
	}


}