package assembler

sealed interface AstNode

class LabelNode(val symbol: LabelSymbol) : AstNode

class IdNode(val name: String) : AstNode

class StringNode(val value: String) : AstNode

class IntNode(val value: Long) : AstNode

class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode

class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode

class RegNode(val value: Register) : AstNode

class ImmNode(val value: AstNode) : AstNode

class MemNode(val width: Width?, val value: AstNode)

class InstructionNode(
	val mnemonic : Mnemonic,
	val op1      : AstNode?,
	val op2      : AstNode?,
	val op3      : AstNode?,
	val op4      : AstNode?
)