package assembler

import core.binary.BinaryWriter
import assembler.Mnemonic.*

class Assembler(private val nodes: List<AstNode>) {


	private val writer = BinaryWriter()



	fun assemble(): ByteArray {
		return writer.trimmedBytes()
	}


}