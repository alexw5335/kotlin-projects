package reader

import core.ReaderBase

class EncodingReader(chars: CharArray) : ReaderBase(chars) {


	/*
	Classes
	 */



	data class Instruction(
		val mnemonic        : String,
		val opcode          : Int,
		val extension       : Int,
		val mandatoryPrefix : Int,
		val oso             : Boolean,
		val rexw            : Boolean,
		val aso             : Boolean,
		val no16            : Boolean,
		val no64            : Boolean,
		val default64       : Boolean,
		val operands        : Operands
	)



	enum class Operand {
		NONE,
		R,
		RM,
		IMM,
		IMM8,
		A,
		AL,
		R8,
		RM8;
	}



	enum class Operands(
		val op1: Operand = Operand.NONE,
		val op2: Operand = Operand.NONE,
		val op3: Operand = Operand.NONE,
		val op4: Operand = Operand.NONE
	) {
		NONE,
		R_RM(Operand.R, Operand.RM),
		RM_R(Operand.RM, Operand.R),
		R8_RM8(Operand.R8, Operand.RM8),
		RM8_R8(Operand.RM8, Operand.R8),
		A_IMM(Operand.A, Operand.IMM),
		AL_IMM8(Operand.AL, Operand.IMM8),
		RM_IMM8(Operand.RM, Operand.IMM8),
		RM_IMM(Operand.RM, Operand.IMM),
		RM8_IMM8(Operand.RM8, Operand.IMM8),
		R(Operand.R);
	}



	private val operandMap = Operand.values().associateBy { it.name }

	private val operandsMap = Operands.values().associateBy {
		(it.op1.ordinal shl 0)  or
		(it.op2.ordinal shl 8)  or
		(it.op3.ordinal shl 16) or
		(it.op4.ordinal shl 24)
	}



	/*
	Reading
	 */



	fun read() = buildList {
		while(pos < chars.size) {
			when(chars[pos]) {
				'\r' -> pos += 2
				'\n' -> pos++
				'#'  -> skipLine()
				'!'  -> break
				else -> add(readInstruction())
			}
		}
	}



	private fun readInstruction(): Instruction {
		skipSpaces()

		var mandatoryPrefix = 0
		var opcode = 0
		var extension = -1
		var oso = false
		var aso = false
		var rexw = false
		var no16 = false
		var no64 = false
		var default64 = false

		fun hex2() = readHex2().also { skipSpaces() }

		val first = hex2()

		if(first == 0x66 || first == 0xF2 || first == 0xF3) {
			mandatoryPrefix = first
			opcode = hex2()
		} else {
			opcode = first
		}

		if(opcode == 0x0F) {
			opcode = when(val byte = hex2()) {
				0x38 -> 0x0F_38_00 or hex2()
				0x3A ->	0x0F_3A_00 or hex2()
				else ->	0x0F_00 or byte
			}
		}

		if(chars[pos] == '/') {
			pos++
			extension = chars[pos++].toString().toInt()
			skipSpaces()
		}

		val mnemonic = readUntil { it.isWhitespace() }
		skipSpaces()

		fun readOperand(): Operand {
			if(chars[pos].isWhitespace() || chars[pos] == '(') return Operand.NONE
			val string = readUntil { it.isWhitespace() }
			skipSpaces()
			return operandMap[string] ?: error("Unrecognised operand: $string")
		}

		val op1 = readOperand()
		val op2 = if(op1 != Operand.NONE) readOperand() else Operand.NONE
		val op3 = if(op2 != Operand.NONE) readOperand() else Operand.NONE
		val op4 = if(op3 != Operand.NONE) readOperand() else Operand.NONE

		val operands = operandsMap[
			(op1.ordinal shl 0)  or
			(op2.ordinal shl 8)  or
			(op3.ordinal shl 16) or
			(op4.ordinal shl 24)
		] ?: error("Unrecognised operands: $op1, $op2, $op3, $op4")

		if(chars[pos] == '(') {
			pos++
			while(true) {
				when(val string = readUntil { it == ',' || it == ')' || it.isWhitespace() }) {
					"OSO"       -> oso = true
					"ASO"       -> aso = true
					"REXW"      -> rexw = true
					"NO16"      -> no16 = true
					"NO64"      -> no64 = true
					"default64" -> default64 = true
					else   -> error("Unrecognised value: $string")
				}
				if(chars[pos] == ',') pos++
				else if(chars[pos] == ')') break
			}
		}

		skipSpaces()

		return Instruction(mnemonic, opcode, extension, mandatoryPrefix, oso, rexw, aso, no16, no64, default64, operands)
	}


}