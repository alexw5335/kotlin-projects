package assembler

sealed interface AstNode

data class LabelNode(val symbol: LabelSymbol) : AstNode

data class IdNode(val name: String) : AstNode

data class StringNode(val value: String) : AstNode

data class IntNode(val value: Long) : AstNode

data class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode

data class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode

data class RegNode(val value: Register) : AstNode

data class ImmNode(val value: AstNode) : AstNode

data class MemNode(val width: Width?, val value: AstNode) : AstNode

data class SymNode(val name: String, var symbol: Symbol? = null): AstNode

data class InstructionNode(
	val mnemonic : Mnemonic,
	val shortImm : Boolean,
	val op1      : AstNode?,
	val op2      : AstNode?,
	val op3      : AstNode?,
	val op4      : AstNode?
) : AstNode

data class ResNode(val symbol: ResSymbol, val size: Int) : AstNode

data class VarNode(val symbol: VarSymbol, val componentsAndWidths: List<Pair<Width, List<AstNode>>>) : AstNode