package assembler

enum class OperandEncoding(val matcher: (OperandNode) -> Boolean = { false }) {

	A    ({ it is RegisterNode && it.register.isGP && it.register.value == 0 }),
	AL   ({ it is RegisterNode && it.register == Register.AL }),
	R    ({ it is RegisterNode && it.register.isGP }),
	R8   ({ it is RegisterNode && it.register.isGP8 }),
	RM   ({ it is RegisterNode && it.register.isGP }),
	RM8  ({ it is RegisterNode && it.register.isGP8 }),
	IMM  ({ it is ImmediateNode }),
	IMM8 ({ it is ImmediateNode && it.value in -128..255 });

}