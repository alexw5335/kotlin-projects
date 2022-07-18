package assembler

import core.binary.BinaryWriter
import assembler.Mnemonic.*

class Assembler(parseResult: ParseResult) {


	private var pos = 0

	private val writer = BinaryWriter()

	private val nodes = parseResult.nodes

	private val symbols = parseResult.symbols



	fun assemble(): ByteArray {
		for(symbol in symbols) {
			when(symbol.type) {
				Symbol.Type.CONST -> {

				}
			}
		}
		/*while(pos < nodes.size) {
			when(val node = nodes[pos++]) {
				is InstructionNode -> { }
				else -> continue
			}
		}*/
		return writer.trimmedBytes()
	}



	private fun chooseEncoding1(instruction: InstructionNode): Int {
		if(instruction.op3 != null || instruction.op4 != null)
			error("Invalid encoding")

		val op1 = instruction.op1 ?: error("Invalid encoding")
		val op2 = instruction.op2 ?: error("Invalid encoding")

		if(op1 is RegisterNode) {
			val reg = op1.value
			if(reg.value == 0) {
				if(op2 !is ImmediateNode) error("Invalid encoding")
				val imm = op2.value.calculate()
				if(reg == Register.AL) {
					if(imm in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return 0
					error("Imm8 out of range")
				}
				if(reg == Register.AX) {
					if(imm in Short.MIN_VALUE..Short.MAX_VALUE)
						return 1
					error("Imm16 out of range")
				}
				if(reg == Register.EAX) {
					if(imm in Int.MIN_VALUE..Int.MAX_VALUE)
						return 2
					error("Imm32 out of range")
				}
				if(reg == Register.RAX) {
					if(imm in Int.MIN_VALUE..Int.MAX_VALUE)
						return 3
					error("Imm32 out of range")
				}
				error("Invalid encoding")
			}
		}

		error("Invalid encoding")
	}



	private fun assemble(instruction: InstructionNode) {
		val type1 = OperandType.M64
		val type2 = OperandType.R32
	}


}