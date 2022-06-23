section .data

mnemonic_search_array:
	dq "UD"
	dq "IN"
	dq "OR"
	dq "BT"
	dq "AAA"
	dq "DAA"
	dq "LEA"
	dq "SBB"
	dq "SUB"
	dq "ADC"
	dq "DEC"
	dq "CLC"
	dq "CMC"
	dq "INC"
	dq "BTC"
	dq "STC"
	dq "AAD"
	dq "ADD"
	dq "CLD"
	dq "FLD"
	dq "AND"
	dq "STD"
	dq "CWD"
	dq "BSF"
	dq "NEG"
	dq "CLI"
	dq "STI"
	dq "SAL"
	dq "RCL"
	dq "SHL"
	dq "ROL"
	dq "LSL"
	dq "MUL"
	dq "AAM"
	dq "RSM"
	dq "CQO"
	dq "REP"
	dq "CMP"
	dq "JMP"
	dq "NOP"
	dq "POP"
	dq "CDQ"
	dq "LAR"
	dq "SAR"
	dq "RCR"
	dq "SHR"
	dq "POR"
	dq "ROR"
	dq "XOR"
	dq "BSR"
	dq "BTR"
	dq "LTR"
	dq "STR"
	dq "AAS"
	dq "DAS"
	dq "LDS"
	dq "LES"
	dq "LFS"
	dq "LGS"
	dq "INS"
	dq "LSS"
	dq "BTS"
	dq "RET"
	dq "HLT"
	dq "INT"
	dq "NOT"
	dq "FST"
	dq "OUT"
	dq "DIV"
	dq "MOV"
	dq "CBW"
	dq "Jcc"
	dq "FLD1"
	dq "INT1"
	dq "INT3"
	dq "POPA"
	dq "KORB"
	dq "INSB"
	dq "FSUB"
	dq "CLWB"
	dq "CLAC"
	dq "STAC"
	dq "FADD"
	dq "XADD"
	dq "FBLD"
	dq "SHLD"
	dq "FILD"
	dq "PAND"
	dq "XEND"
	dq "DPPD"
	dq "ORPD"
	dq "SHRD"
	dq "KORD"
	dq "INSD"
	dq "INVD"
	dq "MOVD"
	dq "CWDE"
	dq "REPE"
	dq "CDQE"
	dq "LAHF"
	dq "SAHF"
	dq "POPF"
	dq "XCHG"
	dq "FXCH"
	dq "PUSH"
	dq "BZHI"
	dq "BLSI"
	dq "LOCK"
	dq "CALL"
	dq "ARPL"
	dq "FMUL"
	dq "IMUL"
	dq "FXAM"
	dq "FCOM"
	dq "ANDN"
	dq "FSIN"
	dq "INTO"
	dq "PDEP"
	dq "FNOP"
	dq "LOOP"
	dq "FSTP"
	dq "KORQ"
	dq "MOVQ"
	dq "PXOR"
	dq "VERR"
	dq "BLSR"
	dq "SCAS"
	dq "FABS"
	dq "LODS"
	dq "FCHS"
	dq "EMMS"
	dq "FCOS"
	dq "STOS"
	dq "CMPS"
	dq "DPPS"
	dq "ORPS"
	dq "CLTS"
	dq "OUTS"
	dq "MOVS"
	dq "XLAT"
	dq "LGDT"
	dq "SGDT"
	dq "LIDT"
	dq "SIDT"
	dq "LLDT"
	dq "SLDT"
	dq "IRET"
	dq "WAIT"
	dq "TEST"
	dq "FIST"
	dq "FTST"
	dq "PEXT"
	dq "FDIV"
	dq "IDIV"
	dq "VERW"
	dq "KORW"
	dq "LMSW"
	dq "SMSW"
	dq "INSW"
	dq "ADCX"
	dq "SHLX"
	dq "MULX"
	dq "ADOX"
	dq "SARX"
	dq "SHRX"
	dq "RORX"
	dq "FLDZ"
	dq "REPZ"
	dq "F2XM1"
	dq "CRC32"
	dq "PUSHA"
	dq "PSUBB"
	dq "KADDB"
	dq "PADDB"
	dq "KANDB"
	dq "PAVGB"
	dq "KXORB"
	dq "SCASB"
	dq "PABSB"
	dq "LODSB"
	dq "STOSB"
	dq "CMPSB"
	dq "OUTSB"
	dq "MOVSB"
	dq "XLATB"
	dq "KNOTB"
	dq "FISUB"
	dq "KMOVB"
	dq "RDPMC"
	dq "RDTSC"
	dq "POPAD"
	dq "PSRAD"
	dq "PSUBD"
	dq "FIADD"
	dq "KADDD"
	dq "PADDD"
	dq "KANDD"
	dq "POPFD"
	dq "RDPID"
	dq "CPUID"
	dq "PSLLD"
	dq "PSRLD"
	dq "BOUND"
	dq "SUBPD"
	dq "ADDPD"
	dq "ANDPD"
	dq "MULPD"
	dq "MINPD"
	dq "CMPPD"
	dq "XORPD"
	dq "DIVPD"
	dq "MAXPD"
	dq "KXORD"
	dq "SCASD"
	dq "PABSD"
	dq "SUBSD"
	dq "ADDSD"
	dq "LODSD"
	dq "MULSD"
	dq "MINSD"
	dq "STOSD"
	dq "CMPSD"
	dq "OUTSD"
	dq "DIVSD"
	dq "MOVSD"
	dq "MAXSD"
	dq "IRETD"
	dq "KNOTD"
	dq "KMOVD"
	dq "MOVBE"
	dq "FFREE"
	dq "REPNE"
	dq "PAUSE"
	dq "LEAVE"
	dq "FSAVE"
	dq "XSAVE"
	dq "PUSHF"
	dq "FCOMI"
	dq "FLDPI"
	dq "BNDMK"
	dq "BNDCL"
	dq "FIMUL"
	dq "FPREM"
	dq "FICOM"
	dq "FUCOM"
	dq "FPTAN"
	dq "BNDCN"
	dq "PANDN"
	dq "BSWAP"
	dq "FSUBP"
	dq "FADDP"
	dq "FMULP"
	dq "FCOMP"
	dq "FBSTP"
	dq "FISTP"
	dq "FDIVP"
	dq "PSRAQ"
	dq "PSUBQ"
	dq "KADDQ"
	dq "PADDQ"
	dq "KANDQ"
	dq "POPFQ"
	dq "PSLLQ"
	dq "PSRLQ"
	dq "KXORQ"
	dq "PABSQ"
	dq "LODSQ"
	dq "STOSQ"
	dq "CMPSQ"
	dq "MOVSQ"
	dq "KNOTQ"
	dq "KMOVQ"
	dq "FSUBR"
	dq "ENTER"
	dq "RDMSR"
	dq "WRMSR"
	dq "BEXTR"
	dq "FDIVR"
	dq "SUBPS"
	dq "ADDPS"
	dq "ANDPS"
	dq "MULPS"
	dq "MINPS"
	dq "RCPPS"
	dq "CMPPS"
	dq "XORPS"
	dq "DIVPS"
	dq "MAXPS"
	dq "SUBSS"
	dq "ADDSS"
	dq "MULSS"
	dq "MINSS"
	dq "RCPSS"
	dq "CMPSS"
	dq "DIVSS"
	dq "MOVSS"
	dq "MAXSS"
	dq "FWAIT"
	dq "MWAIT"
	dq "FINIT"
	dq "LZCNT"
	dq "TZCNT"
	dq "FSQRT"
	dq "PTEST"
	dq "XTEST"
	dq "BNDCU"
	dq "LDDQU"
	dq "FIDIV"
	dq "PSRAW"
	dq "PSUBW"
	dq "FLDCW"
	dq "FSTCW"
	dq "KADDW"
	dq "PADDW"
	dq "KANDW"
	dq "PAVGW"
	dq "PSLLW"
	dq "PSRLW"
	dq "KXORW"
	dq "SCASW"
	dq "PABSW"
	dq "LODSW"
	dq "STOSW"
	dq "CMPSW"
	dq "FSTSW"
	dq "OUTSW"
	dq "MOVSW"
	dq "KNOTW"
	dq "KMOVW"
	dq "FYL2X"
	dq "FCLEX"
	dq "MOVSX"
	dq "MOVZX"
	dq "REPNZ"
	dq "SETcc"
	dq "FPREM1"
	dq "FLDLG2"
	dq "FLDLN2"
	dq "MOVDQA"
	dq "PSHUFB"
	dq "VPERMB"
	dq "KANDNB"
	dq "PSIGNB"
	dq "VPCMPB"
	dq "KXNORB"
	dq "PINSRB"
	dq "PEXTRB"
	dq "PSUBSB"
	dq "PADDSB"
	dq "PMINSB"
	dq "PMAXSB"
	dq "KTESTB"
	dq "PMINUB"
	dq "PMAXUB"
	dq "AESDEC"
	dq "XSAVEC"
	dq "AESIMC"
	dq "AESENC"
	dq "PUSHAD"
	dq "PHSUBD"
	dq "PHADDD"
	dq "RDSEED"
	dq "PUSHFD"
	dq "PSHUFD"
	dq "PMULLD"
	dq "VPROLD"
	dq "VPERMD"
	dq "RDRAND"
	dq "KANDND"
	dq "PSIGND"
	dq "MOVAPD"
	dq "HSUBPD"
	dq "HADDPD"
	dq "SHUFPD"
	dq "MOVHPD"
	dq "MOVLPD"
	dq "VPCMPD"
	dq "ANDNPD"
	dq "SQRTPD"
	dq "MOVUPD"
	dq "KXNORD"
	dq "VPRORD"
	dq "PINSRD"
	dq "PEXTRD"
	dq "COMISD"
	dq "PMINSD"
	dq "SQRTSD"
	dq "PMAXSD"
	dq "KTESTD"
	dq "PMINUD"
	dq "PMAXUD"
	dq "WBINVD"
	dq "MOVSXD"
	dq "FLDL2E"
	dq "LFENCE"
	dq "MFENCE"
	dq "SFENCE"
	dq "FSCALE"
	dq "TPAUSE"
	dq "FNSAVE"
	dq "FXSAVE"
	dq "INVLPG"
	dq "FUCOMI"
	dq "MOVNTI"
	dq "BLSMSK"
	dq "FPATAN"
	dq "XBEGIN"
	dq "RDTSCP"
	dq "FCOMIP"
	dq "FICOMP"
	dq "FUCOMP"
	dq "FCOMPP"
	dq "FSUBRP"
	dq "FDIVRP"
	dq "FISTTP"
	dq "PSLLDQ"
	dq "PSRLDQ"
	dq "PMULDQ"
	dq "PUSHFQ"
	dq "PMULLQ"
	dq "VPROLQ"
	dq "VPERMQ"
	dq "KANDNQ"
	dq "VPCMPQ"
	dq "KXNORQ"
	dq "VPRORQ"
	dq "PINSRQ"
	dq "PEXTRQ"
	dq "PMINSQ"
	dq "PMAXSQ"
	dq "MOVNTQ"
	dq "KTESTQ"
	dq "PMINUQ"
	dq "PMAXUQ"
	dq "FISUBR"
	dq "FRSTOR"
	dq "XRSTOR"
	dq "FIDIVR"
	dq "XSAVES"
	dq "SWAPGS"
	dq "MOVAPS"
	dq "HSUBPS"
	dq "HADDPS"
	dq "SHUFPS"
	dq "MOVHPS"
	dq "MOVLPS"
	dq "ANDNPS"
	dq "SQRTPS"
	dq "MOVUPS"
	dq "COMISS"
	dq "SQRTSS"
	dq "FLDL2T"
	dq "SYSRET"
	dq "UMWAIT"
	dq "FNINIT"
	dq "POPCNT"
	dq "XABORT"
	dq "MOVDQU"
	dq "RDPKRU"
	dq "WRPKRU"
	dq "XGETBV"
	dq "XSETBV"
	dq "FLDENV"
	dq "FSTENV"
	dq "BNDMOV"
	dq "PSADBW"
	dq "PHSUBW"
	dq "FNSTCW"
	dq "PHADDW"
	dq "PSHUFW"
	dq "PMULHW"
	dq "PMULLW"
	dq "VPERMW"
	dq "KANDNW"
	dq "PSIGNW"
	dq "VPCMPW"
	dq "KXNORW"
	dq "PINSRW"
	dq "PEXTRW"
	dq "PSUBSW"
	dq "PADDSW"
	dq "PMINSW"
	dq "FNSTSW"
	dq "PMAXSW"
	dq "KTESTW"
	dq "PMINUW"
	dq "PMAXUW"
	dq "BNDLDX"
	dq "FNCLEX"
	dq "PMOVSX"
	dq "BNDSTX"
	dq "PMOVZX"
	dq "LOOPcc"
	dq "CMOVcc"
	dq "FYL2XP1"
	dq "VPMOVDB"
	dq "PCMPEQB"
	dq "VPMOVQB"
	dq "PSUBUSB"
	dq "PADDUSB"
	dq "PCMPGTB"
	dq "VPCMPUB"
	dq "VPMOVWB"
	dq "INVPCID"
	dq "VALIGND"
	dq "BLENDPD"
	dq "ROUNDPD"
	dq "VPERMPD"
	dq "MOVNTPD"
	dq "VTESTPD"
	dq "PCMPEQD"
	dq "VPMOVQD"
	dq "ROUNDSD"
	dq "UCOMISD"
	dq "PCMPGTD"
	dq "VPCMPUD"
	dq "VPSRAVD"
	dq "VPSLLVD"
	dq "VPROLVD"
	dq "VPSRLVD"
	dq "VPRORVD"
	dq "PMADDWD"
	dq "PTWRITE"
	dq "CMPXCHG"
	dq "CLFLUSH"
	dq "MOVDIRI"
	dq "SYSCALL"
	dq "FUCOMIP"
	dq "FUCOMPP"
	dq "FDECSTP"
	dq "FINCSTP"
	dq "MOVDDUP"
	dq "MOVDQ2Q"
	dq "MOVQ2DQ"
	dq "MOVNTDQ"
	dq "PMULUDQ"
	dq "VALIGNQ"
	dq "PCMPEQQ"
	dq "PCMPGTQ"
	dq "VPCMPUQ"
	dq "VPSRAVQ"
	dq "VPSLLVQ"
	dq "VPROLVQ"
	dq "VPSRLVQ"
	dq "VPRORVQ"
	dq "PALIGNR"
	dq "MONITOR"
	dq "FXRSTOR"
	dq "LDMXCSR"
	dq "STMXCSR"
	dq "FSINCOS"
	dq "BLENDPS"
	dq "ROUNDPS"
	dq "MOVLHPS"
	dq "MOVHLPS"
	dq "VPERMPS"
	dq "MOVNTPS"
	dq "RSQRTPS"
	dq "VTESTPS"
	dq "XRSTORS"
	dq "ROUNDSS"
	dq "UCOMISS"
	dq "RSQRTSS"
	dq "FXTRACT"
	dq "SYSEXIT"
	dq "FRNDINT"
	dq "FNSTENV"
	dq "MPSADBW"
	dq "PBLENDW"
	dq "VPMOVDW"
	dq "PSHUFHW"
	dq "PSHUFLW"
	dq "PCMPEQW"
	dq "VPMOVQW"
	dq "PHSUBSW"
	dq "PHADDSW"
	dq "PSUBUSW"
	dq "PADDUSW"
	dq "PCMPGTW"
	dq "PMULHUW"
	dq "VPCMPUW"
	dq "VPSRAVW"
	dq "VPSLLVW"
	dq "VPSRLVW"
	dq "FCMOVcc"
	dq "SHA1MSG1"
	dq "SHA1MSG2"
	dq "VMOVDQU8"
	dq "MOVNTDQA"
	dq "VPERMI2B"
	dq "VPMOVM2B"
	dq "VPERMT2B"
	dq "VPMOVSDB"
	dq "PMOVMSKB"
	dq "KSHIFTLB"
	dq "VPTESTMB"
	dq "VPMOVSQB"
	dq "KSHIFTRB"
	dq "KORTESTB"
	dq "PBLENDVB"
	dq "PACKSSWB"
	dq "PACKUSWB"
	dq "VPMOVSWB"
	dq "VPERMI2D"
	dq "VPMOVM2D"
	dq "VPERMT2D"
	dq "VPBLENDD"
	dq "KSHIFTLD"
	dq "VPTESTMD"
	dq "CVTPI2PD"
	dq "CVTDQ2PD"
	dq "CVTPS2PD"
	dq "VRCP14PD"
	dq "ADDSUBPD"
	dq "VRANGEPD"
	dq "UNPCKHPD"
	dq "MOVMSKPD"
	dq "UNPCKLPD"
	dq "BLENDVPD"
	dq "VPMOVSQD"
	dq "KSHIFTRD"
	dq "CVTSI2SD"
	dq "CVTSS2SD"
	dq "VRCP14SD"
	dq "VRANGESD"
	dq "VPLZCNTD"
	dq "KORTESTD"
	dq "KUNPCKWD"
	dq "XACQUIRE"
	dq "RDFSBASE"
	dq "WRFSBASE"
	dq "RDGSBASE"
	dq "WRGSBASE"
	dq "XRELEASE"
	dq "CLDEMOTE"
	dq "CVTPD2PI"
	dq "CVTPS2PI"
	dq "CVTSD2SI"
	dq "CVTSS2SI"
	dq "VZEROALL"
	dq "VPMOVB2M"
	dq "VPMOVD2M"
	dq "VPMOVQ2M"
	dq "VPMOVW2M"
	dq "MOVSHDUP"
	dq "MOVSLDUP"
	dq "VPERMI2Q"
	dq "VPMOVM2Q"
	dq "VPERMT2Q"
	dq "CVTPD2DQ"
	dq "CVTPS2DQ"
	dq "KUNPCKDQ"
	dq "KSHIFTLQ"
	dq "VPTESTMQ"
	dq "KSHIFTRQ"
	dq "VPLZCNTQ"
	dq "KORTESTQ"
	dq "MASKMOVQ"
	dq "SYSENTER"
	dq "UMONITOR"
	dq "CVTPD2PS"
	dq "CVTPI2PS"
	dq "CVTDQ2PS"
	dq "VRCP14PS"
	dq "ADDSUBPS"
	dq "VRANGEPS"
	dq "UNPCKHPS"
	dq "MOVMSKPS"
	dq "UNPCKLPS"
	dq "INSERTPS"
	dq "BLENDVPS"
	dq "CVTSD2SS"
	dq "CVTSI2SS"
	dq "VRCP14SS"
	dq "VRANGESS"
	dq "XSAVEOPT"
	dq "VMASKMOV"
	dq "VPERMI2W"
	dq "VPMOVM2W"
	dq "VPERMT2W"
	dq "KUNPCKBW"
	dq "PACKSSDW"
	dq "PACKUSDW"
	dq "VPMOVSDW"
	dq "KSHIFTLW"
	dq "VPTESTMW"
	dq "VPMOVSQW"
	dq "KSHIFTRW"
	dq "PMULHRSW"
	dq "KORTESTW"