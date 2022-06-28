package assembler.generator

import core.ReaderBase
import core.LineSpacingFormatter
import core.hex8

class InstructionReader(chars: CharArray) : ReaderBase(chars) {


	private val operandMap = Operand.values().associateBy { it.name }

	private val instructions = ArrayList<Instruction>()



	fun readInstructions(): List<Instruction> {
		while(pos < chars.size) {
			val char = chars[pos]

			if(char.isWhitespace()) {
				pos++
				continue
			}

			if(char == '#') {
				skipLine()
				continue
			}

			if(pos < chars.size - 1 && char == '/' && chars[pos + 1] == '*') {
				while(pos < chars.size - 1)
					if(chars[pos++] == '*' && chars[pos] == '/')
						break
				pos++
				continue
			}

			readInstruction()
		}

		LineSpacingFormatter(lines, 2).format().forEach(::println)

		return instructions
	}



	private fun readOperandEncoding(): Operand {
		skipSpaces()
		if(pos >= chars.size || chars[pos] == '\n' || chars[pos] == '#' || chars[pos] == '(')
			return Operand.NONE
		val string = readWhile { it.isLetterOrDigit() || it == '_' }
		return operandMap[string] ?: error("Unrecognised operand: $string")
	}



	private val lines = ArrayList<List<String>>()



	private fun readInstruction() {
		var hex = readHex2()
		var mandatoryPrefix = -1

		if(hex == 0x66 || hex == 0xF2 || hex == 0xF3) {
			mandatoryPrefix = hex
			skipSpaces()
			hex = readHex2()
		}

		var opcode = hex
		var opType = OpType.SINGLE

		if(hex == 0x0F) {
			skipSpaces()
			opcode = readHex2()
			when(opcode) {
				0x38 -> { skipSpaces(); opcode = readHex2(); opType = OpType.TRIP38 }
				0x3A -> { skipSpaces(); opcode = readHex2(); opType = OpType.TRIP3A }
				else -> { opType = OpType.DOUBLE }
			}
		}

		skipSpaces()

		val extension = if(chars[pos] == '/') {
			pos++
			chars[pos++] - '0'
		} else -1

		skipSpaces()

		val mnemonic = readUntil { it.isWhitespace() }

		skipSpaces()

		val op1 = readOperandEncoding()
		val op2 = if(!op1.isNone) readOperandEncoding() else Operand.NONE
		val op3 = if(!op2.isNone) readOperandEncoding() else Operand.NONE
		val op4 = if(!op3.isNone) readOperandEncoding() else Operand.NONE

		accumulateOperands(op1, op2, op3, op4)

		lines.add(listOf(opcode.hex8, mnemonic, op1.toString(), op2.toString(), " -> ", operands.joinToString()))

		skipSpaces()

		var rexw = false
		var oso = false

		while(pos < chars.size && chars[pos] == '(') {
			pos++
			when(readWhile { it.isLetterOrDigit() }) {
				"OSO"    -> oso = true
				"REX.W"  -> rexw = true
			}
			skipSpaces()
			if(chars[pos++] != ')') error("Expecting '(")
			skipSpaces()
		}
	}



	private val operands = ArrayList<Operands>()



	fun accumulateOperands(op1: Operand, op2: Operand, op3: Operand, op4: Operand) {
		fun add(vararg operands: Operands) = this.operands.addAll(operands)
		operands.clear()

		when {
			op1.isNone -> add(Operands.NONE)

			op2.isNone -> error("Unsupported operand encoding: $op1")

			op3.isNone -> when {
				op1 == Operand.AL  && op2 == Operand.IMM8 -> add(Operands.IMM8)
				op1 == Operand.A   && op2 == Operand.IMM  -> add(Operands.IMM)
				op1 == Operand.RM8 && op2 == Operand.IMM8 -> add(Operands.R_IMM, Operands.MEM_IMM)
				op1 == Operand.RM  && op2 == Operand.IMM  -> add(Operands.R_IMM, Operands.MEM_IMM)
				op1 == Operand.RM  && op2 == Operand.IMM8 -> add(Operands.R_IMM8, Operands.MEM_IMM8)
				op1 == Operand.RM8 && op2 == Operand.R8   -> add(Operands.R_R, Operands.MEM_R)
				op1 == Operand.RM  && op2 == Operand.R    -> add(Operands.R_R, Operands.MEM_R)
				op1 == Operand.R8  && op2 == Operand.RM8  -> add(Operands.R_R, Operands.R_MEM)
				op1 == Operand.R   && op2 == Operand.RM   -> add(Operands.R_R, Operands.R_MEM)
				else -> error("Unsupported operand encoding: $op1 $op2")
			}

			else -> error("Unsupported operand encoding: $op1 $op2 $op3 $op4")
		}

	}

}