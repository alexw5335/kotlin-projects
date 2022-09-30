package asm

import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Parser(lexResult: LexResult) {


	companion object {

		fun parse(lexResult: LexResult) = Parser(lexResult).parse()

	}



	private var pos = 0

	private val tokens = lexResult.tokens

	private val newlines = lexResult.newlines

	private val nodes = ArrayList<AstNode>()

	private val symbols = HashMap<String, Symbol>()



	private fun atNewline() = newlines[pos]

	private fun atStatementEnd() = tokens[pos] == EndToken || newlines[pos]

	private val prevToken get() = tokens[pos - 1]



	fun parse(): ParseResult {
		while(true) {
			when(val token = tokens[pos++]) {
				is MnemonicToken      -> nodes.add(parseInstruction(token.value))
				is KeywordToken       -> parseKeyword(token)
				is EndToken           -> break
				is IdToken            -> parseId(token)
				SymbolToken.SEMICOLON -> continue
				else                  -> error("Invalid token: $token")
			}
		}

		return ParseResult(nodes, symbols)
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



	private fun identifier() =  (tokens[pos++] as? IdToken)?.value ?: error("Expecting identifier name")



	/*
	Keywords
	 */



	private fun parseId(id: IdToken) {
		if(tokens[pos++] != SymbolToken.COLON)
			error("Expecting colon after identifier")
		val symbol = Symbol(id.value, SymbolType.LABEL)
		symbols[symbol.name] = symbol
		nodes.add(LabelNode(symbol))
	}



	private fun parseKeyword(keyword: KeywordToken) {
		when(keyword) {
			KeywordToken.DB -> parseDb()
			KeywordToken.CONST -> parseConst()
			KeywordToken.EXTERN -> parseExtern()
			else -> { }
		}
	}



	private fun parseExtern() {
		val name = identifier()
		symbols[name] = Symbol(name, SymbolType.EXTERN)
		expectStatementEnd()
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
			if(tokens[pos++] != SymbolToken.COMMA)
				break
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

		is IntToken -> IntNode(token.value)

		is RegisterToken -> RegisterNode(token.value)

		is SRegisterToken -> SRegisterNode(token.value)

		is IdToken -> IdNode(token.value)

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
			width = token.width ?: error("Expected explicit memory width, found: $token")
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

		return when(val node = readExpression()) {
			is RegisterNode -> node
			is IntNode      -> ImmediateNode(node)
			is BinaryNode,
			is UnaryNode,
			is IdNode       -> ImmediateNode(node)
			else            -> error("Expecting operand, found ${tokens[pos - 1]}")
		}
	}



	private fun parseInstruction(mnemonic: Mnemonic): InstructionNode {
		if(mnemonic.stringWidth != null && tokens[pos] == SymbolToken.COLON) {
			pos++
			val prefix = when(val string = (tokens[pos++] as? IdToken)?.value) {
				"rep", "repe", "repz" -> 0xF3
				"repne", "repnz"      -> 0xF2
				else                  -> error("Invalid string prefix: $string")
			}
			return InstructionNode(prefix, mnemonic, null, null, null, null)
		}

		if(atStatementEnd()) return InstructionNode(0, mnemonic, null, null, null, null)
		val operand1 = parseOperand()

		if(atStatementEnd()) return InstructionNode(0, mnemonic, operand1, null, null, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand2 = parseOperand()

		if(atStatementEnd()) return InstructionNode(0, mnemonic, operand1, operand2, null, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand3 = parseOperand()

		if(atStatementEnd()) return InstructionNode(0, mnemonic, operand1, operand2, operand3, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand4 = parseOperand()

		if(!atStatementEnd()) error("unexpected token: ${tokens[pos]}")

		return InstructionNode(0, mnemonic, operand1, operand2, operand3, operand4)
	}


}