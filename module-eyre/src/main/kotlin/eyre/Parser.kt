package eyre

class Parser(lexOutput: LexOutput, private val globalNamespace: NamespaceSymbol) {


	private var pos = 0

	private val file = lexOutput.file

	private val tokens = lexOutput.tokens

	private val newlines = lexOutput.newlines

	private var nodes = ArrayList<AstNode>()

	private var symbols = globalNamespace.symbols

	private var currentNamespace = globalNamespace



	/*
	parsing utils
	 */



	private fun atStatementEnd() = tokens[pos] == EndToken || newlines[pos] || tokens[pos] is SymbolToken

	private fun expectStatementEnd() { if(!atStatementEnd()) error("Expecting statement end") }

	private fun id() = (tokens[pos++] as? IdToken)?.value ?: error("Expecting identifier")

	private fun expect(token: Token) { if(tokens[pos++] != token) error("Expecting $token") }

	private fun<T : AstNode> T.add(): T { nodes.add(this); return this }

	private fun<T : Symbol> T.add(): T { symbols.add(this); return this }



	/*
	Parsing
	 */



	fun parse(): ParseOutput {
		parseLevel(nodes, symbols)
		return ParseOutput(file, nodes)
	}



	private fun parseLevel(nodes: ArrayList<AstNode>, symbols: SymbolTable) {
		val prevNodes   = this.nodes
		val prevSymbols = this.symbols
		this.nodes      = nodes
		this.symbols    = symbols

		while(true) {
			when(val token = tokens[pos++]) {
				is IdToken              -> parseId(token.value)
				SymbolToken.RIGHT_BRACE -> { pos--; break }
				EndToken                -> break
				SymbolToken.SEMICOLON   -> continue
				else                    -> error("Invalid token: $token")
			}
		}

		this.nodes = prevNodes
		this.symbols = prevSymbols
	}



	private fun parseId(intern: Intern) {
		if(intern.type == InternType.KEYWORD) {
			when(Interning.keyword(intern)) {
				Keyword.NAMESPACE -> parseNamespace()
				Keyword.IMPORT    -> parseImport()
				Keyword.CONST     -> parseConst()
				else              -> error("Invalid keyword")
			}
			return
		}

		if(intern.type == InternType.PREFIX) {
			val prefix = Interning.prefix(intern)
			val mnemonic = Interning.mnemonic(id())
			parseInstruction(mnemonic, prefix).add()
			return
		}

		if(intern.type == InternType.MNEMONIC) {
			parseInstruction(Interning.mnemonic(intern), null).add()
			return
		}

		error("Unexpected identifier: $intern")
	}



	/*
	Keywords
	 */



	private fun parseConst() {
		val name = id()
		expect(SymbolToken.EQUALS)
		val symbol = IntSymbol(name).add()
		ConstNode(symbol, readExpression()).add()
	}



	private fun parseImport() {
		val first = id()

		if(tokens[pos] == SymbolToken.REFERENCE) {
			pos++
			val name = id()
			DllImportNode(first, name).add()
			return
		}

		val components = ArrayList<Intern>()
		components.add(first)

		while(true) {
			if(tokens[pos] != SymbolToken.PERIOD) break
			pos++
			components.add(id())
		}

		val node = ImportNode(components)
		nodes.add(node)
	}



	private fun parseNamespace() {
		val name = id()

		if(name == Interns.NULL) {
			currentNamespace = globalNamespace
			symbols = currentNamespace.symbols
			return
		}

		val existing = symbols[name]

		val namespace = if(existing != null) {
			existing as? NamespaceSymbol ?: error("Symbol naming conflict")
		} else {
			NamespaceSymbol(name).add()
		}

		if(tokens[pos] != SymbolToken.LEFT_BRACE) {
			NamespaceNode(namespace, emptyList(), true).add()
			currentNamespace = namespace
			symbols = namespace.symbols
			return
		}

		pos++
		val nodes = ArrayList<AstNode>()
		NamespaceNode(namespace, nodes, false).add()
		parseLevel(nodes, namespace.symbols)
		expect(SymbolToken.RIGHT_BRACE)
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
			if(token.value.type == InternType.REGISTER)
				return RegNode(Interning.register(token.value))
			return SymNode(token.value)
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
		var atom = readAtom()

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
				atom = DotNode(atom, readExpression(6) as? SymNode ?: error("Invalid symbol"))
				continue
			}

			val op = token.binaryOp ?: break
			if(op.precedence < precedence) break
			pos++
			atom = BinaryNode(op, atom, readExpression(op.precedence + 1))
		}

		return atom
	}



	/*
	Instruction
	 */



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


}