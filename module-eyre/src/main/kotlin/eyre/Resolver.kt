package eyre

class Resolver(private val globalNamespace: SymbolTable, private val parseOutput: ParseOutput) {


	private val symbolStack = SymbolStack()



	fun resolve() {
		symbolStack.add(globalNamespace)
		for(node in parseOutput.nodes)
			resolve(node)
	}



	private fun resolve(node: AstNode) {
		if(node is DllImportNode) return
		if(node is ImportNode) return

		if(node is NamespaceNode) {
			if(node.isSingleLine) {
				symbolStack.add(node.symbol.symbols)
				return
			}

			symbolStack.push()
			symbolStack.add(node.symbol.symbols)
			node.nodes.forEach(this::resolve)
			symbolStack.pop()
			return
		}

		if(node is ConstNode) {
			resolveSymbols(node.value)
			node.symbol.value = node.value.resolveInt()
			println(node.symbol.value)
			return
		}

		if(node is InsNode) {
			if(node.op1 == null) return
			resolveSymbols(node.op1)
			if(node.op2 == null) return
			resolveSymbols(node.op2)
			if(node.op3 == null) return
			resolveSymbols(node.op3)
			if(node.op4 == null) return
			resolveSymbols(node.op4)
			return
		}
	}



	private fun resolveSymbols(node: AstNode) {
		when(node) {
			is SymNode    -> node.symbol = symbolStack.get(node.name) ?: error("Missing symbol: ${node.name}")
			is UnaryNode  -> resolveSymbols(node.node)
			is BinaryNode -> { resolveSymbols(node.left); resolveSymbols(node.right) }
			is DotNode    -> resolveDot(node)
			else          -> { }
		}
	}



	private fun resolveDot(node: DotNode): Symbol {
		val name = node.right.name

		if(node.left is SymNode) {
			val namespace = symbolStack.get(node.left.name) as? NamespaceSymbol ?: error("Expecting namespace: ${node.left.name}")
			node.left.symbol = namespace
			val symbol = namespace.symbols[name] ?: error("Missing symbol: $name")
			node.right.symbol = symbol
			return symbol
		}

		if(node.left is DotNode) {
			val left = resolveDot(node.left)
			val namespace = left as? NamespaceSymbol ?: error("Expecting namespace: $left")
			val symbol = namespace.symbols[name] ?: error("Missing symbolL $name")
			node.right.symbol = symbol
			return symbol
		}

		error("Invalid dot node: ${node.left}")
	}



	private fun AstNode.resolveInt(): Long = when(this) {
		is IntNode    -> value
		is StringNode -> resolveStringImm(value.value)
		is UnaryNode  -> op.calculate(node.resolveInt())
		is BinaryNode -> op.calculate(left.resolveInt(), right.resolveInt())
		is SymNode    -> (symbol as? IntSymbol)?.value ?: error("Invalid int symbol: $symbol")
		is DotNode    -> (right.symbol as? IntSymbol)?.value ?: error("Invalid int symbol: ${right.symbol}")
		else          -> error("Invalid int node: $this")
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