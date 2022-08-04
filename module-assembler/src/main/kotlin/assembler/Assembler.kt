package assembler

class Assembler(private val parseResult: ParseResult) {


	fun assemble() {
		for(node in parseResult.nodes)
			if(node is InstructionNode)
				println(operands2(node.op1!!, node.op2!!))
	}



	private fun operands2(op1: AstNode, op2: AstNode): Operands {
		if(op1 is RegisterNode) {
			if(op1.value.width == Width.BIT8) {
				if(op2 is RegisterNode) {
					if(op2.value.width == Width.BIT8)
						return Operands.R8_R8
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT8)
						return Operands.R8_M8
				} else if(op2 is ImmediateNode) {
					return Operands.R8_IMM
				}
			} else if(op1.value.width == Width.BIT16) {
				if(op2 is RegisterNode) {
					if(op2.value.width == Width.BIT16)
						return Operands.R16_R16
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT16)
						return Operands.R16_M16
				} else if(op2 is ImmediateNode) {
					return Operands.R16_IMM
				}
			} else if(op1.value.width == Width.BIT32) {
				if(op2 is RegisterNode) {
					if(op2.value.width == Width.BIT32)
						return Operands.R32_R32
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT32)
						return Operands.R32_M32
				} else if(op2 is ImmediateNode) {
					return Operands.R32_IMM
				}
			} else if(op1.value.width == Width.BIT64) {
				if(op2 is RegisterNode) {
					if(op2.value.width == Width.BIT64)
						return Operands.R64_R64
				} else if(op2 is MemoryNode) {
					if(op2.width == null || op2.width == Width.BIT64)
						return Operands.R64_M64
				} else if(op2 is ImmediateNode) {
					return Operands.R64_IMM
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
				if(op1.width == Width.BIT8)
					return Operands.M8_IMM
				else if(op1.width == Width.BIT16)
					return Operands.M16_IMM
				else if(op1.width == Width.BIT32)
					return Operands.M32_IMM
				else if(op1.width == Width.BIT64)
					return Operands.M64_IMM
			}
		}

		error("Invalid operands: $op1, $op2")
	}


}
