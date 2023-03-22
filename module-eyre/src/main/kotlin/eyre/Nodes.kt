package eyre



sealed interface AstNode

class ImportNode(val parts: List<Intern>) : AstNode

class IntNode(val value: Long) : AstNode

class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode

class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode

class StringNode(val value: Intern) : AstNode

class ConstNode(val symbol: Symbol, val value: AstNode) : AstNode

class ResNode(val symbol: ResSymbol, val size: AstNode) : AstNode

class VarNode(val symbol: VarSymbol, val parts: List<VarPart>) : AstNode

class VarPart(val width: Width, val values: List<AstNode>)

class LabelNode(val symbol: LabelSymbol) : AstNode

class SizeofNode(val value: AstNode, var size: Long = 0) : AstNode

class RelNode(val left: AstNode, val right: AstNode, val divisor: Int) : AstNode

class InvokeNode(val invoker: SymProvider, val args: List<AstNode>) : AstNode

open class SymNode(val name: Intern, override var symbol: Symbol? = null) : SymProvider

class DotNode(val left: AstNode, val right: SymNode) : SymProvider by right

interface SymProvider : AstNode {
	val symbol: Symbol?
}



/*
Scope
 */



class NamespaceNode(val symbol: Namespace) : AstNode

class ProcNode(val symbol: ProcSymbol) : AstNode

class EnumNode(val symbol: Namespace, val entries: List<EnumEntryNode>) : AstNode

class EnumEntryNode(val symbol: IntSymbol, val value: AstNode) : AstNode

object ScopeEndNode : AstNode



/*
Instruction
 */



class RegNode(val value: Register) : AstNode

class ImmNode(val value: AstNode) : AstNode

class MemNode(val width: Width?, val value: AstNode) : AstNode

class DllRefNode(val symbol: DllSymbol) : AstNode

class InsNode(
	val mnemonic : Mnemonic,
	val prefix   : Prefix?,
	val shortImm : Boolean,
	val op1      : AstNode?,
	val op2      : AstNode?,
	val op3      : AstNode?,
	val op4      : AstNode?
) : AstNode



/*
toString
 */



val AstNode.printableString: String get() = when(this) {
	is LabelNode     -> "label ${symbol.name}"
	is StringNode    -> value.toString()
	is IntNode       -> value.toString()
	is UnaryNode     -> "${op.symbol}${node.printableString}"
	is BinaryNode    -> "(${left.printableString} ${op.symbol} ${right.printableString})"
	is DotNode       -> "(${left.printableString}.${right.printableString})"
	is RegNode       -> value.string
	is ImmNode       -> value.printableString
	is MemNode       -> "${width?.string} [${value.printableString}]"
	is SymNode       -> "$name"
	is ResNode       -> "var ${symbol.name} res ${size.printableString}"
	is ConstNode     -> "const ${symbol.name} = ${value.printableString}"
	is DllRefNode    -> "${symbol.dll}::${symbol.name}"
	is ProcNode      -> "proc ${symbol.name}"
	is SizeofNode    -> "sizeof(${value.printableString})"

	is InvokeNode    -> buildString {
		append(invoker.printableString)
		append('(')
		for(i in args.indices) {
			append(args[i].printableString)
			if(i != args.size - 1) append(", ")
		}
		append(')')
	}

	is ImportNode -> buildString {
		append("import ")
		for(i in parts.indices) {
			append(parts[i])
			if(i != parts.size - 1) append('.')
		}
	}

	is VarNode -> buildString {
		append("var ")
		append(symbol.name)
		for(part in parts) {
			append("\n\t")
			append(part.width.varString)
			append(' ')
			for((i, c) in part.values.withIndex()) {
				append(c.printableString)
				if(i < part.values.size - 1) append(", ")
			}
		}
	}

	is InsNode -> buildString {
		if(prefix != null) append(prefix.string + ' ')
		append(mnemonic.string)
		if(shortImm) append(" short")
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

	is NamespaceNode -> "namespace ${symbol.name}"
	is ScopeEndNode -> "SCOPE END"

	is EnumNode -> buildString {
		appendLine("enum ${symbol.name} {")
		for(e in entries)
			appendLine("\t${e.symbol.name} = ${e.value.printableString},")
		append('}')
	}

	else -> toString()
}