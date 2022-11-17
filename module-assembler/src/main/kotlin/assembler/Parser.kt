package assembler

class Parser(lexerResult: LexerResult) {


	private var pos = 0

	private val tokens = lexerResult.tokens

	private val newlines = lexerResult.newlines



	private val nodes = ArrayList<AstNode>()

	private var globalSymbols = SymbolTable()

	private var symbols = globalSymbols

	private val imports = ArrayList<DllImport>()

	private fun atStatementEnd() = tokens[pos] == EndToken || newlines[pos] || tokens[pos] is SymbolToken

	private fun expectStatementEnd() { if(!atStatementEnd()) error("Expecting statement end") }

	private fun id() = (tokens[pos++] as? IdToken)?.value ?: error("Expecting identifier")

	private fun expect(token: Token) { if(tokens[pos++] != token) error("Expecting $token") }

	private var namespace: Namespace? = null



	fun parse(): ParserResult {
		parseLevel(isTopLevel = true)
		return ParserResult(nodes, symbols, imports)
	}



	private fun parseLevel(isTopLevel: Boolean) {
		while(true) {
			when(val token = tokens[pos++]) {
				SymbolToken.LEFT_BRACE -> if(isTopLevel) error("Invalid token: $token") else return
				EndToken               -> break
				SymbolToken.SEMICOLON  -> continue
				is IdToken             -> parseId(token.value)
				else                   -> error("Invalid token: $token")
			}
		}
	}



	private fun parseId(intern: Intern) {
		if(tokens[pos] == SymbolToken.COLON) {
			pos++
			val symbol = LabelSymbol(intern, Section.TEXT)
			nodes.add(LabelNode(symbol))
			symbols[intern] = symbol
			return
		}

		if(intern.type == InternType.KEYWORD) {
			when(Interning.keyword(intern)) {
				Keyword.CONST     -> parseConst()
				Keyword.VAR       -> parseVar()
				Keyword.IMPORT    -> parseImport()
				Keyword.ENUM      -> parseEnum()
				Keyword.NAMESPACE -> parseNamespace()
			}

			return
		}

		if(intern.type == InternType.Prefix) {
			val modifier = Interning.prefix(intern)
			val mnemonic = Interning.mnemonic((tokens[pos++] as? IdToken)?.value ?: error("Expecting instruction"))
			nodes.add(parseInstruction(mnemonic, modifier))
		}

		if(intern.type == InternType.MNEMONIC) {
			nodes.add(parseInstruction(Interning.mnemonic(intern), null))
			return
		}

		error("Unexpected identifier: ${intern.name}")
	}



	private fun parseNamespace() {
		val name = id()
		val namespace = Namespace(name, SymbolTable())
		symbols[name] = namespace
		this.namespace = namespace
		if(tokens[pos] != SymbolToken.LEFT_BRACE) return
		pos++
		parseLevel(false)
		this.namespace = null
	}



	private fun parseImport() {
		val dll = id()
		expect(SymbolToken.REFERENCE)
		val name = id()
		val symbol = ImportSymbol(name, Section.RDATA)
		symbols[name] = symbol
		imports.add(DllImport(dll, symbol))
		expectStatementEnd()
	}



	private fun parseOperand(): AstNode {
		var token = tokens[pos]
		var width: Width? = null

		if(token is IdToken && token.value.type == InternType.WIDTH) {
			width = Interning.width(token.value)
			if(tokens[pos + 1] == SymbolToken.LEFT_BRACKET)
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
			is RegNode -> node
			else       -> ImmNode(node)
		}
	}



	private fun parseInstruction(mnemonic: Mnemonic, prefix: Prefix?): InsNode {
		val token = tokens[pos]
		var shortImm = false

		if(token is IdToken && token.value == Interns.SHORT) {
			shortImm = true
			pos++
		}

		if(newlines[pos] || tokens[pos] == EndToken)
			return InsNode(mnemonic, prefix, shortImm, null, null, null, null)

		val op1 = parseOperand()
		if(tokens[pos] != SymbolToken.COMMA)
			return InsNode(mnemonic, prefix, shortImm, op1, null, null, null)
		pos++

		val op2 = parseOperand()
		if(tokens[pos] != SymbolToken.COMMA)
			return InsNode(mnemonic, prefix, shortImm, op1, op2, null, null)
		pos++

		val op3 = parseOperand()
		if(tokens[pos] != SymbolToken.COMMA)
			return InsNode(mnemonic, prefix, shortImm, op1, op2, op3, null)
		pos++

		val op4 = parseOperand()
		expectStatementEnd()
		return InsNode(mnemonic, prefix, shortImm, op1, op2, op3, op4)
	}



	private fun parseVar() {
		val name = id()
		var initialiser = id()

		if(initialiser == Interns.RES) {
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
			if(initialiser.type != InternType.VAR_WIDTH) break
			val width = Interning.varWidth(initialiser)
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



	private fun parseEnum() {
		val name = id()
		expect(SymbolToken.LEFT_BRACE)

		val symbols = SymbolTable()
		var currentValue = 0L

		if(tokens[pos] == SymbolToken.RIGHT_BRACE) {
			pos++
			return
		}

		while(true) {
			symbols.add(IntSymbol(id(), currentValue++))
			val token = tokens[pos]
			if(token != SymbolToken.COMMA) break
			if(tokens[++pos] !is IdToken)
				break
		}

		expect(SymbolToken.RIGHT_BRACE)

		this.symbols.add(Namespace(name, symbols))
	}



	private fun parseConst() {
		val name = id()
		expect(SymbolToken.EQUALS)
		val value = readExpression().resolveInt()
		val symbol = IntSymbol(name, value)
		symbols[name] = symbol
	}



	private fun AstNode.resolveInt(): Long = when(this) {
		is IntNode    -> value
		is UnaryNode  -> op.calculate(node.resolveInt())
		is BinaryNode -> op.calculate(left.resolveInt(), right.resolveInt())
		is SymNode    -> (symbols[name] as? IntSymbol)?.value ?: error("Invalid symbol: $name")
		else          -> error("Invalid constant integer component: $this")
	}



	/*
	Expressions
	 */



	private fun readAtom(): AstNode {
		val token = tokens[pos++]

		if(token == SymbolToken.LEFT_PAREN) {
			val expression = readExpression()
			expect(SymbolToken.RIGHT_PAREN)
			return expression
		}

		if(token is IdToken) {
			val id = token.value
			if(id.type == InternType.REGISTER)
				return RegNode(Interning.register(id))
			return SymNode(id)
		}

		return when(token) {
			is SymbolToken -> UnaryNode(token.unaryOp ?: error("Unexpected symbol: $token"), readAtom())
			is IntToken    -> IntNode(token.value)
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

			if(token == SymbolToken.PERIOD) {
				pos++
				result = DotNode(result, readExpression(6) as? SymNode ?: error("Invalid symbol"))
				continue
			}

			val op = token.binaryOp ?: break
			if(op.precedence < precedence) break
			pos++
			result = BinaryNode(op, result, readExpression(op.precedence + 1))
		}

		return result
	}


}