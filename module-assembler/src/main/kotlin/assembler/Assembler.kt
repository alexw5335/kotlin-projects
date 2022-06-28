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


	val OperandNode.type get() = when(this) {
		is AddressNode -> Operand.MEM
		is ImmediateNode -> Operand.IMM
		is RegisterNode -> if(register is GP8Register) Operand.R8 else Operand.R
	}

	private fun InstructionNode.assemble() {
		val encodings = Instructions.map[mnemonic] ?: error("Unsupported mnemonic: $mnemonic")

		for(e in encodings) {
			println(e.operands)
		}
/*		when(mnemonic) {
			Mnemonic.ADD -> {
				if(operand1 == null || operand2 == null || operand3 != null || operand4 != null)
					error("Invalid instruction")
			}

			else -> error("Unsupported mnemonic: $mnemonic")
 		}*/
	}


}