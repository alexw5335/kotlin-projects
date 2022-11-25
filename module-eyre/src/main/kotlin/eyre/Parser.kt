package eyre

class Parser(private val lexOutput: LexOutput) {


	private var pos = 0

	private val tokens = lexOutput.tokens

	private val newlines = lexOutput.newlines

	private val nodes = ArrayList<AstNode>()

	private val fileImports = ArrayList<InternArray>()



	/*
	parsing utils
	 */



	private fun atStatementEnd() = tokens[pos] == EndToken || newlines[pos] || tokens[pos] is SymToken

	private fun expectStatementEnd() { if(!atStatementEnd()) error("Expecting statement end") }

	private fun id() = (tokens[pos++] as? IdToken)?.value ?: error("Expecting identifier")

	private fun expect(token: Token) { if(tokens[pos++] != token) error("Expecting $token") }

	private fun<T : AstNode> T.add(): T { nodes.add(this); return this }

	//private fun<T : Symbol> T.add(): T { symbols.add(this); return this }



	/*
	Parsing
	 */



	fun parse(): ParseOutput {
		while(true) {
			when(val token = tokens[pos++]) {
				is IdToken           -> parseId(token.value)
				SymToken.RIGHT_BRACE -> { pos--; break }
				SymToken.SEMICOLON   -> continue
				EndToken             -> break
				else                 -> error("Invalid token: $token")
			}
		}

		return ParseOutput(lexOutput.srcFile, nodes, fileImports)
	}



	private fun parseId(intern: Intern) {
		if(Interner.isKeyword(intern)) {
			when(Interner.keyword(intern)) {
				Keyword.IMPORT -> parseImport()
				else           -> error("Invalid keyword")
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


}