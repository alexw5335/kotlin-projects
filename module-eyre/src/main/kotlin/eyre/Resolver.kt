package eyre

import kotlin.math.max

class Resolver(private val globalNamespace: Namespace, private val srcFiles: List<SrcFile>) {



	private val stack = SymStack()



	fun resolve() {
		stack.push(globalNamespace.symbols)
		srcFiles.forEach(::resolveFile)
		stack.pop()
	}



	private fun resolveFile(srcFile: SrcFile) {
		if(srcFile.resolving)
			error("Circular dependency found. Currently resolving files: ${srcFiles.filter { it.resolving }}")
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
			is ScopeEndNode  -> stack.pop()
			is StructNode    -> resolveStruct(node.symbol)
			is VarNode       -> resolveVar(node)
			is ConstNode     -> resolveConstNode(node)
			else             -> return
		}
	}



	private fun resolveExpression(node: AstNode) {
		when(node) {
			is BinaryNode -> { resolveExpression(node.left); resolveExpression(node.right) }
			is UnaryNode  -> resolveExpression(node)
			is DotNode    -> resolveDot(node)
			is SymNode    -> resolveSymNode(node)
			else          -> return
		}
	}



	private fun resolveConstNode(node: ConstNode) {
		val symbol = node.symbol
		symbol.value = node.value.resolveInt()
		symbol.resolved = true
	}



	private fun resolveConstSymbol(symbol: Symbol?): Long {
		if(symbol !is ConstSymbol) error("Expecting constant integer symbol, found: ${symbol?.name}")
		if(symbol.resolved) return symbol.value
		if(symbol.srcFile.resolving) error("Constant not yet initialised: ${symbol.name}")
		resolveFile(symbol.srcFile)
		return symbol.value
	}



	private fun AstNode.resolveInt(): Long = when(this) {
		is IntNode    -> value
		is UnaryNode  -> op.calculate(node.resolveInt())
		is BinaryNode -> op.calculate(left.resolveInt(), right.resolveInt())
		is SymNode    -> resolveConstSymbol(resolveSymNode(this))
		is DotNode    -> resolveConstSymbol(resolveDot(this))
		else          -> error("Invalid node: $this")
	}



	private fun resolveDot(node: DotNode): Symbol {
		val name = node.right.name

		if(node.left is SymNode) {
			val scope = stack[node.left.name] as? ScopedSymbol ?: error("Expecting scoped symb")
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
			node.symbol.type = invoker as? StructSymbol ?: error("Invalid invoker symbol: $invoker")
		} else {
			resolveExpression(node.value)
		}
	}



	private fun resolveStruct(symbol: StructSymbol) {
		if(symbol.resolved) return

		for(member in symbol.members)
			member.type = stack[member.typeName] as? Type
				?: error("Invalid type: ${member.typeName}")

		var offset = 0
		var maxAlignment = 0

		for(member in symbol.members) {
			val type = member.type.checkResolved()
			val alignment = type.size
			maxAlignment = max(alignment, maxAlignment)
			offset = (offset + alignment - 1) and -alignment
			member.offset = offset
			offset += alignment
		}

		symbol.size = (offset + maxAlignment - 1) and -maxAlignment
	}



	private fun Type.checkResolved(): Type {
		if(resolved) return this

		if(this is StructSymbol) {
			resolveStruct(this)
		} else {
			error("Invalid type: $this")
		}

		return this
	}

}