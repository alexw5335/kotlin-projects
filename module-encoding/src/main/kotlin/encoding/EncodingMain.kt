package encoding

import core.Core
import java.nio.file.Files



fun main() {
	val rawLines = readLines(Files.readAllLines(Core.getResourcePath("/nasm_ins.txt")))
	val filteredLines = filterLines(rawLines)
	val lines = filteredLines.map(::scrapeLine).filter { it.extension in Maps.enabledExtensions }
	lines.forEach(::determineOperands)
	lines.forEach(::convertLine)
}



private fun RawLine.error(message: String = "Misc. error"): Nothing {
	System.err.println("Error on line $lineNumber:")
	System.err.println("\t$this")
	System.err.println("\t$message")
	throw Exception()
}

private fun Line.error(message: String = "Misc. error"): Nothing {
	System.err.println("Error on line ${raw.lineNumber}:")
	System.err.println("\t$this")
	System.err.println("\t$message")
	throw Exception()
}



private fun Line.toEncoding(mnemonic: String, opcode: Int, operands: Operands, width: Width?) = Encoding(
	this,
	mnemonic,
	opcodeExt,
	opcode,
	oplen,
	prefix,
	rexw,
	operands,
	width
)



private fun convertLine(line: Line): List<Encoding> {
	val list = ArrayList<Encoding>()

	fun add() {
		val (operands, width) = combineOperands(line)
		if(line.cc)
			for((mnemonic, opcode) in Maps.ccList)
				list += line.toEncoding(line.mnemonic.dropLast(2) + mnemonic, opcode, operands, width)
		else
			list += line.toEncoding(line.mnemonic, line.opcode, operands, width)
	}

	if(line.compound != null) {
		for(operand in line.compound!!.parts) {
			line.ops[line.compoundIndex] = operand
			add()
		}
		line.ops[line.compoundIndex] = line.compound!!
	} else {
		add()
	}

	return list
}



private fun combineOperands(line: Line) : Pair<Operands, Width?> {
	var sm = false
	var sm2 = false
	var sm3 = false

	val op1 = line.op1
	val op2 = line.op2
	val op3 = line.op3
	val op4 = line.op4

	fun error(): Nothing = line.error("Invalid operands: ${line.ops.joinToString()}    $sm $sm2 $sm3")

	when {
		op1 == null -> return Operands.NONE to null
		op2 == null -> { }
		op3 == null -> {
			sm = op1.width != null && (op1.width == op2.width) || (op2 == Operand.I32 && op1.width == Width.QWORD)
		}
		op4 == null -> {
			sm2 = op1.width != null && op1.width == op2.width
			sm = sm2 && ((op1.width == op3.width) || (op3 == Operand.I32 && op1.width == Width.QWORD))
		}
		else -> {
			sm2 = op1.width != null && op1.width == op2.width
			sm3 = sm2 && op1.width == op3.width
			sm = sm3 && op1.width == op4.width
		}
	}

	outer@ for(operands in Operands.values) {
		if(operands.parts == null) continue
		if(line.ops.size != operands.parts.size) continue
		//for(i in operands.parts.indices)
		//	if(line.ops[i] !in operands.parts[i])
		//		continue
		//if(operands.sm && !sm) continue
		//if(operands.sm2 && !sm2) continue
		//if(operands.sm3 && !sm3) continue

		var width: Width? = null

		for((i, opClass) in operands.parts.withIndex()) {
			val operand = line.ops[i]
			when(opClass) {
				is OperandType ->
					if(operand.type != opClass)
						continue@outer
					else if(width == null)
						width = operand.width
					else if(operand.width != width && !(operand == Operand.I32 && width == Width.QWORD))
						continue@outer
				is Operand ->
					if(operand != opClass)
						continue@outer
			}
		}

		return operands to width
	}

	error()
}



private fun determineOperands(line: Line) {
	val strings = line.raw.operands.split(',')
	val widths = arrayOfNulls<Width>(4)

	if(line.sm) {
		Maps.operands[strings[0]]?.width?.let { widths[1] = it }
		Maps.operands[strings[1]]?.width?.let { widths[0] = it }
	}

	if(line.ar >= 0)
		widths[line.ar] = line.opSize ?: if(line.mnemonic == "PUSH") null else line.error("No width")
	
	if(line.ar < 0 && !line.sm) widths.fill(line.opSize)

	for(i in strings.indices) {
		var string = strings[i]

		if(string.endsWith("*"))     { line.star = true; string = string.dropLast(1) }
		if(string.endsWith("|z"))    { line.z    = true; string = string.dropLast(2) }
		if(string.endsWith("|mask")) { line.k = true; string = string.dropLast(5) }
		if(string.endsWith("|sae"))  { line.sae  = true; string = string.dropLast(4) }
		if(string.endsWith("|er"))   { line.er   = true; string = string.dropLast(3) }
		if(string.endsWith("|b16"))  { line.b16  = true; string = string.dropLast(4) }
		if(string.endsWith("|b32"))  { line.b32  = true; string = string.dropLast(4) }
		if(string.endsWith("|b64"))  { line.b64  = true; string = string.dropLast(4) }
		if(string.endsWith("|rs2"))  { line.rs2  = true; string = string.dropLast(4) }
		if(string.endsWith("|rs4"))  { line.rs4  = true; string = string.dropLast(4) }

		val operand: Operand = when(string) {
			"void" -> continue

			in Maps.operands -> Maps.operands[string]!!

			"mem" -> when(widths[i]) {
				null        -> Operand.MEM
				Width.BYTE  -> Operand.M8
				Width.WORD  -> Operand.M16
				Width.DWORD -> Operand.M32
				Width.QWORD -> Operand.M64
				Width.TWORD -> Operand.M80
				Width.XWORD -> Operand.M128
				Width.YWORD -> Operand.M256
				Width.ZWORD -> Operand.M512
			}

			"xmmrm" -> when(widths[i]) {
				Width.DWORD -> Operand.XM32
				Width.QWORD -> Operand.XM64
				Width.XWORD -> Operand.XM128
				null -> Operand.XM128
				else -> line.error("Invalid width: ${widths[i]}")
			}

			"mmxrm" -> when(widths[i]) {
				Width.QWORD -> Operand.MMM64
				Width.XWORD -> if(line.mnemonic == "PMULUDQ" || line.mnemonic == "PSUBQ")
					Operand.MMM64
				else
					line.error("Invalid width: ${widths[i]}")
				else -> line.error("Invalid width: ${widths[i]}")
			}

			"imm", "imm|near" -> when(line.immType) {
				ImmType.IB   -> Operand.I8
				ImmType.IB_S -> Operand.I8
				ImmType.IB_U -> Operand.I8
				ImmType.IW   -> Operand.I16
				ImmType.ID   -> Operand.I32
				ImmType.ID_S -> Operand.I32
				ImmType.IQ   -> Operand.I64
				ImmType.REL8 -> Operand.REL8
				ImmType.REL  -> if(OpPart.ODF in line.opParts)
					Operand.REL32
				else
					Operand.REL16
				else -> line.error("Invalid width: ${line.immType}")
			}

			"imm|short" -> Operand.I8

			else -> line.error("Unrecognised operand: $string")
		}

		line.addOperand(operand)
	}
}



private fun scrapeLine(raw: RawLine): Line {
	val line = Line(raw)

	for(extra in raw.extras) when(extra) {
		in Maps.arches     -> line.arch = Maps.arches[extra]!!
		in Maps.extensions -> line.extension = Maps.extensions[extra]!!
		in Maps.opWidths   -> line.opSize = Maps.opWidths[extra]!!
		in ignoredExtras   -> continue
		"SM"  -> line.sm = true
		"SM2" -> line.sm = true
		"AR0" -> line.ar = 0
		"AR1" -> line.ar = 1
		"AR2" -> line.ar = 2
		else  -> raw.error("Invalid extra: $extra")
	}
	
	for(part in raw.parts) when {
		part.startsWith("evex") -> line.evex = part
		part.startsWith("vex")  -> line.vex = part
		part.endsWith("+c")     -> { line.cc = true; line.addOpcode(part.dropLast(2).toInt(16)) }
		part.endsWith("+r")     -> { line.opreg = true; line.addOpcode(part.dropLast(2).toInt(16)) }
		part == "/r"            -> line.hasModRM = true
		part == "/is4"          -> line.is4 = true
		part == "o16"           -> line.prefix = 0x66
		part == "o64"           -> line.rexw = true
		part == "a32"           -> line.prefix = 0x67
		part == "f2i"           -> line.prefix = 0xF2
		part == "f3i"           -> line.prefix == 0xF3
		part == "wait"          -> line.addOpcode(0x9B)
		part[0] == '/'          -> line.opcodeExt = part[1].digitToInt(10)
		part.endsWith(':')      -> line.encodingCode = part.dropLast(1)
		part in Maps.immTypes   -> line.immType = Maps.immTypes[part]!!
		part in Maps.opParts    -> line.opParts += Maps.opParts[part]!!
		part in Maps.vsibs      -> line.vsib = Maps.vsibs[part]!!
		part in ignoredParts    -> continue

		part.length == 2 && part[0].isHex && part[1].isHex -> {
			if(line.hasModRM) {
				if(line.postModRM >= 0) error("Too many opcode parts")
				line.postModRM = part.toInt(16)
			} else {
				val value = part.toInt(16)
				when {
					line.evex != null || line.vex != null || line.oplen != 0
						-> line.addOpcode(value)
					value == 0x66 || value == 0xF2 || value == 0xF3
						-> line.prefix = value
					else
						-> line.addOpcode(value)
				}
			}
		}

		else -> if(!part.endsWith("w0") && !part.endsWith("w1")) error("Unrecognised opcode part: $part")
	}

	if(line.arch == Arch.FUTURE && line.extension == Extension.NONE)
		line.extension = Extension.NOT_GIVEN

	return line
}



private fun filterLines(lines: List<RawLine>): List<RawLine> {
	val filtered = ArrayList<RawLine>()

	for(line in lines) {
		if(line.mnemonic in customMnemonics)
			continue

		if("ND" in line.extras && line.operands != "void")
			continue

		if(invalidExtras.any(line.extras::contains))
			continue

		if(invalidOperands.any(line.operands::contains))
			continue

		filtered.add(line)
	}

	return filtered
}



private fun readLines(lines: List<String>): List<RawLine> {
	val nasmLines = ArrayList<RawLine>()

	for((index, line) in lines.withIndex()) {
		if(line.isEmpty() || line.startsWith(';')) continue
		if(line.startsWith('~')) break

		try {
			val beforeBrackets = line.substringBefore('[')
			if(beforeBrackets.length == line.length) continue

			val firstSplit = beforeBrackets.split(' ', '\t').filter(String::isNotEmpty)
			val mnemonic = firstSplit[0]
			val operands = firstSplit[1]

			val parts = line
				.substring(beforeBrackets.length + 1, line.indexOf(']'))
				.split(' ', '\t')
				.filter(String::isNotEmpty)

			val extras = line
				.substringAfter(']')
				.trim()
				.split(',')
				.filter(String::isNotEmpty)

			nasmLines.add(RawLine(index + 1, mnemonic, operands, parts, extras))
		} catch(e: Exception) {
			System.err.println("Error on line ${index + 1}: $line")
			e.printStackTrace()
		}
	}

	return nasmLines
}