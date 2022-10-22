package asm



/*
Node classes
 */



sealed interface AstNode



class VarResNode(val symbol: VarSymbol, val size: Int) : AstNode



class VarDNode(val symbol: VarSymbol, val width: Width, val components: List<AstNode>) : AstNode



class LabelNode(val symbol: LabelSymbol) : AstNode



class IdNode(val name: String) : AstNode



class StringNode(val value: String) : AstNode



class IntNode(val value: Long) : AstNode



class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode



class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode



class DNode(val width: Width, val components: List<AstNode>) : AstNode



class RegNode(val value: Register) : AstNode



class SRegNode(val value: SRegister) : AstNode



class STRegNode(val value: STRegister) : AstNode



class ImmNode(val value: AstNode) : AstNode



class ConstNode(val name: String, val value: AstNode) : AstNode



class MemNode(val width: Width?, val value: AstNode) : AstNode



class InstructionNode(
	val modifier : KeywordToken?,
	val shortImm : Boolean,
	val mnemonic : Mnemonic,
	val op1      : AstNode?,
	val op2      : AstNode?,
	val op3      : AstNode?,
	val op4      : AstNode?
) : AstNode




/*
Formatted printing
 */



val AstNode.printableString: String get() = when(this) {
	is BinaryNode      -> printableString
	is IdNode          -> name
	is IntNode         -> value.toString()
	is RegNode         -> value.string
	is MemNode         -> printableString
	is UnaryNode       -> "${op.symbol}(${node.printableString})"
	is InstructionNode -> printableString
	is ImmNode         -> value.printableString
	is DNode          -> "db ${components.joinToString { it.printableString }}"
	is StringNode      -> "\"$value\""
	is LabelNode       -> "${symbol.name}:"
	is ConstNode       -> "const $name = ${value.printableString}"
	is SRegNode        -> value.string
	is STRegNode       -> value.string
	is VarDNode        -> "var ${symbol.name} (${width.string}) = ${components.joinToString { it.printableString }}"
	is VarResNode      -> "var ${symbol.name} res $size"
	else               -> toString()
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



val MemNode.printableString get() = buildString {
	if(width != null) {
		append(width.string)
		append(' ')
	}
	append(value.printableString)
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



