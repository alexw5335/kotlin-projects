package assembler.generator

import core.hex8

data class Instruction(
	val mnemonic        : String,
	val opcode          : Int,
	val optype          : OpType,
	val operand1        : Operand?,
	val operand2        : Operand?,
	val operand3        : Operand?,
	val operand4        : Operand?,
	val extension       : Int,
	val mandatoryPrefix : Int,
	val noGp16          : Boolean,
	val noGp32          : Boolean,
	val noGp64          : Boolean,
	val default64       : Boolean
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

		if(noGp16) append(" (noGp16)")
		if(noGp32) append(" (noGp32)")
		if(noGp64) append(" (noGp64)")
		if(default64) append(" (default64)")
	}

}