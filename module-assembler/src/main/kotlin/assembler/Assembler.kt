package assembler

import core.binary.BinaryWriter

class Assembler(private val nodes: List<AstNode>) {


	private var pos = 0

	private val writer = BinaryWriter()



	fun assemble(): ByteArray {
		for(n in nodes) {
			if(n is InstructionNode) {

			}
		}

		return writer.trimmedBytes()
	}


}