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

	/*
	Instruction precedence?

	add rax, 10 should use RM IMM8
	add rax, 1000 should use A IMM

	How to handle opcode extensions?
	Cannot just let the Operands encode everything.
	 */



	private val OperandNode?.type get() = when(this) {
		null             -> OperandType.NONE
		is AddressNode   -> OperandType.MEM
		is ImmediateNode -> if(value in -128.. 127) OperandType.IMM8 else OperandType.IMM
		is RegisterNode  -> register.type
	}




	private fun InstructionNode.encode(encoding: InstructionEncoding) {
		//when(encoding.operands) { }
	}



	private fun InstructionNode.encode1(firstOpcode: Int, extension: Int) {
		fun error() : Nothing =
			error("Invalid encoding for instruction $mnemonic $operand1, $operand2, $operand3, $operand4")

		val value = (operand1.type)
			(operand1.flags.value.toLong() shl 0) or
			(operand2.flags.value.toLong() shl 16) or
			(operand3.flags.value.toLong() shl 32) or
			(operand4.flags.value.toLong() shl 48)

		fun matches(operands: Operands) = operands.flags and flags == operands.flags

		Instructions.ADD
			.filter { matches(it.operands) }
			.forEach(::println)

	}


}