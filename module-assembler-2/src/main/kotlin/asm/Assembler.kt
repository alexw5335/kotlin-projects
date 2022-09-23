package asm

import core.binary.BinaryWriter
import java.util.*

class Assembler(parseResult: ParseResult) {


	private val nodes = parseResult.nodes

	private val symbols = parseResult.symbols



	private val writer = BinaryWriter()

	private fun error(): Nothing = error("Invalid encoding")



	/*
	Context
	 */



	private val rex = Rex() // must be cleared

	private val modrm = ModRM() // must be cleared

	private val sib = Sib()

	private var hasDisp = false // must be set

	private var disp = 0

	private var dispNode: AstNode? = null

	private var width = Width.BIT32 // must be set



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
		is ImmediateNode -> resolveImmediate(node.value) // Root node only
		is UnaryNode     -> node.op.calculate(resolveImmediate(node.node))
		is BinaryNode    -> node.op.calculateInt(resolveImmediate(node.left), resolveImmediate(node.right))
		is IntNode       -> node.value
		else             -> error()
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



	private fun writeOpcode(opcode: Int) {
		writer.u32(writer.pos, opcode)
		writer.pos += ((39 - (opcode or 1).countLeadingZeroBits()) and -8) shr 3
	}



	private fun writeImm(imm: Int) {
		when { width.is8 -> writer.s8(imm); width.is16 -> writer.s16(imm); else -> writer.s32(imm) }
	}



	private val Int.isImm8 get() = this in Byte.MIN_VALUE..Byte.MAX_VALUE

	private val Int.isImm16 get() = this in Short.MIN_VALUE..Short.MAX_VALUE



	private fun encodeMem(operand: MemoryNode) {
		if(operand.base != null && operand.base.width.isNot64) error()
		if(operand.index != null && operand.index.width.isNot64) error()

		if(operand.disp != null) {
			hasDisp = true

			if(hasLabel(operand.disp)) {
				dispNode = operand.disp
				disp = 0
			} else {
				disp = resolveImmediate(operand.disp).toInt()
			}
		}

		modrm.mod = when {
			!hasDisp    -> 0b00
			disp.isImm8 -> 0b01
			else        -> 0b10
		}

		// RIP-relative
		if(operand.rel) {
			modrm.mod = 0b00
			modrm.rm = 0b101
			return
		}

		// SIB
		if(operand.index != null) {
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
			sib.set(0b00, 0b100, 0b101)
			return
		}

		// indirect addressing, e.g. [rax], [rax + 10]
		rex.b = operand.base.rex
		modrm.rm = operand.base.value
	}



	/**
	 * Encodes a register in ModRM:RM
	 */
	private fun encodeReg(reg: Register) {
		width = reg.width
		rex.w = reg.width.rex
		rex.b = reg.rex
		modrm.mod = 0b11
		modrm.rm = reg.value
	}



	/**
	 * Register in ModRM:reg, memory operand in ModRM:rm, ModRM:mod, and potentially SIB.
	 */
	private fun encodeRegMem(reg: Register, mem: MemoryNode) {
		width = reg.width
		if(mem.width != null && mem.width != width) error()
		encodeMem(mem)
		rex.w = reg.width.rex
		rex.r = reg.rex
		modrm.reg = reg.value
	}



	/**
	 * Encodes the first register in ModRM:RM and the second in ModRM:REG.
	 */
	private fun encodeRegReg(op1: Register, op2: Register) {
		width = op1.width
		if(op1.width != op2.width) error()
		rex.set(op1.width.rex, op2.rex, 0, op1.rex)
		modrm.set(0b11, op2.value, op1.value)
	}



	private fun encode(opcode: Int, extension: Int, hasModRm: Boolean) {
		if(width.is16) writer.u8(0x66)
		else if(width.is64) rex.w = 1
		if(rex.present) writer.u8(rex.final)
		writeOpcode(opcode)
		if(hasModRm) {
			if(extension >= 0) modrm.reg = extension
			writer.u8(modrm.value)
			if(modrm.hasSib) {
				writer.u8(sib.value)
				if(modrm.mod == 0 && sib.base == )
			}
			if(hasDisp) if(modrm.mod == 0b01) writer.s8(disp) else writer.s32(disp)
		}
	}



	/*
	Assembly
	 */



	private fun assemble(node: InstructionNode) {
		rex.value   = 0
		modrm.value = 0
		sib.value   = 0
		hasDisp     = false

		when {
			node.op1 == null -> assemble0(node)
			node.op2 == null -> assemble1(node)
			node.op3 == null -> assemble2(node)
			node.op4 == null -> assemble3(node)
			else -> error()
		}
	}



	private fun assemble0(node: InstructionNode) {
		when(node.mnemonic) {
			Mnemonic.CBW  -> writer.u16(0x98_66)
			Mnemonic.CWDE -> writer.u8(0x98)
			Mnemonic.CDQE -> writer.u16(0x98_48)

			Mnemonic.CWD -> writer.u16(0x99_66)
			Mnemonic.CDQ -> writer.u8(0x99)
			Mnemonic.CQO -> writer.u16(0x99_48)

			Mnemonic.MOVSB -> assembleString(node, 0xA4)
			Mnemonic.MOVSW -> assembleString(node, 0xA5)
			Mnemonic.MOVSD -> assembleString(node, 0xA5)
			Mnemonic.MOVSQ -> assembleString(node, 0xA5)

			Mnemonic.CMPSB -> assembleString(node, 0xA6)
			Mnemonic.CMPSW -> assembleString(node, 0xA7)
			Mnemonic.CMPSD -> assembleString(node, 0xA7)
			Mnemonic.CMPSQ -> assembleString(node, 0xA7)

			Mnemonic.STOSB -> assembleString(node, 0xAA)
			Mnemonic.STOSW -> assembleString(node, 0xAB)
			Mnemonic.STOSD -> assembleString(node, 0xAB)
			Mnemonic.STOSQ -> assembleString(node, 0xAB)

			Mnemonic.LODSB -> assembleString(node, 0xAC)
			Mnemonic.LODSW -> assembleString(node, 0xAD)
			Mnemonic.LODSD -> assembleString(node, 0xAD)
			Mnemonic.LODSQ -> assembleString(node, 0xAD)

			Mnemonic.SCASB -> assembleString(node, 0xAE)
			Mnemonic.SCASW -> assembleString(node, 0xAF)
			Mnemonic.SCASD -> assembleString(node, 0xAF)
			Mnemonic.SCASQ -> assembleString(node, 0xAF)

			Mnemonic.INSB -> assembleString(node, 0x6C)
			Mnemonic.INSW -> assembleString(node, 0x6D)
			Mnemonic.INSD -> assembleString(node, 0x6D)

			Mnemonic.OUTSB -> assembleString(node, 0x6E)
			Mnemonic.OUTSW -> assembleString(node, 0x6E)
			Mnemonic.OUTSD -> assembleString(node, 0x6E)

			Mnemonic.CPUID -> writer.u16(0xA2_0F)

			Mnemonic.RET -> writer.u8(0xC3)
			Mnemonic.RETF -> writer.u8(0xCB)

			Mnemonic.INT1  -> writer.u8(0xF1)
			Mnemonic.INT3  -> writer.u8(0xCC)

			Mnemonic.HLT   -> writer.u8(0xF4)

			Mnemonic.CMC   -> writer.u8(0xF5)
			Mnemonic.CLC   -> writer.u8(0xF8)
			Mnemonic.STC   -> writer.u8(0xF9)
			Mnemonic.CLI   -> writer.u8(0xFA)
			Mnemonic.STI   -> writer.u8(0xFB)
			Mnemonic.CLD   -> writer.u8(0xFC)
			Mnemonic.STD   -> writer.u8(0xFD)

			Mnemonic.CLTS -> writer.u16(0x06_0F)

			Mnemonic.PUSHF  -> writer.u16(0x9C_66)
			Mnemonic.PUSHFQ -> writer.u8(0x9C)
			Mnemonic.LAHF   -> writer.u8(0x9F)

			Mnemonic.WAIT   -> writer.u8(0x9B)
			Mnemonic.FWAIT  -> writer.u8(0x9B)
			Mnemonic.NOP    -> writer.u8(0x90)

			else -> error()
		}
	}



	private fun assemble1(node: InstructionNode) {
		when(node.mnemonic) {
			Mnemonic.RET   -> { writer.u8(0xC2); writer.s16(resolveImmediate(node.op1 as ImmediateNode).toInt()) }
			Mnemonic.RETF  -> { writer.u8(0xCA); writer.s16(resolveImmediate(node.op1 as ImmediateNode).toInt()) }
			Mnemonic.BSWAP -> assembleBSWAP(node)
			Mnemonic.PUSH  -> assemblePUSH(node)
			Mnemonic.POP   -> assemblePOP(node)
			Mnemonic.INT   -> { writer.u8(0xCD); writer.s8(resolveImmediate(node.op1 as ImmediateNode).toInt()) }
			Mnemonic.INC   -> assembleINCDEC(node, 0)
			Mnemonic.DEC   -> assembleINCDEC(node, 1)
			Mnemonic.NOP   -> assembleNOP(node)
			else -> error()
		}
	}



	private fun assemble2(node: InstructionNode) {
		when(node.mnemonic) {
			Mnemonic.ADD  -> assembleGroup1(node, 0x00, 0)
			Mnemonic.OR   -> assembleGroup1(node, 0x08, 1)
			Mnemonic.ADC  -> assembleGroup1(node, 0x10, 2)
			Mnemonic.SBB  -> assembleGroup1(node, 0x18, 3)
			Mnemonic.AND  -> assembleGroup1(node, 0x20, 4)
			Mnemonic.SUB  -> assembleGroup1(node, 0x28, 5)
			Mnemonic.XOR  -> assembleGroup1(node, 0x30, 6)
			Mnemonic.CMP  -> assembleGroup1(node, 0x38, 7)

			Mnemonic.ROL -> assembleGroup2(node, 0)
			Mnemonic.ROR -> assembleGroup2(node, 1)
			Mnemonic.RCL -> assembleGroup2(node, 2)
			Mnemonic.RCR -> assembleGroup2(node, 3)
			Mnemonic.SHL -> assembleGroup2(node, 4)
			Mnemonic.SAL -> assembleGroup2(node, 4)
			Mnemonic.SHR -> assembleGroup2(node, 5)
			Mnemonic.SAR -> assembleGroup2(node, 7)

			Mnemonic.CMOVA -> assembleCMOV(node, 0x47_0F)

			Mnemonic.MOVSXD -> assembleMOVSXD(node)

			Mnemonic.IN -> assembleIN(node)
			Mnemonic.OUT -> assembleOUT(node)

			else -> error()
		}
	}



	private fun assemble3(node: InstructionNode) {
		when(node.mnemonic) {
			Mnemonic.SHLD -> assembleShiftD(node, 0xA4_0F)
			Mnemonic.SHRD -> assembleShiftD(node, 0xAC_0F)

			else -> error()
		}
	}



	/*
	Mnemonic assembly
	 */



	private fun assembleNOP(node: InstructionNode) {
		if(node.op1 is RegisterNode)
			encodeReg(node.op1.value)
		else if(node.op1 is MemoryNode)
			encodeMem(node.op1)

		if(node.op1 is MemoryNode)
			width = node.op1.width ?: error()
		encode(0x1F_0F, 0, true)
	}



	private fun assembleIN(node: InstructionNode) {
		if(node.op1 !is RegisterNode) error()
		val op1 = node.op1.value
		if(!op1.isA) error()

		if(node.op2 is RegisterNode) {
			if(node.op2.value != Register.DX) error()
			when(op1.width) {
				Width.BIT8  -> writer.u8(0xEC)
				Width.BIT16 -> writer.u16(0xED_66)
				Width.BIT32 -> writer.u8(0xED)
				else        -> error()
			}
		} else if(node.op2 is ImmediateNode) {
			when(op1.width) {
				Width.BIT8  -> writer.u8(0xE4)
				Width.BIT16 -> writer.u16(0xE5_66)
				Width.BIT32 -> writer.u8(0xE5)
				else        -> error()
			}
			writer.s8(resolveImmediate(node.op2).toInt())
		}
	}



	private fun assembleOUT(node: InstructionNode) {
		if(node.op2 !is RegisterNode) error()
		val op2 = node.op2.value
		if(!op2.isA) error()

		if(node.op1 is RegisterNode) {
			if(node.op1.value != Register.DX) error()
			when(op2.width) {
				Width.BIT8  -> writer.u8(0xEE)
				Width.BIT16 -> writer.u16(0xEF_66)
				Width.BIT32 -> writer.u8(0xEF)
				else        -> error()
			}
		} else if(node.op1 is ImmediateNode) {
			when(op2.width) {
				Width.BIT8  -> writer.u8(0xE6)
				Width.BIT16 -> writer.u16(0xE7_66)
				Width.BIT32 -> writer.u8(0xE7)
				else        -> error()
			}
			writer.s8(resolveImmediate(node.op1).toInt())
		}
	}



	private fun assembleINCDEC(node: InstructionNode, extension: Int) {
		if(node.op1 is RegisterNode)
			encodeReg(node.op1.value)
		else if(node.op1 is MemoryNode)
			encodeMem(node.op1)
		else
			error()

		encode(0xFE + if(width.is8) 0 else 1, extension, true)
	}



	private fun assembleMOVSXD(node: InstructionNode) {
		if(node.op1 !is RegisterNode) error()

		if(node.op2 is MemoryNode) {
			encodeRegMem(node.op1.value, node.op2)
		} else if(node.op2 is RegisterNode) {
			encodeRegReg(node.op1.value, node.op2.value)
		} else {
			error()
		}

		if(width.is8) error()
		encode(0x63, 0, true)
	}



	private fun assemblePOP(node: InstructionNode) {
		if(node.op1 is RegisterNode) {
			val op1 = node.op1.value
			width = op1.width
			if(width.is8 || width.is32) error()
			if(width.is64) width = Width.BIT32
			rex.b = op1.rex
			encode(0x58 + op1.value, 0, false)
		} else if(node.op1 is MemoryNode) {
			val op1 = node.op1
			width = op1.width ?: error()
			if(width.is8 || width.is32) error()
			if(width.is64) width = Width.BIT32
			encodeMem(op1)
			encode(0x8F, 0, true)
		}
	}



	private fun assemblePUSH(node: InstructionNode) {
		if(node.op1 is ImmediateNode) {
			val imm = resolveImmediate(node.op1).toInt()

			if(imm.isImm8) {
				writer.u8(0x6A)
				writer.s8(imm)
			} else if(imm.isImm16) {
				writer.u16(0x68_66)
				writer.s16(imm)
			} else {
				writer.u8(0x68)
				writer.s32(imm)
			}
		} else if(node.op1 is RegisterNode) {
			val op1 = node.op1.value
			width = op1.width
			if(width.is8 || width.is32) error()
			if(width.is64) width = Width.BIT32
			rex.b = op1.rex
			encode(0x50 + op1.value, 0, false)
		} else if(node.op1 is MemoryNode) {
			val op1 = node.op1
			width = op1.width ?: error()
			if(width.is8 || width.is32) error()
			if(width.is64) width = Width.BIT32
			encodeMem(op1)
			encode(0xFF, 6, true)
		} else {
			error()
		}
	}



	private fun assembleCMOV(node: InstructionNode, opcode: Int) {
		val op1 = (node.op1 as? RegisterNode)?.value ?: error()

		if(width.is8) error()

		if(node.op2 is RegisterNode)
			encodeRegReg(op1, node.op2.value)
		else if(node.op2 is MemoryNode)
			encodeRegMem(op1, node.op2)

		encode(opcode, 0, true)
	}



	/**
	 * BSWAP
	 */
	private fun assembleBSWAP(node: InstructionNode) {
		if(node.op1 !is RegisterNode) error()
		val op1 = node.op1.value
		width = op1.width
		if(width.is16) error()
		rex.w = width.rex
		rex.b = op1.rex
		if(rex.present) writer.u8(rex.final)
		writer.u16(0xC8_0F + (op1.value shl 8))
	}



	/**
	 * MOVS, CMPS, LODS, SCAS, STOS
	 */
	private fun assembleString(node: InstructionNode, opcode: Int) {
		if(node.prefix != 0) writer.u8(node.prefix)
		if(node.mnemonic.stringWidth!!.is16) writer.u8(0x66)
		else if(node.mnemonic.stringWidth.is64) writer.u8(0x48)
		writer.u8(opcode)
	}



	/**
	 * SHLD, SHRD
	 */
	private fun assembleShiftD(node: InstructionNode, startOpcode: Int) {
		val op2 = (node.op2 as? RegisterNode)?.value ?: error()

		if(node.op1 is RegisterNode) {
			width = node.op1.value.width
			encodeReg(node.op1.value)
		} else if(node.op1 is MemoryNode) {
			width = op2.width
			rex.w = width.rex
			encodeMem(node.op1)
		}

		if(width.is8) error()

		modrm.reg = op2.value
		rex.r = op2.rex

		if(node.op3 is RegisterNode) {
			if(node.op3.value != Register.CL) error()
			encode(startOpcode + (1 shl 8), 0, true)
		} else if(node.op3 is ImmediateNode) {
			val imm = resolveImmediate(node.op3).toInt()
			encode(startOpcode, 0, true)
			writer.s8(imm)
		} else
			error()
	}



	/**
	 * ROL, ROR, RCL, RCR, SHL/SAL, SHR, SAR.
	 */
	private fun assembleGroup2(node: InstructionNode, extension: Int) {
		if(node.op1 is RegisterNode) {
			val op1 = node.op1.value
			width = op1.width
			encodeReg(op1)
		} else if(node.op1 is MemoryNode) {
			val op1 = node.op1
			width = op1.width ?: error()
			encodeMem(op1)
		}

		val offset = if(width.is8) 0 else 1


		if(node.op2 is RegisterNode) {
			if(node.op2.value != Register.CL) error()
			encode(0xD2 + offset, extension, true)
		} else if(node.op2 is ImmediateNode) {
			val imm = resolveImmediate(node.op2).toInt()
			if(imm == 1) {
				encode(0xD0 + offset, extension, true)
			} else {
				encode(0xC0 + offset, extension, true)
				writer.s8(imm)
			}
		} else error()
	}



	/**
	 * ADD, OR, ADC, SBB, AND, SUB, XOR, CMP
	 */
	private fun assembleGroup1(node: InstructionNode, startOpcode: Int, extension: Int) {
		if(node.op1 is RegisterNode) {
			val op1 = node.op1.value
			width = op1.width
			val offset = if(width.is8) 0 else 1

			if(node.op2 is RegisterNode) {
				encodeRegReg(op1, node.op2.value)
				encode(startOpcode + offset, 0, true)
			} else if(node.op2 is MemoryNode) {
				encodeRegMem(op1, node.op2)
				encode(startOpcode + 0x02 + offset, 0, true)
			} else if(node.op2 is ImmediateNode) {
				val imm = resolveImmediate(node.op2.value).toInt()

				if(imm in Byte.MIN_VALUE..Byte.MAX_VALUE && !width.is8) {
					encodeReg(op1)
					encode(0x83, extension, true)
					writer.s8(imm)
				} else if(op1.isA) {
					encode(startOpcode + 4 + offset, 0, false)
					writeImm(imm)
				} else {
					encodeReg(op1)
					encode(0x80 + offset, extension, true)
					writeImm(imm)
				}
			}
		} else if(node.op1 is MemoryNode) {
			val op1 = node.op1

			if(node.op2 is RegisterNode) {
				val op2 = node.op2.value
				width = op2.width
				if(op1.width != null && width != op1.width) error()
				encodeRegMem(op2, op1)
				encode(startOpcode + if(width.is8) 0x00 else 0x01, 0, true)
			} else if(node.op2 is ImmediateNode) {
				val imm = resolveImmediate(node.op2.value).toInt()
				width = op1.width ?: error()

				if(imm in Byte.MIN_VALUE..Byte.MAX_VALUE && !width.is8) {
					encodeMem(op1)
					encode(0x83, extension, true)
					writer.s8(imm)
				} else {
					encodeMem(op1)
					encode(if(width.is8) 0x80 else 0x81, extension, true)
					writeImm(imm)
				}
			} else error()
		} else error()
	}


}