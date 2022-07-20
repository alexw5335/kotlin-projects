package assembler

enum class Operand(val matches: (AstNode) -> Boolean = { false }) {

	R8    ({ it is RegisterNode && it.value.width == 1 }),
	R16   ({ it is RegisterNode && it.value.width == 2 }),
	R32   ({ it is RegisterNode && it.value.width == 3 }),
	R64   ({ it is RegisterNode && it.value.width == 4 }),
	M8    ({ it is MemoryNode }),
	M16   ({ it is MemoryNode }),
	M32   ({ it is MemoryNode }),
	M64   ({ it is MemoryNode }),
	IMM8  ({ it is ImmediateNode }),
	IMM16 ({ it is ImmediateNode }),
	IMM32 ({ it is ImmediateNode }),
	IMM64 ({ it is ImmediateNode }),
	AL    ({ it is RegisterNode && it.value == Register.AL }),
	AX    ({ it is RegisterNode && it.value == Register.AX }),
	EAX   ({ it is RegisterNode && it.value == Register.EAX }),
	RAX   ({ it is RegisterNode && it.value == Register.RAX }),
	CL    ({ it is RegisterNode && it.value == Register.CL }),
	DX    ({ it is RegisterNode && it.value == Register.DX });

}