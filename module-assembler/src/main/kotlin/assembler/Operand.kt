package assembler

interface Operand

class RegisterOperand(val value: Register)

class Imm8Operand(val value: Int)

class Imm16Operand(val value: Int)

class Imm32Operand(val value: Long)

class Imm64Operand(val value: Long)