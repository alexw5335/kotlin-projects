package eyre

class Resolver(private val srcSet: SrcSet, private val globalNamespace: Namespace) {


	private val stack = SymStack()



	fun resolve() {
		stack.push(globalNamespace.symbols)
		for(file in srcSet.srcFiles)
			resolveFile(file)
		stack.pop()
	}



	private fun resolveFile(srcFile: SrcFile) {
		if(srcFile.resolving)
			error("Circular dependency found. Currently resolving files: ${srcSet.srcFiles.filter { it.resolving }}")
		else if(srcFile.resolved)
			return

		srcFile.resolving = true

		val pos = stack.pos
		for(node in srcFile.nodes)
			resolveNode(node)
		stack.pos = pos

		srcFile.resolving = false
		srcFile.resolved = true
	}



	private fun resolveNode(node: AstNode) {
		when(node) {
			is NamespaceNode -> stack.push(node.symbol.symbols)

			is ProcNode -> stack.push(node.symbol.symbols)

			is ScopeEndNode -> stack.pop()

			is ImportNode -> {
				var symbol = stack[node.parts[0]] ?: error("Missing symbol")

				for(i in 1 until node.parts.size) {
					if(symbol !is Namespace) error("Expecting namespace, found: $symbol")
					symbol = symbol.symbols[node.parts[i]] ?: error("Missing symbol")
				}

				when(symbol) {
					is Namespace -> stack.add(symbol.symbols)
					else -> error("Invalid import symbol: $symbol")
				}
			}

			is VarNode ->
				for(part in node.parts)
					for(value in part.values)
						resolveSymbols(value)

			is ResNode -> {
				resolveSymbols(node.size)
				val size = node.size.resolveInt()
				if(size !in 0..Int.MAX_VALUE) error("Res size $size not in range 0..${Int.MAX_VALUE}")
				node.symbol.size = size.toInt()
			}

			is ConstNode -> {
				resolveSymbols(node.value)
				val symbol = node.symbol as? IntSymbol ?: error("Invalid const symbol: ${node.symbol}")
				symbol.value = node.value.resolveInt()
				symbol.resolved = true
			}

			is InsNode -> {
				if(node.op1 == null) return
				resolveSymbols(node.op1)
				if(node.op2 == null) return
				resolveSymbols(node.op2)
				if(node.op3 == null) return
				resolveSymbols(node.op3)
				if(node.op4 == null) return
				resolveSymbols(node.op4)
			}

			is EnumNode -> {
				for(entry in node.entries) {
					resolveSymbols(entry.value)
					entry.symbol.value = entry.value.resolveInt()
					entry.symbol.resolved = true
				}
			}

			else -> { }
		}
	}



	private fun resolveSymbols(node: AstNode) {
		when(node) {
			is SymNode    -> if(node.symbol == null)
				node.symbol = stack[node.name] ?: error("Unresolved symbol: ${node.name}")
			is UnaryNode  -> resolveSymbols(node.node)
			is BinaryNode -> { resolveSymbols(node.left); resolveSymbols(node.right) }
			is DotNode    -> resolveDot(node)
			is ImmNode    -> resolveSymbols(node.value)
			is MemNode    -> resolveSymbols(node.value)
			is SizeofNode -> resolveSizeof(node)
			is StringNode,
			is IntNode,
			is RegNode,
			is DllRefNode -> { }
			else          -> error("Unhandled node: $node")
		}
	}



	private fun resolveDot(node: DotNode): Symbol {
		val name = node.right.name

		if(node.left is SymNode) {
			val namespace = stack[node.left.name] as? Namespace ?: error("Expecting namespace: ${node.left.name}")
			node.left.symbol = namespace
			val symbol = namespace.symbols[name] ?: error("Missing symbol: $name")
			node.right.symbol = symbol
			return symbol
		}

		if(node.left is DotNode) {
			val left = resolveDot(node.left)
			val namespace = left as? Namespace ?: error("Expecting namespace: $left")
			val symbol = namespace.symbols[name] ?: error("Missing symbolL $name")
			node.right.symbol = symbol
			return symbol
		}

		error("Invalid dot node: ${node.left}")
	}



	private fun AstNode.resolveInt(): Long = when(this) {
		is IntNode    -> value
		is StringNode -> resolveStringImm(value.string)
		is UnaryNode  -> op.calculate(node.resolveInt())
		is BinaryNode -> op.calculate(left.resolveInt(), right.resolveInt())
		is DotNode    -> resolveIntSymbol(right.symbol)
		is SymNode    -> resolveIntSymbol(symbol)
		is SizeofNode -> size
		else          -> error("Invalid int node: $this")
	}



	private fun resolveSizeof(node: SizeofNode): Long {
		resolveSymbols(node.value)

		val symbol = when(val value = node.value) {
			is DotNode -> value.right.symbol!!
			is SymNode -> value.symbol!!
			else -> error("Invalid size node: $node")
		}

		node.size = when(symbol) {
			is VarSymbol -> symbol.size.toLong()
			else -> error("Invalid size symbol: $symbol")
		}

		return node.size
	}



	private fun resolveIntSymbol(symbol: Symbol?): Long {
		if(symbol !is IntSymbol) error("Expecting const int, found $symbol")
		if(symbol.resolved) return symbol.value
		resolveFile(symbol.file)
		return symbol.value
	}




	private fun resolveStringImm(string: String): Long {
		var value = 0L
		if(string.length > 8) error("String literal out of range")
		for((i, c) in string.withIndex())
			if(c.code > 255)
				error("String literal char out of range: $c")
			else
				value = value or (c.code.toLong() shl (i shl 3))
		return value
	}


}