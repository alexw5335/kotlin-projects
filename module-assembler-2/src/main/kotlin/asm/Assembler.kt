package asm

import core.binary.BinaryWriter

class Assembler(
	private val nodes: List<AstNode>,
	private val symbols: Map<String, Symbol>
) {


	private val writer = BinaryWriter()

	private fun error(): Nothing = error("Invalid encoding")

	data class Relocation(val pos: Int, val width: Width?, val value: AstNode)

	private val relocations = ArrayList<Relocation>()



	/*
	Context
	 */



	private lateinit var operands: Operands

	private lateinit var group: InstructionGroup

	private lateinit var encoding: Instruction

	private lateinit var width: Width

	private lateinit var immediateWidth: Width



	private var modrm = ModRM()

	private var sib = Sib()

	private var rex = Rex()

	private var immediate = 0L

	private var disp = 0

	private var hasModrm = false

	private var hasSib = false

	private var hasImmediate = false

	private var hasDisp = false

	private var dispNode: AstNode? = null



	private fun clearContext() {
		modrm.value  = 0
		sib.value    = 0
		rex.value    = 0
		hasModrm     = false
		hasSib       = false
		hasImmediate = false
		hasDisp      = false
		dispNode     = null
	}



	/*
	Assembly
	 */



	fun assemble(): ByteArray {
		for(node in nodes) {
			when(node) {
				is InstructionNode -> assemble(node)
				is LabelNode       -> resolveLabel(node)
				else               -> { }
			}
		}

		for(relocation in relocations) {
			val value = resolveInt(relocation.value)

			when(relocation.width) {
				Width.BIT8 -> writer.s8(relocation.pos, value.toInt())
				Width.BIT16 -> writer.s16(relocation.pos, value.toInt())
				Width.BIT32 -> writer.s32(relocation.pos, value.toInt())
				Width.BIT64 -> writer.s64(relocation.pos, value)
				else -> error()
			}
		}

		return writer.trimmedBytes()
	}



	private fun resolveLabel(node: LabelNode) {
		symbols[node.name]!!.data = LabelSymbolData(writer.pos)
	}



	private fun assemble(node: InstructionNode) {
		clearContext()

		group = mnemonicsToInstructions[node.mnemonic]!!

		when {
			node.op1 == null -> error()
			node.op2 == null -> error()
			node.op3 == null -> operands2(node)
		}

		if(operands !in group) error()
		encoding = group[operands]

		if(width.is64) rex.w = 1
		else if(width.is16) writer.u8(0x66)
		if(rex.value != 0) writer.u8(rex.value or 0b0100_0000)

		writer.u8(writer.pos, encoding.opcode)
		writer.pos += encoding.opcodeLength

		if(encoding.extension >= 0) {
			modrm.value = modrm.value or encoding.extension
			hasModrm = true
		}

		if(hasModrm) writer.u8(modrm.value)

		if(hasSib) writer.u8(sib.value)

		if(hasDisp) {
			if(dispNode != null)
				relocations.add(Relocation(
					writer.pos,
					if(modrm.mod == 0b01) Width.BIT8 else Width.BIT32,
					dispNode!!
				))

			if(modrm.mod == 0b01)
				writer.s8(disp)
			else
				writer.s32(disp)
		}

		if(hasImmediate) when(immediateWidth) {
			Width.BIT8  -> writer.s8(immediate.toInt())
			Width.BIT16 -> writer.s16(immediate.toInt())
			Width.BIT32 -> writer.s32(immediate.toInt())
			Width.BIT64 -> writer.s32(immediate.toInt())
			else        -> error()
		}
	}



	private fun resolveInt(node: AstNode): Long = when(node) {
		is UnaryNode  -> resolveInt(node.node)
		is BinaryNode -> node.op.calculateInt(resolveInt(node.left), resolveInt(node.right))
		is IntNode    -> node.value
		is IdNode     -> (symbols[node.name]?.data as? IntSymbolData)?.value ?: error("Unresolved symbol: '${node.name}'")
		is LabelNode  -> (symbols[node.name]?.data as? LabelSymbolData)?.value?.toLong() ?: error("Unresolved symbol: '${node.name}'")
		else          -> error("Cannot determine constant int value from ast node '$node'")
	}



	private fun hasLabel(node: AstNode): Boolean = when(node) {
		is UnaryNode  -> hasLabel(node.node)
		is BinaryNode -> hasLabel(node.left) || hasLabel(node.right)
		is LabelNode  -> true
		else          -> false
	}



	private fun memoryOperand(operand: MemoryNode) {
		if(operand.disp != null) {
			hasDisp = true

			if(hasLabel(operand.disp)) {
				dispNode = operand.disp
				disp = 0
			} else {
				disp = resolveInt(operand.disp).toInt()
			}
		}

		if(hasDisp) {
			if(disp in Byte.MIN_VALUE..Byte.MAX_VALUE)
				modrm.mod = 0b01
			else
				modrm.mod = 0b10
		}

		// RIP-relative addressing
		if(operand.rel) {
			modrm.mod = 0b00
			modrm.rm = 0b101
			return
		}

		// SIB addressing, e.g. [rax + rcx * 2 + 10]
		if(operand.index != null) {
			hasSib = true
			modrm.rm = 0b100
			sib.index = operand.index.value
			rex.x = operand.index.rex

			if(operand.base != null) {
				sib.base = operand.base.value
				rex.b = operand.base.rex
			} else {
				sib.base = 0b101
			}

			if(operand.scale.countOneBits() != 1) error()
			sib.scale = operand.scale.countTrailingZeroBits()
			return
		}

		// Absolute displacement, e.g. [10]
		if(operand.base == null) {
			if(!hasDisp) error()
			modrm.mod = 0b00
			modrm.rm = 0b100
			hasSib = true
			sib.base = 0b101
			sib.index = 0b100
			return
		}

		// indirect addressing, e.g. [rax], [rax + 10]
		rex.b = operand.base.rex
		modrm.rm = operand.base.value
	}



	/*
	Two Operands
	 */



	private fun manualInstruction(mnemonic: Mnemonic) {
		when(mnemonic) {
			Mnemonic.CBW  -> writer.u16(0x66_98)
			Mnemonic.CWDE -> writer.u8(0x98)
			Mnemonic.CDQE -> writer.u16(0x48_F8)
			Mnemonic.CLC  -> writer.u8(0xF8)
			Mnemonic.CLD  -> writer.u8(0xFC)
			Mnemonic.CLI  -> writer.u8(0xFA)
			Mnemonic.CLTS -> writer.u16(0x0F_06)
			Mnemonic.CMC  -> writer.u8(0xF5)
			else          -> { }
		}
	}



	private fun instructionBSWAP(node: InstructionNode) {
		val reg = (node.op1 as? RegisterNode)?.value ?: error()
		if(reg.width == Width.BIT16) error()
		else if(reg.width == Width.BIT64) rex.w = 1
		val opcode = 0x0F_C8 + reg.value
		rex.b = reg.rex
		if(rex.value != 0) writer.u8(rex.value and 0b0100_000)
		writer.u16(opcode)
	}



	private fun operands2(node: InstructionNode) {
		when(val op1 = node.op1) {
			is RegisterNode      -> when(val op2 = node.op2) {
				is RegisterNode  -> operands2RR(op1.value, op2.value)
				is ImmediateNode -> operands2RI(op1.value, op2)
				is MemoryNode    -> operands2RM(op1.value, op2)
				else             -> error()
			}
			is MemoryNode        -> when(val op2 = node.op2) {
				is RegisterNode  -> operands2MR(op1, op2.value)
				is ImmediateNode -> operands2MI(op1, op2)
				else             -> error()
			}
			else                 -> error()
		}
	}



	private fun operands2MR(op1: MemoryNode, op2: Register) {
		hasModrm = true
		modrm.reg = op2.value
		rex.r = op2.rex
		width = op2.width

		if(op1.width != null && op1.width != op2.width) error()

		operands = if(width.is8) Operands.RM8_R8 else Operands.RM_R

		memoryOperand(op1)
	}



	private fun operands2MI(op1: MemoryNode, op2: ImmediateNode) {
		hasModrm = true
		hasImmediate = true
		immediate = resolveInt(op2.value)
		width = op1.width ?: error()
		immediateWidth = width

		memoryOperand(op1)

		when {
			else -> {
				operands = if(width.is8) Operands.RM8_IMM8 else Operands.RM_IMM
			}
		}
	}



	private fun operands2RM(op1: Register, op2: MemoryNode) {
		hasModrm = true
		modrm.reg = op1.value
		rex.r = op1.rex
		width = op1.width

		if(op2.width != null && op1.width != op2.width) error()

		operands = if(width.is8) Operands.R8_RM8 else Operands.R_RM

		memoryOperand(op2)
	}



	private fun operands2RI(op1: Register, op2: ImmediateNode) {
		modrm.mod = 0b11
		modrm.rm = op1.value
		hasModrm = true
		hasImmediate = true
		immediate = resolveInt(op2.value)
		rex.b = op1.rex
		width = op1.width

		when {
			Specifier.RM_IMM8 in group && immediate in Byte.MIN_VALUE..Byte.MAX_VALUE && (Specifier.A_IMM !in group || op1 != Register.AL) -> {
				operands = if(width.is8) Operands.RM8_IMM8 else Operands.RM_IMM8
				immediateWidth = Width.BIT8
			}

			Specifier.RM_ONE in group && immediate == 1L -> {
				operands = if(width.is8) Operands.RM8_ONE else Operands.RM_ONE
				hasImmediate = false
			}

			Specifier.A_IMM in group && op1.value == 0 -> {
				operands = if(width.is8) Operands.AL_IMM8 else Operands.A_IMM
				hasModrm = false
				immediateWidth = width
			}

			else -> {
				operands = if(width.is8) Operands.RM8_IMM8 else Operands.RM_IMM
				immediateWidth = width
			}
		}
	}



	private fun operands2RR(op1: Register, op2: Register) {
		hasModrm = true
		modrm.rm = op1.value
		modrm.mod = 0b11
		rex.b = op1.rex
		width = op1.width

		when {
			Specifier.RM_CL in group && op1 == Register.CL -> {
				operands = if(width.is8) Operands.RM8_CL else Operands.RM_CL
			}

			else -> {
				operands = if(width.is8) Operands.RM8_R8 else Operands.RM_R
				rex.r = op2.rex
				modrm.reg = op2.value
			}
		}
	}


}