package assembler

sealed interface AstNode

class IdNode(val value: String) : AstNode

class IntNode(val value: Long) : AstNode

class UnaryOpNode(val op: UnaryOp, val node: AstNode) : AstNode

class BinaryOpNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode

class InstructionNode(
	val mnemonic: Mnemonic,
	val operand1: OperandNode?,
	val operand2: OperandNode?,
	val operand3: OperandNode?,
	val operand4: OperandNode?
) : AstNode

interface OperandNode : AstNode

class RegisterNode(val register: Register) : OperandNode

class ImmediateNode(val value: Long) : OperandNode

class AddressNode(val value: Long) : OperandNode
