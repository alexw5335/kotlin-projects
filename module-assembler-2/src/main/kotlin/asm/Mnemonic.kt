package asm

enum class Mnemonic(val stringWidth: Width? = null) {

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
	
	CMPSB(stringWidth = Width.BIT8),
	CMPSW(stringWidth = Width.BIT16),
	CMPSD(stringWidth = Width.BIT32),
	CMPSQ(stringWidth = Width.BIT64),

	SCASB(stringWidth = Width.BIT8),
	SCASW(stringWidth = Width.BIT16),
	SCASD(stringWidth = Width.BIT32),
	SCASQ(stringWidth = Width.BIT64),

	STOSB(stringWidth = Width.BIT8),
	STOSW(stringWidth = Width.BIT16),
	STOSD(stringWidth = Width.BIT32),
	STOSQ(stringWidth = Width.BIT64),

	LODSB(stringWidth = Width.BIT8),
	LODSW(stringWidth = Width.BIT16),
	LODSD(stringWidth = Width.BIT32),
	LODSQ(stringWidth = Width.BIT64),

	MOVSB(stringWidth = Width.BIT8),
	MOVSW(stringWidth = Width.BIT16),
	MOVSD(stringWidth = Width.BIT32),
	MOVSQ(stringWidth = Width.BIT64),

	INSB(stringWidth = Width.BIT8),
	INSW(stringWidth = Width.BIT16),
	INSD(stringWidth = Width.BIT32),

	OUTSB(stringWidth = Width.BIT8),
	OUTSW(stringWidth = Width.BIT16),
	OUTSD(stringWidth = Width.BIT32),

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

	PUSH,
	POP,

	MOVSX,
	MOVSXD,

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

	IRET,
	IRETD,
	IRETQ,

	TEST,
	NOT,
	NEG,
	MUL,
	IMUL,
	DIV,
	IDIV,

	MFENCE;

	val string = name.lowercase()

}