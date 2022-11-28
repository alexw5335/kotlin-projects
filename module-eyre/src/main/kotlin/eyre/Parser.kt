package eyre

class Parser(private val srcFile: SrcFile) {


	private var pos = 0

	private val tokens = srcFile.lexOutput.tokens

	private val newlines = srcFile.lexOutput.newlines

	private val fileImports = ArrayList<InternArray>()

	private val nodes = ArrayList<AstNode>()

	private var symbols = SymbolTable()



	/*
	parsing utils
	 */



	private fun atStatementEnd() = tokens[pos] == EndToken || newlines[pos] || tokens[pos] is SymToken

	private fun expectStatementEnd() { if(!atStatementEnd()) error("Expecting statement end") }

	private fun id() = (tokens[pos++] as? IdToken)?.value ?: error("Expecting identifier")

	private fun expect(token: Token) { if(tokens[pos++] != token) error("Expecting $token") }

	private fun<T : AstNode> T.add(): T { nodes.add(this); return this }

	private fun<T : Symbol> T.add(): T { symbols.add(this); return this }



	/*
	Parsing
	 */



	fun parse(): ParseOutput {
		parseFileNamespace()
		parseScope(symbols)
		return ParseOutput(nodes, fileImports)
	}



	private fun parseFileNamespace(): Namespace {
		val keyword = id()
		if(!Interner.isKeyword(keyword) || Interner.keyword(keyword) != Keyword.NAMESPACE)
			error("Expecting file namespace as first declaration")
		val name = id()
		expectStatementEnd()
		return Namespace(name, Visibility.PUBLIC, symbols).add()
	}



	private fun parseBracedScope(symbols: SymbolTable) {
		expect(SymToken.LEFT_BRACE)
		parseScope(symbols)
		expect(SymToken.RIGHT_BRACE)
	}



	private fun parseScope(symbols: SymbolTable) {
		val prevSymbols = this.symbols
		this.symbols = symbols

		while(true) {
			when(val token = tokens[pos++]) {
				is IdToken           -> parseId(token.value)
				SymToken.RIGHT_BRACE -> { pos--; break }
				SymToken.SEMICOLON   -> continue
				EndToken             -> break
				else                 -> error("Invalid token: $token")
			}
		}

		this.symbols = prevSymbols
	}



	private fun parseId(intern: Intern) {
		if(Interner.isKeyword(intern)) {
			when(Interner.keyword(intern)) {
				Keyword.IMPORT    -> parseImport()
				Keyword.CONST     -> parseConst()
				Keyword.NAMESPACE -> parseNamespace()
				else              -> error("Invalid keyword")
			}
		}
	}



	private fun parseImport() {
		val first = id()

		if(first == Interns.DLL && tokens[pos] == SymToken.REFERENCE) {
			pos++
			val dll = id()
			println("DLL import: $dll")
			return
		}

		val startPos = pos
		var count = 0

		while(true) {
			if(tokens[pos] != SymToken.PERIOD) break
			pos++
			id()
			count++
		}

		pos = startPos

		val components = IntArray(count + 1)
		components[0] = first.id

		for(i in 0 until count) {
			pos++
			components[i + 1] = id().id
		}

		expectStatementEnd()
		val internArray = InternArray(components)
		ImportNode(internArray).add()
		fileImports.add(internArray)
	}



	private fun parseConst() {
		val name = id()
		expect(SymToken.EQUALS)
		val value = readExpression()
		val symbol = IntSymbol(name, Visibility.PUBLIC, 0).add()
		ConstNode(symbol, value).add()
	}



	private fun parseNamespace() {
		val name = id()

		val existing = symbols[name]

		val namespace = if(existing != null)
			existing as? Namespace ?: error("Symbol naming conflict")
		else
			Namespace(name, Visibility.PUBLIC, SymbolTable()).add()

		NamespaceNode(namespace).add()
		parseBracedScope(namespace.symbolTable)
	}



	/*
	Expressions
	 */



	private fun readAtom(): AstNode {
		val token = tokens[pos++]

		if(token == SymToken.LEFT_PAREN) {
			val expression = readExpression()
			expect(SymToken.RIGHT_PAREN)
			return expression
		}

		if(token is IdToken) {
			if(Interner.isRegister(token.value))
				return RegNode(Interner.register(token.value))
		}

		return when(token) {
			is SymToken    -> UnaryNode(token.unaryOp ?: error("Unexpected symbol: $token"), readAtom())
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

			if(token !is SymToken)
				if(!atStatementEnd())
					error("Use a semicolon to separate expressions that are on the same line")
				else
					break

			if(token == SymToken.SEMICOLON) break

			if(token == SymToken.PERIOD) {
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

		if(token is IdToken && Interner.isWidth(token.value)) {
			width = Interner.width(token.value)
			if(tokens[pos + 1] == SymToken.LEFT_BRACKET)
				token = tokens[++pos]
		}

		if(token == SymToken.LEFT_BRACKET) {
			pos++
			val value = readExpression()
			if(tokens[pos++] != SymToken.RIGHT_BRACKET)
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
		if(tokens[pos] != SymToken.COMMA)
			return InsNode(mnemonic, prefix, shortImm, op1, null, null, null)
		pos++

		val op2 = parseOperand()
		if(tokens[pos] != SymToken.COMMA)
			return InsNode(mnemonic, prefix, shortImm, op1, op2, null, null)
		pos++

		val op3 = parseOperand()
		if(tokens[pos] != SymToken.COMMA)
			return InsNode(mnemonic, prefix, shortImm, op1, op2, op3, null)
		pos++

		val op4 = parseOperand()
		expectStatementEnd()
		return InsNode(mnemonic, prefix, shortImm, op1, op2, op3, op4)
	}


}