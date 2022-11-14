package assembler

sealed interface AstNode

class LabelNode(val symbol: LabelSymbol) : AstNode {
	override fun toString() = "label ${symbol.name}"
}

class StringNode(val value: String) : AstNode {
	override fun toString() = value
}

class IntNode(val value: Long) : AstNode {
	override fun toString() = "$value"
}

class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode {
	override fun toString() = "${op.symbol}${node}"
}

class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode {
	override fun toString() = "($left ${op.symbol} $right)"
}

class DotNode(val left: AstNode, val right: SymNode) : AstNode {
	override fun toString() = "($left.$right)"
}

class RegNode(val value: Register) : AstNode {
	override fun toString() = "$value"
}

class ImmNode(val value: AstNode) : AstNode {
	override fun toString() = "$value"
}

class MemNode(val width: Width?, val value: AstNode) : AstNode {
	override fun toString() = "$width [$value]"
}

class SymNode(val name: Interned, var symbol: Symbol? = null) : AstNode {
	override fun toString() = "$name"
}

class ConstNode(val name: Interned, val value: AstNode): AstNode {
	override fun toString() = "const $name = $value"
}

class EnumEntry(val name: Interned)

class EnumNode(val name: Interned, val entries: List<EnumEntry>): AstNode

class InstructionNode(
	val mnemonic : Mnemonic,
	val shortImm : Boolean,
	val op1      : AstNode?,
	val op2      : AstNode?,
	val op3      : AstNode?,
	val op4      : AstNode?
) : AstNode {
	override fun toString() = "$mnemonic $op1, $op2, $op3, $op4"
}

class ResNode(val symbol: ResSymbol, val size: Int) : AstNode {
	override fun toString() = "var $symbol res $size"
}

class VarNode(val symbol: VarSymbol, val componentsAndWidths: List<Pair<Width, List<AstNode>>>) : AstNode {
	override fun toString() = "var $symbol..."
}