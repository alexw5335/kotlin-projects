package assembler

class Parser(lexResult: LexResult) {


	private var pos = 0

	private val tokens = lexResult.tokens

	private val newlines = lexResult.newlines

	private val nodes = ArrayList<AstNode>()

	private fun atNewline() = newlines[pos]



	fun parse(): List<AstNode> {
		while(pos < tokens.size) {
			when(val token = tokens[pos++]) {
				is MnemonicToken -> nodes.add(parseInstruction(token.value))
				else -> error("Invalid token: $token")
			}
		}
		return nodes
	}



	private fun readAtom(): AstNode = when(val token = tokens[pos++]) {
		Symbol.LEFT_PAREN -> {
			val expression = readExpression(0)
			if(pos >= tokens.size || tokens[pos++] != Symbol.RIGHT_PAREN)
				error("Expected ')'")
			expression
		}

		is Symbol -> {
			val unaryOp = token.unaryOp ?: error("Unexpected symbol: $token")
			UnaryOpNode(unaryOp, readAtom())
		}

		is IntLiteral -> IntNode(token.value)

		else -> error("Invalid expression operand token: $token")
	}



	private fun readExpression(precedence: Int = 0): AstNode {
		var result = readAtom()

		while(pos < tokens.size) {
			val symbol = tokens[pos] as? Symbol ?: error("Expected symbol, found: ${tokens[pos]}")
			val op = symbol.binaryOp ?: break
			if(op.precedence < precedence) break
			pos++
			val right = readExpression(op.precedence + 1)
			result = BinaryOpNode(op, result, right)
		}

		return result
	}



	private fun parseOperand(): OperandNode {
		if(pos >= tokens.size) error("Expecting operand")

		return when(val token = tokens[pos++]) {
			is RegisterToken -> RegisterNode(token.value)
			is IntLiteral    -> ImmediateNode(token.value)
			else             -> error("Expecting operand, found ${tokens[pos-1]}")
		}
	}



	private fun parseInstruction(mnemonic: Mnemonic): InstructionNode {
		if(pos >= tokens.size || tokens[pos] == Symbol.NEWLINE) {
			pos++
			return InstructionNode(mnemonic, null, null, null, null)
		}

		val operand1 = parseOperand()
		if(pos >= tokens.size || tokens[pos] != Symbol.COMMA)
			return InstructionNode(mnemonic, operand1, null, null, null)
		pos++
		val operand2 = parseOperand()
		if(pos >= tokens.size || tokens[pos] != Symbol.COMMA)
			return InstructionNode(mnemonic, operand1, operand2, null, null)
		pos++
		val operand3 = parseOperand()
		if(pos >= tokens.size || tokens[pos] != Symbol.COMMA)
			return InstructionNode(mnemonic, operand1, operand2, operand3, null)
		val operand4 = parseOperand()
		if(pos >= tokens.size || tokens[pos] != Symbol.COMMA)
			return InstructionNode(mnemonic, operand1, operand2, operand3, operand4)
		pos++
		error("Too many operands")
	}


}