package encoding

import core.Core
import java.nio.file.Files


fun main() {
	val rawLines = readLines(Files.readAllLines(Core.getResourcePath("/nasm_ins.txt")))
	for(l in rawLines)
		if(l.extras.contains("AMD"))
			println(l)
	val filteredLines = filterLines(rawLines)
	val lines = filteredLines.map(::scrapeLine)
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
	width,
	this.operands
)



private fun convertLine(line: Line): List<Encoding> {
	val list = ArrayList<Encoding>()

	fun add() {
		val (operands, width) = combineOperands(line)
		if(line.cc)
			for((mnemonic, opcode) in Maps.ccList)
				list += line.toEncoding(line.mnemonic.dropLast(2) + mnemonic, line.addedOpcode(opcode), operands, width)
		else
			list += line.toEncoding(line.mnemonic, line.opcode, operands, width)
	}

	if(line.compound != null) {
		for(operand in line.compound!!.parts) {
			line.operands[line.compoundIndex] = operand
			add()
		}
		line.operands[line.compoundIndex] = line.compound!!
	} else {
		add()
	}

	return list
}



private fun combineOperands(line: Line) : Pair<Operands, Width?> {
	outer@ for(operands in Operands.values) {
		if(line.operands.size != operands.parts.size) continue

		var width: Width? = null

		for((i, opClass) in operands.parts.withIndex()) {
			val operand = line.operands[i]
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

	//printUnique(line.operands.joinToString("_")); return Operands.M to null
	line.error("Invalid operands: ${line.operands.joinToString()}")
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
		in Maps.extensions -> line.extensions += Maps.extensions[extra]!!
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
		part in Maps.immTypes   -> line.immType = Maps.immTypes[part]!!
		part in Maps.opParts    -> line.opParts += Maps.opParts[part]!!
		part in Maps.vsibs      -> line.vsib = Maps.vsibs[part]!!
		part in ignoredParts    -> continue

		part.contains(':') -> {
			val array = part.split(':').filter { it.isNotEmpty() }
			line.opEnc = Maps.opEncs[array[0]] ?: line.error("Invalid ops: ${array[0]}")
			if(array.size > 1)
				line.tupleType = Maps.tupleTypes[array[1]] ?: line.error("Invalid tuple type")
			if(array.size == 3)
				line.evex = array[2]
		}

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

		else -> line.error("Unrecognised opcode part: $part")
	}

	if(line.arch == Arch.FUTURE && line.extensions.isEmpty())
		line.extensions += Extension.NOT_GIVEN

	val parts = (line.vex ?: line.evex ?: return line).split('.')

	for(part in parts) when(part) {
		"nds",
		"ndd",
		"dds",
		"vex",
		"evex" -> continue
		"lz"   -> line.vexl = VexL.LZ
		"l0"   -> line.vexl = VexL.L0
		"l1"   -> line.vexl = VexL.L1
		"lig"  -> line.vexl = VexL.LIG
		"128"  -> line.vexl = VexL.L128
		"256"  -> line.vexl = VexL.L256
		"512"  -> line.vexl = VexL.L512
		"wig"  -> line.vexw = VexW.WIG
		"w0"   -> line.vexw = VexW.W0
		"w1"   -> line.vexw = VexW.W1
		"0f"   -> line.vexExt = VexExt.E0F
		"0f38" -> line.vexExt = VexExt.E38
		"0f3a" -> line.vexExt = VexExt.E3A
		"66"   -> line.vexPrefix = VexPrefix.P66
		"f2"   -> line.vexPrefix = VexPrefix.PF2
		"f3"   -> line.vexPrefix = VexPrefix.PF3
		"np"   -> line.vexPrefix = VexPrefix.NP
		"map5" -> line.map5 = true
		"map6" -> line.map6 = true
		else   -> line.error("Invalid vex part: $part")
	}

	return line
}



private fun filterLines(lines: List<RawLine>): List<RawLine> {
	val filtered = ArrayList<RawLine>()

	for(line in lines) {
		if(line.mnemonic == "SYSCALL" || line.mnemonic == "SYSRET" || line.mnemonic == "LZCNT" || line.mnemonic == "PREFETCHW") {
			filtered.add(line)
			continue
		}

		if(line.mnemonic == "aw") continue
		if(line.mnemonic in customMnemonics) continue
		if("ND" in line.extras && line.operands != "void") continue
		if(invalidExtras.any(line.extras::contains)) continue
		if(invalidOperands.any(line.operands::contains)) continue
		if("r+mi:" in line.parts) continue
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