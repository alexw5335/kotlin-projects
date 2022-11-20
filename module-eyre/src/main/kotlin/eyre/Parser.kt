package eyre

class Parser(lexOutput: LexOutput, private val globalNamespace: NamespaceSymbol) {


	private var pos = 0

	private val file = lexOutput.file

	private val tokens = lexOutput.tokens

	private val newlines = lexOutput.newlines

	private var nodes = ArrayList<AstNode>()

	private val fileImports = ArrayList<FileImportNode>()

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

	private fun advanceIf(token: Token): Boolean {
		if(tokens[pos] != token) return false
		pos++
		return true
	}



	/*
	Parsing
	 */



	fun parse(): ParseOutput {
		parseLevel(nodes, symbols)
		return ParseOutput(file, nodes, fileImports)
	}



	private fun parseLevel(nodes: ArrayList<AstNode>, symbols: SymbolTable) {
		val prevNodes   = this.nodes
		val prevSymbols = this.symbols
		this.nodes      = nodes
		this.symbols    = symbols

		while(true) {
			when(val token = tokens[pos++]) {
				is IdToken             -> parseId(token.value)
				SymbolToken.LEFT_BRACE -> break
				EndToken               -> break
				SymbolToken.SEMICOLON  -> continue
				else                   -> error("Invalid token: $token")
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
				else              -> error("Invalid keyword")
			}

			return
		}
	}



	/*
	Keywords
	 */



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

		val node = FileImportNode(components)
		nodes.add(node)
		fileImports.add(node)
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
		parseLevel(nodes, symbols)
		expect(SymbolToken.RIGHT_BRACE)
	}


}