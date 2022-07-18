package assembler

import core.binary.BinaryWriter

class Assembler(parseResult: ParseResult) {


	private var pos = 0

	private val writer = BinaryWriter()

	private val nodes = parseResult.nodes

	private val symbols = parseResult.symbols

	private val symbolMap = HashMap<String, ResolvedSymbol>()



	fun assemble() {
		resolveSymbols()
	}



	private fun resolveSymbols() {
		for(symbol in symbols) when(symbol.type) {
			Symbol.Type.CONST ->
				symbolMap[symbol.name] = IntSymbol(symbol.name, symbol.node.calculateInt())
			Symbol.Type.LABEL ->
				error("Labels not yet supported.")
		}
	}



	private fun AstNode.calculateInt(): Long = when(this) {
		is IntNode    -> value
		is IdNode     -> (symbolMap[value] as? IntSymbol)?.value ?: error("Unrecognised integer symbol: $value")
		is UnaryNode  -> op.calculate(node.calculateInt())
		is BinaryNode -> op.calculate(left.calculateInt(), right.calculateInt())
		else          -> error("Cannot perform integer arithmetic on node: $this")
	}


}