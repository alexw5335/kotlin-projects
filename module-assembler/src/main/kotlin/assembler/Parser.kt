package assembler

import java.beans.Expression
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Parser(lexResult: LexResult) {


	private var pos = 0

	private val tokens = lexResult.tokens

	private val newlines = lexResult.newlines

	private val nodes = ArrayList<AstNode>()

	private val symbols = HashMap<String, Symbol<*>>()



	private fun atStatementEnd() = tokens[pos] == EndToken || newlines[pos]

	private val prevToken get() = tokens[pos - 1]



	private var baseReg: Register? = null

	private var indexReg: Register? = null

	private var indexScale = 0



	fun parse(): ParseResult {
		while(true) {
			when(val token = tokens[pos++]) {
				is MnemonicToken    -> nodes.add(parseInstruction(token.value))
				is KeywordToken     -> parseKeyword(token)
				is EndToken         -> break
				else                -> error("Invalid token: $token")
			}
		}

		return ParseResult(nodes)
	}



	private fun parseMemoryOperand(node: AstNode, regValid: Boolean = true): AstNode? {
		fun error(): Nothing = error("Invalid memory operand")

		if(node is RegisterNode) {
			if(!regValid) error()

			if(baseReg != null) {
				if(indexReg != null) error()
				indexReg = node.value
				indexScale = 1
			} else {
				baseReg = node.value
			}

			return null
		}

		if(node !is BinaryNode) return node

		if(node.op == BinaryOp.MUL) {
			if(node.left is RegisterNode && node.right is IntNode) {
				if(indexReg != null) error()
				indexReg = node.left.value
				indexScale = node.right.value.toInt()
				return null
			} else if(node.left is IntNode && node.right is RegisterNode) {
				if(indexReg != null) error()
				indexReg = node.right.value
				indexScale = node.left.value.toInt()
				return null
			}
		}

		val left = parseMemoryOperand(node.left, regValid && (node.op == BinaryOp.ADD || node.op == BinaryOp.SUB))
		val right = parseMemoryOperand(node.right, regValid && node.op == BinaryOp.ADD)

		return when {
			left == null -> right // also handles case where both are null
			right == null -> left
			left == node.left && right == node.right -> node
			else -> BinaryNode(node.op, left, right)
		}
	}



	private fun parseKeyword(keyword: KeywordToken) {
		when(keyword) {
			KeywordToken.CONST -> parseConst()
			//KeywordToken.ENUM -> parseEnum()
			else -> { }
		}
	}



	private fun parseConst() {
		pos += 2
		baseReg = null
		indexReg = null
		val displacement = parseMemoryOperand(readExpression())
		println(baseReg)
		println(indexReg)
		println(indexScale)
		println(displacement?.printableString)
	}



	/*
	private fun parseEnum() {
		val name = (tokens[pos++] as? IdToken)?.value
			?: error("Expecting enum name, found: $prevToken")

		if(tokens[pos++] != SymbolToken.LEFT_BRACE)
			error("Expecting '{', found $prevToken")

		while(true) {
			if(tokens[pos] == SymbolToken.RIGHT_BRACE) {
				pos++
				break
			}

			val entryName = (tokens[pos++] as? IdToken)?.value
				?: error("Expecting enum entry name, found: $prevToken")

			var explicitValue: AstNode? = null

			if(tokens[pos] == SymbolToken.EQUALS) {
				pos++
				explicitValue = readExpression()
			}

			if(tokens[pos] == SymbolToken.SEMICOLON) {
				pos++
				break
			}

			if(tokens[pos++] != SymbolToken.COMMA) error("Expected ',")

			println("$name.$entryName = ${explicitValue?.printableString}")
		}
	}*/



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

		is IdToken -> IdNode(token.value)

		else -> error("Invalid expression operand token: $token")
	}



	private fun readExpression(precedence: Int = 0): AstNode {
		var result = readAtom()

		while(pos < tokens.size && !atStatementEnd()) {
			val symbol = tokens[pos] as? SymbolToken ?: error("Expected symbol, found: ${tokens[pos]}")
			val op = symbol.binaryOp ?: break
			if(op.precedence < precedence) break
			pos++
			val right = readExpression(op.precedence + 1)
			result = BinaryNode(op, result, right)
		}

		return result
	}



	private fun parseOperand(): AstNode {
		var token = tokens[pos]
		var width: Width? = null

		if(token is KeywordToken) {
			width = token.width ?: error("Expected explicit memory width, found: $token")
			token = tokens[++pos]
		}

		if(token == SymbolToken.LEFT_BRACKET) {
			pos++
			baseReg = null
			indexReg = null
			indexScale = 0
			val displacement = parseMemoryOperand(readExpression())
			when(indexScale) { 0, 1, 2, 4, 8 -> Unit else -> error("Invalid index scale") }
			if(tokens[pos++] != SymbolToken.RIGHT_BRACKET) error("Expecting ']', found $prevToken")
			return MemoryNode(width, baseReg, indexReg, indexScale, displacement)
		}

		if(width != null)
			error("Unexpected width specifier")

		return when(val node = readExpression()) {
			is RegisterNode -> node
			is IntNode      -> ImmediateNode(node, node.value)
			is BinaryNode,
			is UnaryNode,
			is IdNode       -> ImmediateNode(node, null)
			else            -> error("Expecting operand, found ${tokens[pos - 1]}")
		}
	}



	private fun parseInstruction(mnemonic: Mnemonic): InstructionNode {
		if(atStatementEnd()) return InstructionNode(mnemonic, null, null, null, null)
		val operand1 = parseOperand()

		if(atStatementEnd()) return InstructionNode(mnemonic, operand1, null, null, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand2 = parseOperand()

		if(atStatementEnd()) return InstructionNode(mnemonic, operand1, operand2, null, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand3 = parseOperand()

		if(atStatementEnd()) return InstructionNode(mnemonic, operand1, operand2, operand3, null)
		if(tokens[pos++] != SymbolToken.COMMA) error("Unexpected token: ${tokens[pos - 1]}")
		val operand4 = parseOperand()

		if(!atStatementEnd()) error("unexpected token: ${tokens[pos]}")

		return InstructionNode(mnemonic, operand1, operand2, operand3, operand4)
	}


}