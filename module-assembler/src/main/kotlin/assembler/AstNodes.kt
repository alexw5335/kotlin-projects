package assembler



fun AstNode.isConstantInt(): Boolean = when(this) {
	is BinaryNode -> left.isConstantInt() && right.isConstantInt()
	is UnaryNode  -> node.isConstantInt()
	is IntNode    -> true
	else          -> false
}


fun AstNode.isConstantInt(resolver: (String) -> Boolean): Boolean = when(this) {
	is BinaryNode -> left.isConstantInt(resolver) && right.isConstantInt(resolver)
	is UnaryNode  -> node.isConstantInt(resolver)
	is IntNode    -> true
	is IdNode     -> resolver(value)
	else          -> false
}



fun AstNode.calculateConstantInt() = calculateConstantInt { error("Cannot resolve symbol: $it") }



fun AstNode.calculateConstantInt(resolver: (String) -> Long): Long = when(this) {
	is IntNode    -> value
	is UnaryNode  -> op.calculate(node.calculateConstantInt(resolver))
	is BinaryNode -> op.calculate(left.calculateConstantInt(resolver), right.calculateConstantInt(resolver))
	is IdNode     -> resolver(value)
	else          -> error("Cannot perform integer arithmetic on node: $this")
}



val AstNode.printableString: String get() = when(this) {
	is BinaryNode      -> "(${left.printableString} ${op.symbol} ${right.printableString})"//"$op(${left.printableString}, ${right.printableString})"
	is IdNode          -> value
	is IntNode         -> value.toString()
	is RegisterNode    -> value.string
	is ImmediateNode   -> value.printableString
	is MemoryNode      -> printableString
	is UnaryNode       -> "${op.symbol}(${node.printableString})"//"$op(${node.printableString})"
	is InstructionNode -> printableString
	is ConstNode       -> "const $name = ${value.printableString}"
	is LabelNode       -> "$name:"
	else               -> "No printable string for AST node: ${this::class.simpleName}"
}



val MemoryNode.printableString get() = buildString {
	if(width != null) {
		append(width.string)
		append(' ')
	}
	append('[')
	append(value.printableString)
	append(']')
}



val InstructionNode.printableString get() = buildString {
	append(mnemonic.string)
	if(op1 == null) return@buildString
	append(' ')
	append(op1.printableString)
	if(op2 == null) return@buildString
	append(", ")
	append(op2.printableString)
	if(op3 == null) return@buildString
	append(", ")
	append(op3.printableString)
	if(op4 == null) return@buildString
	append(", ")
	append(op4.printableString)
}



sealed interface AstNode



class IdNode(val value: String) : AstNode



class IntNode(val value: Long) : AstNode



class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode



class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode



class ConstNode(val name: String, val value: AstNode) : AstNode



class LabelNode(val name: String) : AstNode



class InstructionNode(
	val mnemonic: Mnemonic,
	val op1: AstNode?,
	val op2: AstNode?,
	val op3: AstNode?,
	val op4: AstNode?
) : AstNode {

	var op1Type = Operand.NONE
	var op2Type = Operand.NONE
	var op3Type = Operand.NONE
	var op4Type = Operand.NONE

}



class RegisterNode(val value: Register) : AstNode



class ImmediateNode(val value: AstNode, val calculatedValue: Long? = null) : AstNode



class MemoryNode(val value: AstNode, val width: Width?) : AstNode