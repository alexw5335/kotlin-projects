package assembler

import core.binary.BinaryWriter

class Assembler(private val nodes: List<AstNode>) {


	private var pos = 0

	private val writer = BinaryWriter()



	fun assemble(): ByteArray {
		for(node in nodes)
			if(node is InstructionNode)
				node.assemble()

		return writer.trimmedBytes()
	}



	private fun InstructionNode.assemble() {
		when(mnemonic) {
			Mnemonic.ADD -> {
				if(operand1 == null || operand2 == null || operand3 != null || operand4 != null)
					error("Invalid instruction")
			}

			else -> error("Unsupported mnemonic: $mnemonic")
 		}
	}

	/*
		04    ADD  AL  IMM8
		05    ADD  A   IMM
		80/0  ADD  RM8 IMM8
		81/0  ADD  RM  IMM
		83/0  ADD  RM  IMM8
		00    ADD  RM8 R8
		01    ADD  RM  R
		02    ADD  R8  RM8
		03    ADD  R   RM
	 */


}