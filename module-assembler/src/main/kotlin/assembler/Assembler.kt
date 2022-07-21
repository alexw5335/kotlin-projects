package assembler

import core.binary.BinaryWriter

class Assembler(parseResult: ParseResult) {


	private var pos = 0

	private val writer = BinaryWriter()

	private val nodes = parseResult.nodes

	private val symbols = parseResult.symbols



	fun assemble() {
		while(pos < nodes.size) {
			val node = nodes[pos++]

			if(node is ConstNode) continue
			if(node is LabelNode) error("Labels not yet supported")
			if(node !is InstructionNode) error("Expected instruction")

			chooseEncoding2(node)
		}
	}



	private fun chooseEncoding2(node: InstructionNode) {
		val encodings = Instructions.get(node.mnemonic)
			?: error("Unrecognised mnemonic: ${node.mnemonic}")

		val op1 = node.op1!!
		val op2 = node.op2!!

		var op12 = when(op1) {
			is RegisterNode -> when(op1.value.width) {
				1    -> OperandType2.R8
				2    -> OperandType2.R16
				3    -> OperandType2.R32
				else -> OperandType2.R64
			}
			is MemoryNode -> when(op1.width) {
				null        -> OperandType2.MEMn
				Width.BIT8  -> OperandType2.MEM8
				Width.BIT16 -> OperandType2.MEM16
				Width.BIT32 -> OperandType2.MEM32
				Width.BIT64 -> OperandType2.MEM64
			}
		}
	}


}