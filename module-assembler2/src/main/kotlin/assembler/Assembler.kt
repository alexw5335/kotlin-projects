package assembler

import core.binary.BinaryWriter
import assembler.Width.*

class Assembler(private val parseResult: ParserResult) {


	private val symbols = parseResult.symbols

	private fun error(): Nothing = error("Invalid encoding")

	private val groups = EncodingReader().read()



	private lateinit var group: InstructionGroup

	private lateinit var encoding: Instruction

	private var rexRequired = false

	private var rexDisallowed = false

	private var bssSize = 0

	private val dataWriter = BinaryWriter()

	private val textWriter = BinaryWriter()

	private var writer = textWriter

	private val relocations = ArrayList<Relocation>()



	fun assemble(): AssemblerResult {
		for(node in parseResult.nodes) {
			when(node) {
				is InstructionNode -> assemble(node)
				is LabelNode       -> handleLabel(node)
				else               -> error()
			}
		}

		return AssemblerResult(
			text        = textWriter.trimmedBytes(),
			data        = dataWriter.trimmedBytes(),
			bssSize     = bssSize,
			imports     = parseResult.imports,
			relocations = relocations,
			symbols     = symbols
		)
	}



	private fun handleLabel(node: LabelNode) {
		node.symbol.pos = writer.pos
		node.symbol.section = Section.TEXT
	}



	/*
	Resolution
	 */



	private var baseReg: Register? = null
	private var indexReg: Register? = null
	private var indexScale = 0
	private var aso = false
	private var refCount = 0
	private val hasReloc get() = refCount > 0
	private var importSymbol: Symbol? = null



	private fun resolveRec(node: AstNode, positivity: Int): Long {
		if(node is RegNode) {
			if(positivity <= 0) error()

			if(baseReg != null) {
				if(indexReg != null) error()
				indexReg = node.value
				indexScale = 1
			} else {
				baseReg = node.value
			}

			return 0
		}

		if(node is UnaryNode) {
			return node.op.calculate(resolveRec(node.node, positivity * node.op.positivity))
		}

		if(node is BinaryNode) {
			if(node.op == BinaryOp.MUL) {
				if(node.left is RegNode && node.right is IntNode) {
					if(indexReg != null || positivity <= 0) error()
					indexReg = node.left.value
					indexScale = node.right.value.toInt()
					return 0
				} else if(node.left is IntNode && node.right is RegNode) {
					if(indexReg != null || positivity <= 0) error()
					indexReg = node.right.value
					indexScale = node.left.value.toInt()
					return 0
				}
			}

			return node.op.calculate(
				resolveRec(node.left, positivity * node.op.leftPositivity),
				resolveRec(node.right, positivity * node.op.rightPositivity)
			)
		}

		if(node is IntNode)
			return node.value

		if(node is IdNode) {
			val symbol = symbols[node.name] ?: error()

			if(symbol is IntSymbol)
				return symbol.value

			if(symbol is ImportSymbol) {
				refCount++
				importSymbol = symbol
				return 0
			}

			if(symbol is Ref) {
				refCount++
				return 0
			}

			error()
		}

		error()
	}



	private fun resolve(root: AstNode, isMem: Boolean = false): Long {
		baseReg = null
		indexReg = null
		indexScale = 0
		aso = false
		importSymbol = null
		refCount = 0

		val disp = resolveRec(if(root is ImmNode) root.value else root, 1)

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
		} else if(baseReg != null || indexReg != null) {
			error()
		}

		if(importSymbol != null && refCount != 1) error()

		return disp
	}



	private fun addImmReloc(node: AstNode, width: Width) {
		relocations.add(Relocation(
			position = writer.pos,
			width    = width,
			value    = node,
			base     = null
		))
	}



	private fun addReloc(node: AstNode, width: Width, offset: Int = 0) {
		relocations.add(Relocation(
			position = writer.pos,
			width    = width,
			value    = node,
			base     = Ref(Section.TEXT, writer.pos + width.bytes + offset)
		))
	}



	/*
	Assembly
	 */



	private fun encoding(operands: Operands) {
		var bits = group.operandsBits
		if(bits and operands.bit == 0) error("Invalid encoding")
		bits = group.operandsBits and (operands.bit - 1)
		encoding = group.instructions[bits.countOneBits()]
	}



	private fun assemble(node: InstructionNode) {
		rexRequired = false
		rexDisallowed = false

		group = groups[node.mnemonic] ?: error("No encodings for mnemonic ${node.mnemonic}")

		val customEncoding = customEncodings[node.mnemonic]

		if(customEncoding != null)
			customEncoding(node)
		else
			operands(node)
	}



	/*
	Base encoding
	 */



	private fun opcodeLength(opcode: Int) =
		((39 - (opcode or 1).countLeadingZeroBits()) and -8) shr 3



	private fun writeOpcode(opcode: Int) {
		writer.u32(opcode)
		writer.pos -= 4 - opcodeLength(opcode)
	}



	private fun writeOpReg(opcode: Int, offset: Int) {
		val length = opcodeLength(opcode)
		writer.u32(opcode + (offset shl ((length - 1) shl 3)))
		writer.pos -= 4 - length
	}



	private fun writeModRM(mod: Int, reg: Int, rm: Int) {
		writer.u8((mod shl 6) or (reg shl 3) or rm)
	}



	private fun writeSib(scale: Int, index: Int, base: Int) {
		writer.u8((scale shl 6) or (index shl 3) or base)
	}



	private fun writeRex(w: Int, r: Int, x: Int, b: Int) {
		val value = 0b0100_0000 or (w shl 3) or (r shl 2) or (x shl 1) or b

		if(rexRequired || value != 0b0100_0000)
			if(rexDisallowed)
				error()
			else
				writer.u8(value)
	}



	private fun writeMem(
		opcode: Int,
		node: AstNode,
		rexW: Int,
		rexR: Int,
		reg: Int,
		immLength: Int
	) {
		val disp   = resolve(node, true).toInt()
		val base   = baseReg
		val index  = indexReg
		val scale  = indexScale

		if(aso) writer.u8(0x67)

		val mod = when {
			refCount > 0   -> 2
			disp == 0      -> if(base != null && base.value == 5) 1 else 0
			disp.isImm8    -> 1
			else           -> 2
		}

		fun relocAndDisp() {
			if(hasReloc) {
				relocations.add(Relocation(writer.pos, BIT32, node, null))
				writer.u32(0)
			} else if(mod == 0b01)
				writer.s8(disp)
			else if(mod == 0b10)
				writer.s32(disp)
		}

		if(index != null) { // SIB
			if(scale.countOneBits() != 1) error()
			val finalScale = scale.countTrailingZeroBits()

			if(base != null) {
				writeRex(rexW, rexR, index.rex, base.rex)
				writeOpcode(opcode)
				writeModRM(mod, reg, 0b100)
				writeSib(finalScale, index.value, base.value)
				relocAndDisp()
			} else {
				writeRex(rexW, rexR, index.rex, 0)
				writeOpcode(opcode)
				writeModRM(0, reg, 0b100)
				writeSib(finalScale, index.value, 0b101)
				relocAndDisp()
			}
		} else if(base != null) { // Indirect
			writeRex(rexW, rexR, 0, base.rex)
			writeOpcode(opcode)

			if(base.isSP) {
				writeModRM(mod, reg, 0b100)
				writeSib(0, 0b100, 0b100)
			} else {
				writeModRM(mod, reg, base.value)
			}

			relocAndDisp()
		} else if(refCount == 1) { // RIP-relative
			writeRex(rexW, rexR, 0, 0)
			writeOpcode(opcode)
			writeModRM(0b00, reg, 0b101)
			relocations.add(Relocation(writer.pos, BIT32, node, Ref(Section.TEXT, writer.pos + 4 + immLength)))
			writer.advance(4)
		} else if(mod != 0) { // Absolute 32-bit
			writeRex(rexW, rexR, 0, 0)
			writeOpcode(opcode)
			writeModRM(0b00, reg, 0b100)
			writeSib(0b00, 0b100, 0b101)
			relocAndDisp()
		} else { // Empty memory operand
			error()
		}
	}



	private fun writeRel8(node: ImmNode, value: Long) {
		if(hasReloc) {
			addReloc(node, BIT8)
			writer.advance(1)
		} else {
			if(value !in Byte.MIN_VALUE..Byte.MAX_VALUE) error()
			writer.s8(value.toInt())
		}
	}



	private fun writeRel32(node: ImmNode, value: Long) {
		if(hasReloc) {
			addReloc(node, BIT8)
			writer.advance(4)
		} else {
			if(value !in Int.MIN_VALUE..Int.MAX_VALUE) error()
			writer.s32(value.toInt())
		}
	}



	private fun writeImmInternal(node: ImmNode, width: Width, value: Long) {
		if(hasReloc) {
			addImmReloc(node, width)
			writer.advance(width.bytes)
			return
		}

		if(value !in width) error()

		when(width) {
			BIT8 ->writer.s8(value.toInt())
			BIT16 -> writer.s16(value.toInt())
			BIT32 -> writer.s32(value.toInt())
			BIT64 -> writer.s64(value)
			else  -> error()
		}
	}



	private fun writeImm(node: ImmNode, width: Width, value: Long)
		= writeImmInternal(node, if(width.is64) BIT32 else width, value)



	/*
	Encoding
	 */



/*	private fun encodeNone(width: Width) {
		if(encoding.prefix != 0) writer.u8(encoding.prefix)
		if(width.is16) writer.u8(0x66)
		else if(width.is64) writer.u8(0x48)
		writeOpcode(encoding.opcode + (encoding.widths.bits and width.opcodeOffset))
	}*/



	private fun encodeNone() {
		if(encoding.prefix != 0) writer.u8(encoding.prefix)
		writeOpcode(encoding.opcode)
	}



	private fun encode1O(op1: Register) {
		val width = op1.width
		if(width !in encoding.widths) error()
		if(width.is16) writer.u8(0x66)
		rexRequired = op1.rex8
		rexDisallowed = op1.noRex8
		writeRex(width.rex and encoding.widths.rexMod, 0, 0, op1.rex)
		writeOpReg(encoding.opcode + ((encoding.widths.bits and width.opcodeOffset) shl 3), op1.value)
	}



	/**
	 * Encodes a register in ModRM:RM
	 */
	private fun encode1R(op1: Register) {
		val width = op1.width
		if(width !in encoding.widths) error()
		if(width.is16) writer.u8(0x66)
		rexRequired = op1.rex8
		rexDisallowed = op1.noRex8
		writeRex(width.rex and encoding.widths.rexMod, 0, 0, op1.rex)
		writeOpcode(encoding.opcode + (encoding.widths.bits and width.opcodeOffset))
		writeModRM(0b11, encoding.extension, op1.value)
	}



	/**
	 * Encodes a memory operand.
	 */
	private fun encode1M(op1: MemNode, immLength: Int) {
		val width = op1.width ?: error()
		if(width !in encoding.widths) error()
		if(width.is16) writer.u8(0x66)
		writeMem(
			encoding.opcode + (encoding.widths.bits and width.opcodeOffset),
			op1.value,
			width.rex and encoding.widths.rexMod,
			0,
			encoding.extension,
			immLength
		)
	}



	/**
	 * Encodes 2 registers in ModRM:RM and ModRM:REG respectively
	 */
	private fun encode2RR(op1: Register, op2: Register) {
		val width = op1.width
		if(width !in encoding.widths) error()
		if(width.is16) writer.u8(0x66)
		rexRequired = op1.rex8 || op2.rex8
		rexDisallowed = op1.noRex8 || op2.noRex8
		writeRex(width.rex and encoding.widths.rexMod, op2.rex, 0, op1.rex)
		writeOpcode(encoding.opcode + (width.opcodeOffset and encoding.widths.bits))
		writeModRM(0b11, op2.value, op1.value)
	}



	/**
	 * Encodes a register operand and a memory operand in ModRM:REG and ModRM:RM respectively.
	 */
	private fun encode2RM(op1: Register, op2: MemNode, immLength: Int) {
		val width = op1.width
		if(width !in encoding.widths) error()
		if(width.is16) writer.u8(0x66)
		rexRequired = op1.rex8
		rexDisallowed = op1.noRex8
		writeMem(
			encoding.opcode + (encoding.widths.bits and width.opcodeOffset),
			op2.value,
			width.rex and encoding.widths.rexMod,
			op1.rex,
			op1.value,
			immLength
		)
	}



	/*
	Operands
	 */



	private fun operands(node: InstructionNode) {
		when {
			node.op1 == null -> operands0()
			node.op2 == null -> operands1(node)
			node.op3 == null -> if(node.op1 is RegNode) operands2R(node) else operands2M(node)
			node.op4 == null -> error()
			else             -> error()
		}
	}



	private fun operands0() {
		encoding(Operands.NONE)
		encodeNone()
	}



	private fun operands1(node: InstructionNode) {
		val op1 = node.op1

		if(op1 is RegNode) {
			if(Specifier.O in group) {
				encoding(Operands.O)
				encode1O(op1.value)
			} else {
				encoding(Operands.R)
				encode1R(op1.value)
			}
		} else if(op1 is MemNode) {
			encoding(Operands.M)
			encode1M(op1, 0)
		} else if(op1 is ImmNode) {
			val imm = resolve(op1)

			if(importSymbol != null) {
				encoding(Operands.M)
				encode1M(MemNode(BIT64, op1), 0)
			} else if(Specifier.REL8 in group && ((!hasReloc && node.shortImm) || Specifier.REL32 !in group)) {
				encoding(Operands.REL8)
				encodeNone()
				writeRel8(op1, imm)
			} else if(Specifier.REL32 in group) {
				encoding(Operands.REL32)
				encodeNone()
				writeRel32(op1, imm)
			} else if(Specifier.I8 in group && ((!hasReloc && imm.isImm8) || (Specifier.I16 !in group && Specifier.I32 !in group))) {
				encoding(Operands.I8)
				encodeNone()
				writeImm(op1, BIT8, imm)
			} else if(Specifier.I16 in group && ((!hasReloc && imm.isImm16) || Specifier.I32 !in group)) {
				encoding(Operands.I16)
				encodeNone()
				writeImm(op1, BIT16, imm)
			} else {
				encoding(Operands.I32)
				encodeNone()
				writeImm(op1, BIT32, imm)
			}
		} else {
			error()
		}
	}



	private fun operands2R(node: InstructionNode) {
		val op1 = (node.op1 as RegNode).value
		val op2 = node.op2
		val width = op1.width

		if(op2 is RegNode) {
			val r2 = op2.value
			val width2 = r2.width

			if(width != width2) {
				if(Specifier.RM_CL in group && r2 == Register.CL) {
					encoding(Operands.RM_CL)
					encode1R(op1)
				} else {
					error()
				}
			} else {
				encode2RR(op1, r2)
			}
		} else if(op2 is MemNode) {
			val width2 = op2.width

			if(width2 != null && width2 != width)
				error()

			encoding(Operands.R_M)
			encode2RM(op1, op2, 0)
		} else if(op2 is ImmNode) {
			val imm = resolve(op2)

			if(Specifier.RM_I8 in group && !hasReloc && width.isNot8 && imm.isImm8) {
				encoding(Operands.R_I8)
				encode1R(op1)
				writeImm(op2, BIT8, imm)
			} else if(Specifier.RM_1 in group && !hasReloc && imm == 1L) {
				encoding(Operands.RM_1)
				encode1R(op1)
			} else if(op1.isA && Specifier.A_I in group) {
				encoding(Operands.A_I)
				encodeNone()
				writeImm(op2, width, imm)
			} else {
				encoding(Operands.R_I)
				encode1R(op1)
				writeImm(op2, width, imm)
			}
		} else {
			error()
		}
	}



	private fun operands2M(node: InstructionNode) {
		val op1 = node.op1 as MemNode
		val op2 = node.op2!!

		if(op2 is RegNode) {
			if(Specifier.RM_CL in group && op2.value == Register.CL) {
				encoding(Operands.RM_CL)
				encode1M(op1, 0)
			} else {
				if(op1.width != null && op1.width != op2.value.width) error()
				encoding(Operands.M_R)
				encode2RM(op2.value, op1, 0)
			}
		} else if(op2 is ImmNode) {
			val width = op1.width ?: error()
			val imm = resolve(op2)

			if(Specifier.RM_I8 in group && !hasReloc && width.isNot8 && imm.isImm8) {
				encoding(Operands.M_I8)
				encode1M(op1, 1)
				writeImm(op2, BIT8, imm)
			} else if(Specifier.RM_1 in group && !hasReloc && imm == 1L) {
				encoding(Operands.RM_1)
				encode1M(op1, 0)
			} else {
				encoding(Operands.M_I)
				encode1M(op1, width.immLength)
				writeImm(op2, width, imm)
			}
		} else {
			error()
		}
	}



	/*
	Custom encodings
	 */



	private val customEncodings: Map<Mnemonic, (InstructionNode) -> Unit> = mapOf(
		Mnemonic.IMUL   to ::customEncodeIMUL,
		Mnemonic.XCHG   to ::customEncodeXCHG,
		Mnemonic.MOV    to ::customEncodeMOV,
		Mnemonic.MOVSX  to ::customEncodeMOVSX,
		Mnemonic.MOVZX  to ::customEncodeMOVZX,
		Mnemonic.MOVSXD to ::customEncodeMOVSXD,
		Mnemonic.IN     to ::customEncodeIN,
		Mnemonic.OUT    to ::customEncodeOUT
	)



	private val customEncodingIMUL1 = Instruction(0x6B, 0, 0, Widths.NO8)

	private val customEncodingIMUL2 = Instruction(0x69, 0, 0, Widths.NO8)

	private val customEncodingMOV = Instruction(0xB0, 0, 0, Widths.ALL)

	private val customEncodingXCHG = Instruction(0x90, 0, 0, Widths.NO8)

	private val customEncodingMOVSXD = Instruction(0x63, 0, 0, Widths.NO8)

	private val customEncodingMOVSX1 = Instruction(0xBE0F, 0, 0, Widths.NO8)

	private val customEncodingMOVSX2 = Instruction(0xBF0F, 0, 0, Widths.NO816)

	private val customEncodingMOVZX1 = Instruction(0xB60F, 0, 0, Widths.NO8)

	private val customEncodingMOVZX2 = Instruction(0xB70F, 0, 0, Widths.NO816)



	/**
	 * E6    OUT  I8_AL
	 * E766  OUT  I8_AX
	 * E7    OUT  I8_EAX
	 * EE    OUT  DX_AL
	 * EF66  OUT  DX_AX
	 * EF    OUT  DX_EAX
	 */
	private fun customEncodeOUT(node: InstructionNode) {
		if(node.op3 != null) error()
		if(node.op2 !is RegNode) error()
		val op2 = node.op2.value

		if(!op2.isA) error()

		if(node.op1 is RegNode) {
			if(node.op1.value != Register.DX) error()
			when(op2.width) {
				BIT8  -> writer.u8(0xEE)
				BIT16 -> writer.u16(0xEF66)
				BIT32 -> writer.u8(0xEF)
				else  -> error()
			}
		} else if(node.op1 is ImmNode) {
			val imm = resolve(node.op1)
			when(op2.width) {
				BIT8  -> writer.u8(0xE6)
				BIT16 -> writer.u16(0xE766)
				BIT32 -> writer.u8(0xE7)
				else  -> error()
			}
			if(hasReloc) addImmReloc(node.op1, BIT8)
			else if(!imm.isImm8) error()
			writer.s8(imm.toInt())
		} else {
			error()
		}
	}



	/**
	 * E4    IN  AL_I8
	 * E566  IN  AX_IMM8
	 * E5    IN  EAX_IMM8
	 * EC    IN  AL_DX
	 * ED66  IN  AX_DX
	 * ED    IN  EAX_DX
	 */
	private fun customEncodeIN(node: InstructionNode) {
		if(node.op3 != null) error()
		if(node.op1 !is RegNode) error()
		val op1 = node.op1.value

		if(!op1.isA) error()

		if(node.op2 is RegNode) {
			if(node.op2.value != Register.DX) error()
			when(op1.width) {
				BIT8  -> writer.u8(0xEC)
				BIT16 -> writer.u16(0xED66)
				BIT32 -> writer.u8(0xED)
				else  -> error()
			}
		} else if(node.op2 is ImmNode) {
			val imm = resolve(node.op2)
			when(op1.width) {
				BIT8  -> writer.u8(0xE4)
				BIT16 -> writer.u16(0xE566)
				BIT32 -> writer.u8(0xE5)
				else  -> error()
			}
			if(hasReloc) addImmReloc(node.op2, BIT8)
			else if(!imm.isImm8) error()
			writer.s8(imm.toInt())
		} else {
			error()
		}
	}



	/**
	 *     BE0F  MOVSX   R_RM8   NO8
	 *     BF0F  MOVSX   R_RM16  NO816
	 */
	private fun customEncodeMOVSX(node: InstructionNode) {
		if(node.op3 != null) error()
		if(node.op1 !is RegNode) error()

		if(node.op2 is RegNode) {
			encoding = when(node.op2.value.width) {
				BIT8  -> customEncodingMOVSX1
				BIT16 -> customEncodingMOVSX2
				else  -> error()
			}

			encode2RR(node.op1.value, node.op2.value)
		} else if(node.op2 is MemNode) {
			when(node.op2.width) {
				BIT8  -> customEncodingMOVSX1
				BIT16 -> customEncodingMOVSX2
				else  -> error()
			}

			encode2RM(node.op1.value, node.op2, 0)
		} else {
			error()
		}
	}



	/**
	 *     B60F  MOVZX   R_RM8   NO8
	 *     B70F  MOVZX   R_RM16  NO816
	 */
	private fun customEncodeMOVZX(node: InstructionNode) {
		if(node.op3 != null) error()
		if(node.op1 !is RegNode) error()

		if(node.op2 is RegNode) {
			encoding = when(node.op2.value.width) {
				BIT8  -> customEncodingMOVZX1
				BIT16 -> customEncodingMOVZX2
				else  -> error()
			}

			encode2RR(node.op1.value, node.op2.value)
		} else if(node.op2 is MemNode) {
			encoding = when(node.op2.width) {
				BIT8  -> customEncodingMOVZX1
				BIT16 -> customEncodingMOVZX2
				else  -> error()
			}

			encode2RM(node.op1.value, node.op2, 0)
		} else {
			error()
		}
	}



	/**
	 *     63    MOVSXD  R16_RM16  OSO
	 *     63    MOVSXD  R32_RM32
	 *     63    MOVSXD  R64_RM32  REX.W
	 */
	private fun customEncodeMOVSXD(node: InstructionNode) {
		if(node.op3 != null) error()
		if(node.op1 !is RegNode) error()
		val op1 = node.op1.value
		val width = op1.width

		if(node.op2 is RegNode) {
			val op2 = node.op2.value

			when {
				width.is16 && op2.width.is16 -> { }
				width.is32 && op2.width.is32 -> { }
				width.is64 && op2.width.is32 -> { }
				else                         -> error()
			}

			encoding = customEncodingMOVSXD
			encode2RR(op1, op2)
		} else if(node.op2 is MemNode) {
			val op2 = node.op2

			when {
				op2.width == null            -> { }
				width.is16 && op2.width.is16 -> { }
				width.is32 && op2.width.is32 -> { }
				width.is64 && op2.width.is32 -> { }
				else                         -> error()
			}

			encoding = customEncodingMOVSXD
			encode2RM(op1, op2, 0)
		} else {
			error()
		}
	}



	private fun customEncodeIMUL(node: InstructionNode) {
		if(node.op2 == null || node.op3 == null) operands(node)
		if(node.op4 != null) error()

		if(node.op1 !is RegNode || node.op3 !is ImmNode) error()

		val imm = resolve(node.op3)

		if(node.op2 is RegNode) {
			if(node.op1.value.width != node.op2.value.width) error()

			if(!hasReloc && imm.isImm8) {
				encoding = customEncodingIMUL1
				encode2RR(node.op1.value, node.op2.value)
				writeImm(node.op3, BIT8, imm)
			} else {
				encoding = customEncodingIMUL2
				encode2RR(node.op1.value, node.op2.value)
				writeImm(node.op3, node.op1.value.width, imm)
			}
		} else if(node.op2 is MemNode) {
			if(node.op2.width != null && node.op2.width != node.op1.value.width) error()

			if(!hasReloc && imm.isImm8) {
				encoding = customEncodingIMUL1
				encode2RM(node.op1.value, node.op2, 1)
				writeImm(node.op3, BIT8, imm)
			} else {
				encoding = customEncodingIMUL2
				encode2RM(node.op1.value, node.op2, node.op1.value.width.immLength)
				writeImm(node.op3, node.op1.value.width, imm)
			}
		} else {
			operands(node)
		}
	}

	

	private fun customEncodeMOV(node: InstructionNode) {
		if(node.op3 != null) error()

		if(node.op1 is RegNode && node.op2 is ImmNode) {
			val imm = resolve(node.op2)
			encoding = customEncodingMOV
			encode1O(node.op1.value)
			writeImmInternal(node.op2, node.op1.value.width, imm)
		} else {
			operands(node)
		}
	}



	private fun customEncodeXCHG(node: InstructionNode) {
		if(node.op3 != null) error()

		if(node.op1 is RegNode && node.op2 is RegNode) {
			if(node.op1.value.isA) {
				encoding = customEncodingXCHG
				encode1O(node.op2.value)
			} else if(node.op2.value.isA) {
				encoding = customEncodingXCHG
				encode1O(node.op1.value)
			} else {
				operands(node)
			}
		} else {
			operands(node)
		}
	}


}