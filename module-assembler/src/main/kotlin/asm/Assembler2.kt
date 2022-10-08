package asm

class Assembler2(parseResult: ParseResult) {


	private val nodes = parseResult.nodes

	private val symbols = parseResult.symbols

	private fun error(): Nothing = error("Invalid encoding")

	private val groups = EncodingReader().read()



	lateinit var group: InstructionGroup

	lateinit var operands: Operands

	lateinit var width: Width



	/*
	Node resolution
	 */



	private var baseReg: Register? = null

	private var indexReg: Register? = null

	private var indexScale = 0

	private var posLabel: Symbol? = null

	private var negLabel: Symbol? = null // only non-null if posLabel is also non-null

	private var hasLabel = false



	private fun resolve(root: AstNode, isMem: Boolean = false): Long {
		baseReg    = null
		indexReg   = null
		indexScale = 0
		posLabel   = null
		negLabel   = null

		fun rec(node: AstNode, positivity: Int): Long {
			if(node is RegNode) {
				if(positivity <= 0 || !isMem) error()

				if(baseReg != null) {
					if(indexReg != null)
						error()
					indexReg = node.value
					indexScale = 1
				} else
					baseReg = node.value

				return 0
			}

			if(node is UnaryNode)
				return node.op.calculate(rec(node.node, positivity * node.op.positivity))

			if(node is BinaryNode) {
				if(node.op == BinaryOp.MUL) {
					if(node.left is RegNode && node.right is IntNode) {
						if(indexReg != null || positivity <= 0 || !isMem) error()
						indexReg = node.left.value
						indexScale = node.right.value.toInt()
						return 0
					} else if(node.left is IntNode && node.right is RegNode) {
						if(indexReg != null || positivity <= 0 || !isMem) error()
						indexReg = node.right.value
						indexScale = node.left.value.toInt()
						return 0
					}
				}

				return node.op.calculate(
					rec(node.left, positivity * node.op.leftPositivity),
					rec(node.right, positivity * node.op.rightPositivity)
				)
			}

			if(node is IntNode)
				return node.value

			if(node is IdNode) {
				val symbol = symbols[node.name] ?: error()

				if(symbol.type == SymbolType.INT)
					return (symbol.data as IntSymbolData).value

				if(symbol.type == SymbolType.LABEL) {
					if(positivity == 0) {
						error()
					} else if(positivity > 0) {
						if(posLabel != null) error()
						posLabel = symbol
					} else {
						if(negLabel != null) error()
						negLabel = symbol
					}
				} else {
					error()
				}

				return 0
			}

			error()
		}

		val disp = rec(if(root is ImmNode) root.value else root, 1)
		hasLabel = posLabel != null
		return disp
	}



	/*
	Assembly
	 */



	fun assemble() {
		for(node in nodes) {
			when(node) {
				is InstructionNode -> assemble(node)
				else -> { }
			}
		}
	}



	private fun assemble(node: InstructionNode) {
		group = groups[node.mnemonic]!!
		operands(node)
		var bits = group.operandsBits
		if(bits and operands.bit == 0) error("Invalid encoding")
		bits = group.operandsBits and (operands.bit - 1)
		val encoding = group.instructions[bits.countOneBits()]
		println(encoding)
	}



	private fun operands(node: InstructionNode) {
		when {
			node.op1 == null -> error()
			node.op2 == null -> operands1(node.op1)
			node.op3 == null -> operands2(node.op1, node.op2)
			node.op4 == null -> error()
			else             -> error()
		}
	}



	private fun operands1(op1: AstNode) {
		if(op1 is RegNode) {
			operands = Operands.R
			width = op1.value.width
		} else if(op1 is MemNode) {
			operands = Operands.M
			width = op1.width ?: error()
		} else {
			error()
		}
	}



	private fun operands2(op1: AstNode, op2: AstNode) {
		if(op1 is RegNode) {
			if(op2 is RegNode) {
				width = op1.value.width
				if(width != op2.value.width) error()
				operands = Operands.R_R
			} else if(op2 is MemNode) {
				width = op1.value.width
				if(op2.width != null && op2.width != width) error()
				operands = Operands.R_M
			} else if(op2 is ImmNode) {
				width = op1.value.width
				operands = if(op1.value.isA && Specifier.A_I in group)
					Operands.A_I
				else
					Operands.R_I
			} else {
				error()
			}
		} else if(op1 is MemNode) {
			if(op2 is RegNode) {
				width = op2.value.width
				if(op1.width != null && width != op1.width) error()
				operands = Operands.M_R
			} else if(op2 is ImmNode) {
				width = op1.width ?: error()
				operands = Operands.M_I
			} else {
				error()
			}
		} else {
			error()
		}
	}


}



data class Instruction(
	val opcode    : Int,
	val extension : Int,
	val prefix    : Int,
	val operands  : Operands,
	val widths    : Widths
)



data class InstructionGroup(
	val instructions  : List<Instruction>,
	val operandsBits  : Int,
	val specifierBits : Int
) {

	operator fun contains(specifier: Specifier) = specifierBits and specifier.bit != 0

}



enum class Operands(val specifier: Specifier = Specifier.NONE) {

	NONE,

	R,
	M,

	R_R,
	R_M,
	M_R,
	R_I,
	R_I8(Specifier.RM_I8),
	M_I,
	M_I8(Specifier.RM_I8),
	A_I(Specifier.A_I),

	R_R8,
	R_M8,
	R_R16,
	R_M16,
	R64_R32,
	R64_M32;

	val bit = 1 shl ordinal

}



enum class Specifier {

	NONE,
	RM_I8,
	RM_CL,
	RM_1,
	A_I;

	val bit = 1 shl ordinal

}



enum class SpecialEncoding {

	OPREG;

}