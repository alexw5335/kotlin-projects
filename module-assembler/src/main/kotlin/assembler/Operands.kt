package assembler



/*

04    ADD  AL  IMM8    (IMM*)
05    ADD  A   IMM     (IMM*)
80/0  ADD  RM8 IMM8
81/0  ADD  RM  IMM     (IMM*)
83/0  ADD  RM  IMM8
00    ADD  RM8 R8
01    ADD  RM  R
02    ADD  R8  RM8
03    ADD  R   RM
 */
enum class Operands2(matcher: (InstructionNode) -> Boolean) {


}



enum class OperandType1(val matcher: (AstNode) -> Boolean){

	REG({ it is RegisterNode }),
	MEM({ it is MemoryNode }),
	IMM({ it is ImmediateNode || it is ImmediateIntNode });

}



enum class OperandType2(val parent: OperandType1, val matcher: (AstNode) -> Boolean) {

	R8(OperandType1.REG,    { (it as RegisterNode).value.width == 1 }),
	R16(OperandType1.REG,   { (it as RegisterNode).value.width == 2 }),
	R32(OperandType1.REG,   { (it as RegisterNode).value.width == 3 }),
	R64(OperandType1.REG,   { (it as RegisterNode).value.width == 4 }),
	MEMn(OperandType1.MEM,  { (it as MemoryNode).width == null }),
	MEM8(OperandType1.MEM,  { (it as MemoryNode).width == null }),
	MEM16(OperandType1.MEM, { (it as MemoryNode).width == null }),
	MEM32(OperandType1.MEM, { (it as MemoryNode).width == null }),
	MEM64(OperandType1.MEM, { (it as MemoryNode).width == null }),
	IMMn(OperandType1.IMM,  { (it as MemoryNode).width == null }),
	IMM8(OperandType1.IMM,  { (it as MemoryNode).width == null }),
	IMM16(OperandType1.IMM, { (it as MemoryNode).width == null }),
	IMM32(OperandType1.IMM, { (it as MemoryNode).width == null }),
	IMM64(OperandType1.IMM, { (it as MemoryNode).width == null });

}



enum class OperandType3(val parent: OperandType2) {

	AL(OperandType2.R8),
	CL(OperandType2.R8),
	AX(OperandType2.R16),
	DX(OperandType2.R16),
	EAX(OperandType2.R32),
	RAX(OperandType2.R64),
	ZERO(OperandType2.IMM8),
	ONE(OperandType2.IMM8);

}