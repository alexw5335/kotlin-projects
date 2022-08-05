package assembler

class Assembler(private val parseResult: ParseResult) {


	fun assemble() {
		for(node in parseResult.nodes) {
			if(node !is InstructionNode) continue

			println(encoding(node))
		}
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
		node.op2 == null -> operands1(node.op1)
		node.op3 == null -> operands2(group, node.op1, node.op2)
		else -> error("Invalid encoding")
	}



	private fun operands1(op1: AstNode): Operands = when(op1) {
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
	}



	private fun operands2(group: InstructionGroup, op1: AstNode, op2: AstNode): Operands {
		val A_IMM = Specialisation.A_IMM.inFlags(group.specialisationFlags)
		val R_IMM8 = Specialisation.R_IMM8.inFlags(group.specialisationFlags)
		val M_IMM8 = Specialisation.M_IMM8.inFlags(group.specialisationFlags)

		if(op1 is RegisterNode) {
			if(op1.value.width == Width.BIT8) {
				if(op2 is RegisterNode) {
					if(op2.value.width == Width.BIT8)
						return Operands.R8_R8
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT8)
						return Operands.R8_M8
				} else if(op2 is ImmediateNode) {
					if(A_IMM && op1.value == Register.AL)
						return Operands.AL_IMM8
					return Operands.R8_IMM8
				}
			} else if(op1.value.width == Width.BIT16) {
				if(op2 is RegisterNode) {
					if(op2.value.width == Width.BIT16)
						return Operands.R16_R16
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT16)
						return Operands.R16_M16
				} else if(op2 is ImmediateNode) {
					if(R_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.R16_IMM8
					if(A_IMM && op1.value == Register.AX)
						return Operands.AX_IMM16
					return Operands.R16_IMM16
				}
			} else if(op1.value.width == Width.BIT32) {
				if(op2 is RegisterNode) {
					if(op2.value.width == Width.BIT32)
						return Operands.R32_R32
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT32)
						return Operands.R32_M32
				} else if(op2 is ImmediateNode) {
					if(R_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.R32_IMM8
					if(A_IMM && op1.value == Register.EAX)
						return Operands.EAX_IMM32
					return Operands.R32_IMM32
				}
			} else if(op1.value.width == Width.BIT64) {
				if(op2 is RegisterNode) {
					if(op2.value.width == Width.BIT64)
						return Operands.R64_R64
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT64)
						return Operands.R64_M64
				} else if(op2 is ImmediateNode) {
					if(R_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.R64_IMM8
					if(A_IMM && op1.value == Register.RAX)
						return Operands.RAX_IMM32
					return Operands.R64_IMM32
				}
			}
		} else if(op1 is MemoryNode) {
			if(op2 is RegisterNode) {
				if(op1.width == null) {
					if(op2.value.width == Width.BIT8)
						return Operands.M8_R8
					else if(op2.value.width == Width.BIT16)
						return Operands.M16_R16
					else if(op2.value.width == Width.BIT32)
						return Operands.M32_R32
					else if(op2.value.width == Width.BIT64)
						return Operands.M64_R64
				} else if(op1.width == Width.BIT8) {
					if(op2.value.width == Width.BIT8)
						return Operands.M8_R8
				} else if(op1.width == Width.BIT16) {
					if(op2.value.width == Width.BIT16)
						return Operands.M16_R16
				} else if(op1.width == Width.BIT32) {
					if(op2.value.width == Width.BIT32)
						return Operands.M32_R32
				} else if(op1.width == Width.BIT64) {
					if(op2.value.width == Width.BIT64)
						return Operands.M64_R64
				}
			} else if(op2 is ImmediateNode) {
				if(op1.width == Width.BIT8) {
					return Operands.M8_IMM8
				} else if(op1.width == Width.BIT16) {
					if(M_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.M16_IMM8
					return Operands.M16_IMM16
				} else if(op1.width == Width.BIT32) {
					if(M_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.M32_IMM8
					return Operands.M32_IMM32
				} else if(op1.width == Width.BIT64) {
					if(M_IMM8 && op2.constValue != null && op2.constValue in Byte.MIN_VALUE..Byte.MAX_VALUE)
						return Operands.M64_IMM8
					return Operands.M64_IMM32
				}
			}
		}

		error("Invalid operands: $op1, $op2")
	}


}
