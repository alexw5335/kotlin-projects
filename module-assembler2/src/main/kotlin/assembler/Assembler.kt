package assembler

import core.binary.BinaryWriter

class Assembler(private val parseResult: ParserResult) {


	private val symbols = parseResult.symbols

	private fun error(): Nothing = error("Invalid encoding")

	private val groups = EncodingReader().read()



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
				is LabelNode       -> { }
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



	private fun determineEncoding(group: InstructionGroup, operands: Operands): Instruction {
		var bits = group.operandsBits
		if(bits and operands.bit == 0) error("Invalid encoding")
		bits = group.operandsBits and (operands.bit - 1)
		return group.instructions[bits.countOneBits()]
	}




	private fun assemble(node: InstructionNode) {
		rexRequired = false
		rexDisallowed = false

		val group = groups[node.mnemonic] ?: error("No encodings for mnemonic ${node.mnemonic}")

		val customEncoding = customEncodings[node.mnemonic]

		if(customEncoding != null) {
			customEncoding(group, node)
		} else {
			operands(group, node)
		}
	}



	/*
	Base encoding
	 */



	val Int.asImm8 get() = if(this !in Byte.MIN_VALUE..Byte.MAX_VALUE) error() else toInt()

	val Int.asImm16 get() = if(this !in Short.MIN_VALUE..Short.MAX_VALUE) error() else toInt()

	val Long.asImm8 get() = if(this !in Byte.MIN_VALUE..Byte.MAX_VALUE) error() else toInt()

	val Long.asImm16 get() = if(this !in Short.MIN_VALUE..Short.MAX_VALUE) error() else toInt()

	val Long.asImm32 get() = if(this !in Int.MIN_VALUE..Int.MAX_VALUE) error() else toInt()



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



	private fun writeMem(opcode: Int, node: AstNode, rexW: Int, rexR: Int, reg: Int, immLength: Int = 0) {
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
				addReloc(node, Width.BIT32, immLength)
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
			relocAndDisp()
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



	private fun writeImm(node: AstNode, width: Width, value: Int) {
		val actualWidth = if(width.is64) Width.BIT32 else width

		if(hasReloc)
			addImmReloc(node, actualWidth)
		else if(value !in actualWidth)
			error()

		writer.uint(actualWidth, value)
	}



	private fun writeImm8(node: AstNode, value: Int) {
		if(hasReloc)
			addImmReloc(node, Width.BIT8)
		else if(!value.isImm8)
			error()

		writer.s8(value)
	}



	private fun writeImm16(node: AstNode, value: Int) {
		if(hasReloc)
			addImmReloc(node, Width.BIT16)
		else if(!value.isImm16)
			error()

		writer.s16(value)
	}



	private fun writeImm32(node: AstNode, value: Int) {
		if(hasReloc)
			addImmReloc(node, Width.BIT32)

		writer.s32(value)
	}



	private fun writeImm64(node: AstNode, width: Width, value: Long) {
		if(hasReloc)
			addImmReloc(node, width)
		else if(value !in width)
			error()

		writer.ulong(width, value)
	}




	/*
	Encoding
	 */



	private fun encodeNone(encoding: Instruction, width: Width) {
		if(encoding.prefix != 0) writer.u8(encoding.prefix)
		if(width.is16) writer.u8(0x66)
		writeRex(width.rex, 0, 0, 0)
		writeOpcode(encoding.opcode + (encoding.widths.bits and width.opcodeOffset))
	}



	private fun encodeNone(encoding: Instruction) {
		if(encoding.prefix != 0) writer.u8(encoding.prefix)
		writeOpcode(encoding.opcode)
	}



	private fun encode1O(encoding: Instruction, op1: Register) {
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
	private fun encode1R(encoding: Instruction, op1: Register) {
		val width = op1.width
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
	private fun encode1M(encoding: Instruction, op1: MemNode, immLength: Int = 0) {
		val width = op1.width ?: error()
		if(width !in encoding.widths) error()
		if(width.is16) writer.u8(0x66)

		writeMem(
			opcode    = encoding.opcode + (encoding.widths.bits and width.opcodeOffset),
			node      = op1.value,
			rexW      = width.rex and encoding.widths.rexMod,
			rexR      = 0,
			reg       = encoding.extension,
			immLength = immLength
		)
	}



	/**
	 * Encodes 2 registers in ModRM:RM and ModRM:REG respectively
	 */
	private fun encode2RR(encoding: Instruction, op1: Register, op2: Register, width: Width = op1.width) {
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
	private fun encode2RM(encoding: Instruction, op1: Register, op2: MemNode, immLength: Int = 0) {
		val width = op1.width
		if(width !in encoding.widths) error()
		if(width.is16) writer.u8(0x66)
		rexRequired = op1.rex8
		rexDisallowed = op1.noRex8

		writeMem(
			opcode    = encoding.opcode + (width.opcodeOffset and encoding.widths.bits),
			node      = op2.value,
			rexW      = width.rex,
			rexR      = op1.rex,
			reg       = op1.value,
			immLength = immLength
		)
	}



	/*
	Operands
	 */



	private fun operands(group: InstructionGroup, node: InstructionNode) {
		when {
			node.op1 == null -> operands0(group)
			node.op2 == null -> operands1(group, node.op1)
			node.op3 == null -> operands2(group, node.op1, node.op2)
			node.op4 == null -> error()
			else             -> error()
		}
	}



	private fun operands0(group: InstructionGroup) {
		encodeNone(determineEncoding(group, Operands.NONE))
	}



	private fun operands1(group: InstructionGroup, op1: AstNode) {
		if(op1 is RegNode) {
			if(Specifier.O in group) {
				encode1O(determineEncoding(group, Operands.O), op1.value)
			} else {
				encode1R(determineEncoding(group, Operands.R), op1.value)
			}
		} else if(op1 is MemNode) {
			encode1M(determineEncoding(group, Operands.M), op1)
		} else if(op1 is ImmNode) {
			val imm = resolve(op1)

			if(Specifier.REL32 in group) {
				encodeNone(determineEncoding(group, Operands.REL32))
				if(hasReloc) addReloc(op1.value, Width.BIT32)
				writer.s32(imm.asImm32)
			} else if(Specifier.REL8 in group) {
				encodeNone(determineEncoding(group, Operands.REL8))
				if(hasReloc) addReloc(op1.value, Width.BIT8)
				writer.s8(imm.asImm8)
			} else if(Specifier.I8 in group && ((!hasReloc && imm.isImm8) || (Specifier.I16 !in group && Specifier.I32 !in group))) {
				val encoding = determineEncoding(group, Operands.I8)
				encodeNone(encoding)
				writeImm8(op1, imm.toInt())
			} else if(Specifier.I16 in group && ((!hasReloc && imm.isImm16) || Specifier.I32 !in group)) {
				val encoding = determineEncoding(group, Operands.I16)
				encodeNone(encoding)
				writeImm16(op1, imm.toInt())
			} else {
				val encoding = determineEncoding(group, Operands.I32)
				encodeNone(encoding)
				writeImm32(op1, imm.asImm32)
			}
		} else if(op1 is SRegNode) {
			if(op1.value == SRegister.FS) {
				encodeNone(determineEncoding(group, Operands.FS))
			} else if(op1.value == SRegister.GS) {
				encodeNone(determineEncoding(group, Operands.GS))
			} else {
				error()
			}
		} else {
			error()
		}
	}



	private fun operands2R(group: InstructionGroup, r1: Register, op2: AstNode) {
		val width1 = r1.width

		if(op2 is RegNode) {
			val r2 = op2.value
			val width2 = r2.width

			if(width1 != width2) {
				if(width2.is8) {
					if(Specifier.RM_CL in group && r2 == Register.CL)
						encode1R(determineEncoding(group, Operands.R_CL), r1)
					else
						encode2RR(determineEncoding(group, Operands.R_RM8), r2, r1, r1.width)
				} else if(width2.is16) {
					encode2RR(determineEncoding(group, Operands.R_RM16), r2, r1, r1.width)
				} else if(width2.is32 && width1.is64) {
					encode2RR(determineEncoding(group, Operands.R64_RM32), r2, r1, r1.width)
				} else {
					error()
				}
			} else {
				encode2RR(determineEncoding(group, Operands.R_R), r1, r2)
			}

			return
		}

		if(op2 is MemNode) {
			val width2 = op2.width

			if(width2 != null) {
				if(width2.is8) {
					encode2RM(determineEncoding(group, Operands.R_RM8), r1, op2)
				} else if(width2.is16) {
					encode2RM(determineEncoding(group, Operands.R_RM16), r1, op2)
				} else if(width2.is32 && width1.is64) {
					encode2RM(determineEncoding(group, Operands.R64_RM32), r1, op2)
				} else {
					error()
				}
			} else {
				encode2RM(determineEncoding(group, Operands.R_M), r1, op2)
			}

			return
		}

		if(op2 is ImmNode) {
			val imm = resolve(op2)

			if(Specifier.RM_I8 in group && !hasReloc && width1.isNot8 && imm.isImm8) {
				encode1R(determineEncoding(group, Operands.R_I8), r1)
				writeImm8(op2, imm.toInt())
			} else if(Specifier.RM_1 in group && !hasReloc && imm == 1L) {
				encode1R(determineEncoding(group, Operands.R_1), r1)
			} else if(r1.isA && Specifier.A_I in group) {
				encodeNone(determineEncoding(group, Operands.A_I), width1)
				writeImm(op2, width1, imm.toInt())
			} else {
				encode1R(determineEncoding(group, Operands.R_I), r1)
				writeImm(op2, width1, imm.toInt())
			}

			return
		}

		error()
	}



	private fun operands2(group: InstructionGroup, op1: AstNode, op2: AstNode) {
		if(op1 is RegNode) {
			operands2R(group, op1.value, op2)
		} else if(op1 is MemNode) {
			if(op2 is RegNode) {
				if(Specifier.RM_CL in group && op2.value == Register.CL) {
					encode1M(determineEncoding(group, Operands.M_CL), op1)
				} else {
					if(op1.width != null && op1.width != op2.value.width) error()
					encode2RM(determineEncoding(group, Operands.M_R), op2.value, op1)
				}
			} else if(op2 is ImmNode) {
				val width = op1.width ?: error()
				val imm = resolve(op2)

				if(Specifier.RM_I8 in group && !hasReloc && width.isNot8 && imm.isImm8) {
					encode1M(determineEncoding(group, Operands.M_I8), op1, 1)
					writeImm8(op2, imm.toInt())
				} else if(Specifier.RM_1 in group && !hasReloc && imm == 1L) {
					encode1M(determineEncoding(group, Operands.M_1), op1)
				} else {
					encode1M(determineEncoding(group, Operands.M_I), op1, width.bytes)
					writeImm(op2, width, imm.toInt())
				}
			} else {
				error()
			}
		} else {
			error()
		}
	}



	/*
	Custom encodings:
	XCHG
	MOV
	 */




	private val customEncodings = mapOf(
		Mnemonic.CALL to ::customEncodeCALL,
		Mnemonic.IMUL to ::customEncodeIMUL,
		Mnemonic.XCHG to ::customEncodeXCHG,
		Mnemonic.MOV  to ::customEncodeMOV
	)



	private val customEncodingIMUL1 = Instruction(0x6B, 0, 0, Widths.NO8)

	private val customEncodingIMUL2 = Instruction(0x69, 0, 0, Widths.NO8)

	private val customEncodingMOV1 = Instruction(0xB0, 0, 0, Widths.ALL)

	private val customEncodingXCHG1 = Instruction(0x90, 0, 0, Widths.NO8)



	/**
	 *     E8    CALL  REL32
	 *     FF/2  CALL  RM64
	 *     EB    JMP   REL8
	 *     E9    JMP   REL32
	 *     FF/4  JMP   RM64
	 */
	private fun customEncodeCALL(group: InstructionGroup, node: InstructionNode) {
		if(node.op2 != null) error()

		if(node.op1 is ImmNode) {
			val imm = resolve(node.op1)

			if(importSymbol != null) {
				writeMem(
					opcode = 0xFF,
					node   = node.op1,
					rexW   = 0,
					rexR   = 0,
					reg    = 2
				)
			} else {
				writer.u8(0xE8)
				writeImm32(node.op1, imm.asImm32)
			}
		} else {
			operands(group, node)
		}

	}



	private fun customEncodeIMUL(group: InstructionGroup, node: InstructionNode) {
		if(node.op2 == null || node.op3 == null) operands(group, node)
		if(node.op4 != null) error()

		if(node.op1 !is RegNode || node.op3 !is ImmNode) error()

		val imm = resolve(node.op3)

		if(node.op2 is RegNode) {
			if(node.op1.value.width != node.op2.value.width) error()

			if(!hasReloc && imm.isImm8) {
				encode2RR(customEncodingIMUL1, node.op1.value, node.op2.value)
				writeImm8(node, imm.toInt())
			} else {
				encode2RR(customEncodingIMUL2, node.op1.value, node.op2.value)
				writeImm(node, node.op1.value.width, imm.asImm32)
			}
		} else if(node.op2 is MemNode) {
			if(node.op2.width != null && node.op2.width != node.op1.value.width) error()

			if(!hasReloc && imm.isImm8) {
				encode2RM(customEncodingIMUL1, node.op1.value, node.op2)
				writeImm8(node, imm.toInt())
			} else {
				encode2RM(customEncodingIMUL2, node.op1.value, node.op2)
				writeImm(node, node.op1.value.width, imm.asImm32)
			}
		} else {
			operands(group, node)
		}
	}

	

	private fun customEncodeMOV(group: InstructionGroup, node: InstructionNode) {
		if(node.op3 != null) error()

		if(node.op1 is RegNode && node.op2 is ImmNode) {
			val imm = resolve(node.op2)
			encode1O(customEncodingMOV1, node.op1.value)
			writeImm64(node.op2, node.op1.value.width, imm)
		} else {
			operands(group, node)
		}
	}



	private fun customEncodeXCHG(group: InstructionGroup, node: InstructionNode) {
		if(node.op3 != null) error()

		if(node.op1 is RegNode && node.op2 is RegNode) {
			if(node.op1.value.isA) {
				encode1O(customEncodingXCHG1, node.op2.value)
			} else if(node.op2.value.isA) {
				encode1O(customEncodingXCHG1, node.op1.value)
			} else {
				operands(group, node)
			}
		} else {
			operands(group, node)
		}
	}


}