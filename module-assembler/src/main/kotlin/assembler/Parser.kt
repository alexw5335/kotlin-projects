package assembler

import core.LexResult

class Parser(lexResult: LexResult<Token>) {


	private var pos = 0

	private val tokens = lexResult.tokens

	private val newlines = lexResult.newlines

	private val nodes = ArrayList<AstNode>()

	private fun atNewline() = newlines[pos]



	fun parse(): ParseResult {
		while(pos < tokens.size) {
			when(val token = tokens[pos++]) {
				is MnemonicToken -> nodes.add(parseInstruction(token.value))
				is EndToken      -> break
				else             -> error("Invalid token: $token")
			}
		}

		return ParseResult(nodes)
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

		is RegisterToken -> RegisterNode(token.value)

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
		val token = tokens[pos++]

		if(token == Symbol.LEFT_BRACKET) {
			val expression = readExpression()

			if(tokens[pos++] != Symbol.RIGHT_BRACKET)
				error("Expecting ']', found ${tokens[pos - 1]}")

			if(expression is IntNode)
				return MemoryNode(null, null, 0, expression.value.toInt(), null)
			if(expression is RegisterNode)
				return MemoryNode(expression.value, null, 0, 0, null)
			//if(expression !is BinaryOpNode)
			error("Invalid effective address")
		}

		return when(token) {
			is RegisterToken -> RegisterNode(token.value)
			is IntLiteral    -> ImmediateNode(token.value)
			else             -> error("Expecting operand, found ${tokens[pos-1]}")
		}
	}



	private fun parseInstruction(mnemonic: Mnemonic): InstructionNode {
		if(atNewline()) return InstructionNode(mnemonic, null, null, null, null)
		val operand1 = parseOperand()

		if(atNewline() || tokens[pos] == EndToken) return InstructionNode(mnemonic, operand1, null, null, null)
		if(tokens[pos++] != Symbol.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand2 = parseOperand()

		if(atNewline() || tokens[pos] == EndToken) return InstructionNode(mnemonic, operand1, operand2, null, null)
		if(tokens[pos++] != Symbol.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand3 = parseOperand()

		if(atNewline() || tokens[pos] == EndToken) return InstructionNode(mnemonic, operand1, operand2, operand3, null)
		if(tokens[pos++] != Symbol.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand4 = parseOperand()

		if(!atNewline() && tokens[pos] != EndToken) error("unexpected token: ${tokens[pos]}")

		return InstructionNode(mnemonic, operand1, operand2, operand3, operand4)
	}


}