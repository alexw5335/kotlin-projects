package assembler

class Assembler(private val parseResult: ParseResult) {


	fun assemble() {
		for(node in parseResult.nodes) {
			if(node is InstructionNode) {
				node.op1?.operandType()?.let(::println)
				node.op2?.operandType()?.let(::println)
				node.op3?.operandType()?.let(::println)
				node.op4?.operandType()?.let(::println)
			}
		}
	}



	private fun AstNode.operandType(): OperandType {
		if(this is RegisterNode) {
			var type = OperandType(0)
			if(value.width == Width.BIT8) {
				type = type or OperandType.R8
				if(value == Register.AL)
					type = type or OperandType.AL
				else if(value == Register.CL)
					type = type or OperandType.CL
			} else if(value.width == Width.BIT16) {
				type = type or OperandType.R16
				if(value == Register.AX)
					type = type or OperandType.AX
				else if(value == Register.DX)
					type = type or OperandType.DX
			} else if(value.width == Width.BIT32) {
				type = type or OperandType.R32
				if(value == Register.EAX)
					type = type or OperandType.EAX
			} else if(value.width == Width.BIT64) {
				type = type or OperandType.R64
				if(value == Register.RAX)
					type = type or OperandType.RAX
			}
			return type
		}

		if(this is MemoryNode) {
			return when(width) {
				Width.BIT8  -> OperandType.M8
				Width.BIT16 -> OperandType.M16
				Width.BIT32 -> OperandType.M32
				Width.BIT64 -> OperandType.M64
				null        -> OperandType.MN
				else        -> error("Invalid memory width")
			}
		}

		if(this is IntNode) {
			var type = OperandType(0)
			type = type or OperandType.IMM64
			if(value in Int.MIN_VALUE..Int.MAX_VALUE) {
				type = type or OperandType.IMM32
				if(value in Short.MIN_VALUE..Short.MAX_VALUE) {
					type = type or OperandType.IMM16
					if(value in Byte.MIN_VALUE..Byte.MAX_VALUE) {
						type = type or OperandType.IMM8
						if(value == 0L)
							type = type or OperandType.ZERO
						else if(value == 1L)
							type = type or OperandType.ONE
					}
				}
			}
			return type
		}

		error("Unsupported operand type: $this")
	}


}



/*

04    ADD  AL  IMM8
05    ADD  A   IMM
80/0  ADD  RM8 IMM8
81/0  ADD  RM  IMM
83/0  ADD  RM  IMM8
00    ADD  RM8 R8
01    ADD  RM  R
02    ADD  R8  RM8
03    ADD  R   RM

MN_R8
M8_R8
M8_IMM8
M16_IMM16
M32_IMM32

Treat immediates and memory operands the same regardless of width. Handle width later.

if(op1.isR)
	if(op1.isR8)
		if(op2.isIMM8)
			if(op1.isAL)
				AL_IMM8
			else
				R8_IMM8
		else if(op2.isR8)
			R8_R8
		else if(op2.isM8 || op2.isMN)
			R8_M8
		else
			error
	else if(op1.isR16)
		if(op2.isIMM8)
			R16_IMM8
		else if(op2.isIMM16)
			if(op1.isAX)
				AX_IMM16
			else
				R16_IMM16
		else if(op2.isR16)
			R16_R16
		else if(op2.isM16 || op2.isMN)
			R16_M16
		else
			error
	else if(op1.isR32)
		if(op2.isIMM8)
			R32_IMM8
		else if(op2.isIMM32)
			if(op1.isEAX)
				EAX_IMM32
			else
				R32_IMM32
		else if(op2.isR32)
			R32_R32
		else if(op2.isM32 || op2.isMN)
			R32_M32
		else
			error
	else if(op1.isR64)
		if(op2.isIMM8)
			R64_IMM8
		else if(op2.isIMM32)
			if(op1.isRAX)
				RAX_IMM32
			else
				R64_IMM32
		else if(op2.isR64)
			R64_R64
		else if(op2.isM64 || op2.isMN)
			R64_M64
		else
			error
else if(op1.isM)
	if(op2.isR)
		if(op2.isR8)
			if(op1.isM8 || op1.isMN)
				M8_R8
			else
				errorInvalidMemoryWidth
		else if(op2.isR16)
			if(op1.isM16 || op1.isMN)
				M16_R16
			else
				errorInvalidMemoryWidth
		else if(op2.isR32)
			if(op1.isM32 || op1.isMN)
				M32_R32
			else
				errorInvalidMemoryWidth
		else if(op2.isR64)
			if(op1.isM64 || op1.isMN)
				M64_R64
			else
				errorInvalidMemoryWidth
		else
			error
	else if(
else
	error


 */