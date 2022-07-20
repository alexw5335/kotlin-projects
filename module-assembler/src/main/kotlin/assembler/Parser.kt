package assembler

class Parser(lexResult: LexResult) {


	private var pos = 0

	private val tokens = lexResult.tokens

	private val nodes = ArrayList<AstNode>()

	private val symbols = ArrayList<Symbol>()

	private fun atStatementEnd() = pos >= tokens.size || tokens[pos] == SymbolToken.NEWLINE

	private val constSymbols = HashMap<String, Long>()



	fun parse(): ParseResult {
		while(pos < tokens.size) {
			when(val token = tokens[pos++]) {
				KeywordToken.CONST  -> parseConst()
				SymbolToken.NEWLINE -> continue
				is MnemonicToken    -> nodes.add(parseInstruction(token.value))
				is IdToken          -> parseLabel(token)
				else                -> error("Invalid token: $token")
			}
		}

		return ParseResult(nodes, symbols)
	}



	private fun parseLabel(id: IdToken) {
		if(pos >= tokens.size || tokens[pos++] != SymbolToken.COLON)
			error("Label identifier $id must be followed by a colon")
		val node = LabelNode(id.value)
		nodes.add(node)
		symbols.add(Symbol(id.value, Symbol.Type.LABEL, node))
	}



	private fun readAtom(): AstNode = when(val token = tokens[pos++]) {
		SymbolToken.LEFT_PAREN -> {
			val expression = readExpression(0)
			if(pos >= tokens.size || tokens[pos++] != SymbolToken.RIGHT_PAREN)
				error("Expected ')'")
			expression
		}

		is SymbolToken -> {
			val unaryOp = token.unaryOp ?: error("Unexpected symbol: $token")
			UnaryNode(unaryOp, readAtom())
		}

		is IntToken -> IntNode(token.value)

		is RegisterToken -> RegisterNode(token.value)

		is IdToken -> IdNode(token.value)

		else -> error("Invalid expression operand token: $token")
	}



	private fun readExpression(precedence: Int = 0): AstNode {
		var result = readAtom()

		while(pos < tokens.size && !atStatementEnd()) {
			val symbol = tokens[pos] as? SymbolToken ?: error("Expected symbol, found: ${tokens[pos]}")
			val op = symbol.binaryOp ?: break
			if(op.precedence < precedence) break
			pos++
			val right = readExpression(op.precedence + 1)
			result = BinaryNode(op, result, right)
		}

		return result
	}



	private fun parseConst() {
		val name = (tokens[pos++] as? IdToken)?.value ?: error("Expecting identifier, found ${tokens[pos - 1]}")
		if(tokens[pos++] != SymbolToken.EQUALS) error("Expecting '=', found ${tokens[pos - 1]}")
		val node = readExpression()
		val value = node.calculateConstantInt { constSymbols[it] ?: error("Unresolved symbol: $it") }
		constSymbols[name] = value
		nodes.add(ConstNode(name, node))
		symbols.add(Symbol(name, Symbol.Type.CONST, node))
	}



	private fun parseOperand(): AstNode {
		var token = tokens[pos]
		var width: Width? = null

		if(token is KeywordToken) {
			width = token.width ?: error("Expected explicit memory width, found: $token")
			token = tokens[++pos]
		}

		if(token == SymbolToken.LEFT_BRACKET) {
			pos++
			val node = MemoryNode(readExpression(), width)
			if(tokens[pos++] != SymbolToken.RIGHT_BRACKET)
				error("Expecting ']', found ${tokens[pos - 1]}")
			return node
		}

		if(width != null)
			error("Unexpected width specifier")

		return when(val node = readExpression()) {
			is RegisterNode -> node
			is IntNode      -> ImmediateNode(node, node.value)
			is BinaryNode   -> ImmediateNode(node, if(node.isConstantInt()) node.calculateConstantInt() else null)
			is UnaryNode    -> ImmediateNode(node, if(node.isConstantInt()) node.calculateConstantInt() else null)
			is IdNode       -> ImmediateNode(node, null)
			else            -> error("Expecting operand, found ${tokens[pos - 1]}")
		}
	}



	private fun parseInstruction(mnemonic: Mnemonic): InstructionNode {
		if(atStatementEnd()) return InstructionNode(mnemonic, null, null, null, null)
		val operand1 = parseOperand()

		if(atStatementEnd()) return InstructionNode(mnemonic, operand1, null, null, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand2 = parseOperand()

		if(atStatementEnd()) return InstructionNode(mnemonic, operand1, operand2, null, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand3 = parseOperand()

		if(atStatementEnd()) return InstructionNode(mnemonic, operand1, operand2, operand3, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand4 = parseOperand()

		if(!atStatementEnd()) error("unexpected token: ${tokens[pos]}")

		return InstructionNode(mnemonic, operand1, operand2, operand3, operand4)
	}


}