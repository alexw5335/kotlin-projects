package assembler



fun AstNode.calculateInt(resolver: (Namespace) -> Long = { error("Undefined symbol: $it") }): Long = when(this) {
	is IntNode    -> value
	is UnaryNode  -> op.calculate(node.calculateInt(resolver))
	is BinaryNode -> {
		if(op == BinaryOp.DOT)
			resolver(Namespace(ArrayList<String>().also { namespace(it) }))
		else
			op.calculateInt(left.calculateInt(resolver), right.calculateInt(resolver))
	}
	is IdNode     -> resolver(Namespace(listOf(value)))
	else          -> error("Cannot perform integer arithmetic on node: $this")
}



fun BinaryNode.namespace(components: MutableList<String>) {
	if(op != BinaryOp.DOT) error("Invalid binary op $op for dot operator")

	when(left) {
		is BinaryNode -> left.namespace(components)
		is IdNode     -> components.add(left.value)
		else          -> error("Invalid ast node $left for namespace operator")
	}

	when(right) {
		is BinaryNode -> right.namespace(components)
		is IdNode -> components.add(right.value)
		else -> error("Invalid ast node $right for namespace operator")
	}
}



fun AstNode.calculateIntOrNull(resolver: (Namespace) -> Long = { error("Undefined symbol: $it") }): Long? = try {
	calculateInt(resolver)
} catch(e: Exception) {
	null
}




val AstNode.printableString: String get() = when(this) {
	is BinaryNode      -> printableString
	is IdNode          -> value
	is IntNode         -> value.toString()
	is RegisterNode    -> value.string
	is MemoryNode      -> printableString
	is UnaryNode       -> "${op.symbol}(${node.printableString})"//"$op(${node.printableString})"
	is InstructionNode -> printableString
	is ImmediateNode   -> value.printableString
	is DefineNode      -> "db ${components.joinToString { it.printableString }}"
	is StringNode      -> "\"$value\""
	is LabelNode       -> "$name:"
}



val BinaryNode.printableString get() = buildString {
	append('(')
	append(left.printableString)
	if(op == BinaryOp.DOT)
		append('.')
	else
		append(" ${op.symbol} ")
	append(right.printableString)
	append(')')
}



val MemoryNode.printableString get() = buildString {
	if(width != null) {
		append(width.string)
		append(' ')
	}
	append("[$base + $index * $scale + ${disp?.printableString}]")
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



class LabelNode(val name: String) : AstNode



class IdNode(val value: String) : AstNode



class StringNode(val value: String) : AstNode



class IntNode(val value: Long) : AstNode



class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode



class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode



class DefineNode(val components: List<AstNode>) : AstNode



class RegisterNode(val value: Register) : AstNode



class MemoryNode(
	val width  : Width?,
	val rel    : Boolean,
	val base   : Register?,
	val index  : Register?,
	val scale  : Int,
	val disp   : AstNode?
) : AstNode



class ImmediateNode(val value: AstNode) : AstNode



class InstructionNode(
	val mnemonic: Mnemonic,
	val op1: AstNode?,
	val op2: AstNode?,
	val op3: AstNode?,
	val op4: AstNode?
) : AstNode