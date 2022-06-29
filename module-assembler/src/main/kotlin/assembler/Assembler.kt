package assembler

import core.binary.BinaryWriter
import assembler.Mnemonic.*

class Assembler(private val nodes: List<AstNode>) {


	private val writer = BinaryWriter()



	fun assemble(): ByteArray {
		for(node in nodes)
			if(node is InstructionNode)
				node.assemble()

		return writer.trimmedBytes()
	}



	private fun InstructionNode.assemble() {
		when(mnemonic) {
			ADD  -> encode1(0x00, 0)
			OR   -> encode1(0x08, 1)
			ADC  -> encode1(0x10, 2)
			SBB  -> encode1(0x18, 3)
			AND  -> encode1(0x20, 4)
			SUB  -> encode1(0x28, 5)
			XOR  -> encode1(0x30, 6)
			CMP  -> encode1(0x38, 7)
			else -> error("Unsupported operand")
		}
	}



	private fun InstructionNode.encode1(startOpcode: Int, extension: Int) {
		fun error() : Nothing =
			error("Invalid encoding for instruction $mnemonic $operand1, $operand2, $operand3, $operand4")

		if(operand1 == null || operand2 == null || operand3 != null)
			error()

		when(operand1) {
			is RegisterNode -> {
				val r = (operand1.register as? GPRegister) ?: error()

				if(r.value == 0) {
					val imm = (operand2 as? ImmediateNode)?.value ?: error()

					when(r) {
						is GP8Register -> {
							writer.u8(startOpcode + 4)
							writer.s8(imm.toInt())
						}

						is GP16Register -> {
							writer.u8(0x66)
							writer.u8(startOpcode + 5)
							writer.s16(imm.toInt())
						}

						is GP32Register -> {
							writer.u8(startOpcode + 5)
							writer.s32(imm.toInt())
						}

						is GP64Register -> {
							writer.u8(0x48)
							writer.u8(startOpcode + 5)
							writer.s32(imm.toInt())
						}
					}
				} else {
					when(operand2) {

					}
				}
			}

			is AddressNode -> {

			}

			else -> error()
		}
	}


}