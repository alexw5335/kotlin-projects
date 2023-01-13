package eyre

import kotlin.math.max

class Resolver(private val context: EyreContext) {


	private val srcFiles = context.srcFiles

	private val stack = SymStack()



	fun resolve() {
		stack.push(context.globalNamespace.symbols)
		srcFiles.forEach(::resolveFile)
		stack.pop()
	}



	private fun resolveFile(srcFile: SrcFile) {
		if(srcFile.resolving)
			error("Circular dependency found. Currently resolving files: ${srcFiles.filter { it.resolving }}")
		else if(srcFile.resolved)
			return

		srcFile.resolving = true

		for(node in srcFile.nodes) {
			when(node) {
				is NamespaceNode -> stack.push(node.symbol.symbols)
				is ScopeEndNode  -> stack.pop()
				is StructNode    -> resolveStruct(node)
				is VarNode       -> resolveVar(node)
				is ConstNode     -> resolveConstNode(node)
				is EnumNode      -> resolveEnum(node)
				else             -> continue
			}
		}

		srcFile.resolving = false
		srcFile.resolved = true
	}



	/**
	 * Fills in any [SymNode] nodes in an expression.
	 */
	private fun resolveExpressionSymbols(node: AstNode) {
		when(node) {
			is BinaryNode -> { resolveExpressionSymbols(node.left); resolveExpressionSymbols(node.right) }
			is UnaryNode  -> resolveExpressionSymbols(node)
			is DotNode    -> resolveDot(node)
			is SymNode    -> resolveSymNode(node)
			else          -> return
		}
	}



	private fun resolveEnum(node: EnumNode) {
		for(entry in node.entries)
			if(entry.value != NullNode)
				entry.symbol.value = entry.value.resolveInt()
	}



	private fun resolveConstNode(node: ConstNode) {
		val symbol = node.symbol
		symbol.value = node.value.resolveInt()
		symbol.resolved = true
	}



	private fun AstNode.resolveInt(): Long = when(this) {
		is IntNode    -> value
		is UnaryNode  -> op.calculate(node.resolveInt())
		is BinaryNode -> op.calculate(left.resolveInt(), right.resolveInt())
		is SymNode    -> resolveConstSymbol(resolveSymNode(this))
		is DotNode    -> resolveConstSymbol(resolveDot(this))
		else          -> error("Invalid node: $this")
	}



	private fun resolveConstSymbol(symbol: Symbol?): Long {
		if(symbol !is ConstSymbol) error("Expecting constant integer symbol, found: ${symbol?.name}")
		if(symbol.resolved) return symbol.value
		if(symbol.srcFile.resolving) error("Constant not yet initialised: ${symbol.name}")
		resolveFile(symbol.srcFile)
		return symbol.value
	}




	private fun resolveDot(node: DotNode): Symbol {
		val name = node.right.name

		if(node.left is SymNode) {
			val scope = stack[node.left.name]
			if(scope !is ScopedSymbol) error("expecting scoped symbol, found: $scope, ${node.left.name}")
			node.left.symbol = scope
			val symbol = scope.symbols[name] ?: error("Missing symbol: $name")
			node.right.symbol = symbol
			return symbol
		}

		if(node.left is DotNode) {
			val left = resolveDot(node.left)
			val scope = left as? ScopedSymbol ?: error("Expecting scoped symbol: $left")
			val symbol = scope.symbols[name] ?: error("Missing symbolL $name")
			node.right.symbol = symbol
			return symbol
		}

		error("Invalid dot node: ${node.left}")
	}



	private fun resolveSymNode(node: SymNode): Symbol {
		val symbol = stack[node.name] ?: error("Unrecognised symbol: ${node.name}")
		node.symbol = symbol
		return symbol
	}



	private fun resolveSymbol(node: AstNode): Symbol {
		return if(node is DotNode) {
			resolveDot(node)
			node.right.symbol!!
		} else if(node is SymNode) {
			resolveSymNode(node)
		} else {
			error("Invalid node: $node")
		}
	}



	private fun resolveVar(node: VarNode) {
		if(node.value is InvokeNode) {
			val invoker = resolveSymbol(node.value.invoker)
			node.symbol.type = invoker as? Type ?: error("Invalid invoker symbol: $invoker")
		} else {
			resolveExpressionSymbols(node.value)
		}
	}



	private fun getType(name: Intern) = stack[name] as? Type ?: error("Invalid type: ${name}")



	private fun resolveStruct(node: StructNode) {
		for(member in node.members)
			member.symbol.type = getType(member.type)
	}



	private fun resolveTypeSize(type: Type) {
		if(type is StructSymbol) {
			resolveStructSize(type)
		} else {
			error("Invalid type")
		}
	}



	private fun resolveStructSize(symbol: StructSymbol) {
		if(symbol.isSizeResolved) return

		var offset = 0
		var maxAlignment = 0

		for(member in symbol.members) {
			val type = member.type
			if(!type.isSizeResolved) resolveTypeSize(type)
			val size = type.size
			val alignment = size.coerceAtMost(8)
			maxAlignment = max(alignment, maxAlignment)
			offset = (offset + alignment - 1) and -alignment
			member.offset = offset
			offset += size
		}

		symbol.isSizeResolved = true
		symbol.size = (offset + maxAlignment - 1) and -maxAlignment
		println("${symbol.name} ${symbol.size}")
	}




}