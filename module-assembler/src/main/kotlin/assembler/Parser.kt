package assembler

class Parser(lexerResult: LexerResult) {


	private var pos = 0

	private val tokens = lexerResult.tokens

	private val newlines = lexerResult.newlines



	private val nodes = ArrayList<AstNode>()

	private val symbols = HashMap<String, Symbol>()

	private val imports = ArrayList<DllImport>()

	private val idMap = HashMap<String, () -> Unit>()

	private fun atStatementEnd() = tokens[pos] == EndToken || newlines[pos] || tokens[pos] is SymbolToken

	private val widthMap = Width.values().associateBy { it.string }

	private val varWidthMap = Width.values().associateBy { it.varString }

	private fun expectStatementEnd() { if(!atStatementEnd()) error("Expecting statement end") }



	init {
		populateIdMap()
	}



	fun parse(): ParserResult {
		while(true) {
			when(val token = tokens[pos++]) {
				EndToken              -> break
				SymbolToken.SEMICOLON -> continue
				is IdToken            -> parseId(token)
				else                  -> error("Invalid token: $token")
			}
		}

		return ParserResult(nodes, symbols, imports)
	}



	private fun parseId(token: IdToken) {
		val string = token.value

		if(tokens[pos] == SymbolToken.COLON) {
			pos++
			val symbol = LabelSymbol(string, Section.TEXT)
			nodes.add(LabelNode(symbol))
			symbols[string] = symbol
			return
		}

		idMap[string]?.let {
			it()
			return
		}
	}



	private fun populateIdMap() {
		idMap["const"] = ::parseConst
		idMap["var"] = ::parseVar
		idMap["import"] = ::parseImport

		for(mnemonic in Mnemonic.values()) idMap[mnemonic.string] = {
			nodes.add(parseInstruction(mnemonic))
		}
	}



	private fun parseImport() {
		val dll = (tokens[pos++] as? IdToken)?.value ?: error("Expecting import dll name")
		if(tokens[pos++] != SymbolToken.PERIOD) error("Expecting '.'")
		val name = (tokens[pos++] as? IdToken)?.value ?: error("Expecting import name")
		val symbol = ImportSymbol(name, Section.RDATA)
		symbols[name] = symbol
		imports.add(DllImport(dll, symbol))
		expectStatementEnd()
	}



	private fun parseOperand(): AstNode {
		var token = tokens[pos]
		var width: Width? = null

		if(token is IdToken) {
			width = widthMap[token.value]
			if(width != null && tokens[pos + 1] == SymbolToken.LEFT_BRACKET)
				token = tokens[++pos]
		}

		if(token == SymbolToken.LEFT_BRACKET) {
			pos++
			val value = readExpression()
			if(tokens[pos++] != SymbolToken.RIGHT_BRACKET)
				error("Expecting ']'")
			return MemNode(width, value)
		}

		return when(val node = readExpression()) {
			is RegNode    -> node
			is IntNode    -> ImmNode(node)
			is BinaryNode -> ImmNode(node)
			is UnaryNode  -> ImmNode(node)
			is IdNode     -> ImmNode(node)
			else          -> error("Invalid operand")
		}
	}



	private fun parseInstruction(mnemonic: Mnemonic): InstructionNode {
		val token = tokens[pos]
		var shortImm = false

		if(token is IdToken && token.value == "short") {
			shortImm = true
			pos++
		}

		if(atStatementEnd()) return InstructionNode(mnemonic, shortImm, null, null, null, null)
		val op1 = parseOperand()

		if(atStatementEnd()) return InstructionNode(mnemonic, shortImm, op1, null, null, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token")
		val op2 = parseOperand()

		if(atStatementEnd()) return InstructionNode(mnemonic, shortImm, op1, op2, null, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token")
		val op3 = parseOperand()

		if(atStatementEnd()) return InstructionNode(mnemonic, shortImm, op1, op2, op3, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token")
		val op4 = parseOperand()

		expectStatementEnd()

		return InstructionNode(mnemonic, shortImm, op1, op2, op3, op4)
	}



	private fun parseVar() {
		val name = (tokens[pos++] as? IdToken)?.value
			?: error("Expecting name")

		var initialiser = (tokens[pos++] as? IdToken)?.value
			?: error("Expecting variable initialiser")

		if(initialiser == "res") {
			val size = readExpression().resolveInt()
			val symbol = ResSymbol(name, Section.BSS)
			symbols[name] = symbol
			if(size !in Int.MIN_VALUE..Int.MAX_VALUE)
				error("Too many bytes reserved")
			nodes.add(ResNode(symbol, size.toInt()))
			return
		}

		val componentsAndWidths = ArrayList<Pair<Width, List<AstNode>>>()

		while(true) {
			val width = varWidthMap[initialiser] ?: break
			val components = ArrayList<AstNode>()

			while(true) {
				val component = readExpression()
				components.add(component)
				if(tokens[pos] != SymbolToken.COMMA) break
				pos++
			}

			componentsAndWidths.add(width to components)
			initialiser = (tokens[pos++] as? IdToken)?.value ?: break
		}

		pos--

		if(componentsAndWidths.isEmpty()) error("Expecting variable initialiser")

		val symbol = VarSymbol(name, Section.DATA)
		symbols[name] = symbol
		nodes.add(VarNode(symbol, componentsAndWidths))
	}



	private fun parseConst() {
		val name = (tokens[pos++] as? IdToken)?.value ?: error("Expecting name")
		if(tokens[pos++] != SymbolToken.EQUALS) error("Expecting '='")
		val value = readExpression().resolveInt()
		val symbol = IntSymbol(name, value)
		symbols[name] = symbol
	}



	private fun AstNode.resolveInt(): Long = when(this) {
		is IntNode    -> value
		is UnaryNode  -> op.calculate(node.resolveInt())
		is BinaryNode -> op.calculate(left.resolveInt(), right.resolveInt())
		is IdNode     -> (symbols[name] as? IntSymbol)?.value ?: error("Invalid symbol: $name")
		else          -> error("Invalid constant integer component: $this")
	}



	/*
	Expressions
	 */



	private fun readAtom(): AstNode {
		val token = tokens[pos++]

		if(token == SymbolToken.LEFT_PAREN) {
			val expression = readExpression()

			if(tokens[pos++] != SymbolToken.RIGHT_PAREN)
				error("Expected ')")

			return expression
		}

		return when(token) {
			is SymbolToken -> UnaryNode(token.unaryOp ?: error("Unexpected symbol: $token"), readAtom())
			is IntToken    -> IntNode(token.value)
			is RegToken    -> RegNode(token.value)
			is IdToken     -> IdNode(token.value)
			is StringToken -> StringNode(token.value)
			is CharToken   -> IntNode(token.value.code.toLong())
			else           -> error("Invalid token: $token")
		}
	}



	private fun readExpression(precedence: Int = 0): AstNode {
		var result = readAtom()

		while(true) {
			val token = tokens[pos]

			if(token is EndToken || token == SymbolToken.SEMICOLON) break

			if(token !is SymbolToken)
				if(!atStatementEnd())
					error("Use a semicolon to separate expressions that are on the same line")
				else
					break

			val op = token.binaryOp ?: break
			if(op.precedence < precedence) break
			pos++
			result = BinaryNode(op, result, readExpression(op.precedence + 1))
		}

		return result
	}


}