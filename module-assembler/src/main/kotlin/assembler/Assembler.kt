package assembler

import core.binary.BinaryWriter

class Assembler(private val parseResult: ParseResult) {


	private val writer = BinaryWriter()

	var context = Context()



	inner class Context {

		var rex = REX(0)
		var memoryNode: AstNode? = null
		lateinit var width: Width
		lateinit var operands: Operands
		var cannotHaveRex = false
		var oso = false

	}



	fun assemble() {
		for(node in parseResult.nodes) {
			if(node !is InstructionNode) continue
			assemble(node)
		}
	}



	private fun assemble(node: InstructionNode) {
		val encoding = encoding(node)
	}



	private fun encoding(node: InstructionNode): InstructionEncoding {
		val group = Instructions.get(node.mnemonic)
			?: error("No encodings for mnemonic: ${node.mnemonic}")

		val operands = operands(group, node)
		var flags = group.encodingFlags
		if(flags and operands.bit == 0L) error("Invalid encoding")
		flags = group.encodingFlags and ((1L shl operands.ordinal) - 1)
		return group.list[flags.countOneBits()]
	}



	private fun operands(group: InstructionGroup, node: InstructionNode): Operands = when {
		node.op1 == null -> Operands.NONE
		node.op2 == null -> error("Invalid encoding")
		//node.op2 == null -> operands1(node.op1)
		node.op3 == null -> operands2(group, node.op1, node.op2)
		else -> error("Invalid encoding")
	}



/*	private fun operands1(op1: AstNode): Operands = when(op1) {
		is RegisterNode -> when(op1.value.width) {
			Width.BIT8 -> Operands.R8
			Width.BIT16 -> Operands.R16
			Width.BIT32 -> Operands.R32
			Width.BIT64 -> Operands.R64
			else -> error("Invalid register width")
		}
		is MemoryNode -> Operands.M
		is ImmediateNode -> Operands.IMM
		else -> error("Invalid operand node")
	}*/

	private fun invalidEncoding(): Nothing = error("Invalid encoding")

	private fun operands2RR(group: InstructionGroup, op1: Register, op2: Register) {
		// oso used for 16-bit
		// REX.W used for 64-bit
		// REX.R used for second register
		// REX.X not used
		// REX.B used for first register

		val RM_CL = Specialisation.RM_CL.inFlags(group.specialisationFlags)

		context.width = op1.width
		context.memoryNode = null
		context.cannotHaveRex = false
		context.oso = false

		if(op1.width != op2.width)
			invalidEncoding()

		if(op1.rex != 0)
			context.rex = context.rex.withB

		if(op2.rex != 0)
			context.rex = context.rex.withR

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



	private fun operands2RM(group: InstructionGroup, op1: Register, op2: MemOperand): Operands {

	}



	private fun operands2(group: InstructionGroup, op1: AstNode, op2: AstNode): Operands {
		val A_IMM = Specialisation.A_IMM.inFlags(group.specialisationFlags)
		val RM_IMM8 = Specialisation.RM_IMM8.inFlags(group.specialisationFlags)
		val RM_ONE = Specialisation.RM_ONE.inFlags(group.specialisationFlags)
		val RM_CL = Specialisation.RM_CL.inFlags(group.specialisationFlags)

		if(op1 is RegisterNode) {
			if(op1.value.width == Width.BIT8) {
				if(op2 is RegisterNode) {
					if(RM_CL && op2.value == Register.CL)
						return Operands.RM8_CL
					if(op2.value.width == Width.BIT8)
						return Operands.RM8_R8
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT8)
						return Operands.R8_RM8
				} else if(op2 is ImmediateNode) {
					if(RM_ONE && op2.constValue != null && op2.constValue == 1L)
						return Operands.RM8_ONE
					if(A_IMM && op1.value == Register.AL)
						return Operands.AL_IMM8
					return Operands.RM8_IMM8
				}
			} else if(op1.value.width == Width.BIT16) {
				if(op2 is RegisterNode) {
					if(RM_CL && op2.value == Register.CL)
						return Operands.RM_CL
					if(op2.value.width == Width.BIT16)
						return Operands.RM_R
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT16)
						return Operands.R_RM
				} else if(op2 is ImmediateNode) {
					if(RM_ONE && op2.constValue != null && op2.constValue == 1L)
						return Operands.RM_ONE
					if(RM_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.RM_IMM8
					if(A_IMM && op1.value == Register.AX)
						return Operands.A_IMM
					return Operands.RM_IMM
				}
			} else if(op1.value.width == Width.BIT32) {
				if(op2 is RegisterNode) {
					if(RM_CL && op2.value == Register.CL)
						return Operands.RM_CL
					if(op2.value.width == Width.BIT32)
						return Operands.RM_R
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT32)
						return Operands.R_RM
				} else if(op2 is ImmediateNode) {
					if(RM_ONE && op2.constValue != null && op2.constValue == 1L)
						return Operands.RM_ONE
					if(RM_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.RM_IMM8
					if(A_IMM && op1.value == Register.EAX)
						return Operands.A_IMM
					return Operands.RM_IMM
				}
			} else if(op1.value.width == Width.BIT64) {
				if(op2 is RegisterNode) {
					if(RM_CL && op2.value == Register.CL)
						return Operands.RM_CL
					if(op2.value.width == Width.BIT64)
						return Operands.RM_R
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT64)
						return Operands.R_RM
				} else if(op2 is ImmediateNode) {
					if(RM_ONE && op2.constValue != null && op2.constValue == 1L)
						return Operands.RM_ONE
					if(RM_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.RM_IMM8
					if(A_IMM && op1.value == Register.RAX)
						return Operands.A_IMM
					return Operands.RM_IMM
				}
			}
		} else if(op1 is MemoryNode) {
			if(op2 is RegisterNode) {
				if(op1.width == null) {
					if(op2.value.width == Width.BIT8)
						return Operands.RM8_R8
					else if(op2.value.width == Width.BIT16)
						return Operands.RM_R
					else if(op2.value.width == Width.BIT32)
						return Operands.RM_R
					else if(op2.value.width == Width.BIT64)
						return Operands.RM_R
				} else if(op1.width == Width.BIT8) {
					if(op2.value.width == Width.BIT8)
						return Operands.RM8_R8
				} else if(op1.width == Width.BIT16) {
					if(op2.value.width == Width.BIT16)
						return Operands.RM_R
				} else if(op1.width == Width.BIT32) {
					if(op2.value.width == Width.BIT32)
						return Operands.RM_R
				} else if(op1.width == Width.BIT64) {
					if(op2.value.width == Width.BIT64)
						return Operands.RM_R
				}
			} else if(op2 is ImmediateNode) {
				if(op1.width == Width.BIT8) {
					return Operands.RM8_IMM8
				} else if(op1.width == Width.BIT16) {
					if(RM_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.RM_IMM8
					return Operands.RM_IMM
				} else if(op1.width == Width.BIT32) {
					if(RM_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.RM_IMM8
					return Operands.RM_IMM
				} else if(op1.width == Width.BIT64) {
					if(RM_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.RM_IMM8
					return Operands.RM_IMM
				}
			}
		}

		error("Invalid operands: $op1, $op2")
	}


}
