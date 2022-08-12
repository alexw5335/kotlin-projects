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



	private fun traverse(node: AstNode) {
		val stack = LinkedList<AstNode>()
		stack.add(node)

		var base: Register? = null
		var index: Register? = null
		var scale = 1
		var displacement: AstNode? = null

		while(stack.isNotEmpty()) {
			val current = stack.remove()
			if(current is RegisterNode) {
				println("found register: ${current.value}")
			} else if(current is BinaryNode) {
				if(current.op == BinaryOp.ADD) {
					stack.add(current.left)
					stack.add(current.right)
				} else if(current.op == BinaryOp.SUB) {
					stack.add(current.left)
				}
			}
		}

		println(getDisplacement2(node)?.printableString)
	}



	private fun getDisplacement2(node: AstNode, regValid: Boolean = true): AstNode? {
		if(node is RegisterNode) {
			if(!regValid) error("Invalid memory operand")
			return null
		}

		if(node !is BinaryNode) return node

		if(node.op == BinaryOp.MUL && ((node.left is RegisterNode && node.right is IntNode) || (node.left is IntNode && node.right is RegisterNode))) {
			return null
		}

		val left = getDisplacement2(node.left, regValid && (node.op == BinaryOp.ADD || node.op == BinaryOp.SUB))
		val right = getDisplacement2(node.right, regValid && node.op == BinaryOp.ADD)

		return when {
			left == null -> right
			right == null -> left
			left == node.left && right == node.right -> node
			else -> BinaryNode(node.op, left, right)
		}
	}



	private fun getDisplacement(node: AstNode, regValid: Boolean): AstNode? {
		if(node !is BinaryNode) return node

		fun regError(): Nothing = error("Invalid memory operand")

		if(node.left is RegisterNode) {
			if(!regValid) regError()
			if(node.right is RegisterNode) return null
			return getDisplacement(node.right, regValid && node.op == BinaryOp.ADD)
		} else if(node.right is RegisterNode) {
			if(!regValid) regError()
			return getDisplacement(node.left, regValid && (node.op == BinaryOp.ADD || node.op == BinaryOp.SUB))
		} else {
			val left = getDisplacement(node.left, regValid && (node.op == BinaryOp.ADD))
			val right = getDisplacement(node.right, regValid && (node.op == BinaryOp.ADD || node.op == BinaryOp.SUB))
			if(left == null) return right
			if(right == null) return left
			if(left == node.left && right == node.right) return node
			return BinaryNode(node.op, left, right)
		}
	}



	/*
	- ADD(ADD(1, 2), 3)
		- No registers, return nodes unchanged
	- ADD(ADD(RAX, 2), 3
		- removeRegisters(ADD(RAX, 2)) returns 2
		- left node
	 */
	// ADD(ADD(RAX, 1), 2) -> ADD(1, 2)
	// ADD(ADD(1, RAX), 2) -> ADD(1, 2)
	// ADD(ADD(RAX, RCX), 2) -> 2
	// Turn any binary nodes that contain registers into single nodes
	// Remove any binary nodes that contain two registers



	private fun parseKeyword(keyword: KeywordToken) {
		when(keyword) {
			KeywordToken.CONST -> parseConst()
			//KeywordToken.ENUM -> parseEnum()
			else -> { }
		}
	}



	private fun parseConst() {
		pos += 2
		traverse(readExpression())
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
			val components = ArrayList<AstNode>()
			while(true) {
				components.add(readExpression())
				if(tokens[pos] == SymbolToken.RIGHT_BRACKET) { pos++; break }
				if(tokens[pos++] != SymbolToken.COMMA) error("Expecting ',' or ']', found: $prevToken")
			}
			return MemoryNode(components, width)
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