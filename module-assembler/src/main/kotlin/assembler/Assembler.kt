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
		null             -> OperandFlags.NONE
		is MemoryNode   -> OperandFlags.M
		is ImmediateNode -> if(value in -128..127) OperandFlags.IMM8 else OperandFlags.IMM
		is RegisterNode  -> register.flags
	}



	private fun InstructionNode.encode(encoding: InstructionEncoding) {
		val width = when(encoding.operands) {
			Operands.AL_IMM8  -> 1
			Operands.A_IMM    -> op1!!.width
			Operands.RM8_IMM8 -> 1
			Operands.RM_IMM   -> op1!!.width
			Operands.RM_IMM8  -> op1!!.width
			Operands.RM8_R8   -> 1
			Operands.RM_R     -> op1!!.width
			Operands.R8_RM8   -> 1
			Operands.R_RM     -> op1!!.width
		}

		when(width) {
			2 -> println(0x66)
			4 -> println(0x48)
		}

		println(encoding.opcode)

		var modrm = 0

		if(encoding.extension > 0) modrm = modrm or encoding.extension

		when(encoding.operands) {
			Operands.AL_IMM8  -> { }
			Operands.A_IMM    -> op1!!.width
			Operands.RM8_IMM8 -> 1
			Operands.RM_IMM   -> op1!!.width
			Operands.RM_IMM8  -> op1!!.width
			Operands.RM8_R8   -> 1
			Operands.RM_R     -> op1!!.width
			Operands.R8_RM8   -> 1
			Operands.R_RM     -> op1!!.width
		}

	}



	private fun InstructionNode.error(): Nothing =
		error("Invalid encoding for instruction $mnemonic $op1, $op2, $op3, $op4")



	private fun InstructionNode.encode1(firstOpcode: Int, extension: Int) {
		val flags = (op1.flags shl 0) + (op2.flags shl 16) + (op3.flags shl 32) + (op4.flags shl 48)

		val operands = when(flags) {
			in Operands.AL_IMM8.flags -> Operands.AL_IMM8
			in Operands.RM_IMM8.flags -> Operands.RM_IMM8
			in Operands.A_IMM.flags   -> Operands.A_IMM
			in Operands.RM_IMM8.flags -> Operands.RM_IMM8
			in Operands.RM8_R8.flags  -> Operands.RM8_R8
			in Operands.RM_R.flags    -> Operands.RM_R
			in Operands.R8_RM8.flags  -> Operands.R8_RM8
			in Operands.R_RM.flags    -> Operands.R_RM
			else -> error()
		}

		encode(InstructionEncoding(firstOpcode, operands, extension))
	}


}