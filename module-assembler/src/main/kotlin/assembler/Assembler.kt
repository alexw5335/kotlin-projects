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



	private val OperandNode?.flags get() = when(this) {
		null -> OperandFlags.NONE
		is AddressNode -> OperandFlags.MEM
		is ImmediateNode -> OperandFlags { IMM }
		is RegisterNode -> register.flags
	}




	private fun InstructionNode.encode(encoding: InstructionEncoding) {
		when(encoding.operands) {
			Operands.AL_IMM8  -> TODO()
			Operands.A_IMM    -> TODO()
			Operands.RM8_IMM8 -> TODO()
			Operands.RM_IMM   -> TODO()
			Operands.RM_IMM8  -> TODO()
			Operands.RM8_R8   -> TODO()
			Operands.RM_R     -> TODO()
			Operands.R8_RM8   -> TODO()
			Operands.R_RM     -> TODO()
		}
	}



	private fun InstructionNode.encode1(firstOpcode: Int, extension: Int) {
		fun error() : Nothing =
			error("Invalid encoding for instruction $mnemonic $operand1, $operand2, $operand3, $operand4")

		val flags =
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