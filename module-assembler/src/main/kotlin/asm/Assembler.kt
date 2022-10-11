package asm

import core.binary.BinaryWriter
import java.util.*

class Assembler(parserResult: ParserResult) {


	private val nodes = parserResult.nodes

	private val symbols = parserResult.symbols

	private val imports = parserResult.imports

	private val writer = BinaryWriter()

	private fun error(): Nothing = error("Invalid encoding")

	private val relocations = ArrayList<Relocation>()



	fun assemble(): AssembleResult {
		for(node in nodes) {
			when(node) {
				is InstructionNode -> assemble(node)
				is DbNode          -> handleDb(node)
				is LabelNode       -> handleLabel(node)
				is ConstNode       -> { }
				else               -> error()
			}
		}

		return AssembleResult(writer.trimmedBytes(), imports, relocations)
	}



	private fun handleDb(dbNode: DbNode) {
		for(node in dbNode.components) {
			when(node) {
				is StringNode -> {
					for(c in node.value)
						writer.u8(c.code)
				}
				else -> {
					val value = resolve(node, false)
					if(hasLabel || value !in Byte.MIN_VALUE..Byte.MAX_VALUE) error()
					if(value > 0) writer.u8(value.toInt()) else writer.s8(value.toInt())
				}
			}
		}
	}



	private fun handleLabel(label: LabelNode) {
		label.symbol.pos = writer.pos
	}



	/*
	Node traversal

	- resolve: traverses an AST node and populates register, label, and disp information
	- write: takes an integer value and writes it to data and creates any necessary relocations.
	- encode: handles both resolve and write in a single function.
	- resolve and write must be separated for instructions where the encoding can change based on the immediate width.
	 */



	private var baseReg: Register? = null
	private var indexReg: Register? = null
	private var indexScale = 0
	private var posLabel: PosRef? = null
	private var negLabel: PosRef? = null // only non-null if posLabel is also non-null
	private var hasLabel = false
	private var aso = false



	private fun resolve(root: AstNode, isMem: Boolean = false): Long {
		baseReg = null
		indexReg = null
		indexScale = 0
		posLabel = null
		negLabel = null
		aso = false

		fun rec(node: AstNode, positivity: Int): Long {
			if(node is RegNode) {
				if(positivity <= 0 || !isMem) error()

				if(baseReg != null) {
					if(indexReg != null)
						error()
					indexReg = node.value
					indexScale = 1
				} else
					baseReg = node.value

				return 0
			}

			if(node is UnaryNode)
				return node.op.calculate(rec(node.node, positivity * node.op.positivity))

			if(node is BinaryNode) {
				if(node.op == BinaryOp.MUL) {
					if(node.left is RegNode && node.right is IntNode) {
						if(indexReg != null || positivity <= 0 || !isMem) error()
						indexReg = node.left.value
						indexScale = node.right.value.toInt()
						return 0
					} else if(node.left is IntNode && node.right is RegNode) {
						if(indexReg != null || positivity <= 0 || !isMem) error()
						indexReg = node.right.value
						indexScale = node.left.value.toInt()
						return 0
					}
				}

				return node.op.calculate(
					rec(node.left, positivity * node.op.leftPositivity),
					rec(node.right, positivity * node.op.rightPositivity)
				)
			}

			if(node is IntNode)
				return node.value

			if(node is IdNode) {
				val symbol = symbols[node.name] ?: error()

				if(symbol is IntSymbol)
					return symbol.value

				if(symbol is PosRef) {
					if(positivity == 0) {
						error()
					} else if(positivity > 0) {
						if(posLabel != null) error()
						posLabel = symbol
					} else {
						if(posLabel == null) error()
						if(negLabel != null) error()
						negLabel = symbol
					}
				} else {
					error()
				}

				return 0
			}

			error()
		}

		val disp = rec(if(root is ImmNode) root.value else root, 1)
		hasLabel = posLabel != null

		if(isMem) {
			if(baseReg != null) {
				if(indexReg != null) {
					if(baseReg!!.width != indexReg!!.width)
						error()
					if(baseReg!!.width.is32)
						aso = true
					else if(baseReg!!.width.isNot64)
						error()

					if(indexReg!!.isSP) {
						if(indexScale != 1) error()
						val temp = indexReg
						indexReg = baseReg
						baseReg = temp
					}
				} else {
					if(baseReg!!.width.is32)
						aso = true
					else if(baseReg!!.width.isNot64)
						error()
				}
			}
		}
		return disp
	}

	/*
	- indirect rsp/r12 must use SIB (with or without index, with or without disp) due to SIB encoding
	- indirect rbp/r13 must use +disp8 or +disp32 due to RIP-relative encoding
	- rsp can never be used as an index (but r12 can be)
	- if mod == 00, then rbp/r13 cannot be used as a base.
		- In this case, +disp8 or +disp32 is required.
	 */


	private fun write(value: Long, width: Width) {
		when(width) {
			Width.BIT8 ->
				if(!value.isImm8)
					error()
				else
					writer.s8(value.toInt())

			Width.BIT16 ->
				if(!value.isImm16)
					error()
				else
					writer.s16(value.toInt())

			Width.BIT32 ->
				if(!value.isImm32)
					error()
				else
					writer.s32(value.toInt())

			else -> writer.s64(value)
		}
	}



	private fun writeRel(value: Long, width: Width) {
		write(value, width)
		if(negLabel != null) error()
		if(posLabel != null)
			relocations.add(Relocation(posLabel!!, PosRef(writer.pos), writer.pos - width.bytes, width, value))
	}



	private fun writeImm(imm: Long, width: Width, isImm64: Boolean = false) {
		val actualWidth = if(width.is64 && !isImm64) Width.BIT32 else width

		write(imm, actualWidth)

		if(posLabel != null)
			if(negLabel != null)
				relocations.add(Relocation(posLabel!!, negLabel!!, writer.pos - actualWidth.bytes, actualWidth, imm))
			else
				error()
	}



	private fun encodeRel(root: AstNode?, width: Width) =
		writeRel(resolve(root!!, false), width)

	private fun encodeImm(root: AstNode?, width: Width, isImm64: Boolean = false) =
		writeImm(resolve(root!!, false), width, isImm64)



	private fun encodeRel8(root: AstNode?) =
		encodeRel(root, Width.BIT8)

	private fun encodeRel16(root: AstNode?) =
		encodeRel(root, Width.BIT16)

	private fun encodeRel32(root: AstNode?) =
		encodeRel(root, Width.BIT32)



	private fun encodeImm8(root: AstNode?) =
		encodeImm(root, Width.BIT8)

	private fun encodeImm16(root: AstNode?) =
		encodeImm(root, Width.BIT16)

	private fun encodeImm32(root: AstNode?) =
		encodeImm(root, Width.BIT32)



	/*
	Encoding utils
	 */



	private val AstNode?.asReg get() = if(this is RegNode) value else error()

	private val AstNode?.asMem get() = if(this is MemNode) this else error()

	private val Int.isImm8 get() = this in Byte.MIN_VALUE..Byte.MAX_VALUE

	private val Int.isImm16 get() = this in Short.MIN_VALUE..Short.MAX_VALUE

	private val Long.isImm8 get() = this in Byte.MIN_VALUE..Byte.MAX_VALUE

	private val Long.isImm16 get() = this in Short.MIN_VALUE..Short.MAX_VALUE

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



	/*
	Base encodings
	 */



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
		op1       : MemNode,
		widths    : Widths = Widths.ALL
	) {
		val width = op1.width ?: error("Unspecified memory operand width")
		if(width !in widths) error()
		if(width.is16) writer.u8(0x66)
		encodeMem(opcode + (widths.bits and width.opcodeOffset), op1, width.rex and widths.rexMod, 0, extension)
	}



	private fun encodeFpu1M(opcode: Int, extension: Int, op1: MemNode) {
		encodeMem(opcode, op1, 0, 0, extension)
	}



	/**
	 * Encodes 2 registers of equal width in ModRM:RM and ModRM:REG respectively
	 */
	private fun encode2RR(
		opcode : Int,
		op1    : Register,
		op2    : Register, 
		widths : Widths
	) {
		if(op1.width != op2.width) error()
		encodeBase2RR(opcode, op1, op2, widths)
	}



	private fun encodeBase2RR(
		opcode: Int,
		op1: Register,
		op2: Register,
		widths: Widths
	) {
		val width = op1.width
		if(width !in widths) error()
		if(width.is16) writer.u8(0x66)
		encodeRex(width.rex and widths.rexMod, op2.rex, 0, op1.rex)
		encodeOpcode(opcode + (width.opcodeOffset and widths.bits))
		encodeModRM(0b11, op2.value, op1.value)
	}



	private fun encodeBase2RM(
		opcode : Int,
		op1    : Register,
		op2    : MemNode,
		widths : Widths
	) {
		val width = op1.width
		if(width !in widths) error()
		if(op1.width.is16) writer.u8(0x66)
		encodeMem(opcode + (op1.width.opcodeOffset and widths.bits), op2, op1.width.rex, op1.rex, op1.value)
	}



	/**
	 * Encodes a register operand and a memory operand of equal width.
	 */
	private fun encode2RM(
		opcode : Int,
		op1    : Register,
		op2    : MemNode,
		widths : Widths
	) {
		if(op2.width != null && op2.width != op1.width) error()
		encodeBase2RM(opcode, op1, op2, widths)
	}

	

	/*
	Compound encodings
	- Should only be used as standalone encoding functions with no checks for operand type.
	 */



	/**
	 * Encodes either a register or a memory operand.
	 */
	private fun encode1RM(
		opcode    : Int,
		extension : Int,
		node      : InstructionNode,
		widths    : Widths
	) {
		when(val op1 = node.op1) {
			is RegNode -> encode1R(opcode, extension, op1.value, widths)
			is MemNode      -> encode1M(opcode, extension, op1, widths)
			else            -> error()
		}
	}



	private fun encode2RMR(
		opcode : Int, 
		node   : InstructionNode, 
		widths : Widths
	) {
		if(node.op2 !is RegNode) error()
		when(node.op1) {
			is RegNode -> encode2RR(opcode, node.op1.value, node.op2.value, widths)
			is MemNode      -> encode2RM(opcode, node.op2.value, node.op1, widths)
			else            -> error()
		}
	}



	private fun encode2RRM(
		opcode : Int,
		node   : InstructionNode, 
		widths : Widths
	) {
		if(node.op1 !is RegNode) error()
		when(node.op2) {
			is RegNode -> encode2RR(opcode, node.op2.value, node.op1.value, widths)
			is MemNode      -> encode2RM(opcode, node.op1.value, node.op2, widths)
			else            -> error()
		}
	}



	private fun encodeMem(opcode: Int, memNode: MemNode, rexW: Int, rexR: Int, reg: Int) {
		val disp = resolve(memNode.value, true).toInt()
		val base = baseReg
		val index = indexReg
		val scale = indexScale
		val label = posLabel
		val negLabel = negLabel

		if(aso) writer.u8(0x67)

		// RIP-relative disp32
		if(label != null && negLabel == null) {
			if(base != null || index != null) error()
			encodeRex(rexW, rexR, 0, 0)
			encodeOpcode(opcode)
			encodeModRM(0b00, reg, 0b101)
			writer.s32(disp)
			relocations.add(Relocation(label, PosRef(writer.pos), writer.pos - 4, Width.BIT32, disp.toLong()))
			return
		}

		val mod = when {
			label != null -> 2
			disp == 0     -> if(base != null && base.value == 5) 1 else 0
			disp.isImm8   -> 1
			else          -> 2
		}

		fun checkRelocation() {
			if(label != null)
				if(negLabel != null)
					relocations.add(Relocation(label, negLabel, writer.pos, Width.BIT32, disp.toLong()))
				else
					error()
		}

		if(index != null) { // SIB
			if(scale.countOneBits() != 1) error()
			val finalScale = scale.countTrailingZeroBits()

			if(base != null) {
				encodeRex(rexW, rexR, index.rex, base.rex)
				encodeOpcode(opcode)
				encodeModRM(mod, reg, 0b100)
				encodeSIB(finalScale, index.value, base.value)
				checkRelocation()
				if(mod == 0b01) writer.s8(disp) else if(mod == 0b10) writer.s32(disp)
			} else {
				encodeRex(rexW, rexR, index.rex, 0)
				encodeOpcode(opcode)
				encodeModRM(0, reg, 0b100)
				encodeSIB(finalScale, index.value, 0b101)
				checkRelocation()
				writer.s32(disp)
			}
		} else if(base != null) { // Indirect
			if(base.isSP) {
				encodeRex(rexW, rexR, 0, base.rex)
				encodeOpcode(opcode)
				encodeModRM(mod, reg, 0b100)
				encodeSIB(0, 0b100, 0b100)
				checkRelocation()
				if(mod == 0b01) writer.s8(disp) else if(mod == 0b10) writer.s32(disp)
				return
			}
			encodeRex(rexW, rexR, 0, base.rex)
			encodeOpcode(opcode)
			encodeModRM(mod, reg, base.value)
			checkRelocation()
			if(mod == 0b01) writer.s8(disp) else if(mod == 0b10) writer.s32(disp)
		} else if(mod != 0) { // Absolute 32-bit
			encodeRex(rexW, rexR, 0, 0)
			encodeOpcode(opcode)
			encodeModRM(0b00, reg, 0b100)
			encodeSIB(0b00, 0b100, 0b101)
			checkRelocation()
			writer.s32(disp)
		} else { // Empty memory operand
			error()
		}
	}



	/*
	Assembly
	 */



	private fun BinaryWriter.u24(value: Int) { u32(value); pos-- }



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

			Mnemonic.LEAVE -> { writer.u8(0xC9) }
			Mnemonic.LEAVEW -> { writer.u16(0xC9_66) }

			Mnemonic.RSM -> writer.u16(0xAA_0F)

			Mnemonic.FADD    -> writer.u16(0xC1DC)
			Mnemonic.FADDP   -> writer.u16(0xC1DE)
			Mnemonic.FMUL    -> writer.u16(0xC9DC)
			Mnemonic.FMULP   -> writer.u16(0xC9DE)
			Mnemonic.FCOM    -> writer.u16(0xD1D8)
			Mnemonic.FCOMP   -> writer.u16(0xD9D8)
			Mnemonic.FCOMPP  -> writer.u16(0xD9DE)
			Mnemonic.FSUB    -> writer.u16(0xE9DC)
			Mnemonic.FSUBP   -> writer.u16(0xE9DE)
			Mnemonic.FSUBR   -> writer.u16(0xE1DC)
			Mnemonic.FSUBRP  -> writer.u16(0xE1DE)
			Mnemonic.FDIV    -> writer.u16(0xF9DC)
			Mnemonic.FDIVP   -> writer.u16(0xF9DE)
			Mnemonic.FDIVR   -> writer.u16(0xF1DC)
			Mnemonic.FDIVRP  -> writer.u16(0xF1DE)
			Mnemonic.FXCH    -> writer.u16(0xC9D9)
			Mnemonic.FNOP    -> writer.u16(0xD0D9)
			Mnemonic.FCHS    -> writer.u16(0xE0D9)
			Mnemonic.FABS    -> writer.u16(0xE1D9)
			Mnemonic.FTST    -> writer.u16(0xE4D9)
			Mnemonic.FXAM    -> writer.u16(0xE5D9)
			Mnemonic.FLD1    -> writer.u16(0xE8D9)
			Mnemonic.FLDL2T  -> writer.u16(0xE9D9)
			Mnemonic.FLDL2E  -> writer.u16(0xEAD9)
			Mnemonic.FLDPI   -> writer.u16(0xE8D9)
			Mnemonic.FLDLG2  -> writer.u16(0xECD9)
			Mnemonic.FLDLN2  -> writer.u16(0xEDD9)
			Mnemonic.FLDZ    -> writer.u16(0xEED9)
			Mnemonic.F2XM1   -> writer.u16(0xF0D9)
			Mnemonic.FYL2X   -> writer.u16(0xF1D9)
			Mnemonic.FPTAN   -> writer.u16(0xF2D9)
			Mnemonic.FPATAN  -> writer.u16(0xF3D9)
			Mnemonic.FXTRACT -> writer.u16(0xF4D9)
			Mnemonic.FPREM1  -> writer.u16(0xF5D9)
			Mnemonic.FDECSTP -> writer.u16(0xF6D9)
			Mnemonic.FINCSTP -> writer.u16(0xF7D9)
			Mnemonic.FPREM   -> writer.u16(0xF8D9)
			Mnemonic.FYL2XP1 -> writer.u16(0xF9D9)
			Mnemonic.FSQRT   -> writer.u16(0xFAD9)
			Mnemonic.FSINCOS -> writer.u16(0xFBD9)
			Mnemonic.FRNDINT -> writer.u16(0xFCD9)
			Mnemonic.FSCALE  -> writer.u16(0xFDD9)
			Mnemonic.FSIN    -> writer.u16(0xFED9)
			Mnemonic.FCOS    -> writer.u16(0xFFD9)
			Mnemonic.FUCOM   -> writer.u16(0xE1DD)
			Mnemonic.FUCOMP  -> writer.u16(0xE9DD)
			Mnemonic.FUCOMPP -> writer.u16(0xE9DA)
			Mnemonic.FCLEX   -> writer.u24(0xE2DB9B)
			Mnemonic.FNCLEX  -> writer.u16(0xE2DB)
			Mnemonic.FINIT   -> writer.u24(0xE3DB9B)

			else -> error()
		}
	}



	private fun assemble1(node: InstructionNode) {
		when(node.mnemonic) {
			Mnemonic.NOP -> encode1RM(0x1F_0F, 0, node, Widths.NO864)

			Mnemonic.SETA   -> encode1RM(0x97_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETAE  -> encode1RM(0x93_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETB   -> encode1RM(0x92_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETBE  -> encode1RM(0x96_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETC   -> encode1RM(0x92_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETE   -> encode1RM(0x94_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETG   -> encode1RM(0x9F_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETGE  -> encode1RM(0x9D_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETL   -> encode1RM(0x9C_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETLE  -> encode1RM(0x9E_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNA  -> encode1RM(0x96_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNAE -> encode1RM(0x92_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNB  -> encode1RM(0x93_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNBE -> encode1RM(0x97_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNC  -> encode1RM(0x93_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNE  -> encode1RM(0x95_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNG  -> encode1RM(0x9E_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNGE -> encode1RM(0x9C_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNL  -> encode1RM(0x9D_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNLE -> encode1RM(0x9F_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNO  -> encode1RM(0x91_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNP  -> encode1RM(0x9B_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNS  -> encode1RM(0x99_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETNZ  -> encode1RM(0x95_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETO   -> encode1RM(0x90_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETP   -> encode1RM(0x9A_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETPE  -> encode1RM(0x9A_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETPO  -> encode1RM(0x9B_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETS   -> encode1RM(0x98_0F, 0, node, Widths.ONLY8)
			Mnemonic.SETZ   -> encode1RM(0x94_0F, 0, node, Widths.ONLY8)

			Mnemonic.RET   -> { writer.u8(0xC2); encodeImm16(node.op1) }
			Mnemonic.RETF  -> { writer.u8(0xCA); encodeImm16(node.op1) }
			Mnemonic.INT   -> { writer.u8(0xCD); encodeImm16(node.op1) }
			Mnemonic.BSWAP -> encode1OpReg(0xC8_0F, node.op1.asReg, Widths.NO16)
			Mnemonic.PUSH  -> assemblePUSH(node)
			Mnemonic.POP   -> assemblePOP(node)
			Mnemonic.INC   -> encode1RM(0xFE, 0, node, Widths.ALL)
			Mnemonic.DEC   -> encode1RM(0xFE, 1, node, Widths.ALL)

			Mnemonic.NOT  -> encode1RM(0xF6, 2, node, Widths.ALL)
			Mnemonic.NEG  -> encode1RM(0xF6, 3, node, Widths.ALL)
			Mnemonic.MUL  -> encode1RM(0xF6, 4, node, Widths.ALL)
			Mnemonic.IMUL -> encode1RM(0xF6, 5, node, Widths.ALL)
			Mnemonic.DIV  -> encode1RM(0xF6, 6, node, Widths.ALL)
			Mnemonic.IDIV -> encode1RM(0xF6, 7, node, Widths.ALL)

			Mnemonic.ENTER -> assembleENTER(node)
			Mnemonic.ENTERW -> assembleENTER(node)

			Mnemonic.JMP -> assembleJMP(node)
			Mnemonic.CALL -> assembleCALL(node)

			Mnemonic.JA   -> assembleJCC(node, 0x77, 0x87_0F)
			Mnemonic.JAE  -> assembleJCC(node, 0x73, 0x83_0F)
			Mnemonic.JB   -> assembleJCC(node, 0x72, 0x82_0F)
			Mnemonic.JBE  -> assembleJCC(node, 0x76, 0x86_0F)
			Mnemonic.JC   -> assembleJCC(node, 0x72, 0x82_0F)
			Mnemonic.JE   -> assembleJCC(node, 0x74, 0x84_0F)
			Mnemonic.JG   -> assembleJCC(node, 0x7F, 0x8F_0F)
			Mnemonic.JGE  -> assembleJCC(node, 0x7D, 0x8D_0F)
			Mnemonic.JL   -> assembleJCC(node, 0x7C, 0x8C_0F)
			Mnemonic.JLE  -> assembleJCC(node, 0x7E, 0x8E_0F)
			Mnemonic.JNA  -> assembleJCC(node, 0x76, 0x86_0F)
			Mnemonic.JNAE -> assembleJCC(node, 0x72, 0x82_0F)
			Mnemonic.JNB  -> assembleJCC(node, 0x73, 0x83_0F)
			Mnemonic.JNBE -> assembleJCC(node, 0x77, 0x87_0F)
			Mnemonic.JNC  -> assembleJCC(node, 0x73, 0x83_0F)
			Mnemonic.JNE  -> assembleJCC(node, 0x75, 0x85_0F)
			Mnemonic.JNG  -> assembleJCC(node, 0x7E, 0x8E_0F)
			Mnemonic.JNGE -> assembleJCC(node, 0x7C, 0x8C_0F)
			Mnemonic.JNL  -> assembleJCC(node, 0x7D, 0x8D_0F)
			Mnemonic.JNLE -> assembleJCC(node, 0x7F, 0x8F_0F)
			Mnemonic.JNO  -> assembleJCC(node, 0x71, 0x81_0F)
			Mnemonic.JNP  -> assembleJCC(node, 0x7B, 0x8B_0F)
			Mnemonic.JNS  -> assembleJCC(node, 0x79, 0x89_0F)
			Mnemonic.JNZ  -> assembleJCC(node, 0x75, 0x85_0F)
			Mnemonic.JO   -> assembleJCC(node, 0x70, 0x80_0F)
			Mnemonic.JP   -> assembleJCC(node, 0x7A, 0x8A_0F)
			Mnemonic.JPE  -> assembleJCC(node, 0x7A, 0x8A_0F)
			Mnemonic.JPO  -> assembleJCC(node, 0x7B, 0x8B_0F)
			Mnemonic.JS   -> assembleJCC(node, 0x78, 0x88_0F)
			Mnemonic.JZ   -> assembleJCC(node, 0x74, 0x84_0F)

			Mnemonic.LOOP   -> { writer.u8(0xE2); encodeRel8(node.op1) }
			Mnemonic.LOOPE  -> { writer.u8(0xE1);encodeRel8(node.op1) }
			Mnemonic.LOOPNE -> { writer.u8(0xE0); encodeRel8(node.op1) }

			Mnemonic.FLD -> assembleFLD(node)

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

			Mnemonic.CMOVA   -> encode2RRM(0x47_0F, node, Widths.NO8)
			Mnemonic.CMOVAE  -> encode2RRM(0x43_0F, node, Widths.NO8)
			Mnemonic.CMOVB   -> encode2RRM(0x42_0F, node, Widths.NO8)
			Mnemonic.CMOVBE  -> encode2RRM(0x46_0F, node, Widths.NO8)
			Mnemonic.CMOVC   -> encode2RRM(0x42_0F, node, Widths.NO8)
			Mnemonic.CMOVE   -> encode2RRM(0x44_0F, node, Widths.NO8)
			Mnemonic.CMOVG   -> encode2RRM(0x4F_0F, node, Widths.NO8)
			Mnemonic.CMOVGE  -> encode2RRM(0x4D_0F, node, Widths.NO8)
			Mnemonic.CMOVL   -> encode2RRM(0x4C_0F, node, Widths.NO8)
			Mnemonic.CMOVLE  -> encode2RRM(0x4E_0F, node, Widths.NO8)
			Mnemonic.CMOVNA  -> encode2RRM(0x46_0F, node, Widths.NO8)
			Mnemonic.CMOVNAE -> encode2RRM(0x42_0F, node, Widths.NO8)
			Mnemonic.CMOVNB  -> encode2RRM(0x43_0F, node, Widths.NO8)
			Mnemonic.CMOVNBE -> encode2RRM(0x47_0F, node, Widths.NO8)
			Mnemonic.CMOVNC  -> encode2RRM(0x43_0F, node, Widths.NO8)
			Mnemonic.CMOVNE  -> encode2RRM(0x45_0F, node, Widths.NO8)
			Mnemonic.CMOVNG  -> encode2RRM(0x4E_0F, node, Widths.NO8)
			Mnemonic.CMOVNGE -> encode2RRM(0x4C_0F, node, Widths.NO8)
			Mnemonic.CMOVNL  -> encode2RRM(0x4D_0F, node, Widths.NO8)
			Mnemonic.CMOVNLE -> encode2RRM(0x4F_0F, node, Widths.NO8)
			Mnemonic.CMOVNO  -> encode2RRM(0x41_0F, node, Widths.NO8)
			Mnemonic.CMOVNP  -> encode2RRM(0x4B_0F, node, Widths.NO8)
			Mnemonic.CMOVNS  -> encode2RRM(0x49_0F, node, Widths.NO8)
			Mnemonic.CMOVNZ  -> encode2RRM(0x45_0F, node, Widths.NO8)
			Mnemonic.CMOVO   -> encode2RRM(0x40_0F, node, Widths.NO8)
			Mnemonic.CMOVP   -> encode2RRM(0x4A_0F, node, Widths.NO8)
			Mnemonic.CMOVPE  -> encode2RRM(0x4A_0F, node, Widths.NO8)
			Mnemonic.CMOVPO  -> encode2RRM(0x4B_0F, node, Widths.NO8)
			Mnemonic.CMOVS   -> encode2RRM(0x48_0F, node, Widths.NO8)
			Mnemonic.CMOVZ   -> encode2RRM(0x44_0F, node, Widths.NO8)

			Mnemonic.MOVZX   -> assembleMovExtend(node, 0xB6_0F, 0xB7_0F)
			Mnemonic.MOVSX   -> assembleMovExtend(node, 0xBE_0F, 0xBF_0F)
			Mnemonic.MOVSXD  -> assembleMOVSXD(node)

			Mnemonic.IN  -> assembleIN(node)
			Mnemonic.OUT -> assembleOUT(node)

			Mnemonic.LEA -> encode2RM(0x8D, node.op1.asReg, node.op2.asMem, Widths.NO8)

			Mnemonic.XCHG -> assembleXCHG(node)

			Mnemonic.MOV  -> assembleMOV(node)

			Mnemonic.TEST -> assembleTEST(node)

			Mnemonic.IMUL -> encode2RRM(0xAF_0F, node, Widths.NO8)

			Mnemonic.ENTER -> assembleENTER(node)
			Mnemonic.ENTERW -> assembleENTER(node)

			Mnemonic.BT  -> assembleBT(node, 0xA3_0F, 4)
			Mnemonic.BTS -> assembleBT(node, 0xAB_0F, 5)
			Mnemonic.BTR -> assembleBT(node, 0xB3_0F, 6)
			Mnemonic.BTC -> assembleBT(node, 0xBB_0F, 7)

			Mnemonic.BSF -> encode2RRM(0xBC_0F, node, Widths.NO8)
			Mnemonic.BSR -> encode2RRM(0xBD_0F, node, Widths.NO8)

			Mnemonic.MOVNTI -> encode2RM(0xC3_0F, node.op2.asReg, node.op1.asMem, Widths.NO16)

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




	private fun encodeFpu1ST(opcode: Int, op1: STRegister) {
		writer.u16(opcode + (op1.value shl 8))
	}



	private fun assembleFLD(node: InstructionNode) {
		if(node.op1 is STRegNode) {
			encodeFpu1ST(0xC0D9, node.op1.value)
		} else if(node.op1 is MemNode) {
			when(node.op1.width) {
				null -> error()
				Width.BIT32 -> encodeFpu1M(0xD9, 0, node.op1)
				Width.BIT64 -> encodeFpu1M(0xDD, 0, node.op1)
				Width.BIT80 -> encodeFpu1M(0xDB, 5, node.op1)
				else -> error()
			}
		} else {
			error()
		}
	}



	/**
	 * 	   0F B6  MOVZX  R16/32/64 RM8
	 *     0F B6  MOVZX  R32/64    RM16
	 *     0F BE  MOVSX  R16/32/64 RM8
	 *     0F BF  MOVSX  R32/64    RM16
	 */
	private fun assembleMovExtend(node: InstructionNode, opcode1: Int, opcode2: Int) {
		val op1 = node.op1.asReg

		if(node.op2 is RegNode) {
			val op2 = node.op2.value

			when(op2.width) {
				Width.BIT8  -> encodeBase2RR(opcode1, op1, op2, Widths.NO8)
				Width.BIT16 -> encodeBase2RR(opcode2, op1, op2, Widths.NO16)
				else        -> error()
			}
		} else if(node.op2 is MemNode) {
			when(node.op2.width) {
				null        -> error()
				Width.BIT8  -> encodeBase2RM(opcode1, op1, node.op2, Widths.NO8)
				Width.BIT16 -> encodeBase2RM(opcode2, op1, node.op2, Widths.NO16)
				else        -> error()
			}
		} else {
			error()
		}
	}



	/**
	 *     63 MOVSXD R16 RM16 (OSO)
	 *     63 MOVSXD R32 RM32
	 *     63 MOVSXD R64 RM32 (REXW)
	 */
	private fun assembleMOVSXD(node: InstructionNode) {
		val op1 = node.op1.asReg

		if(op1.width.isNot64) error()

		if(node.op2 is RegNode) {
			val op2 = node.op2.value

			if(op2.width.isNot32) error()

			encodeBase2RR(0x63, op1, op2, Widths.NO8)
		} else if(node.op2 is MemNode) {
			val width2 = node.op2.width

			if(width2 != null && width2.isNot32) error()

			encodeBase2RM(0x63, op1, node.op2, Widths.NO8)
		} else {
			error()
		}
	}



	/**
	 *     0F A3    BT   RM R
	 *     0F BA/4  BT   RM IMM8
	 *     0F BB    BTC  RM R
	 *     0F BA/7  BTC  RM IMM8
	 *     0F B3    BTR  RM R
	 *     0F BA/6  BTR  RM IMM8
	 *     0F AB    BTS  RM R
	 *     0F BA/5  BTS  RM IMM8
	 */
	private fun assembleBT(node: InstructionNode, opcode: Int, extension: Int) {
		if(node.op2 is RegNode) {
			encode2RMR(opcode, node, Widths.NO8)
		} else if(node.op2 is ImmNode) {
			encode1RM(0xBA_0F, extension, node, Widths.NO8)
			encodeImm8(node.op2)
		} else {
			error()
		}
	}



	private fun assembleJCC(node: InstructionNode, rel8Opcode: Int, rel32Opcode: Int) {
		val rel = resolve(node.op1!!)

		if(!hasLabel && (node.modifier == Modifier.SHORT || rel.isImm8)) {
			writer.u8(rel8Opcode)
			writeRel(rel, Width.BIT8)
		} else {
			writer.u16(rel32Opcode)
			writeRel(rel, Width.BIT32)
		}
	}
	
	
	
	/**
	 *     E8    CALL  REL32
	 *     FF/2  CALL  RM64
	 *     FF/3  CALL  M16:16/32/64  (FAR)
	 */
	private fun assembleCALL(node: InstructionNode) {
		if(node.op1 is ImmNode) {
			val rel = resolve(node.op1)
			writer.u8(0xE8)
			writeRel(rel, Width.BIT32)
		} else if(node.modifier == Modifier.FAR) {
			encode1M(0xFF, 3, node.op1.asMem, Widths.NO8)
		} else {
			encode1RM(0xFF, 2, node, Widths.ONLY64)
		}
	}



	/**
	 *     EB    JMP  REL8
	 *     E9    JMP  REL32
	 *     FF/4  JMP  RM64
	 *     FF/5  JMP  M16:16/32/64  (FAR)
	 */
	private fun assembleJMP(node: InstructionNode) {
		if(node.op1 is ImmNode) {
			val rel = resolve(node.op1)
			if(!hasLabel && (node.modifier == Modifier.SHORT || rel.isImm8)) {
				writer.u8(0xEB)
				writeRel(rel, Width.BIT8)
			} else {
				writer.u8(0xE9)
				writeRel(rel, Width.BIT32)
			}
		} else if(node.modifier == Modifier.FAR) {
			encode1M(0xFF, 5, node.op1.asMem, Widths.NO8)
		} else {
			encode1RM(0xFF, 4, node, Widths.ONLY64)
		}
	}



	private fun assembleENTER(node: InstructionNode) {
		if(node.mnemonic == Mnemonic.ENTERW)
			writer.u8(0x66)

		writer.u8(0xC8)

		encodeImm16(node.op1)

		if(node.op2 != null)
			encodeImm8(node.op2)
		else
			writer.s8(0)
	}



	/**
	 *     F6/5   IMUL  RM8
	 *     F7/5   IMUL  RM
	 *     0F AF  IMUL  R RM
	 *     6B     IMUL  R RM IMM8
	 *     69     IMUL  R RM IMM
	 */
	private fun assembleIMUL(node: InstructionNode) {
		if(node.op1 !is RegNode) error()
		if(node.op3 !is ImmNode) error()

		val imm = resolve(node.op3)

		if(imm.isImm8) {
			encode2RRM(0x6B, node, Widths.NO8)
			writeImm(imm, Width.BIT8)
		} else {
			encode2RRM(0x69, node, Widths.NO8)
			writeImm(imm, node.op1.value.width)
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
		if(node.op1 is RegNode) {
			val op1 = node.op1.value

			if(node.op2 is RegNode) {
				encode2RR(0x88, op1, node.op2.value, Widths.ALL)
			} else if(node.op2 is MemNode) {
				encode2RM(0x8A, op1, node.op2, Widths.ALL)
			} else if(node.op2 is ImmNode) {
				encode1OpReg(0xB0, op1)
				encodeImm(node.op2, op1.width, isImm64 = true)
			} else {
				error()
			}
		} else if(node.op1 is MemNode) {
			if(node.op2 is RegNode) {
				encode2RM(0xC6, node.op2.value, node.op1, Widths.ALL)
			} else if(node.op2 is ImmNode) {
				encode1M(0xC6, 0, node.op1)
				encodeImm(node.op2, node.op1.width ?: error())
			}
		} else {
			error()
		}
	}



	private fun assembleXCHG(node: InstructionNode) {
		if(node.op1 is RegNode) {
			val op1 = node.op1.value
			if(node.op2 is RegNode) {
				val op2 = node.op2.value
				if(op1.isA) encode1OpReg(0x90, op2, Widths.NO8)
				else if(op2.isA) encode1OpReg(0x90, op1, Widths.NO8)
				else encode2RR(0x86, op1, op2, Widths.ALL)
			} else if(node.op2 is MemNode) {
				encode2RM(0x86, op1, node.op2, Widths.ALL)
			} else {
				error()
			}
		} else {
			encode2RMR(0x86, node, Widths.ALL)
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
		if(node.op1 is RegNode) {
			val op1 = node.op1.value
			if(node.op2 is RegNode) {
				encode2RR(0x84, op1, node.op2.value, Widths.ALL)
			} else if(node.op2 is ImmNode) {
				if(op1.isA) encodeNone(0xA8, op1.width)
				else encode1R(0xF6, 0, op1)
				encodeImm(node.op2, op1.width)
			} else {
				error()
			}
		} else if(node.op1 is MemNode) {
			if(node.op2 is RegNode) {
				encode2RM(0x84, node.op2.value, node.op1, Widths.ALL)
			} else if(node.op2 is ImmNode) {
				encode1M(0xF6, 0, node.op1)
				encodeImm(node.op2, node.op1.width ?: error())
			} else {
				error()
			}
		} else {
			error()
		}
	}



	private fun assembleIN(node: InstructionNode) {
		if(node.op1 !is RegNode) error()
		val op1 = node.op1.value
		if(!op1.isA) error()

		if(node.op2 is RegNode) {
			if(node.op2.value != Register.DX) error()
			when(op1.width) {
				Width.BIT8  -> writer.u8(0xEC)
				Width.BIT16 -> writer.u16(0xED_66)
				Width.BIT32 -> writer.u8(0xED)
				else        -> error()
			}
		} else if(node.op2 is ImmNode) {
			when(op1.width) {
				Width.BIT8  -> writer.u8(0xE4)
				Width.BIT16 -> writer.u16(0xE5_66)
				Width.BIT32 -> writer.u8(0xE5)
				else        -> error()
			}
			encodeImm8(node.op2)
		}
	}



	private fun assembleOUT(node: InstructionNode) {
		if(node.op2 !is RegNode) error()
		val op2 = node.op2.value
		if(!op2.isA) error()

		if(node.op1 is RegNode) {
			if(node.op1.value != Register.DX) error()
			when(op2.width) {
				Width.BIT8  -> writer.u8(0xEE)
				Width.BIT16 -> writer.u16(0xEF_66)
				Width.BIT32 -> writer.u8(0xEF)
				else        -> error()
			}
		} else if(node.op1 is ImmNode) {
			when(op2.width) {
				Width.BIT8  -> writer.u8(0xE6)
				Width.BIT16 -> writer.u16(0xE7_66)
				Width.BIT32 -> writer.u8(0xE7)
				else        -> error()
			}
			encodeImm8(node.op1)
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
		if(node.op1 is RegNode) {
			encode1OpReg(0x50, node.op1.value, Widths.NO832)
		} else if(node.op1 is MemNode) {
			encode1M(0xFF, 6, node.op1, Widths.NO832)
		} else if(node.op1 is ImmNode) {
			val imm = resolve(node.op1)
			if(imm.isImm8) {
				writer.u8(0x6A)
				writeImm(imm, Width.BIT8)
			} else if(imm.isImm16) {
				writer.u16(0x68_66)
				writeImm(imm, Width.BIT16)
			} else {
				writer.u8(0x68)
				writeImm(imm, Width.BIT32)
			}
		} else if(node.op1 is SRegNode) {
			when(node.op1.value) {
				SRegister.FS -> writer.u16(0xA0_0F)
				SRegister.GS -> writer.u16(0xA8_0F)
				else         -> error()
			}
		} else {
			error()
		}
	}



	/**
	 * 8F/0   POP  RM
	 * 58     POP  R   (OPREG)
	 * 0F A1  POP  FS  (default 64-bit, 66 for 16-bit)
	 * 0F A9  POP  GS  (default 64-bit, 66 for 16-bit)
	 */
	private fun assemblePOP(node: InstructionNode) {
		if(node.op1 is RegNode) {
			encode1OpReg(0x58, node.op1.value, Widths.NO832)
		} else if(node.op1 is MemNode) {
			encode1M(0x8F, 0, node.op1, Widths.NO832)
		} else if(node.op1 is SRegNode) {
			if(node.modifier == Modifier.O16) writer.u8(0x66)
			when(node.op1.value) {
				SRegister.FS -> writer.u16(0xA1_0F)
				SRegister.GS -> writer.u16(0xA9_0F)
				else         -> error()
			}
		} else {
			error()
		}
	}



	/**
	 * ROL, ROR, RCL, RCR, SHL/SAL, SHR, SAR.
	 */
	private fun assembleGroup2(node: InstructionNode, extension: Int) {
		if(node.op2 is RegNode) {
			if(node.op2.value != Register.CL) error()
			encode1RM(0xD2, extension, node, Widths.ALL)
		} else if(node.op2 is ImmNode) {
			val imm = resolve(node.op2)
			if(imm == 1L) {
				encode1RM(0xD0, extension, node, Widths.ALL)
			} else {
				encode1RM(0xC0, extension, node, Widths.ALL)
				writeImm(imm, Width.BIT8)
			}
		} else {
			error()
		}
	}



	/**
	 * ADD, OR, ADC, SBB, AND, SUB, XOR, CMP
	 */
	private fun assembleGroup1(node: InstructionNode, startOpcode: Int, extension: Int) {
		if(node.op1 is RegNode) {
			val op1 = node.op1.value

			if(node.op2 is RegNode) {
				encode2RR(startOpcode, op1, node.op2.value, Widths.ALL)
			} else if(node.op2 is MemNode) {
				encode2RM(startOpcode + 2, op1, node.op2, Widths.ALL)
			} else if(node.op2 is ImmNode) {
				val imm = resolve(node.op2)
				if(imm in Byte.MIN_VALUE..Byte.MAX_VALUE && op1.width.isNot8) {
					encode1R(0x83, extension, op1, Widths.NO8)
					writeImm(imm, Width.BIT8)
				} else if(op1.isA) {
					encodeNone(startOpcode + 4, op1.width)
					writeImm(imm, op1.width)
				} else {
					encode1R(0x80, extension, op1)
					writeImm(imm, op1.width)
				}
			}
		} else if(node.op1 is MemNode) {
			if(node.op2 is RegNode) {
				encode2RM(startOpcode, node.op2.value, node.op1, Widths.ALL)
			} else if(node.op2 is ImmNode) {
				val imm = resolve(node.op2)
				val width = node.op1.width ?: error()
				if(imm in Byte.MIN_VALUE..Byte.MAX_VALUE && width.isNot8) {
					encode1M(0x83, extension, node.op1, Widths.NO8)
					writeImm(imm, Width.BIT8)
				} else {
					encode1M(0x80, extension, node.op1)
					writeImm(imm, width)
				}
			} else error()
		} else error()
	}



	/**
	 * 0F A4  SHLD  RM R IMM8
	 * 0F A5  SHLD  RM R CL
	 * 0F AC  SHRD  RM R IMM8
	 * 0F AD  SHRD  RM R CL
	 */
	private fun assembleShiftD(node: InstructionNode, startOpcode: Int) {
		if(node.op3 is RegNode) {
			if(node.op3.value != Register.CL) error()
			encode2RMR(startOpcode + 1, node, Widths.NO8)
		} else if(node.op3 is ImmNode) {
			encode2RMR(startOpcode, node, Widths.NO8)
			encodeImm8(node.op3)
		} else {
			error()
		}
	}



	/**
	 * MOVS, CMPS, LODS, SCAS, STOS
	 */
	private fun assembleString(node: InstructionNode, opcode: Int) {
		if(node.modifier == Modifier.REP)
			writer.u8(0xF3)
		else if(node.modifier == Modifier.REPNE)
			writer.u8(0xF2)

		if(node.mnemonic.stringWidth!!.is16)
			writer.u8(0x66)
		else if(node.mnemonic.stringWidth.is64)
			writer.u8(0x48)

		writer.u8(opcode)
	}



}