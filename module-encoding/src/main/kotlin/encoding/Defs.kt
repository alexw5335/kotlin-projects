package encoding



val Int.hex8 get() = Integer.toHexString(this).uppercase().let { if(it.length == 1) "0$it" else it }

val Char.isHex get() = this in '0'..'9' || this in 'a'..'f' || this in 'A'..'F'



data class Encoding(
	val line      : Line,
	val mnemonic  : String,
	val opcodeExt : Int,
	val opcode    : Int,
	val oplen     : Int,
	val prefix    : Int,
	val rexw      : Boolean,
	val operands  : Operands,
	val width     : Width?
) {

	override fun toString() = buildString {
		if(prefix != 0) append("${prefix.hex8} ")
		for(i in 0 until oplen) {
			val value = (opcode shr (i shl 3)) and 0xFF
			append(value.hex8)
			append(' ')
		}
		deleteAt(length - 1)
		if(opcodeExt >= 0) {
			append('/')
			append(opcodeExt)
		}
		append("  ")
		append(mnemonic)
		append("  ")
		append(operands)
	}

}



class RawLine(
	val lineNumber : Int,
	val mnemonic   : String,
	val operands   : String,
	val parts      : List<String>,
	val extras     : List<String>
) {
	override fun toString() = "$lineNumber: $mnemonic $operands $parts $extras"
}



class Line(val raw: RawLine) {

	val mnemonic = raw.mnemonic
	var arch = Arch.NONE
	var extension = Extension.NONE
	var opSize: Width? = null
	var sm = false
	var ar = -1
	var immType = ImmType.NONE
	var opcode = 0
	var oplen = 0
	var cc = false
	var opreg = false
	var evex: String? = null
	var vex: String? = null
	var opcodeExt = -1
	var hasModRM = false
	var is4 = false
	var encodingCode = ""
	var postModRM = -1
	var vsib = VSib.NONE
	val opParts = ArrayList<OpPart>()
	var prefix = 0
	var rexw = false
	var k = false
	var z = false
	var sae = false
	var er = false
	var b16 = false
	var b32 = false
	var b64 = false
	var rs2 = false
	var rs4 = false
	var star = false

	var compoundIndex = -1
	var compound: Operand? = null

	val ops = ArrayList<Operand>()
	val op1 get() = ops.getOrNull(0)
	val op2 get() = ops.getOrNull(1)
	val op3 get() = ops.getOrNull(2)
	val op4 get() = ops.getOrNull(3)

	var operands: Operands? = null
	var width: Width? = null

	fun set(operands: Operands, width: Width? = null) {
		this.operands = operands
		this.width = width
	}

	fun addOperand(operand: Operand) {
		if(operand.parts.isNotEmpty()) {
			compound = operand
			compoundIndex = ops.size
		}
		ops += operand
	}

	fun addOpcode(value: Int) {
		opcode = opcode or (value shl (oplen shl 3))
		oplen++
	}

	override fun toString() = raw.toString()

}



object Maps {
	val arches     = Arch.values().associateBy { it.name.trimStart('_') }
	val extensions = Extension.values().associateBy { it.name.trimStart('_') }
	val opParts    = OpPart.values().associateBy { it.name.lowercase().replace('_', ',') }
	val vsibs      = VSib.values().associateBy { it.name.lowercase() }
	val operands   = Operand.values().associateBy { it.string }
	val opWidths   = Width.values().associateBy { it.sizeString }
	val immTypes   = ImmType.values().associateBy { it.name.lowercase().replace('_', ',') }

	val enabledExtensions = setOf(
		Extension.NONE,
		Extension.FPU
	)

	val ccList = arrayOf(
		"O" to 0,
		"NO" to 1,
		"B" to 2, "NAE" to 2, "C" to 2,
		"NB" to 3, "AE" to 3, "NC" to 3,
		"Z" to 4, "E" to 4,
		"NZ" to 5, "NE" to 5,
		"BE" to 6, "NA" to 6,
		"NBE" to 7, "A" to 7,
		"S" to 8,
		"NS" to 9,
		"P" to 9, "PE" to 10,
		"NP" to 11, "PO" to 11,
		"L" to 12, "NGE" to 12,
		"NL" to 13, "GE" to 13,
		"LE" to 14, "NG" to 14,
		"NLE" to 15, "JG" to 15
	)
}



private val uniques = HashMap<String, HashSet<String>>()

fun printUnique(key: String, value: String, print: String) {
	val set = uniques.getOrPut(key, ::HashSet)
	if(value in set) return
	set += value
	println(print)
}

fun printUnique(key: String, value: String) = printUnique(key, value, value)

fun printUnique(value: String) = printUnique("misc", value)



enum class RegType {
	R8,
	R16,
	R32,
	R64,
	ST,
	Y,
	Z,
	X,
	MM,
	K,
	SEG;
	val bit = 1 shl ordinal
}



@JvmInline
value class OpMask(val value: Int) {
	operator fun contains(type: RegType) = type.bit and value != 0
	//operator fun contains(reg: Reg) = reg.type.bit and value != 0
	//operator fun contains(width: Width) = width.bit and value != 0
	companion object {
		val BYTE     = OpMask(0b0001)
		val WORD     = OpMask(0b0010)
		val DWORD    = OpMask(0b0100)
		val QWORD    = OpMask(0b1000)
		val TWORD    = OpMask(0b0001_0000)
		val XWORD    = OpMask(0b0010_0000)
		val YWORD    = OpMask(0b0100_0000)
		val ZWORD    = OpMask(0b1000_0000)
		val GP       = OpMask(0b1111)
		val GP816    = OpMask(0b0011)
		val GP81632  = OpMask(0b0111)
		val GP1664   = OpMask(0b1010)
		val GP163264 = OpMask(0b1110)
		val FPU      = OpMask(0b0001_0000)
		val SSE      = OpMask(0b1110_0000)
	}
}



enum class OpPart {
	A32,
	A64,
	O16,
	O32,
	O64NW,
	O64,
	ODF,
	F2I,
	F3I;
}



enum class VSib {
	NONE,
	VM32X,
	VM64X,
	VM64Y,
	VM32Y,
	VSIBX,
	VSIBY,
	VSIBZ;
}



enum class ImmType {
	NONE,
	IB,
	IW,
	ID,
	IQ,
	IB_S,
	IB_U,
	ID_S,
	REL,
	REL8;
}



enum class Width(val sizeString: String?) {
	BYTE("SB"),
	WORD("SW"),
	DWORD("SD"),
	QWORD("SQ"),
	TWORD(null),
	XWORD("SO"),
	YWORD("SY"),
	ZWORD("SZ");
}



val ignoredExtras = setOf(
	"DEFAULT",
	"ANY",
	"VEX",
	"EVEX",
	"NOP",
	"HLE",
	"NOHLE",
	"PRIV",
	"SMM",
	"PROT",
	"LOCK",
	"LONG",
	"BND",
	"MIB",
	"SIB",
	"SIZE",
	"ANYSIZE",
	"ND",
	"SX"
)



val ignoredParts = setOf(
	"hle",
	"nof3",
	"hlenl",
	"hlexr",
	"adf",
	"norexb",
	"norexx",
	"norexr",
	"norexw",
	"nohi",
	"nof3",
	"norep",
	"repe",
	"np",
	"iwdq"
)



val customMnemonics = setOf(
	// 16-bit encodings
	"ENTER",
	"LEAVE",

	// Unique encodings
	"IN",
	"OUT",
	"MOV",

	// Opreg
	"XCHG",
	"BSWAP",

	// Opreg, imm, 16-bit encodings
	"PUSH",
	"POP",

	// Far
	"JMP",
	"CALL",

	// Mismatched register widths
	"MOVSX",
	"MOVZX",
	"MOVSXD",

	// Mismatched register widths
	"LEA",
	"LAR",
	"LSL",
	"LGS",
	"LFS",
	"LSS",

	// Obsolete
	"JMPE",

	// Not found in Intel manuals?
	"CMPccXADD",
)



val invalidExtras = setOf(
	"NOLONG",
	"NEVER",
	"UNDOC",
	"OBSOLETE",
	"AMD",
	"CYRIX",
	"LATEVEX",
	"OPT",
	"3DNOW"
)



val invalidOperands = setOf(
	"sbyte",
	"fpureg|to",
	"xmm0",
	"imm64|near"
)