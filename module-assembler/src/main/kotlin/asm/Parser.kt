package asm

import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Parser(lexerResult: LexerResult) {


	companion object {

		fun parse(lexerResult: LexerResult) = Parser(lexerResult).parse()

	}



	private var pos = 0

	private val tokens = lexerResult.tokens

	private val newlines = lexerResult.newlines

	private val nodes = ArrayList<AstNode>()

	private val symbols = HashMap<String, Symbol>()

	private val imports = ArrayList<DllImport>()

	private var shortImm = false



	private fun atNewline() = newlines[pos]

	private fun atStatementEnd() = tokens[pos] == EndToken || newlines[pos]

	private val prevToken get() = tokens[pos - 1]

	private fun expect(token: Token) {
		if(tokens[pos++] != token) error("Expecting $token, found: $prevToken")
	}



	fun parse(): ParserResult {
		while(true) {
			when(val token = tokens[pos++]) {
				is MnemonicToken      -> nodes.add(parseInstruction(null, token.value))
				is KeywordToken       -> parseKeyword(token)
				is EndToken           -> break
				is IdToken            -> parseId(token)
				SymbolToken.SEMICOLON -> continue
				else                  -> error("Invalid token: $token")
			}
		}

		return ParserResult(nodes, symbols, imports)
	}



	private fun expect(token: SymbolToken) {
		if(tokens[pos++] != token) error("Expecting '${token.string}', found: $prevToken")
	}



	private fun expectStatementEnd() {
		if(atNewline()) return
		val token = tokens[pos++]
		if(token == EndToken || token == SymbolToken.SEMICOLON) return
		error("Expecting statement end, found: $token")
	}



	private fun identifier() = (tokens[pos++] as? IdToken)?.value ?: error("Expecting identifier name")



	/*
	Keywords
	 */



	private fun parseId(id: IdToken) {
		if(tokens[pos++] != SymbolToken.COLON)
			error("Expecting colon after identifier")
		val symbol = LabelSymbol(id.value)
		symbols[symbol.name] = symbol
		nodes.add(LabelNode(symbol))
	}



	private fun parseKeyword(keyword: KeywordToken) {
		if(keyword.isModifier) {
			val mnemonic = (tokens[pos++] as? MnemonicToken)?.value ?: error("Expecting instruction")
			nodes.add(parseInstruction(keyword, mnemonic))
			return
		}

		when(keyword) {
			KeywordToken.DB     -> parseDb()
			KeywordToken.CONST  -> parseConst()
			KeywordToken.IMPORT -> parseImport()
			KeywordToken.VAL    -> parseVal()
			else -> { }
		}
	}



	private fun parseVal() {
		val name = identifier()
		expect(SymbolToken.EQUALS)
		val value = readExpression()
		val symbol = ValSymbol(name)
		symbols[name] = symbol
		nodes.add(ValNode(symbol, value))
	}



	private fun parseImport() {
		val dll = identifier()
		expect(SymbolToken.COLON)
		val name = identifier()
		expectStatementEnd()
		val symbol = ImportSymbol(name)
		symbols[name] = symbol
		imports.add(DllImport(dll, symbol))
	}



	private fun parseConst() {
		val name = identifier()
		expect(SymbolToken.EQUALS)
		nodes.add(ConstNode(name, readExpression()))
	}



	private fun parseDb() {
		val components = ArrayList<AstNode>()

		while(true) {
			components.add(readExpression())
			if(tokens[pos] != SymbolToken.COMMA)
				break
			pos++
		}

		nodes.add(DbNode(components))
	}



	/*
	Expressions
	 */



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

		is IntToken    -> IntNode(token.value)
		is RegToken    -> RegNode(token.value)
		is SRegToken   -> SRegNode(token.value)
		is STRegToken  -> STRegNode(token.value)
		is IdToken     -> IdNode(token.value)
		is StringToken -> StringNode(token.value)

		else -> error("Invalid expression operand token: $token")
	}



	private fun readExpression(precedence: Int = 0): AstNode {
		var result = readAtom()

		while(true) {
			val token = tokens[pos]

			if(token is EndToken || token == SymbolToken.SEMICOLON) break

			if(token !is SymbolToken) {
				if(!atNewline())
					error("Use a semicolon to separate expressions that are on the same line.")
				break
			}

			val op = token.binaryOp ?: break
			if(op.precedence < precedence) break
			pos++
			result = BinaryNode(op, result, readExpression(op.precedence + 1))
		}

		return result
	}



	/*
	Instructions
	 */



	private fun parseOperand(): AstNode {
		var token = tokens[pos]
		var width: Width? = null

		if(token is KeywordToken) {
			if(token.width != null)
				width = token.width
			else if(token == KeywordToken.SHORT)
				shortImm = true

			token = tokens[++pos]
		}

		if(token == SymbolToken.LEFT_BRACKET) {
			pos++
			val value = readExpression()
			if(tokens[pos++] != SymbolToken.RIGHT_BRACKET) error("Expecting ']', found $prevToken")
			return MemNode(width, value)
		}

		if(width != null)
			error("Unexpected width specifier")

		val result = when(val node = readExpression()) {
			is RegNode       -> node
			is IntNode       -> ImmNode(node)
			is BinaryNode,
			is UnaryNode,
			is IdNode        -> ImmNode(node)
			is SRegNode      -> node
			is STRegNode     -> node
			else             -> error("Expecting operand, found ${tokens[pos - 1]}")
		}

		if(shortImm && result !is ImmNode) error("Unexpected immediate width specifier")

		return result
	}



	private fun parseInstruction(modifier: KeywordToken?, mnemonic: Mnemonic): InstructionNode {
		shortImm = false

		if(atStatementEnd()) return InstructionNode(modifier, shortImm, mnemonic, null, null, null, null)
		val operand1 = parseOperand()

		if(atStatementEnd()) return InstructionNode(modifier, shortImm, mnemonic, operand1, null, null, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand2 = parseOperand()

		if(atStatementEnd()) return InstructionNode(modifier, shortImm, mnemonic, operand1, operand2, null, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand3 = parseOperand()

		if(atStatementEnd()) return InstructionNode(modifier, shortImm, mnemonic, operand1, operand2, operand3, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand4 = parseOperand()

		if(!atStatementEnd()) error("unexpected token: ${tokens[pos]}")

		return InstructionNode(modifier, shortImm, mnemonic, operand1, operand2, operand3, operand4)
	}


}