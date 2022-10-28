package assembler

class Parser(lexerResult: LexerResult) {


	private var pos = 0

	private val tokens = lexerResult.tokens

	private val newlines = lexerResult.newlines

	private val newlineCounts = lexerResult.newlineCounts

	private var newlineIndex = 0



	private val nodes = ArrayList<AstNode>()

	private val symbols = ArrayList<Symbol>()

	private val imports = ArrayList<DllImport>()



	fun parse(): ParserResult {
		while(true) {
			val token = tokens[pos++]

			if(token is EndToken) break

			if(token == SymbolToken.SEMICOLON) continue

			if(token is IdToken) {
				if(tokens[pos] == SymbolToken.COLON) {
					val symbol = LabelSymbol(token.value)

				}
				val mnemonic = mnemonicMap[token.value]
			}

			error("Invalid token: $token")
		}
	}


}