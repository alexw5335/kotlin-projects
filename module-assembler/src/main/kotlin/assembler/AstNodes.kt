package assembler



fun calculate(node: AstNode): Long = when(node) {
	is IntNode     -> node.value
	is UnaryNode   -> node.op.calculate(calculate(node.node))
	is BinaryNode  -> node.op.calculate(calculate(node.left), calculate(node.right))
	else           -> error("Cannot perform integer arithmetic on AST node: $node")
}



val AstNode.printableString: String get() = when(this) {
	is BinaryNode      -> "(${left.printableString} ${op.symbol} ${right.printableString})"//"$op(${left.printableString}, ${right.printableString})"
	is IdNode          -> value
	is IntNode         -> value.toString()
	is RegisterNode    -> value.string
	is ImmediateNode   -> value.printableString
	is MemoryNode      -> "[${value.printableString}]"
	is UnaryNode       -> "${op.symbol}${node.printableString}"//"$op(${node.printableString})"
	is InstructionNode -> printableString
	is ConstNode       -> "const $name = ${value.printableString}"
	else               -> "No printable string for AST node: ${this::class.simpleName}"
}



val InstructionNode.printableString get() = buildString {
	append(mnemonic)
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



class InstructionNode(
	val mnemonic: Mnemonic,
	val op1: OperandNode?,
	val op2: OperandNode?,
	val op3: OperandNode?,
	val op4: OperandNode?
) : AstNode



sealed interface OperandNode : AstNode



class RegisterNode(val value: Register) : OperandNode



class ImmediateNode(val value: AstNode) : OperandNode



class MemoryNode(val value: AstNode) : OperandNode