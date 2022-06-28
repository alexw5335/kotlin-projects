package assembler

import core.binary.BinaryWriter
import core.hex8

class Assembler(private val nodes: List<AstNode>) {


	private var pos = 0

	private val writer = BinaryWriter()


	fun assemble(): ByteArray {
		for(node in nodes)
			if(node is InstructionNode)
				node.assemble()

		return writer.trimmedBytes()
	}

	val OperandNode?.flags get() = when(this) {
		null             -> OperandFlags.NONE
		is AddressNode   -> OperandFlags.MEM
		is ImmediateNode -> OperandFlags.compose { IMM + IMM8 }
		is RegisterNode  -> when (register) {
			GP8Register.AL    -> OperandFlags.compose { AL + R8 }
			GP16Register.AX,
			GP32Register.EAX,
			GP64Register.RAX  -> OperandFlags.compose { A + R }
			is GP8Register    -> OperandFlags.compose { R8 }
			else              -> OperandFlags.compose { R }
		}
	}

	val Operand.flags get() = when(this) {
		Operand.NONE -> OperandFlags.NONE
		Operand.A    -> OperandFlags.compose { A }
		Operand.AL   -> OperandFlags.compose { AL }
		Operand.R    -> OperandFlags.compose { A + R }
		Operand.R8   -> OperandFlags.compose { AL + R8 }
		Operand.RM   -> OperandFlags.compose { A + R + MEM }
		Operand.RM8	 -> OperandFlags.compose { AL + R8 + MEM }
		Operand.IMM8 -> OperandFlags.compose { IMM8 }
		Operand.IMM  -> OperandFlags.compose { IMM8 + IMM }
	}

	private fun InstructionNode.assemble() {
		val encodings = Instructions.map[mnemonic] ?: error("Unsupported mnemonic: $mnemonic")

		val flags =
			(operand1.flags shl 0)  or
			(operand2.flags shl 8)  or
			(operand3.flags shl 16) or
			(operand4.flags shl 24)

		for(e in encodings) {
			val instructionFlags =
				(e.operand1.flags shl 0)  or
				(e.operand2.flags shl 8)  or
				(e.operand3.flags shl 16) or
				(e.operand4.flags shl 24)

			if(instructionFlags in flags) {
				println("encoding found: ${e.opcode.hex8} $mnemonic ${e.operand1} ${e.operand2}")
			}
		}
	}


}