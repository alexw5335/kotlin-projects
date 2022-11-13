package assembler

sealed interface AstNode

data class LabelNode(val symbol: LabelSymbol) : AstNode

data class StringNode(val value: String) : AstNode {
	override fun toString() = value
}

data class IntNode(val value: Long) : AstNode {
	override fun toString() = "$value"
}

data class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode {
	override fun toString() = "${op.symbol}${node}"
}

data class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode {
	override fun toString() = "($left ${op.symbol} $right)"
}

data class DotNode(val left: AstNode, val right: SymNode) : AstNode {
	override fun toString() = "($left.$right)"
}

data class RegNode(val value: Register) : AstNode {
	override fun toString() = "$value"
}

data class ImmNode(val value: AstNode) : AstNode {
	override fun toString() = "$value"
}

data class MemNode(val width: Width?, val value: AstNode) : AstNode

data class SymNode(val name: Interned, var symbol: Symbol? = null): AstNode

data class InstructionNode(
	val mnemonic : Mnemonic,
	val shortImm : Boolean,
	val op1      : AstNode?,
	val op2      : AstNode?,
	val op3      : AstNode?,
	val op4      : AstNode?
) : AstNode {
	override fun toString() = "$mnemonic $op1, $op2, $op3, $op4"
}

data class ResNode(val symbol: ResSymbol, val size: Int) : AstNode

data class VarNode(val symbol: VarSymbol, val componentsAndWidths: List<Pair<Width, List<AstNode>>>) : AstNode