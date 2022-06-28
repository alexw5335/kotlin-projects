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



	/*
	Instruction precedence?

	add rax, 10 should use RM IMM8
	add rax, 1000 should use A IMM
	 */

	private val OperandNode?.flags get() = when(this) {
		null             -> OperandFlags.NONE
		is AddressNode   -> OperandFlags.MEM
		is ImmediateNode -> if(value in -128..127) OperandFlags.IMM8 else OperandFlags.IMM
		is RegisterNode  -> when (register) {
			GP8Register.AL    -> OperandFlags { AL }
			GP16Register.AX,
			GP32Register.EAX,
			GP64Register.RAX  -> OperandFlags { A }
			is GP8Register    -> OperandFlags { REG8 }
			else              -> OperandFlags { REG }
		}
	}



	private fun InstructionNode.assemble() {
		val encodings = Instructions.map[mnemonic] ?: error("Unsupported mnemonic: $mnemonic")

		val flags =
			(operand1.flags shl 0)  or
			(operand2.flags shl 8)  or
			(operand3.flags shl 16) or
			(operand4.flags shl 24)

		for(e in encodings) {
			if(flags in e.operands.flags) {
				println("encoding found: ${e.opcode.hex8} $mnemonic ${e.operands}")

				if(e.operands == Operands.AL_IMM8) {
					println(e.opcode.hex8)
					println((operand2 as ImmediateNode).value.toInt().hex8)
				} else if(e.operands == Operands.A_IMM) {
					when((operand1 as RegisterNode).register) {
						is GP16Register -> println("66")
						is GP64Register -> println("48")
					}
					println(e.opcode.hex8)
					println((operand2 as ImmediateNode).value.toInt().hex8)
				}
			}
		}
	}


}