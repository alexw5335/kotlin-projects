package asm

import core.binary.BinaryWriter
import java.util.*

class Assembler2(parseResult: ParseResult) {


	private val nodes = parseResult.nodes

	private val symbols = parseResult.symbols

	private val writer = BinaryWriter()

	private fun error(): Nothing = error("Invalid encoding")



	private enum class Widths(val bits: Int) {
		ALL(0b1111),
		NO_8(0b1110),
		NO_8_32(0b1010),
		NO_8_64(0b0110),
		NO_8_16(0b1100),
		ONLY_8(0b0001);

		/** 1 if this is 64-bit default, 0 if not. */
		val rexMod = (bits shr 2) and 1

		operator fun contains(width: Width) = bits and width.bit != 0

	}



	/*
	Node traversal
	 */



	/*
	Valid label references:
	- For RIP-relative, a single label reference, no base, no index, can have other disp nodes
	- For REL, a single label reference and/or multiple label differences.
	- For memory operands, any number of label differences, no direct references.
	 */



	private class LabelDiff(val first: Symbol, val second: Symbol)

	private var label: Symbol? = null

	private var diff: LabelDiff? = null

	private var stop: Boolean = false



	private fun resolveImmediate(
		node: AstNode,
		isDisp: Boolean   = false,
		isRel: Boolean    = false,
		isRipRel: Boolean = false
	) {
		label = null
		diff = null
		stop = false
	}


	private val nodeStack = LinkedList<AstNode>()



	private fun resolveImmediateRec(node: AstNode): Long {
		if(node is LabelNode) {
			if(label != null) error()
			label = symbols[node.name]!!
			stop = true
			return 0
		}

		if(stop) return 0

		if(node is IntNode)
			return node.value

		if(node is UnaryNode)
			return resolveImmediateRec(node.node)

	}



	private fun resolveImm2Rec(node: AstNode): Long {
		when(node) {
			is IntNode -> return node.value

			is IdNode -> {
				val symbol = symbols[node.name] ?: error("Unrecognised symbol: ${node.name}")
				if(symbol.data is IntSymbolData)
					return (symbol.data as IntSymbolData).value
				else
					error()
			}

			is UnaryNode -> {
				return node.op.calculate(resolveImm2Rec(node.node))
			}

			is BinaryNode -> {
				if(node.left is LabelNode && node.right is LabelNode && node.op == BinaryOp.SUB) {
					return 0
				}

				return node.op.calculateInt(resolveImm2Rec(node.left), resolveImm2Rec(node.right))
			}

			is LabelNode -> error("Cannot directly reference a label in an immediate operand.")

			else -> error()
		}
	}



	private fun resolveImm(node: AstNode): Long = when(node) {
		is ImmediateNode -> resolveImm(node.value) // Root node only
		is UnaryNode     -> node.op.calculate(resolveImm(node.node))
		is BinaryNode    -> node.op.calculateInt(resolveImm(node.left), resolveImm(node.right))
		is IntNode       -> node.value
		else             -> error()
	}



	private fun hasLabel(root: AstNode): Boolean {
		val nodeStack = LinkedList<AstNode>()
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



	private val AstNode?.asReg get() = if(this is RegisterNode) value else error()

	private val AstNode?.asMem get() = if(this is MemoryNode) this else error()

	private val Int.isImm8 get() = this in Byte.MIN_VALUE..Byte.MAX_VALUE

	private val Int.isImm16 get() = this in Short.MIN_VALUE..Short.MAX_VALUE

	private val Long.isImm32 get() = this in Int.MIN_VALUE..Int.MAX_VALUE



	private fun opcodeLength(opcode: Int) =
		((39 - (opcode or 1).countLeadingZeroBits()) and -8) shr 3



	private fun encodeOpcode(opcode: Int) {
		writer.u32(writer.pos, opcode)
		writer.pos += opcodeLength(opcode)
	}



	private fun encodeModRM(mod: Int, reg: Int, rm: Int) {
		writer.u8((mod shl 6) or (reg shl 3) or rm)
	}



	private fun encodeSIB(scale: Int, index: Int, base: Int) {
		writer.u8((scale shl 6) or (index shl 3) or base)
	}



	private fun encodeRex(w: Int, r: Int, x: Int, b: Int) {
		val value = 0b0100_0000 or (w shl 3) or (r shl 2) or (x shl 1) or b
		if(value != 0b0100_0000) writer.u8(value)
	}



	private fun encode1OpReg(opcode: Int, op1: Register, widths: Widths = Widths.ALL) {
		val width = op1.width
		if(width !in widths) error()
		if(width.is16) writer.u8(0x66)
		encodeRex(width.rex and widths.rexMod, 0, 0, op1.rex)
		val opcodeLength = opcodeLength(opcode)
		// Must take opcode length into account when adding the register's value to the opcode
		// Opcode offset is multiplied by 8
		val finalOpcode = opcode + ((((widths.bits and width.opcodeOffset) shl 3) + op1.value) shl ((opcodeLength - 1) shl 3))
		writer.u32(writer.pos, finalOpcode)
		writer.pos += opcodeLength
	}



	private fun encodeNone(opcode: Int, width: Width, widths: Widths = Widths.ALL) {
		if(width !in widths) error()
		if(width.is16) writer.u8(0x66)
		encodeRex(width.rex and widths.rexMod, 0, 0, 0)
		encodeOpcode(opcode + (widths.bits and width.opcodeOffset))
	}



	/**
	 * Encodes a register in ModRM:RM
	 */
	private fun encode1R(
		opcode    : Int,
		extension : Int,
		op1       : Register,
		widths    : Widths = Widths.ALL
	) {
		if(op1.width !in widths) error()
		if(op1.width.is16) writer.u8(0x66)
		encodeRex(op1.width.rex and widths.rexMod, 0, 0, op1.rex)
		encodeOpcode(opcode + (widths.bits and op1.width.opcodeOffset))
		encodeModRM(0b11, extension, op1.value)
	}



	/**
	 * Encodes a memory operand.
	 */
	private fun encode1M(
		opcode    : Int,
		extension : Int,
		op1       : MemoryNode,
		widths    : Widths = Widths.ALL
	) {
		val width = op1.width ?: error("Unspecified memory operand width")
		if(width !in widths) error()
		if(width.is16) writer.u8(0x66)
		encodeMem(opcode + (widths.bits and width.opcodeOffset), op1, width.rex and widths.rexMod, 0, extension)
	}



	/**
	 * Encodes either a register or a memory operand.
	 */
	private fun encode1RM(
		opcode    : Int,
		extension : Int,
		op1       : AstNode,
		widths    : Widths = Widths.ALL
	) {
		if(op1 is RegisterNode)
			encode1R(opcode, extension, op1.value, widths)
		else if(op1 is MemoryNode)
			encode1M(opcode, extension, op1, widths)
		else
			error()
	}



	/**
	 * Encodes 2 registers of equal width in ModRM:RM and ModRM:REG respectively
	 */
	private fun encode2RR(opcode: Int, op1: Register, op2: Register, widths: Widths = Widths.ALL) {
		if(op1.width != op2.width) error()
		if(op1.width.is16) writer.u8(0x66)
		encodeRex(op1.width.rex and widths.rexMod, op2.rex, 0, op1.rex)
		encodeOpcode(opcode + (op1.width.opcodeOffset and widths.bits))
		encodeModRM(0b11, op2.value, op1.value)
	}



	/**
	 * Encodes a register operand and a memory operand of equal width.
	 */
	private fun encode2RM(opcode: Int, op1: Register, op2: MemoryNode, widths: Widths = Widths.ALL) {
		if(op2.width != null && op2.width != op1.width) error()
		if(op1.width.is16) writer.u8(0x66)
		encodeMem(opcode + (op1.width.opcodeOffset and widths.bits), op2, op1.width.rex, op1.rex, op1.value)
	}



	private fun encode2RMR(opcode: Int, node: InstructionNode, widths: Widths = Widths.ALL) {
		if(node.op2 !is RegisterNode) error()
		when(node.op1) {
			is RegisterNode -> encode2RR(opcode, node.op1.value, node.op2.value, widths)
			is MemoryNode   -> encode2RM(opcode, node.op2.value, node.op1, widths)
			else            -> error()
		}
	}



	private fun encode2RRM(opcode: Int, node: InstructionNode, widths: Widths = Widths.ALL) {
		if(node.op1 !is RegisterNode) error()
		when(node.op2) {
			is RegisterNode -> encode2RR(opcode, node.op2.value, node.op1.value, widths)
			is MemoryNode   -> encode2RM(opcode, node.op1.value, node.op2, widths)
			else            -> error()
		}
	}



	private fun encodeImm(width: Width, imm: Int) {
		when(width) {
			Width.BIT8  -> writer.s8(imm)
			Width.BIT16 -> writer.s16(imm)
			else        -> writer.s32(imm)
		}
	}



	/**
	 * Helper function for encoding instructions that have a memory operand.
	 */
	private fun encodeMem(opcode: Int, mem: MemoryNode, rexW: Int, rexR: Int, reg: Int) {
		if(mem.base != null && mem.base.width.isNot64) error()
		if(mem.index != null && mem.index.width.isNot64) error()

		var mod = 0
		var disp = 0

		if(mem.disp != null) {
			if(hasLabel(mem.disp)) error()
			disp = resolveImm(mem.disp).toInt()
			if(disp != 0)
				mod = if(disp.isImm8) 0b01 else 0b10
		}

		if(mem.rel) {
			encodeRex(rexW, rexR, 0, 0)
			encodeOpcode(opcode)
			encodeModRM(0b00, reg, 0b101)
			writer.s32(disp)
		} else if(mem.index != null) {
			if(mem.scale.countOneBits() != 1) error()
			val scale = mem.scale.countTrailingZeroBits()

			if(mem.base != null) {
				encodeRex(rexW, rexR, mem.index.rex, mem.base.rex)
				encodeOpcode(opcode)
				encodeModRM(mod, reg, 0b100)
				encodeSIB(scale, mem.index.value, mem.base.value)
				if(mod == 0b01) writer.s8(disp) else if(mod == 0b10) writer.s32(disp)
			} else {
				encodeRex(rexW, rexR, mem.index.rex, 0)
				encodeOpcode(opcode)
				encodeModRM(0, reg, 0b100)
				encodeSIB(scale, mem.index.value, 0b101)
				writer.s32(disp)
			}
		} else if(mem.base != null) { // indirect in RM
			encodeRex(rexW, rexR, 0, mem.base.rex)
			encodeOpcode(opcode)
			encodeModRM(mod, reg, mem.base.value)
			if(mod == 0b01) writer.s8(disp) else if(mod == 0b10) writer.s32(disp)
		} else if(mod != 0) { // absolute 32-bit
			encodeRex(rexW, rexR, 0, 0)
			encodeOpcode(opcode)
			encodeModRM(0b00, reg, 0b100)
			encodeSIB(0b00, 0b100, 0b101)
			writer.s32(disp)
		} else { // Empty memory operand
			error()
		}
	}



	/*
	Assembly
	 */



	fun assemble(): ByteArray {
		for(node in nodes) {
			when(node) {
				is InstructionNode -> assemble(node)
				is DbNode -> handleDb(node)
				else -> error()
			}
		}

		return writer.trimmedBytes()
	}



	private fun handleDb(dbNode: DbNode) {
		for(node in dbNode.components) {
			when(node) {
				is StringNode -> {
					for(c in node.value)
						writer.u8(c.code)
				}
				else -> {
					val value = resolveImm(node)
					if(value > 0) writer.u8(value.toInt()) else writer.s8(value.toInt())
				}
			}
		}
	}



	private fun assemble(node: InstructionNode) {
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

			Mnemonic.IRET,
			Mnemonic.IRETD  -> writer.u8(0xCF)
			Mnemonic.IRETQ  -> writer.u16(0xCF_48)

			Mnemonic.MFENCE -> { writer.u16(0xAE_0F); writer.u8(0xF0) }

			else -> error()
		}
	}



	private fun assemble1(node: InstructionNode) {
		when(node.mnemonic) {
			Mnemonic.NOP -> encode1RM(0x1F_0F, 0, node.op1!!, Widths.NO_8_64)

			Mnemonic.SETA   -> encode1RM(0x97_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETAE  -> encode1RM(0x93_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETB   -> encode1RM(0x92_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETBE  -> encode1RM(0x96_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETC   -> encode1RM(0x92_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETE   -> encode1RM(0x94_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETG   -> encode1RM(0x9F_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETGE  -> encode1RM(0x9D_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETL   -> encode1RM(0x9C_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETLE  -> encode1RM(0x9E_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNA  -> encode1RM(0x96_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNAE -> encode1RM(0x92_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNB  -> encode1RM(0x93_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNBE -> encode1RM(0x97_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNC  -> encode1RM(0x93_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNE  -> encode1RM(0x95_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNG  -> encode1RM(0x9E_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNGE -> encode1RM(0x9C_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNL  -> encode1RM(0x9D_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNLE -> encode1RM(0x9F_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNO  -> encode1RM(0x91_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNP  -> encode1RM(0x9B_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNS  -> encode1RM(0x99_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETNZ  -> encode1RM(0x95_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETO   -> encode1RM(0x90_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETP   -> encode1RM(0x9A_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETPE  -> encode1RM(0x9A_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETPO  -> encode1RM(0x9B_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETS   -> encode1RM(0x98_0F, 0, node.op1!!, Widths.ONLY_8)
			Mnemonic.SETZ   -> encode1RM(0x94_0F, 0, node.op1!!, Widths.ONLY_8)

			Mnemonic.RET   -> { writer.u8(0xC2); writer.s16(resolveImm(node.op1 as ImmediateNode).toInt()) }
			Mnemonic.RETF  -> { writer.u8(0xCA); writer.s16(resolveImm(node.op1 as ImmediateNode).toInt()) }
			Mnemonic.INT   -> { writer.u8(0xCD); writer.s8(resolveImm(node.op1 as ImmediateNode).toInt()) }
			Mnemonic.BSWAP -> encode1OpReg(0xC8_0F, node.op1.asReg, Widths.NO_8_16)
			Mnemonic.PUSH  -> assemblePUSH(node)
			Mnemonic.POP   -> assemblePOP(node)
			Mnemonic.INC   -> encode1RM(0xFE, 0, node.op1!!)
			Mnemonic.DEC   -> encode1RM(0xFE, 1, node.op1!!)

			Mnemonic.NOT  -> encode1RM(0xF6, 2, node.op1!!)
			Mnemonic.NEG  -> encode1RM(0xF6, 3, node.op1!!)
			Mnemonic.MUL  -> encode1RM(0xF6, 4, node.op1!!)
			Mnemonic.IMUL -> encode1RM(0xF6, 5, node.op1!!)
			Mnemonic.DIV  -> encode1RM(0xF6, 6, node.op1!!)
			Mnemonic.IDIV -> encode1RM(0xF6, 7, node.op1!!)

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

			Mnemonic.CMOVA   -> encode2RRM(0x47_0F, node, Widths.NO_8)
			Mnemonic.CMOVAE  -> encode2RRM(0x43_0F, node, Widths.NO_8)
			Mnemonic.CMOVB   -> encode2RRM(0x42_0F, node, Widths.NO_8)
			Mnemonic.CMOVBE  -> encode2RRM(0x46_0F, node, Widths.NO_8)
			Mnemonic.CMOVC   -> encode2RRM(0x42_0F, node, Widths.NO_8)
			Mnemonic.CMOVE   -> encode2RRM(0x44_0F, node, Widths.NO_8)
			Mnemonic.CMOVG   -> encode2RRM(0x4F_0F, node, Widths.NO_8)
			Mnemonic.CMOVGE  -> encode2RRM(0x4D_0F, node, Widths.NO_8)
			Mnemonic.CMOVL   -> encode2RRM(0x4C_0F, node, Widths.NO_8)
			Mnemonic.CMOVLE  -> encode2RRM(0x4E_0F, node, Widths.NO_8)
			Mnemonic.CMOVNA  -> encode2RRM(0x46_0F, node, Widths.NO_8)
			Mnemonic.CMOVNAE -> encode2RRM(0x42_0F, node, Widths.NO_8)
			Mnemonic.CMOVNB  -> encode2RRM(0x43_0F, node, Widths.NO_8)
			Mnemonic.CMOVNBE -> encode2RRM(0x47_0F, node, Widths.NO_8)
			Mnemonic.CMOVNC  -> encode2RRM(0x43_0F, node, Widths.NO_8)
			Mnemonic.CMOVNE  -> encode2RRM(0x45_0F, node, Widths.NO_8)
			Mnemonic.CMOVNG  -> encode2RRM(0x4E_0F, node, Widths.NO_8)
			Mnemonic.CMOVNGE -> encode2RRM(0x4C_0F, node, Widths.NO_8)
			Mnemonic.CMOVNL  -> encode2RRM(0x4D_0F, node, Widths.NO_8)
			Mnemonic.CMOVNLE -> encode2RRM(0x4F_0F, node, Widths.NO_8)
			Mnemonic.CMOVNO  -> encode2RRM(0x41_0F, node, Widths.NO_8)
			Mnemonic.CMOVNP  -> encode2RRM(0x4B_0F, node, Widths.NO_8)
			Mnemonic.CMOVNS  -> encode2RRM(0x49_0F, node, Widths.NO_8)
			Mnemonic.CMOVNZ  -> encode2RRM(0x45_0F, node, Widths.NO_8)
			Mnemonic.CMOVO   -> encode2RRM(0x40_0F, node, Widths.NO_8)
			Mnemonic.CMOVP   -> encode2RRM(0x4A_0F, node, Widths.NO_8)
			Mnemonic.CMOVPE  -> encode2RRM(0x4A_0F, node, Widths.NO_8)
			Mnemonic.CMOVPO  -> encode2RRM(0x4B_0F, node, Widths.NO_8)
			Mnemonic.CMOVS   -> encode2RRM(0x48_0F, node, Widths.NO_8)
			Mnemonic.CMOVZ   -> encode2RRM(0x44_0F, node, Widths.NO_8)

			//Mnemonic.MOVSXD -> assembleMOVSXD(node)

			Mnemonic.IN  -> assembleIN(node)
			Mnemonic.OUT -> assembleOUT(node)

			Mnemonic.LEA -> encode2RM(0x8D, node.op1.asReg, node.op2.asMem, Widths.NO_8)

			Mnemonic.XCHG -> assembleXCHG(node)

			Mnemonic.MOV  -> assembleMOV(node)

			Mnemonic.TEST -> assembleTEST(node)

			Mnemonic.IMUL -> encode2RRM(0xAF_0F, node, Widths.NO_8)

			else -> error()
		}
	}



	private fun assemble3(node: InstructionNode) {
		when(node.mnemonic) {
			Mnemonic.SHLD -> assembleShiftD(node, 0xA4_0F)
			Mnemonic.SHRD -> assembleShiftD(node, 0xAC_0F)
			Mnemonic.IMUL -> assembleIMUL(node)
			else -> error()
		}
	}



	/*
	Specific assembly
	 */



	/*
	MOVSXD
	MOVSX
	ENTER?
	LEAVE?
	MOV SREG and MOFFS?
	PUSH/POP SREG?
	FPU
	LOOPcc
	Jcc
	JECXZ
	CALL
	JMP
	 */



	/**
	 *     F6/5   IMUL  RM8
	 *     F7/5   IMUL  RM
	 *     0F AF  IMUL  R RM
	 *     6B     IMUL  R RM IMM8
	 *     69     IMUL  R RM IMM
	 */
	private fun assembleIMUL(node: InstructionNode) {
		if(node.op3 !is ImmediateNode) error()
		val imm = resolveImm(node.op3).toInt()
		if(imm.isImm8) {
			encode2RRM(0x6B, node, Widths.NO_8)
			writer.s8(imm)
		} else {
			encode2RRM(0x69, node, Widths.NO_8)
			encodeImm(node.op1.asReg.width, imm)
		}
	}



	/**
	 *     88  MOV  RM8 R8
	 *     89  MOV  RM  R
	 *     8A  MOV  R8  RM8
	 *     8B  MOV  R   RM
	 *     B0  MOV  R8  IMM8  (OPREG)
	 *     B8  MOV  R   IMM   (OPREG, IMM64)
	 *     8C  MOV  R16/R32/M16  SREG
	 *     8C  MOV  R64/M16      SREG  (REXW)
	 *     8E  MOV  SREG  RM16
	 *     8E  MOV  SREG  RM64  (REXW)
	 *     A0  MOV  AL  MOFFS8
	 *     A1  MOV  A   MOFFS
	 *     A2  MOV  MOFFS8  AL
	 *     A3  MOV  MOFFS   A
	 *     C6  MOV  RM8 IMM8
	 *     C7  MOV  RM  IMM
	 *
	 *     - Missing SREG and MOFFS encodings
	 */
	private fun assembleMOV(node: InstructionNode) {
		if(node.op1 is RegisterNode) {
			val op1 = node.op1.value

			if(node.op2 is RegisterNode) {
				encode2RR(0x88, op1, node.op2.value)
			} else if(node.op2 is MemoryNode) {
				encode2RM(0x8A, op1, node.op2)
			} else if(node.op2 is ImmediateNode) {
				val imm = resolveImm(node.op2)
				encode1OpReg(0xB0, op1)
				when(op1.width) {
					Width.BIT8  -> writer.s8(imm.toInt())
					Width.BIT16 -> writer.s16(imm.toInt())
					Width.BIT32 -> writer.s32(imm.toInt())
					Width.BIT64 -> writer.s64(imm)
					else        -> error()
				}
			} else {
				error()
			}
		} else if(node.op1 is MemoryNode) {
			if(node.op2 is RegisterNode) {
				encode2RM(0xC6, node.op2.value, node.op1)
			} else if(node.op2 is ImmediateNode) {
				val width = node.op1.width ?: error()
				encode1M(0xC6, 0, node.op1)
				encodeImm(width, resolveImm(node.op2).toInt())
			}
		} else {
			error()
		}
	}



	private fun assembleXCHG(node: InstructionNode) {
		if(node.op1 is RegisterNode) {
			val op1 = node.op1.value
			if(node.op2 is RegisterNode) {
				val op2 = node.op2.value
				if(op1.isA) encode1OpReg(0x90, op2, Widths.NO_8)
				else if(op2.isA) encode1OpReg(0x90, op1, Widths.NO_8)
				else encode2RR(0x86, op1, op2)
			} else if(node.op2 is MemoryNode) {
				encode2RM(0x86, op1, node.op2)
			} else {
				error()
			}
		} else {
			encode2RMR(0x86, node)
		}
	}



	/**
	 *
	 *     A8    TEST  AL  IMM8
	 *     A9    TEST  A   IMM
	 *     F6/0  TEST  RM8 IMM8
	 *     F7/0  TEST  RM  IMM
	 *     84    TEST  RM8 R8
	 *     85    TEST  RM  R
	 */
	private fun assembleTEST(node: InstructionNode) {
		if(node.op1 is RegisterNode) {
			val op1 = node.op1.value
			if(node.op2 is RegisterNode) {
				encode2RR(0x84, op1, node.op2.value)
			} else if(node.op2 is ImmediateNode) {
				if(op1.isA) encodeNone(0xA8, op1.width)
				else encode1R(0xF6, 0, op1)
				encodeImm(op1.width, resolveImm(node.op2).toInt())
			} else {
				error()
			}
		} else if(node.op1 is MemoryNode) {
			if(node.op2 is RegisterNode) {
				encode2RM(0x84, node.op2.value, node.op1)
			} else if(node.op2 is ImmediateNode) {
				encode1M(0xF6, 0, node.op1)
				encodeImm(node.op1.width ?: error(), resolveImm(node.op2).toInt())
			} else {
				error()
			}
		} else {
			error()
		}
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
			writer.s8(resolveImm(node.op2).toInt())
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
			writer.s8(resolveImm(node.op1).toInt())
		}
	}



	/**
	 *     FF/6   PUSH  RM
	 *     50     PUSH  R   (OPREG)
	 *     6A     PUSH  IMM8
	 *     68     PUSH  IMM16/32
	 *     0F A0  PUSH  FS  (default 64-bit, 66 for 16-bit)
	 *     0F A8  PUSH  GS  (default 64-bit, 66 for 16-bit)
	 */
	private fun assemblePUSH(node: InstructionNode) {
		if(node.op1 is RegisterNode) {
			encode1OpReg(0x50, node.op1.value, Widths.NO_8_32)
		} else if(node.op1 is MemoryNode) {
			encode1M(0xFF, 6, node.op1, Widths.NO_8_32)
		} else if(node.op1 is ImmediateNode) {
			val imm = resolveImm(node.op1).toInt()
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
		}
	}



	/**
	 * 8F/0   POP  RM
	 * 58     POP  R   (OPREG)
	 * 0F A1  POP  FS  (default 64-bit, 66 for 16-bit)
	 * 0F A9  POP  GS  (default 64-bit, 66 for 16-bit)
	 */
	private fun assemblePOP(node: InstructionNode) {
		when(val op1 = node.op1) {
			is RegisterNode  -> encode1OpReg(0x58, op1.value, Widths.NO_8_32)
			is MemoryNode    -> encode1M(0x8F, 0, op1, Widths.NO_8_32)
			else             -> error()
		}
	}



	/**
	 * ROL, ROR, RCL, RCR, SHL/SAL, SHR, SAR.
	 */
	private fun assembleGroup2(node: InstructionNode, extension: Int) {
		if(node.op2 is RegisterNode) {
			if(node.op2.value != Register.CL) error()
			encode1RM(0xD2, extension, node.op1!!)
		} else if(node.op2 is ImmediateNode) {
			val imm = resolveImm(node.op2).toInt()
			if(imm == 1) {
				encode1RM(0xD0, extension, node.op1!!)
			} else {
				encode1RM(0xC0, extension, node.op1!!)
				writer.s8(imm)
			}
		} else {
			error()
		}
	}



	/**
	 * ADD, OR, ADC, SBB, AND, SUB, XOR, CMP
	 */
	private fun assembleGroup1(node: InstructionNode, startOpcode: Int, extension: Int) {
		if(node.op1 is RegisterNode) {
			val op1 = node.op1.value

			if(node.op2 is RegisterNode) {
				encode2RR(startOpcode, op1, node.op2.value)
			} else if(node.op2 is MemoryNode) {
				encode2RM(startOpcode + 2, op1, node.op2)
			} else if(node.op2 is ImmediateNode) {
				val imm = resolveImm(node.op2.value).toInt()
				if(imm in Byte.MIN_VALUE..Byte.MAX_VALUE && op1.width.isNot8) {
					encode1R(0x83, extension, op1, Widths.NO_8)
					writer.s8(imm)
				} else if(op1.isA) {
					encodeNone(startOpcode + 4, op1.width)
					encodeImm(op1.width, imm)
				} else {
					encode1R(0x80, extension, op1)
					encodeImm(op1.width, imm)
				}
			}
		} else if(node.op1 is MemoryNode) {
			if(node.op2 is RegisterNode) {
				encode2RM(startOpcode, node.op2.value, node.op1)
			} else if(node.op2 is ImmediateNode) {
				val imm = resolveImm(node.op2.value).toInt()
				val width = node.op1.width ?: error()
				if(imm in Byte.MIN_VALUE..Byte.MAX_VALUE && width.isNot8) {
					encode1M(0x83, extension, node.op1, Widths.NO_8)
					writer.s8(imm)
				} else {
					encode1M(0x80, extension, node.op1)
					writer.s8(imm)
				}
			} else error()
		} else error()
	}



	/**
	 * SHLD, SHRD
	 */
	private fun assembleShiftD(node: InstructionNode, startOpcode: Int) {
		if(node.op3 is RegisterNode) {
			if(node.op3.value != Register.CL) error()
			encode2RMR(startOpcode + 1, node, Widths.NO_8)
		} else if(node.op3 is ImmediateNode) {
			encode2RMR(startOpcode, node, Widths.NO_8)
			writer.s8(resolveImm(node.op3).toInt())
		} else {
			error()
		}
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



}