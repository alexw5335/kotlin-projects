package assembler

import core.binary.BinaryWriter

class Assembler(parseResult: ParseResult) {


	private var pos = 0

	private val writer = BinaryWriter()

	private val nodes = parseResult.nodes

	private val symbols = parseResult.symbols



	fun assemble() {
		while(pos < nodes.size) {
			val node = nodes[pos++]

			if(node is ConstNode) continue
			if(node is LabelNode) error("Labels not yet supported")
			if(node !is InstructionNode) error("Expected instruction")

			chooseEncodings(node)
		}
	}


	//private fun chooseOperandType(node: AstNode) = when(node) {
	//	is ImmediateNode ->
	//}

	private fun chooseEncodings(node: InstructionNode) {
		val encodings = Instructions.get(node.mnemonic)
			?: error("Unrecognised mnemonic: ${node.mnemonic}")
	}


}