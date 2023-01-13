package eyre

class Parser(private val context: EyreContext, private val srcFile: SrcFile) {


	private var pos = 0

	private val nodes = ArrayList<AstNode>()

	private val tokens = srcFile.tokens

	private val newlines = srcFile.newlines

	private val terminators = srcFile.terminators

	private var symbols = context.globalNamespace.symbols

	private var currentNamespace: Namespace? = null // single-line namespace declaration



	/*
	Parsing utils
	 */



	private val next get() = tokens[pos]

	private val prev get() = tokens[pos - 1]

	private fun atNewline() = newlines[pos]

	private fun atTerminator() = terminators[pos]

	private fun expectTerminator() { if(!atTerminator()) error("Expecting terminator") }

	private fun expect(symbol: SymToken) { if(tokens[pos++] != symbol) error(1, "Expecting '${symbol.string}', found: $prev") }

	private fun id() = (tokens[pos++] as? IdToken)?.value ?: error(2, "Expecting identifier, found: $prev")

	private fun SymTable.addChecked(symbol: Symbol) = add(symbol)?.let { error("Symbol redefinition: $symbol") }

	private fun<T : AstNode> T.add(): T { nodes.add(this); return this }

	private fun<T : Type> T.add(): T { symbols.addChecked(this); context.types.add(this); return this }

	private fun<T : Symbol> T.add(): T { symbols.addChecked(this); return this }

	private fun error(message: String): Nothing = error(0, message)

	private fun error(numTokens: Int, message: String): Nothing {
		if(srcFile.lineNumbers == null) PosLexer(srcFile).lex()
		val lineNumber = srcFile.lineNumbers!![pos - numTokens]
		System.err.println("error on line $lineNumber of ${srcFile.relPath}:\n\t$message\n")
		kotlin.error("Parsing error")
	}



	/*
	Parsing
	 */



	fun parse() {
		parseTopLevel()
		if(currentNamespace != null) nodes.add(ScopeEndNode)
		srcFile.nodes = nodes
	}



	private fun parseTopLevel() {
		while(true) {
			when(val token = tokens[pos++]) {
				is IdToken  -> parseId(token.value)
				EndToken    -> break
				is SymToken -> if(token != SymToken.SEMICOLON) error(1, "Invalid symbol: ${token.string}")
				else        -> error(1, "Invalid token: $token")
			}
		}
	}



	private fun parseScope(symbols: SymTable) {
		val prevSymbols = this.symbols
		this.symbols = symbols

		while(true) {
			when(val token = tokens[pos++]) {
				is IdToken           -> parseId(token.value)
				SymToken.RIGHT_BRACE -> break
				is SymToken          -> if(token != SymToken.SEMICOLON) error(1, "Invalid symbol: ${token.string}")
				else                 -> error(1, "Invalid token: $token")
			}
		}

		this.symbols = prevSymbols
	}



	private fun parseId(intern: Intern) {
		if(intern in Interner.keywords) {
			when(Interner.keywords[intern]) {
				Keyword.NAMESPACE -> parseNamespace()
				Keyword.VAR       -> parseVar()
				Keyword.STRUCT    -> parseStruct()
				Keyword.CONST     -> parseConst()
				Keyword.ENUM      -> parseEnum(false)
				Keyword.BITMASK   -> parseEnum(true)
				else -> error("Invalid keyword: $intern")
			}
			return
		}
	}



	private fun parseConst() {
		val name = id()
		expect(SymToken.EQUALS)
		val value = readExpression()
		ConstNode(ConstSymbol(name, srcFile).add(), value).add()
	}



	private fun parseVar() {
		val name = id()

		if(tokens[pos] == SymToken.EQUALS) {
			pos++
			VarNode(VarSymbol(name, VoidType).add(), readExpression()).add()
			return
		}

		val initialiser = id()

		if(initialiser == Interns.RES) {
			ResNode(ResSymbol(name, VoidType).add(), readExpression()).add()
		} else {
			error(1, "Unexpected initialiser: $initialiser")
		}
	}



	private fun parseStruct() {
		val structName = id()
		expect(SymToken.LEFT_BRACE)
		val symbols = SymTable()
		val memberSymbols = ArrayList<StructMemberSymbol>()
		val members = ArrayList<StructMemberNode>()

		while(true) {
			if(tokens[pos] == SymToken.RIGHT_BRACE) break
			val type = id()
			val name = id()
			val symbol = StructMemberSymbol(name)
			members.add(StructMemberNode(symbol, type))
			symbols.add(symbol)
			memberSymbols.add(symbol)
			expectTerminator()
		}

		pos++

		val symbol = StructSymbol(structName, symbols, memberSymbols).add()
		StructNode(symbol, members).add()
	}



	private fun parseNamespace() {
		val name = id()

		val existing = symbols[name]

		val namespace = if(existing != null)
			existing as? Namespace ?: error(1, "Namespace naming conflict '$name'")
		else
			Namespace(name, SymTable()).add()

		if(next != SymToken.LEFT_BRACE) {
			expectTerminator()
			if(currentNamespace != null) ScopeEndNode.add()
			currentNamespace = namespace
			NamespaceNode(namespace).add()
			symbols = namespace.symbols
		} else {
			pos++
			NamespaceNode(namespace).add()
			parseScope(symbols)
			ScopeEndNode.add()
		}
	}



	private fun parseEnum(isBitmask: Boolean) {
		val symbols = SymTable()
		var current = if(isBitmask) 1L else 0L
		val enumName = id()

		expect(SymToken.LEFT_BRACE)

		if(tokens[pos] == SymToken.RIGHT_BRACE) {
			pos++
			EnumNode(EnumSymbol(enumName, EmptySymTable, emptyList()).add(), emptyList()).add()
			return
		}

		val entries = ArrayList<EnumEntryNode>()
		val entrySymbols = ArrayList<EnumEntrySymbol>()

		while(true) {
			if(tokens[pos] == SymToken.RIGHT_BRACE)
				break

			val name = id()

			val symbol: EnumEntrySymbol
			val entry: EnumEntryNode

			if(tokens[pos] == SymToken.EQUALS) {
				pos++
				symbol = EnumEntrySymbol(name, 0)
				entry = EnumEntryNode(symbol, readExpression())
			} else {
				symbol = EnumEntrySymbol(name, current)
				entry = EnumEntryNode(symbol, NullNode)
				current += if(isBitmask) current else 1
			}

			symbols.add(symbol)
			entrySymbols.add(symbol)
			entries.add(entry)

			if(!atNewline() && (tokens[pos] != SymToken.COMMA || tokens[++pos] !is IdToken)) break
		}

		expect(SymToken.RIGHT_BRACE)
		EnumNode(EnumSymbol(enumName, symbols, entrySymbols).add(), entries).add()
	}



	/*
	Expression Parsing
	 */



	private fun readAtom(): AstNode {
		val token = tokens[pos++]

		if(token == SymToken.LEFT_PAREN) {
			val expression = readExpression()
			expect(SymToken.RIGHT_PAREN)
			return expression
		}

		if(token is IdToken) {
			val intern = token.value
			if(intern in Interner.registers)
				return RegNode(Interner.registers[intern])
			return SymNode(intern)
		}

		return when(token) {
			is SymToken    -> UnaryNode(token.unaryOp ?: error(1, "Unexpected symbol: $token"), readAtom())
			is IntToken    -> IntNode(token.value)
			is StringToken -> StringNode(token.value)
			is CharToken   -> IntNode(token.value.code.toLong())
			else           -> error(1, "Invalid token: $token")
		}
	}



	private fun readExpression(precedence: Int = 0): AstNode {
		var atom = readAtom()

		while(true) {
			val token = tokens[pos]

			if(token !is SymToken)
				if(!atTerminator())
					error("Use a semicolon to separate expressions that are on the same line")
				else
					break

			if(token == SymToken.SEMICOLON) break

			val op = token.binaryOp ?: break
			if(op.precedence < precedence) break
			pos++

			atom = if(op == BinaryOp.DOT)
				DotNode(atom, readExpression(op.precedence + 1) as? SymNode ?: kotlin.error("Invalid node"))
			else if(op == BinaryOp.FUN)
				InvokeNode(atom, readArgs())
			else
				BinaryNode(op, atom, readExpression(op.precedence + 1))
		}

		return atom
	}



	private fun readArgs(): List<AstNode> {
		val args = ArrayList<AstNode>()
		while(true) {
			if(tokens[pos] == SymToken.RIGHT_PAREN) break
			args.add(readExpression())
			if(tokens[pos] != SymToken.COMMA) break
			pos++
		}
		expect(SymToken.RIGHT_PAREN)
		return args
	}



}