package asm

import core.binary.BinaryWriter
import java.util.*

class Assembler2(parseResult: ParseResult) {


	private val nodes = parseResult.nodes

	private val symbols = parseResult.symbols



	private val writer = BinaryWriter()

	private fun error(): Nothing = error("Invalid encoding")

	private val rex = Rex()

	private val modrm = ModRM()

	private val sib = Sib()

	private var hasDisp = false

	private var disp = 0

	private var dispNode: AstNode? = null

	private var hasSib = false

	private var hasModRm = false

	private var width = Width.BIT32



	fun assemble(): ByteArray {
		for(node in nodes) {
			when(node) {
				is InstructionNode -> assemble(node)
				else -> error()
			}
		}

		return writer.trimmedBytes()
	}



	/*
	Node traversal
	 */



	private fun resolveImmediate(node: AstNode): Long = when(node) {
		is UnaryNode  -> node.op.calculate(resolveImmediate(node.node))
		is BinaryNode -> node.op.calculateInt(resolveImmediate(node.left), resolveImmediate(node.right))
		is IntNode    -> node.value
		else          -> error()
	}



	private fun hasLabel(root: AstNode): Boolean {
		val nodeStack: Deque<AstNode> = LinkedList()
		nodeStack.clear()
		nodeStack.push(root)

		while(nodeStack.isNotEmpty()) {
			when(val node = nodeStack.pop()) {
				is LabelNode  -> return true
				is UnaryNode  -> nodeStack.add(node.node)
				is BinaryNode -> { nodeStack.add(node.left); nodeStack.add(node.right) }
				else          -> { }
			}
		}

		return false
	}



	/*
	Encoding
	 */



	private fun encodeRR(op1: Register, op2: Register, opcode: Int) {
		if(op1.width != op2.width) error()
		rex.set(op1.width.rex, op2.rex, 0, op1.rex)
		modrm.set(0b11, op2.value, op1.value)
		if(op1.width.is16) writer.u8(0x66)
		if(rex.present) writer.u8(rex.final)
		writeOpcode(opcode)
		writer.u8(modrm.value)
	}



	private fun encodeMem(operand: MemoryNode) {
		if(operand.disp != null) {
			hasDisp = true

			if(hasLabel(operand.disp)) {
				dispNode = operand.disp
				disp = 0
			} else {
				disp = resolveImmediate(operand.disp).toInt()
			}
		}

		if(hasDisp) {
			modrm.mod = if(disp in Byte.MIN_VALUE..Byte.MAX_VALUE)
				0b01
			else
				0b10
		}

		// RIP-relative
		if(operand.rel) {
			modrm.mod = 0b00
			modrm.rm = 0b101
			return
		}

		// SIB
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



	/**
	 * Register in ModRM:reg, memory operand in ModRM:rm, ModRM:mod, and potentially SIB.
	 */
	fun encodeRegMem(op1: Register, op2: MemoryNode) {
		encodeMem(op2)
		rex.w = op1.width.rex
		rex.r = op1.rex
		modrm.reg = op1.value
		width = op1.width
	}



	fun encode(opcode: Int, extension: Int) {
		if(width.is16) writer.u8(0x66)
		else if(width.is64) rex.w = 1
		if(rex.present) writer.u8(rex.final)
		writeOpcode(opcode)
		if(extension >= 0) modrm.reg = extension
	}


	/*rex.w = op1.width.rex
	rex.r = op1.rex
	modrm.reg = op1.value
	if(width.is16) writer.u8(0x66)
	if(rex.present) writer.u8(rex.final)
	writer.u8(startOpcode + if(width.is8) 0x02 else 0x03)
	writer.u8(modrm.value)
	if(hasSib) writer.u8(sib.value)
	if(hasDisp) {
		if(modrm.mod == 0b01)
			writer.s8(disp)
		else
			writer.s32(disp)
	}*/


	/*
	Assembly
	 */



	private fun assemble(node: InstructionNode) {
		rex.value = 0

		when(node.mnemonic) {
			Mnemonic.CBW  -> { writer.u8(0x66); writer.u8(0x98) }
			Mnemonic.CWDE -> { writer.u8(0x98) }
			Mnemonic.CDQE -> { writer.u8(0x48); writer.u8(0x98) }
			Mnemonic.ADD  -> { assembleGroup1(node, 0x00, 0x00) }
			Mnemonic.OR   -> { assembleGroup1(node, 0x08, 0x01) }
			else -> error()
		}
	}



	private fun writeOpcode(opcode: Int) {
		writer.u32(writer.pos, opcode)
		writer.pos += ((39 - (opcode or 1).countLeadingZeroBits()) and -8) shr 3
	}



	private fun assembleGroup1(node: InstructionNode, startOpcode: Int, extension: Int) {
		if(node.op1 is RegisterNode) {
			val op1 = node.op1.value
			val width = op1.width

			if(node.op2 is RegisterNode) {
				encodeRR(op1, node.op2.value, if(width.is8) startOpcode else startOpcode + 1)
			} else if(node.op2 is MemoryNode) {
				encodeMem(node.op2)
				rex.w = op1.width.rex
				rex.r = op1.rex
				modrm.reg = op1.value
				if(width.is16) writer.u8(0x66)
				if(rex.present) writer.u8(rex.final)
				writer.u8(startOpcode + if(width.is8) 0x02 else 0x03)
				writer.u8(modrm.value)
				if(hasSib) writer.u8(sib.value)
				if(hasDisp) {
					if(modrm.mod == 0b01)
						writer.s8(disp)
					else
						writer.s32(disp)
				}
			} else if(node.op2 is ImmediateNode) {
				val imm = resolveImmediate(node.op2.value).toInt()

				if(imm in Byte.MIN_VALUE..Byte.MAX_VALUE && !width.is8) {
					rex.set(width.rex, 0, 0, op1.rex)
					modrm.set(0b11, extension, op1.value)
					if(width.is16) writer.u8(0x66)
					if(rex.present) writer.u8(rex.final)
					writer.u8(0x83)
					writer.u8(modrm.value)
					writer.s8(imm)
				} else if(op1.isA) {
					if(width.is16) writer.u8(0x66)
					else if(width.is64) writer.u8(0x48)
					if(width.is8) writer.u8(startOpcode + 4) else writer.u8(startOpcode + 5)
					when { width.is8 -> writer.s8(imm); width.is16 -> writer.s16(imm); else -> writer.s32(imm) }
				} else {
					rex.set(width.rex, 0, 0, op1.rex)
					modrm.set(0b11, extension, op1.value)
					if(width.is16) writer.u8(0x66)
					if(rex.present) writer.u8(rex.final)
					writer.u8(if(width.is8) 0x80 else 0x81)
					writer.u8(modrm.value)
					when { width.is8 -> writer.s8(imm); width.is16 -> writer.s16(imm); else -> writer.s32(imm) }
				}
			}
		} else error()
	}


}