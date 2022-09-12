package asm

import core.bin
import core.bin233
import core.binary.BinaryWriter
import core.hexFull

class Assembler(
	private val nodes: List<AstNode>,
	private val symbols: Map<String, Symbol>
) {


	private val writer = BinaryWriter()

	private fun error(): Nothing = error("Invalid encoding")



	/*
	Assembly
	 */



	private lateinit var group: InstructionGroup



	fun assemble(): ByteArray {
		for(node in nodes)
			if(node is InstructionNode)
				assemble(node)

		return writer.trimmedBytes()
	}



	private fun assemble(node: InstructionNode) {
		group = mnemonicsToInstructions[node.mnemonic]!!

		when {
			node.op1 == null -> error()
			node.op2 == null -> error()
			node.op3 == null -> operands2(node)
		}
	}



	private fun operands2(node: InstructionNode) {
		when(node.op1) {
			is RegisterNode      -> when(node.op2) {
				is RegisterNode  -> operands2RR(node.op1.value, node.op2.value)
				is ImmediateNode -> operands2RI(node.op1.value, node.op2)
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



	private fun encode(operands: Operands, width: Width, rex: Int, modrm: Int) {
		if(operands !in group) error()
		val encoding = group[operands]

		if(width.is64) {
			writer.u8(rex or 0b0100_1000)
		} else {
			if(width.is16) writer.u8(0x66)
			if(rex != 0) writer.u8(rex or 0b0100_0000)
		}

		writer.u8(writer.pos, encoding.opcode)
		writer.pos += encoding.opcodeLength

		if(modrm >= 0) {
			if(encoding.extension >= 0)
				writer.u8(modrm or encoding.extension)
			else
				writer.u8(modrm)
		} else if(encoding.extension >= 0) {
			writer.u8(encoding.extension)
		}
	}



	private fun imm(width: Width, immediate: Int) {
		when(width) {
			Width.BIT8  -> writer.s8(immediate)
			Width.BIT16 -> writer.s16(immediate)
			Width.BIT32 -> writer.s32(immediate)
			Width.BIT64 -> writer.s64(immediate.toLong())
			else        -> error()
		}
	}



	private fun operands2RM(op1: Register, op2: MemoryNode) {

	}



	private fun operands2RI(op1: Register, op2: ImmediateNode) {
		val immediate = resolveInt(op2.value)

		when {
			Specifier.RM_IMM8 in group && immediate in Byte.MIN_VALUE..Byte.MAX_VALUE && (Specifier.A_IMM !in group || op1 != Register.AL) -> {
				encode(
					operands = if(op1.width.is8) Operands.RM8_IMM8 else Operands.RM_IMM8,
					width    = op1.width,
					rex      = op1.rex,
					modrm    = 0b11_000_000 or op1.value
				)
				writer.u8(immediate.toInt())
			}

			Specifier.RM_ONE in group && immediate == 1L -> {
				encode(
					operands = if(op1.width.is8) Operands.RM8_ONE else Operands.RM_ONE,
					width    = op1.width,
					rex      = op1.rex,
					modrm    = 0b11_000_000 or op1.value
				)
			}

			Specifier.A_IMM in group && op1.value == 0 -> {
				encode(
					operands = if(op1.width.is8) Operands.AL_IMM8 else Operands.A_IMM,
					width    = op1.width,
					rex      = 0,
					modrm    = -1
				)
				imm(op1.width, immediate.toInt())
			}

			else -> {
				encode(
					operands = if(op1.width.is8) Operands.RM8_IMM8 else Operands.RM_IMM,
					width    = op1.width,
					rex      = op1.rex,
					modrm    = 0b11_000_000 or op1.value
				)
				imm(op1.width, immediate.toInt())
			}
		}
	}



	private fun operands2RR(op1: Register, op2: Register) {
		when {
			Specifier.RM_CL in group && op1 == Register.CL -> {
				encode(
					operands = if(op1.width.is8) Operands.RM8_CL else Operands.RM_CL,
					width    = op1.width,
					rex      = op1.rex,
					modrm    = 0b11_000_000 or op1.value
				)
			}

			else -> {
				encode(
					operands = if(op1.width.is8) Operands.RM8_R8 else Operands.RM_R,
					width    = op1.width,
					rex      = op1.rex or (op2.rex shl 2),
					modrm    = 0b11_000_000 or (op2.value shl 3) or op1.value
				)
			}
		}
	}


}