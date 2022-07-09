package assembler

class ParseResult(val nodes: List<AstNode>) {


	fun printNodes() {
		for(n in nodes)
			println(n.printableString)
	}


}