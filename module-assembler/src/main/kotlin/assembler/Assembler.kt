package assembler

import core.binary.BinaryWriter

class Assembler(private val nodes: List<AstNode>) {


	private var pos = 0

	private val writer = BinaryWriter()



	fun assemble(): ByteArray {
		for(node in nodes) {
			if(node is InstructionNode) {
				val encodings = Instructions.getEncodings(node.mnemonic) ?: error("Invalid mnemonic: $node")
				for(encoding in encodings) {

				}
			}
		}

		return writer.trimmedBytes()
	}


}