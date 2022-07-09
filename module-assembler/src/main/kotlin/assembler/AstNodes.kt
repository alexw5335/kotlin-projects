package assembler



fun calculate(node: AstNode): Long = when(node) {
	is IntNode      -> node.value
	is UnaryOpNode  -> node.op.calculate(calculate(node.node))
	is BinaryOpNode -> node.op.calculate(calculate(node.left), calculate(node.right))
	else            -> error("Invalid node: $node")
}



val AstNode.printableString: String get() = when(this) {
	is BinaryOpNode    -> "$op(${left.printableString}, ${right.printableString})"
	is IdNode          -> value
	is IntNode         -> value.toString()
	is RegisterNode    -> value.string
	is ImmediateNode   -> value.toString()
	is MemoryNode      -> "[$base + $index * $scale + $displacement]"
	is OperandNode     -> "ERROR: Unhandled operand"
	is UnaryOpNode     -> "$op($node)"
	is InstructionNode -> buildString {
		append(mnemonic)
		if(op1 != null) append(" ${op1.printableString}")
		if(op2 != null) append(", ${op2.printableString}")
		if(op3 != null) append(", ${op3.printableString}")
		if(op4 != null) append(", ${op4.printableString}")
	}
	else -> "Unhandled AST node: $this"
}



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

sealed interface OperandNode : AstNode

class RegisterNode(val value: Register) : OperandNode {
	val width = value.width
}

class ImmediateNode(val value: Long) : OperandNode {
	val width = when(value) {
		in Long.MIN_VALUE..Long.MAX_VALUE   -> 4
		in Int.MIN_VALUE..Int.MAX_VALUE     -> 3
		in Short.MIN_VALUE..Short.MAX_VALUE -> 2
		in Byte.MIN_VALUE..Byte.MAX_VALUE   -> 1
		else                                -> error("Out-of-range immediate: $value")
	}
}

class MemoryNode(
	val base         : Register?,
	val index        : Register?,
	val scale        : Int,
	val displacement : Int,
	val width        : Width?
) : OperandNode