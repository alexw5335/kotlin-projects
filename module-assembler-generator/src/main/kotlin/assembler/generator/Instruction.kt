package assembler.generator

import core.hex8

class Instruction(
	val prefix    : Int,
	val opType    : OpType,
	val opcode    : Int,
	val extension : Int,
	val mnemonic  : String,
	val operands  : Operands,
	val oso       : Boolean,
	val rexw      : Boolean
) {

	override fun toString() = buildString {
		if(prefix > 0) {
			append(prefix.hex8)
			append(' ')
		}

		when(opType) {
			OpType.SINGLE -> Unit
			OpType.DOUBLE -> append("0F ")
			OpType.TRIP38 -> append("0F 38 ")
			OpType.TRIP3A -> append("0F 3A ")
		}

		append(opcode.hex8)
		append(' ')

		if(extension > 0) {
			append('/')
			append(extension)
			append(' ')
		}

		append(mnemonic)
		append(' ')
		append(operands)

		if(rexw)
			append(" (REX.W)")
		else if(oso)
			append(" (OSO)")
	}
}



/*
class Instruction(
	val mnemonic        : String,
	val opcode          : Int,
	val optype          : OpType,
	val operand1        : Operand?,
	val operand2        : Operand?,
	val operand3        : Operand?,
	val operand4        : Operand?,
	val extension       : Int,
	val mandatoryPrefix : Int,
	val oso             : Boolean,
	val rexw            : Boolean
) {

	override fun toString() = buildString {
		if(mandatoryPrefix > 0)
			append("${mandatoryPrefix.hex8} ")

		when(optype) {
			OpType.SINGLE -> Unit
			OpType.DOUBLE -> append("0F ")
			OpType.TRIP38 -> append("0F 38 ")
			OpType.TRIP3A -> append("0F 3A ")
		}

		append(opcode.hex8)

		if(extension > 0)
			append("/$extension ")
		else
			append(' ')

		append(" $mnemonic")

		if(operand1 != null) append(" $operand1")
		if(operand2 != null) append(" $operand2")
		if(operand3 != null) append(" $operand3")
		if(operand4 != null) append(" $operand4")
	}

}*/
