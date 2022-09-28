package asm



/*
Formatted printing
 */



val AstNode.printableString: String get() = when(this) {
	is BinaryNode      -> printableString
	is IdNode          -> name
	is IntNode         -> value.toString()
	is RegisterNode    -> value.string
	is MemoryNode      -> printableString
	is UnaryNode       -> "${op.symbol}(${node.printableString})"
	is InstructionNode -> printableString
	is ImmediateNode   -> value.printableString
	is DbNode      -> "db ${components.joinToString { it.printableString }}"
	is StringNode      -> "\"$value\""
	is LabelNode       -> "$name:"
	is ConstNode       -> "val $name = ${value.printableString}"
	is SRegisterNode   -> value.string
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



/*
Node classes
 */



sealed interface AstNode



class LabelNode(val name: String) : AstNode



class IdNode(val name: String) : AstNode



class StringNode(val value: String) : AstNode



class IntNode(val value: Long) : AstNode



class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode



class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode



class DbNode(val components: List<AstNode>) : AstNode



class RegisterNode(val value: Register) : AstNode



class SRegisterNode(val value: SRegister) : AstNode



class ImmediateNode(val value: AstNode) : AstNode



class ConstNode(val name: String, val value: AstNode) : AstNode



class MemoryNode(
	val width  : Width?,
	val rel    : Boolean,
	val base   : Register?,
	val index  : Register?,
	val scale  : Int,
	val disp   : AstNode?
) : AstNode



class InstructionNode(
	val prefix       : Int,
	val mnemonic     : Mnemonic,
	val op1          : AstNode?,
	val op2          : AstNode?,
	val op3          : AstNode?,
	val op4          : AstNode?
) : AstNode