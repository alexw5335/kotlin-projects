package assembler

enum class Mnemonic {

	ADD,
	OR,
	ADC,
	SBB,
	AND,
	SUB,
	XOR,
	CMP,

	ROL,
	ROR,
	RCL,
	RCR,
	SHL,
	SAL,
	SHR,
	SAR,

	CBW,
	CWDE,
	CDQE,

	CWD,
	CDQ,
	CQO,

	CLC,
	CLD,
	CLI,
	CLTS,
	CMC,

	STC,
	STI,
	STD,

	BSWAP,
	
	CMPSB,
	CMPSW,
	CMPSD,
	CMPSQ,

	SCASB,
	SCASW,
	SCASD,
	SCASQ,

	STOSB,
	STOSW,
	STOSD,
	STOSQ,

	LODSB,
	LODSW,
	LODSD,
	LODSQ,

	MOVSB,
	MOVSW,
	MOVSD,
	MOVSQ,

	INSB,
	INSW,
	INSD,

	OUTSB,
	OUTSW,
	OUTSD,

	CPUID,

	RET,
	RETF,

	SHLD,
	SHRD,

	CMOVA,
	CMOVAE,
	CMOVB,
	CMOVBE,
	CMOVC,
	CMOVE,
	CMOVG,
	CMOVGE,
	CMOVL,
	CMOVLE,
	CMOVNA,
	CMOVNAE,
	CMOVNB,
	CMOVNBE,
	CMOVNC,
	CMOVNE,
	CMOVNG,
	CMOVNGE,
	CMOVNL,
	CMOVNLE,
	CMOVNO,
	CMOVNP,
	CMOVNS,
	CMOVNZ,
	CMOVO,
	CMOVP,
	CMOVPE,
	CMOVPO,
	CMOVS,
	CMOVZ,

	SETA,
	SETAE,
	SETB,
	SETBE,
	SETC,
	SETE,
	SETG,
	SETGE,
	SETL,
	SETLE,
	SETNA,
	SETNAE,
	SETNB,
	SETNBE,
	SETNC,
	SETNE,
	SETNG,
	SETNGE,
	SETNL,
	SETNLE,
	SETNO,
	SETNP,
	SETNS,
	SETNZ,
	SETO,
	SETP,
	SETPE,
	SETPO,
	SETS,
	SETZ,

	JA,
	JAE,
	JB,
	JBE,
	JC,
	JE,
	JG,
	JGE,
	JL,
	JLE,
	JNA,
	JNAE,
	JNB,
	JNBE,
	JNC,
	JNE,
	JNG,
	JNGE,
	JNL,
	JNLE,
	JNO,
	JNP,
	JNS,
	JNZ,
	JO,
	JP,
	JPE,
	JPO,
	JS,
	JZ,

	JCXZ,
	JECXZ,
	JRCXZ,

	LOOP,
	LOOPE,
	LOOPNE,

	CALL,
	CALLF,
	JMP,
	JMPF,

	MOVZX,
	MOVSX,
	MOVSXD,

	PUSH,
	PUSHW,
	POP,
	POPW,

	INT1,
	INT3,
	INT,

	HLT,

	INC,
	DEC,

	PUSHF,
	PUSHFQ,
	LAHF,

	IN,
	OUT,

	WAIT,
	FWAIT,

	NOP,

	LEA,

	XCHG,

	MOV,

	IRETW,
	IRETD,
	IRETQ,

	TEST,
	NOT,
	NEG,
	MUL,
	IMUL,
	DIV,
	IDIV,

	MFENCE,

	LEAVE,
	LEAVEW,
	ENTER,
	ENTERW,

	BT,
	BTC,
	BTR,
	BTS,

	RSM,

	BSF,
	BSR,

	MOVNTI,

	SLDT,
	LLDT,

	STR,
	LTR,

	SGDT,
	SIDT,

	VERR,
	VERW,
	VMCALL,
	VMLAUNCH,
	VMRESUME,
	VMXOFF,

	FADD,
	FADDP,
	FIADD,
	FMUL,
	FMULP,
	FIMUL,
	FCOM,
	FCOMP,
	FCOMPP,
	FSUB,
	FSUBP,
	FISUB,
	FSUBR,
	FSUBRP,
	FISUBR,
	FDIV,
	FDIVP,
	FIDIV,
	FDIVR,
	FDIVRP,
	FIDVR,
	FLD,
	FXCH,
	FST,
	FSTP,
	FNOP,
	FLDENV,
	FCHS,
	FABS,
	FTST,
	FXAM,
	FLDCW,
	FLD1,
	FLDL2T,
	FLDL2E,
	FLDPI,
	FLDLG2,
	FLDLN2,
	FLDZ,
	FSTENV,
	FNSTENV,
	F2XM1,
	FYL2X,
	FPTAN,
	FPATAN,
	FXTRACT,
	FPREM1,
	FDECSTP,
	FINCSTP,
	FPREM,
	FYL2XP1,
	FSQRT,
	FSINCOS,
	FRNDINT,
	FSCALE,
	FSIN,
	FCOS,
	FNSTCW,
	FSTCW,
	FCMOVB,
	FCMOVE,
	FCMOVBE,
	FCMOVU,
	FCMOVNB,
	FCMOVNE,
	FCMOVNBE,
	FCMOVNU,
	FUCOM,
	FUCOMP,
	FUCOMPP,
	FILD,
	FISTTP,
	FCLEX,
	FNCLEX,
	FINIT,
	FNINIT,
	FCOMI,
	FCOMIP,
	FUCOMI,
	FUCOMIP,
	FFREE,
	FRSTOR,
	FICOM,
	FICOMP,
	FSAVE,
	FNSAVE,
	FSTSW,
	FNSTSW,
	FIST,
	FISTP,
	FBSTP;

	val string = name.lowercase()

}