package assembler

import core.binary.BinaryWriter

class Assembler(private val parseResult: ParseResult) {


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

	}



	fun assemble() {
		for(node in parseResult.nodes) {
			if(node !is InstructionNode) continue
			assemble(node)
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



	private fun toMemOperand(node: MemoryNode): MemOperand {
		var base: Register? = null
		var index: Register? = null
		var scale = 1
		var displacement: AstNode? = null

		for(c in node.components) {
			if(c is RegisterNode) {
				if(base == null) {
					base = c.value
				} else if(index == null) {
					index = c.value
				} else {
					error("Invalid memory operand")
				}

				continue
			}

			if(c is BinaryNode) {
				if(c.op == BinaryOp.MUL && (c.left is RegisterNode || c.right is RegisterNode)) {
					when {
						index != null -> error("Invalid memory opreand")

						c.left is RegisterNode && c.right is IntNode -> {
							index = c.left.value
							scale = c.right.value.toInt()
						}

						c.left is IntNode && c.right is RegisterNode -> {
							index = c.right.value
							scale = c.left.value.toInt()
						}

						else -> error("Invalid memory operand")
					}

					when(scale) {
						1, 2, 4, 8 -> { }
						else -> error("Invalid memory operand")
					}

					continue
				}
			}

			if(displacement != null)
				error("Invalid memory operand")

			displacement = c
		}

		return MemOperand(node.width, base, index, scale, displacement)
	}



	private fun operands2(group: InstructionGroup, node: InstructionNode) {
		when(node.op1) {
			is RegisterNode -> when(node.op2) {
				is RegisterNode -> operands2RR(group, node.op1.value, node.op2.value)
				is MemoryNode -> operands2RM(group, node.op1.value, toMemOperand(node.op2))
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



	private fun operands2RM(group: InstructionGroup, op1: Register, op2: MemOperand) {
		val RM_CL = Specialisation.RM_CL.inFlags(group.specialisationFlags)

		context.width = op1.width
		context.hasModRM = true

		if(op2.width != null && op1.width != op2.width)
			invalidEncoding()

		if(op1.rex != 0)
			context.rex = context.rex.withB
	}



	private fun encodeMem(operand: MemoryNode) {

	}


	/*
	- mod = 11: Direct register addressing mode
	- mod = 00, r/m = 100: SIB with no displacement
	- mod = 00, r/m = 101: Displacement only
	- mod = 00, r/m != 100 and r/m != 101: Indirect register, no displacement
	- mod = 01, r/m = 100: SIB with disp8
	- mod = 10, r/m = 100: SIB with disp32
	 */


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
