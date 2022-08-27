package assembler

import core.binary.BinaryWriter

class Assembler(parseResult: ParseResult) {


	private val nodes = parseResult.nodes

	private val symbols = parseResult.symbols

	private val writer = BinaryWriter()

	var context = Context()

	private fun invalidEncoding(): Nothing = error("Invalid encoding")



	inner class Context {

		var modrm = ModRM(0)
		var sib = SIB(0)
		var rex = REX(0)
		lateinit var width: Width
		lateinit var operands: Operands
		var cannotHaveRex = false
		var oso = false
		var hasModRM = false
		var hasSIB = false
		var imm = 0L
		var immLength = 0
		var disp = 0L

	}



	fun assemble() {
		for(node in nodes) {
			when(node) {
				is InstructionNode -> assemble(node)
				is LabelNode -> handleLabel(node)
				else -> { }
			}
		}
	}



	private fun handleLabel(node: LabelNode) {
		 symbols[node.name]!!.data = LabelSymbolData(writer.pos.toLong())
	}



	private fun resolveIntSymbol(name: String): Long? {
		val symbol = symbols[name] ?: return null

		return when(symbol.type) {
			SymbolType.INT   -> (symbol.data as IntSymbolData).value
			SymbolType.LABEL -> (symbol.data as LabelSymbolData).value
			else             -> null
		}
	}



	private fun assemble(node: InstructionNode) {
		val group = Instructions.get(node.mnemonic)
			?: error("No encodings for mnemonic: ${node.mnemonic}")

		context.rex = REX(0)
		context.cannotHaveRex = false
		context.oso = false
		context.modrm = ModRM(0)
		context.sib = SIB(0)
		context.hasModRM = false
		context.hasSIB = false
		context.imm = 0
		context.immLength = 0
		context.disp = 0

		when {
			node.op1 == null -> invalidEncoding()
			node.op2 == null -> invalidEncoding()
			node.op3 == null -> operands2(group, node)
			else             -> invalidEncoding()
		}

		var flags = group.encodingFlags
		if(flags and context.operands.bit == 0L)
			error("Invalid encoding")
		flags = group.encodingFlags and ((1L shl context.operands.ordinal) - 1)
		val encoding = group.list[flags.countOneBits()]
	}



	private fun operands2(group: InstructionGroup, node: InstructionNode) {
		when(node.op1) {
			is RegisterNode -> when(node.op2) {
				is RegisterNode -> operands2RR(group, node.op1.value, node.op2.value)
				is MemoryNode -> operands2RM(group, node.op1.value, node.op2)
				else -> invalidEncoding()
			}
			else -> invalidEncoding()
		}
	}



	private fun operands2RR(group: InstructionGroup, op1: Register, op2: Register) {
		// oso used for 16-bit
		// REX.W used for 64-bit
		// REX.R used for second register
		// REX.X not used
		// REX.B used for first register
		// First register in ModRM:r/m
		// Second register in ModRM:reg

		val RM_CL = Specialisation.RM_CL.inFlags(group.specialisationFlags)

		context.width = op1.width
		context.hasModRM = true

		if(op1.width != op2.width)
			invalidEncoding()

		if(op1.rex != 0)
			context.rex = context.rex.withB

		if(op2.rex != 0)
			context.rex = context.rex.withR

		context.modrm = ModRM(0b11_000_000 or (op1.value shl 3) or op2.value)

		when(op1.width) {
			Width.BIT8 -> {
				context.operands = if(RM_CL && op2 == Register.CL)
					Operands.RM8_CL
				else
					Operands.RM8_R8

				if(op1.noRex || op2.noRex)
					context.cannotHaveRex = true
			}

			Width.BIT16 -> {
				context.operands = if(RM_CL && op2 == Register.CL)
					Operands.RM_CL
				else
					Operands.RM_R

				context.oso = true
			}

			Width.BIT32 -> {
				context.operands = if(RM_CL && op2 == Register.CL)
					Operands.RM_CL
				else
					Operands.RM_R
			}

			Width.BIT64 -> {
				context.operands = if(RM_CL && op2 == Register.CL)
					Operands.RM_CL
				else
					Operands.RM_R

				context.rex = context.rex.withW
			}

			else -> invalidEncoding()
		}
	}



	private fun operands2RM(group: InstructionGroup, op1: Register, op2: MemoryNode) {
		val RM_CL = Specialisation.RM_CL.inFlags(group.specialisationFlags)

		context.width = op1.width
		context.hasModRM = true

		if(op2.width != null && op1.width != op2.width)
			invalidEncoding()

		if(op1.rex != 0)
			context.rex = context.rex.withB
	}



	private fun AstNode.forEach(block: (AstNode) -> Unit) {
		block(this)

		if(this is BinaryNode) {
			left.forEach(block)
			right.forEach(block)
		} else if(this is UnaryNode) {
			node.forEach(block)
		}
	}



	private fun encodeMem(operand: MemoryNode) {
		val disp = operand.disp?.calculateInt(::resolveIntSymbol)

		if(disp != null) {
			context.modrm = context.modrm.withMod(0b10) // + disp32
			context.disp = disp
		}

		if(operand.rel) {
			context.modrm = context.modrm.withMod(0b00)
			context.modrm = context.modrm.withRm(0b101)
			return
		}

		if(operand.index != null) {
			context.modrm = context.modrm.withRm(0b100)

			context.modrm = if(disp != null)
				context.modrm.withMod(0b00) // SIB with no disp
			else
				context.modrm.withMod(0b10) // SIB + disp32

			context.sib = context.sib.withBase(operand.base?.value ?: 0b101)
			context.sib = context.sib.withIndex(operand.index.value)

			if(operand.scale.countOneBits() != 1) error("Invalid scale: ${operand.scale}")
			val scale = operand.scale.countLeadingZeroBits()
			if(scale > 3) error("Invalid scale: ${operand.scale}")
			context.sib = context.sib.withScale(scale)
		}
	}


}
