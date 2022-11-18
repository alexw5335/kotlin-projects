package assembler



sealed interface AstNode

class LabelNode(val symbol: LabelSymbol) : AstNode

class StringNode(val value: String) : AstNode

class IntNode(val value: Long) : AstNode

class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode

class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode

class DotNode(val left: AstNode, val right: SymNode) : AstNode

class RegNode(val value: Register) : AstNode

class ImmNode(val value: AstNode) : AstNode

class MemNode(val width: Width?, val value: AstNode) : AstNode

class SymNode(val name: Intern, var symbol: Symbol? = null) : AstNode

class InsNode(
	val mnemonic : Mnemonic,
	val prefix   : Prefix?,
	val shortImm : Boolean,
	val op1      : AstNode?,
	val op2      : AstNode?,
	val op3      : AstNode?,
	val op4      : AstNode?
) : AstNode

class ResNode(val symbol: ResSymbol, val size: AstNode) : AstNode

class VarNode(val symbol: VarSymbol, val componentsAndWidths: List<Pair<Width, List<AstNode>>>) : AstNode

class ConstNode(val symbol: Symbol, val value: AstNode) : AstNode

class DllImportNode(val symbol: Symbol, val dll: Intern) : AstNode

class ImportNode(val components: List<Intern>) : AstNode



val AstNode.printableString: String get() = when(this) {
	is LabelNode  -> "label ${symbol.name}"
	is StringNode -> value
	is IntNode    -> value.toString()
	is UnaryNode  -> "${op.symbol}${node.printableString}"
	is BinaryNode -> "(${left.printableString} ${op.symbol} ${right.printableString}"
	is DotNode    -> "(${left.printableString}.${right.printableString}"
	is RegNode    -> value.string
	is ImmNode    -> value.printableString
	is MemNode    -> "$width [${value.printableString}]"
	is SymNode    -> "$name"
	is ResNode    -> "var ${symbol.name} res $size"

	is VarNode -> buildString {
		append("var ")
		append(symbol.name)
		for((width, components) in componentsAndWidths) {
			append("\n\t")
			append(width.varString)
			append(' ')
			for((i, c) in components.withIndex()) {
				append(c.printableString)
				if(i < components.size - 1) append(", ")
			}
		}
	}

	is InsNode -> buildString {
		append(mnemonic.string)
		if(prefix != null) { append(':'); append(prefix.string) }
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

	else -> toString()
}