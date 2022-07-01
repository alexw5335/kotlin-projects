package assembler

sealed interface AstNode

class IdNode(val value: String) : AstNode

class IntNode(val value: Long) : AstNode

class UnaryOpNode(val op: UnaryOp, val node: AstNode) : AstNode

class BinaryOpNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode

class InstructionNode(
	val mnemonic: Mnemonic,
	val op1: OperandNode?,
	val op2: OperandNode?,
	val op3: OperandNode?,
	val op4: OperandNode?
) : AstNode

sealed interface OperandNode : AstNode {
	val width: Int
}

class RegisterNode(val register: Register) : OperandNode {
	override val width = register.width
}

class ImmediateNode(val value: Long) : OperandNode {
	override val width = when(value) {
		in Long.MIN_VALUE..Long.MAX_VALUE   -> 4
		in Int.MIN_VALUE..Int.MAX_VALUE     -> 3
		in Short.MIN_VALUE..Short.MAX_VALUE -> 2
		in Byte.MIN_VALUE..Byte.MAX_VALUE   -> 1
		else                                -> error("Out-of-range immediate: $value")
	}
}

class MemoryNode(val value: Long, override val width: Int) : OperandNode
