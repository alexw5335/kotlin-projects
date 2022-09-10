package asm

import core.binary.BinaryWriter

class Assembler(
	private val nodes: List<AstNode>,
	private val symbols: Map<String, Symbol>
) {


	private val writer = BinaryWriter()

	private fun error(): Nothing = error("Invalid encoding")



	/*
	Context
	 */



	private lateinit var operands: Operands

	private lateinit var encoding: Instruction

	private lateinit var width: Width

	private var rex = Rex()

	private var modrm = ModRM()

	private var sib = Sib()

	private var immediate = 0L

	private var hasImmediate = false

	private lateinit var immediateWidth: Width



	/*
	Assembly
	 */



	fun assemble(): ByteArray {
		for(node in nodes)
			if(node is InstructionNode)
				assemble(node)

		return writer.trimmedBytes()
	}



	private fun assemble(node: InstructionNode) {
		val group = mnemonicsToInstructions[node.mnemonic]!!

		when {
			node.op1 == null -> error()
			node.op2 == null -> error()
			node.op3 == null -> operands2(group, node)
		}

		if(group.operandsFlags and operands.bit == 0) error()
		val encoding = group.instructions[(group.operandsFlags and (operands.bit - 1)).countOneBits()]
		if(width.is64) rex.w = 1

		if(encoding.extension >= 0) {
			modrm.rm = encoding.extension
			if(!modrm.present) modrm.present = true
		}

		if(width.is16) writer.u8(0x66)
		if(rex.present) writer.u8(rex.finalValue)
		writer.u8(writer.pos, encoding.opcode)
		writer.pos += encoding.opcodeLength
		if(modrm.present) writer.u8(modrm.value)
		if(sib.present) writer.u8(sib.value)
		if(hasImmediate) when(immediateWidth) {
			Width.BIT8 -> writer.s8(immediate.toInt())
			Width.BIT16 -> writer.s16(immediate.toInt())
			Width.BIT32 -> writer.s32(immediate.toInt())
			Width.BIT64 -> writer.s64(immediate)
			else -> error()
		}
	}



	private fun operands2(group: InstructionGroup, node: InstructionNode) {
		when(node.op1) {
			is RegisterNode -> when(node.op2) {
				is RegisterNode -> operands2RR(group, node.op1.value, node.op2.value)
				is ImmediateNode -> operands2RI(group, node.op1.value, node.op2)
				else -> error()
			}
			else -> error()
		}
	}



	private fun resolveInt(node: AstNode): Long = when(node) {
		is UnaryNode  -> resolveInt(node.node)
		is BinaryNode -> node.op.calculateInt(resolveInt(node.left), resolveInt(node.right))
		is IntNode    -> node.value
		is IdNode     -> (symbols[node.value]?.data as? IntSymbolData)?.value ?: error("Unresolved symbol: '${node.value}'")
		else          -> error("Cannot determine constant int value from ast node '$node'")
	}



	private fun writePrefixesAndOpcode(group: InstructionGroup, operands: Operands) {
		if(group.operandsFlags and operands.bit == 0) error()
		val encoding = group.instructions[(group.operandsFlags and (operands.bit - 1)).countOneBits()]
		if(width.is64) rex.w = 1

		if(encoding.extension >= 0) {
			modrm.rm = encoding.extension
			if(!modrm.present) modrm.present = true
		}
	}



	private fun operands2RI(group: InstructionGroup, op1: Register, op2: ImmediateNode) {
		width = op1.width

		rex.b = op1.rex

		modrm.mod = 0b11
		modrm.present = true

		hasImmediate = true
		immediate = resolveInt(op2.value)
		width = op1.width

		operands = when {
			Specifier.RM_IMM8.inFlags(group.specifierFlags) && immediate in Byte.MIN_VALUE..Byte.MAX_VALUE -> {
				immediateWidth = Width.BIT8
				modrm.rm = op1.value
				rex.b = op1.rex
				if(op1.width.is8) Operands.RM8_IMM8 else Operands.RM_IMM8
			}

			Specifier.RM_ONE.inFlags(group.specifierFlags) && immediate == 1L -> {
				hasImmediate = false
				modrm.rm = op1.value
				rex.b = op1.rex
				if(op1.width.is8) Operands.RM8_ONE else Operands.RM_ONE
			}

			Specifier.A_IMM.inFlags(group.specifierFlags) && op1.value == 0 -> {
				immediateWidth = op1.width
				modrm.present = false
				if(op1.width.is8) Operands.AL_IMM8 else Operands.A_IMM
			}

			else -> {
				hasImmediate = true
				immediateWidth = op1.width
				modrm.rm = op1.value
				rex.b = op1.rex
				if(op1.width.is8) Operands.RM8_IMM8 else Operands.RM_IMM
			}
		}
	}



	private fun operands2RR(group: InstructionGroup, op1: Register, op2: Register) {
		width = op1.width
		modrm.present = true

		rex.b = op1.rex
		rex.r = op2.rex

		modrm.mod = 0b11
		modrm.reg = op2.value
		modrm.rm = op1.value

		operands = when {
			Specifier.RM_CL.inFlags(group.specifierFlags) && op1 == Register.CL -> {
				if(op1.width.is8) Operands.RM8_CL else Operands.RM_CL
			}

			width.is8 -> {
				if(op1.width != op2.width) error()
				Operands.RM8_R8
			}

			else -> {
				if(op1.width != op2.width) error()
				Operands.RM_R
			}
		}
	}


}