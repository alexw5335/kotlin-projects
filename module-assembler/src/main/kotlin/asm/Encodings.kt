package asm

import core.ReaderBase



class EncodingReader(chars: CharArray) : ReaderBase(chars) {


	constructor() : this(TEXT.toCharArray())



	private class InstructionTemp(
		val opcode    : Int,
		val extension : Int,
		val prefix    : Int,
		val operands  : Operands,
		val widths    : Widths
	)



	private enum class CompoundOperands(vararg val components: Operands) {

		R_RM(Operands.R_R, Operands.R_M),
		RM_R(Operands.R_R, Operands.M_R),
		RM(Operands.R, Operands.M),
		RM_I(Operands.R_I, Operands.M_I),
		RM_I8(Operands.R_I8, Operands.M_I8);

	}



	private val encodingsMap = HashMap<String, ArrayList<InstructionTemp>>()

	private val operandsMap = Operands.values().associateBy { it.name }

	private val compoundOperandsMap = CompoundOperands.values().associateBy { it.name }

	private val mnemonicMap = Mnemonic.values().associateBy { it.name }



	fun read(): Map<Mnemonic, InstructionGroup> {
		while(pos < chars.size) {
			when(chars[pos]) {
				'#'  -> skipLine()
				'\r' -> pos += 2
				'\n' -> pos++
				' '  -> skipLine()
				else -> readEncoding()
			}
		}

		//for(mnemonic in encodingsMap.keys) println("$mnemonic,")

		val groupMap = HashMap<Mnemonic, InstructionGroup>()

		for((mnemonicString, encodings) in encodingsMap) {
			encodings.sortBy { it.operands }
			var operandsBits = 0
			var specifierBits = 0

			for(e in encodings) {
				operandsBits = operandsBits or e.operands.bit
				specifierBits = specifierBits or e.operands.specifier.bit
			}

			val mnemonic = mnemonicMap[mnemonicString] ?: error("Invalid mnemonic: $mnemonicString")

			groupMap[mnemonic] = InstructionGroup(
				encodings.map { Instruction(it.opcode, it.extension, it.prefix, it.operands, it.widths) },
				operandsBits,
				specifierBits
			)
		}

		return groupMap
	}



	private fun readEncoding() {
		var opcode = 0
		var extension = 0
		var prefix = 0
		var widths = Widths.NONE

		while(true) {
			val char = chars[pos++]

			if(char.isWhitespace())
				break

			if(char == '/') {
				extension = chars[pos++].digitToInt()
				break
			}

			opcode = (opcode shl 4) or char.digitToInt(16)
		}

		when(opcode and 0xFF) {
			0xF2, 0xF3, 0x66 -> {
				prefix = opcode and 0xFF
				opcode = opcode shr 8
			}
		}

		skipSpaces()
		val mnemonicString = readUntil { it.isWhitespace() }
		skipSpaces()
		val operandsString = readUntil { it.isWhitespace() }

		while(chars[pos] != '\n' && chars[pos] != '\r') {
			skipSpaces()
			widths = when(val string = readUntil { it.isWhitespace() }) {
				"ALL"    -> Widths.ALL
				"NO8"    -> Widths.NO8
				"NO816"  -> Widths.NO16
				"NO832"  -> Widths.NO832
				"NO864"  -> Widths.NO864
				"ONLY8"  -> Widths.ONLY8
				"ONLY16" -> Widths.ONLY16
				"ONLY64" -> Widths.ONLY64
				else     -> error("Invalid modifier: $string")
			}
		}

		val list = encodingsMap.getOrPut(mnemonicString, ::ArrayList)

		if(operandsMap.contains(operandsString)) {
			list.add(InstructionTemp(opcode, prefix, extension, operandsMap[operandsString]!!, widths))
		} else {
			val compoundOperands = compoundOperandsMap[operandsString]
				?: error("Invalid operands: $operandsString")

			for(operands in compoundOperands.components)
				if(list.none { it.operands == operands })
					list.add(InstructionTemp(opcode, prefix, extension, operands, widths))
		}

		skipLine()
	}


}



private const val TEXT = """

01    ADD  A_I    ALL
80/0  ADD  RM_I   ALL
83/0  ADD  RM_I8  NO8
00    ADD  RM_R   ALL
02    ADD  R_RM   ALL

0C    OR  A_I    ALL
80/1  OR  RM_I   ALL
83/1  OR  RM_I8  NO8
08    OR  RM_R   ALL
0A    OR  R_RM   ALL

14    ADC  A_I    ALL
80/2  ADC  RM_I   ALL
83/2  ADC  RM_I8  NO8
10    ADC  RM_R   ALL
12    ADC  R_RM   ALL

1C    SBB  A_I    ALL
80/3  SBB  RM_I   ALL
83/3  SBB  RM_I8  NO8
18    SBB  RM_R   ALL
1A    SBB  R_RM   ALL

24    AND  A_I    ALL
80/4  AND  RM_I   ALL
83/4  AND  RM_I8  NO8
20    AND  RM_R   ALL
22    AND  R_RM   ALL

2C    SUB  A_I    ALL
80/5  SUB  RM_I   ALL
83/5  SUB  RM_I8  NO8
28    SUB  RM_R   ALL
2A    SUB  R_RM   ALL

34    XOR  A_I    ALL
80/6  XOR  RM_I   ALL
83/6  XOR  RM_I8  NO8
30    XOR  RM_R   ALL
32    XOR  R_RM   ALL

3C    CMP  A_I    ALL
80/7  CMP  RM_I   ALL
83/7  CMP  RM_I8  NO8
38    CMP  RM_R   ALL
3A    CMP  R_RM   ALL

"""



private const val FPU_ENCODINGS_TEXT = """
	
D8/0  FADD   M32FP
DC/0  FADD   M64FP
C0D8  FADD   ST0_STI
C0DC  FADD   STI_ST0
C1DC  FADD
C1DE  FADDP  STI_ST0
C1DE  FADDP
DA/0  FIADD  M32INT
DE/0  FIADD  M16INT

D8/1  FMUL   M32FP
DC/1  FMUL   M64FP
C8D8  FMUL   ST0_STI
C8DC  FMUL   STI_ST0
C9DC  FMUL
C8DE  FMULP  STI_ST0
C9DE  FMULP
DA/1  FIMUL  M32INT
DE/1  FIMUL  M16INT

D8/2  FCOM    M32FP
DC/2  FCOM    M64FP
D0D8  FCOM    STI
D1D8  FCOM
D8/3  FCOMP   M32FP
DC/3  FCOMP   M64FP
D8D8  FCOMP   STI
D9D8  FCOMP
D9DE  FCOMPP

D8/4  FSUB   M32FP
DC/4  FSUB   M64FP
E0D8  FSUB   ST0_STI
E8DC  FSUB   STI_ST0
E9DC  FSUB
E8DE  FSUBP  STI_ST0
E9DE  FSUBP
DA/4  FISUB  M32INT
DE/4  FISUB  M16INT

D8/5  FSUBR   M32FP
DC/5  FSUBR   M64FP
E8D8  FSUBR   ST0_STI
E0DC  FSUBR   STI_ST0
E1DC  FSUBR
E0DE  FSUBRP  STI_ST0
E1DE  FSUBRP
DA/5  FISUBR  M32INT
DE/5  FISUBR  M16INT

D8/6  FDIV   M32FP
DC/6  FDIV   M64FP
F0D8  FDIV   ST0_STI
F8DC  FDIV   STI_ST0
F9DC  FDIV
F8DE  FDIVP  STI_ST0
F9DE  FDIVP
DA/6  FIDIV  M32INT
DE/6  FIDIV  M16INT

D8/7  FDIVR   M32FP
DC/7  FDIVR   M64FP
F8D8  FDIVR   STI_ST
F0DC  FDIVR   STI_ST0
F1DC  FDIVR
F0DE  FDIVRP  STI_ST0
F1DE  FDIVRP
DA/7  FIDIVR  M32INT
DE/7  FIDIVR  M16INT

D9/0  FLD  M32FP
DD/0  FLD  M64FP
DB/5  FLD  M80FP
C0D9  FLD  STI

C8D9  FXCH STI
C9D9  FXCH

D9/2  FST   M32FP
DD/2  FST   M64FP
D0DD  FST   STI
D9/3  FSTP  M32FP
DD/3  FSTP  M64FP
DB/7  FSTP  M80FP
D8DD  FSTP  STI

D0D9  FNOP

D9/4  FLDENV  M

E0D9  FCHS

E1D9  FABS

E4D9  FTST

E5D9  FXAM

D9/5  FLDCW  M

E8D9  FLD1
E9D9  FLDL2T
EAD9  FLDL2E
EBD9  FLDPI
ECD9  FLDLG2
EDD9  FLDLN2
EED9  FLDZ

D99B  FSTENV   M
D9/6  FNSTENV  M

F0D9  F2XM1
F1D9  FYL2X
F2D9  FPTAN
F3D9  FPATAN
F4D9  FXTRACT
F5D9  FPREM1
F6D9  FDECSTP
F7D9  FINCSTP
F8D9  FPREM
F9D9  FYL2XP1
FAD9  FSQRT
FBD9  FSINCOS
FCD9  FRNDINT
FDD9  FSCALE
FED9  FSIN
FFD9  FCOS

D9/7    FNSTCW  M16
D99B/7  FSTCW   M16

C0DA  FCMOVB    ST0_STI
C8DA  FCMOVE    ST0_STI
D0DA  FCMOVBE   ST0_STI
D8DA  FCMOVU    ST0_STI
C0DB  FCMOVNB   ST0_STI
C8DB  FCMOVNE   ST0_STI
D0DB  FCMOVNBE  ST0_STI
D8DB  FCMOVNU   ST0_STI

E0DD  FUCOM    STI
E1DD  FUCOM
E8DD  FUCOMP   STI
E9DD  FUCOMP
E9DA  FUCOMPP

DF/0  FILD  M16INT
DB/0  FILD  M32INT
DF/5  FILD  M64INT

DF/1  FISTTP  M16INT
DB/1  FISTTP  M32INT
DD/1  FISTTP  M64INT

E2DB9B  FCLEX
E2DB    FNCLEX

E3DB9B  FINIT
E3DB    FNINIT

F0DB  FCOMI    ST0_STI
F0DF  FCOMIP   ST0_STI
E8DB  FUCOMI   ST0_STI
E8DF  FUCOMIP  ST0_STI

C0DD  FFREE  STI

DD/4  FRSTOR  M

DE/2  FICOM   M16INT
DA/2  FICOM   M32INT
DE/3  FICOMP  M16INT
DA/3  FICOMP  M32INT

DD9B/6  FSAVE   M
DD/6    FNSAVE  M

DD9B/7  FSTSW   M16
E0DF9B  FSTSW   AX
DD/7    FNSTSW  M16
E0DF    FNSTSW  AX

DF/2  FIST   M16INT
DB/2  FIST   M32INT
DF/3  FISTP  M16INT
DB/3  FISTP  M32INT
DF/7  FISTP  M64INT

DF/6  FBSTP  M80BCD


"""