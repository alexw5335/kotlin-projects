default rel

%include "std.asm"

global main



; struct Operand (8 bytes)
%assign OPERAND_NONE      0 ; u8 type
%assign OPERAND_REGISTER  1 ; u8 type, u8 register, u8 registerType
%assign OPERAND_MEMORY    2 ; u8 type, u8 width, u8 sib, i32 offset
%assign OPERAND_IMMEDIATE 3 ; u8 type, u8 width, i32 value or offset into global array of 64-bit immediate values

%assign MNEMONIC_ADD 0
%assign MNEMONIC_OR 1
%assign MNEMONIC_ADC 2
%assign MNEMONIC_SBB 3
%assign MNEMONIC_AND 4
%assign MNEMONIC_SUB 5
%assign MNEMONIC_XOR 6
%assign MNEMONIC_CMP 7

%assign REGTYPE_GP8 0
%assign REGTYPE_GP16 1
%assign REGTYPE_GP32 2
%assign REGTYPE_GP64 3

%assign WIDTH_8   0
%assign WIDTH_16  1
%assign WIDTH_32  2
%assign WIDTH_64  3
%assign WIDTH_128 4
%assign WIDTH_256 5
%assign WIDTH_512 6

%assign OPERAND_R8   OPERAND_REGISTER | (REGTYPE_GP8 << 8)
%assign OPERAND_R16  OPERAND_REGISTER | (REGTYPE_GP16 << 8)
%assign OPERAND_R32  OPERAND_REGISTER | (REGTYPE_GP32 << 8)
%assign OPERAND_R64  OPERAND_REGISTER | (REGTYPE_GP64 << 8)

%assign OPERAND_M8   OPERAND_MEMORY | (WIDTH_8 << 8)
%assign OPERAND_M16  OPERAND_MEMORY | (WIDTH_16 << 8)
%assign OPERAND_M32  OPERAND_MEMORY | (WIDTH_32 << 8)
%assign OPERAND_M64  OPERAND_MEMORY | (WIDTH_64 << 8)

%assign OPERAND_IMM8  OPERAND_IMMEDIATE | (WIDTH_8 << 8)
%assign OPERAND_IMM16 OPERAND_IMMEDIATE | (WIDTH_16 << 8)
%assign OPERAND_IMM32 OPERAND_IMMEDIATE | (WIDTH_32 << 8)
%assign OPERAND_IMM64 OPERAND_IMMEDIATE | (WIDTH_64 << 8)

%define OPERANDS(a, b) ((a) | ((b) << 16))



; struct OperandEncoding
; u8[7] data     66 40 F2 0F 38 01 CA
; u8 dataLength
; data may contain: OSO, REX, REX.W, mandatory prefix (F2, F3, 66), secondary/trinary opcode, MODRM extension.



section .text



; add al, cl
mnemonic dq MNEMONIC_ADD
operands:
	db OPERAND_REGISTER, REGTYPE_GP8, 0, 0, 0, 0, 0, 0
	db OPERAND_REGISTER, REGTYPE_GP8, 1, 0, 0, 0, 0, 0
	
invalidEncodingError db "Invalid encoding error", 10, 0

main:
	sub rsp, 40
	mov ecx, [mnemonic]
	lea rdx, [operands]
	call assemble
	call ExitProcess



; rcx: operand1
; rdx: operand2
; rbx: operands
assemble_type1:
	cmp cl, OPERAND_REGISTER
	je .reg
	cmp cl, OPERAND_MEMORY
	je .mem
	jmp .error
	.reg:
		cmp cx, OPERAND_R8
		je .reg8
		cmp cx, OPERAND_R16
		je .reg16
		cmp cx, OPERAND_R32
		je .reg32
		cmp cx, OPERAND_R64
		je .reg64
		jmp .error
		.reg8:
			cmp dx, OPERAND_IMM8
			je .reg8_imm8
		.reg16:
		.reg32:
		.reg64:
	.mem:
		cmp cx, OPERAND_M8
		je .mem8
		cmp cx, OPERAND_M16
		je .mem16
		cmp cx, OPERAND_M32
		je .mem32
		cmp cx, OPERAND_M64
		je .mem64




; rcx: mnemonic
; rdx: operand1
; r8:  operand2
assemble2:
	push rbx
	push rsi
	push rdi
	sub rsp, 32

	mov bx, r8w
	shl ebx, 8
	mov bx, dx ; rbx = operands

	mov rax, r8

	movzx esi, byte [.jump_table + rcx]
	add rsi, .start
	jmp rsi

	.end:
		add rsp, 32
		pop rdi
		pop rsi
		pop rbx
		ret

	.error:
		lea rcx, [invalidEncodingError]
		call printf
		call ExitProcess

	.start:

	.m_add:
		cmp dl, OPERAND_REGISTER
		je .reg
		cmp dl, OPERAND_MEMORY
		je .mem
		jmp .error
		.reg:
			cmp dx, OPERAND_R8
			je .reg8
			cmp dx, OPERAND_R16
			je .reg16
			cmp dx, OPERAND_R32
			je .reg32
			cmp dx, OPERAND_R64
			.reg8:
				cmp ax, OPERAND_IMM8
			.reg16:
			.reg32:
			.reg64:
		.mem:
			cmp dx, OPERAND_M8
			je .mem8
			cmp dx, OPERAND_M16
			je .mem16
			cmp dx, OPERAND_M32
			je .mem32
			cmp dx, OPERAND_M64
			je .mem64

		jmp .end

	.m_or:
		jmp .end
	.m_adc:
		jmp .end
	.m_sbb:
		jmp .end
	.m_and:
		jmp .end
	.m_sub:
		jmp .end
	.m_xor:
		jmp .end
	.m_cmp:
		jmp .end

	.jump_table:
		dw .m_add - .start
		dw .m_or - .start
		dw .m_adc - .start
		dw .m_sbb - .start
		dw .m_and - .start
		dw .m_sub - .start
		dw .m_xor - .start
		dw .m_cmp - .start



%if 0



IMUL
REG
	REG8 -> REG8
	REG16






BT
REG
	REG16
		_REG16
			REG16_REG16
		_IMM8
			REG16_IMM8
	REG32
		_REG32
			REG32_REG32
		_IMM8
			REG32_IMM8
	REG64
		_REG64
			REG64_REG64
		_IMM8
			REG64_IMM8
MEM
	MEM16
		_REG16
			MEM16_REG16
		_IMM8
			MEM8_IMM8
	MEM32
		_REG32
			MEM32_REG32
		_IMM8
			MEM32_IMM8
	MEM64
		_REG64
			MEM64_REG64
		_IMM8
			MEM64_IMM8



ADD
REG
	REG8
		_IMM8
			AL_IMM8
			REG8_IMM8
		_REG8
			REG8_REG8
		_MEM8
			REG8_MEM8
	REG16
		_IMM8
			REG16_IMM8
		_IMM16
			AX_IMM16
			REG16_IMM16
		_REG16
			REG16_REG16
		_MEM16
			REG16_MEM16
	REG32
		_IMM8
			REG32_IMM8
		_IMM32
			EAX_IMM32
			REG32_IMM32
		_REG32
			REG32_REG32
		_MEM32
			REG32_MEM32
	REG64
		_IMM8
			REG64_IMM8
		_IMM32
			RAX_IMM32
			REG64_IMM32
		_REG64
			REG64_REG64
		_MEM64
			REG64_MEM64
MEM
	MEM8
		_IMM8
			MEM8_IMM8
		_REG8
			MEM8_REG8
	MEM16
		_IMM8
			MEM16_IMM8
		_IMM16
			MEM16_IMM16
		_REG16
			MEM16_REG16
	MEM32
		_IMM8
			MEM32_IMM8
		_IMM32
			MEM32_IMM32
		_REG32
			MEM32_REG32
	MEM64
		_IMM8
			MEM64_IMM8
		_IMM32
			MEM64_IMM32
		_REG64
			MEM64_REG64

R8_IMM8
MEM8_IMM8
R16_IMM16
R32_IMM32
R64_IMM32
MEM16_IMM16
MEM32_IMM32
MEM64_IMM64
R16_IMM8
R32_IMM8
R64_IMM8


04    ADD  AL  IMM8
05    ADD  A   IMM
80/0  ADD  RM8 IMM8
81/0  ADD  RM  IMM
83/0  ADD  RM  IMM8
00    ADD  RM8 R8
01    ADD  RM  R
02    ADD  R8  RM8
03    ADD  R   RM
%endif;0