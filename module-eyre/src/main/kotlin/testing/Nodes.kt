package testing



sealed interface AstNode

object NullNode : AstNode

object ScopeEndNode : AstNode

class NamespaceNode(val symbol: Namespace) : AstNode

class IntNode(val value: Long) : AstNode

class RegNode(val value: Register) : AstNode

class UnaryNode(val op: UnaryOp, val node: AstNode) : AstNode

class BinaryNode(val op: BinaryOp, val left: AstNode, val right: AstNode) : AstNode

class StringNode(val value: String) : AstNode

class SymNode(val name: Intern, var symbol: Symbol? = null): AstNode

class DotNode(val left: AstNode, val right: SymNode) : AstNode

class InvokeNode(val invoker: AstNode, val args: List<AstNode>) : AstNode

class StructMemberNode(val symbol: StructMemberSymbol, val type: Intern)

class StructNode(val symbol: StructSymbol, val members: List<StructMemberNode>) : AstNode

class ConstNode(val symbol: ConstSymbol, val value: AstNode) : AstNode

class ResNode(val symbol: ResSymbol, val value: AstNode) : AstNode

class VarNode(val symbol: VarSymbol, val value: AstNode) : AstNode

class EnumEntryNode(val symbol: EnumEntrySymbol, val value: AstNode)

class EnumNode(val symbol: EnumSymbol, val entries: List<EnumEntryNode>) : AstNode



/*
Formatting
 */



val AstNode.printString: String get() = when(this) {
	//is LabelNode     -> "label ${symbol.name}"
	is StringNode    -> value
	is IntNode       -> value.toString()
	is UnaryNode     -> "${op.symbol}${node.printString}"
	is BinaryNode    -> "(${left.printString} ${op.symbol} ${right.printString})"
	is DotNode       -> "(${left.printString}.${right.printString})"
	is RegNode       -> value.string
	//is ImmNode       -> value.printString
	//is MemNode       -> "${width?.string} [${value.printString}]"
	is SymNode       -> "$name"
	is ResNode       -> "var ${symbol.name} res ${value.printString}"
	is ConstNode     -> "const ${symbol.name} = ${value.printString}"
	//is DllRefNode    -> "${symbol.dll}::${symbol.name}"
	//is ProcNode      -> "proc ${symbol.name}"
	//is SizeofNode    -> "sizeof(${value.printString})"
	is NamespaceNode -> "namespace ${symbol.name}"
	is ScopeEndNode  -> "SCOPE END"
	is VarNode       -> "var ${symbol.name} = ${value.printString}"

	is StructNode -> buildString {
		append("struct ")
		append(symbol.name)
		append(" {\n")
		for(member in members)
			append("\t${member.type} ${member.symbol.name}\n")
		append("}\n")
	}

	is InvokeNode    -> buildString {
		append(invoker.printString)
		append('(')
		for(i in args.indices) {
			append(args[i].printString)
			if(i != args.size - 1) append(", ")
		}
		append(')')
	}

/*	is ImportNode -> buildString {
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
				append(c.printString)
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
		append(op1.printString)
		if(op2 == null) return@buildString
		append(", ")
		append(op2.printString)
		if(op3 == null) return@buildString
		append(", ")
		append(op3.printString)
		if(op4 == null) return@buildString
		append(", ")
		append(op4.printString)
	}
	*/

	is EnumNode -> buildString {
		appendLine("enum ${symbol.name} {")
		for(e in entries)
			appendLine("\t${e.symbol.name} = ${e.value.printString}")
		append('}')
	}

	else -> toString()
}
