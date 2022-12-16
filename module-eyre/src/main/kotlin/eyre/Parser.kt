package eyre

class Parser(
	private val compiler : Compiler,
	private val srcFile  : SrcFile
) {


	private var pos = 0

	private val tokens = srcFile.tokens

	private val newlines = srcFile.newlines

	private val nodes = ArrayList<AstNode>()

	private var symbols = compiler.globalNamespace.symbols

	private var currentNamespace: Namespace? = null // Current single-line namespace declaration



	/*
	parsing utils
	 */



	private fun atNewline() = newlines[pos]

	private fun atStatementEnd() = tokens[pos] == EndToken || newlines[pos] || tokens[pos] is SymToken

	private fun expectStatementEnd() { if(!atStatementEnd()) error("Expecting statement end") }

	private fun id() = (tokens[pos++] as? IdToken)?.value ?: error("Expecting identifier")

	private fun expect(token: Token) { if(tokens[pos++] != token) error("Expecting $token") }

	private fun<T : AstNode> T.add(): T { nodes.add(this); return this }

	private fun<T : Symbol> T.add(): T {
		if(symbols.add(this) != null) error("Symbol already defined: ${this.name}")
		return this
	}



	/*
	Parsing
	 */



	fun parse() {
		parseScope(symbols)
		if(currentNamespace != null) nodes.add(ScopeEndNode)
		srcFile.nodes = nodes
	}



	private fun parseScope(symbols: SymTable) {
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



	private fun parseScopeBraced(symbols: SymTable) {
		expect(SymToken.LEFT_BRACE)
		parseScope(symbols)
		expect(SymToken.RIGHT_BRACE)
	}



	private fun parseId(intern: Intern) {
		if(tokens[pos] == SymToken.COLON) {
			pos++
			val symbol = LabelSymbol(intern, Section.TEXT).add()
			if(intern == Interns.MAIN) {
				if(compiler.entryPoint != null)
					error("Multiple entry points (labels named 'main')")
				compiler.entryPoint = symbol
			}
			LabelNode(symbol).add()
			return
		}

		if(intern in Interner.keywords) {
			when(Interner.keywords[intern]) {
				Keyword.IMPORT    -> parseImport()
				Keyword.CONST     -> parseConst()
				Keyword.NAMESPACE -> parseNamespace()
				Keyword.VAR       -> parseVar()
				Keyword.ENUM      -> parseEnum(false)
				Keyword.FLAGS     -> parseEnum(true)
				Keyword.STRUCT    -> parseStruct()
				Keyword.PROC      -> parseProc()
				//else              -> error("Unexpected keyword: $intern")
			}
			return
		}

		if(intern in Interner.prefixes) {
			val prefix = Interner.prefixes[intern]
			val mnemonic = Interner.mnemonics[id()]
			parseInstruction(mnemonic, prefix).add()
			return
		}

		if(intern in Interner.mnemonics) {
			parseInstruction(Interner.mnemonics[intern], null).add()
			return
		}

		error("Unexpected identifier: $intern")
	}



	private fun parseProc() {
		val name = id()
		val symbols = SymTable()
		val symbol = ProcSymbol(name, Section.TEXT, 0, symbols).add()
		ProcNode(symbol).add()
		parseScopeBraced(symbols)
		ScopeEndNode.add()
	}



	private fun parseStruct() {
		val structName = id()
		val symbols = SymTable()
		var size = -1L

		expect(SymToken.LEFT_BRACE)

		while(true) {
			val token = tokens[pos++]
			if(token == SymToken.RIGHT_BRACE) break
			if(token !is IntToken) error("Expecting offset")
			val offset = token.value
			if(tokens[pos] !is IdToken) {
				size = offset
				break
			}
			val name = id()
			val symbol = IntSymbol(name, srcFile, offset, true)
			symbols.add(symbol)
		}

		if(size < 0L)
			error("Invalid or unspecified size")

		if(Interns.SIZEOF in symbols)
			error("Cannot have struct member named sizeof")

		symbols.add(IntSymbol(Interns.SIZEOF, srcFile, size, true))

		Namespace(structName, symbols).add()
	}



/*
	private fun parseStruct() {
		val name = id()
		class Component(width: Width, name: Intern)
		val components = ArrayList<Component>()
		expect(SymToken.LEFT_BRACE)
		while(true) {
			val token = tokens[pos++]
			if(token == SymToken.RIGHT_BRACE) break
			if(token !is IdToken) error("Invalid struct type: $token")
			val intern = token.value
			if(intern !in Interner.widths) error("Invalid struct type: $token")
			val width = Interner.widths[intern]
			components.add(Component(width, id()))
			expectStatementEnd()
		}
	}
*/



	private fun parseEnum(isBitmask: Boolean) {
		val symbols = SymTable()
		var current = if(isBitmask) 1L else 0L

		val enumName = id()

		expect(SymToken.LEFT_BRACE)

		if(tokens[pos] == SymToken.RIGHT_BRACE) {
			pos++
			return
		}

		val entries = ArrayList<EnumEntryNode>()

		while(true) {
			if(tokens[pos] == SymToken.RIGHT_BRACE) break

			val name = id()

			val value = if(tokens[pos] == SymToken.EQUALS) {
				pos++
				readExpression()
			} else {
				val value = current
				current += if(isBitmask) current else 1
				IntNode(value)
			}

			val symbol = IntSymbol(name, srcFile)
			symbols += symbol
			entries += EnumEntryNode(symbol, value)

			if(!atNewline() && (tokens[pos] != SymToken.COMMA || tokens[++pos] !is IdToken)) break
		}

		expect(SymToken.RIGHT_BRACE)

		EnumNode(Namespace(enumName, symbols).add(), entries).add()
	}



	private fun parseImport() {
		val parts = ArrayList<Intern>()

		while(true) {
			parts.add(id())
			if(tokens[pos] != SymToken.PERIOD) break
			pos++
		}

		expectStatementEnd()
		ImportNode(parts).add()
	}



	private fun parseConst() {
		val name = id()
		expect(SymToken.EQUALS)
		val value = readExpression()
		val symbol = IntSymbol(name, srcFile, 0, false).add()
		ConstNode(symbol, value).add()
	}



	private fun parseNamespace() {
		val name = id()

		val existing = symbols[name]

		val namespace = if(existing != null)
			existing as? Namespace ?: error("Symbol naming conflict")
		else
			Namespace(name, SymTable()).add()

		if(tokens[pos] != SymToken.LEFT_BRACE) {
			if(currentNamespace != null) ScopeEndNode.add()
			currentNamespace = namespace
			NamespaceNode(namespace).add()
			symbols = namespace.symbols
		} else {
			pos++
			NamespaceNode(namespace).add()
			parseScopeBraced(symbols)
			ScopeEndNode.add()
		}
	}



	private fun parseVar() {
		val name = id()
		var initialiser = id()

		if(initialiser == Interns.RES) {
			val size = readExpression()
			ResNode(ResSymbol(name, Section.BSS).add(), size).add()
			return
		}

		val parts = ArrayList<VarPart>()

		var size = 0

		while(true) {
			if(initialiser !in Interner.varWidths) break
			val width = Interner.varWidths[initialiser]
			val values = ArrayList<AstNode>()

			while(true) {
				val component = readExpression()
				values.add(component)

				size += if(component is StringNode)
					width.bytes * component.value.string.length
				else
					width.bytes

				if(tokens[pos] != SymToken.COMMA) break
				pos++
			}

			parts.add(VarPart(width, values))
			initialiser = (tokens[pos++] as? IdToken)?.value ?: break
		}

		pos--

		if(parts.isEmpty()) error("Expecting variable initialiser")

		VarNode(VarSymbol(name, Section.DATA, 0, size).add(), parts).add()
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
			val intern = token.value

			if(intern in Interner.registers)
				return RegNode(Interner.registers[intern])

			if(intern == Interns.SIZEOF && tokens[pos] == SymToken.LEFT_PAREN) {
				pos++
				val node = SizeofNode(readExpression())
				expect(SymToken.RIGHT_PAREN)
				return node
			}

			return SymNode(intern)
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

			val op = token.binaryOp ?: break
			if(op.precedence < precedence) break
			pos++

			atom = if(op != BinaryOp.DOT)
				BinaryNode(op, atom, readExpression(op.precedence + 1))
			else
				DotNode(atom, readExpression(op.precedence + 1) as? SymNode ?: error("Invalid node"))
		}

		return atom
	}



	/*
	Instruction
	 */



	private fun parseOperand(): AstNode {
		var token = tokens[pos]
		var width: Width? = null

		if(token is IdToken && token.value in Interner.widths) {
			width = Interner.widths[token.value]
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

		if(token is IdToken && tokens[pos + 1] == SymToken.REFERENCE) {
			pos += 2
			val dllName = Interner.add(token.value.string.lowercase())
			val symbol = compiler.dllImports.add(dllName, id())
			return MemNode(Width.BIT64, SymNode(symbol.name, symbol))
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