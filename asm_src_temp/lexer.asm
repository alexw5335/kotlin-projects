default rel

%include "std.asm"
%include "data.asm"
%include "lexer_data.asm"

global main



; struct Node (16 bytes)
%define NODE_INSTRUCTION 0 ; u8 type, u8 operandCount, u16 mnemonic, Node* operands (operands stored separately)
%define NODE_REGISTER    1 ; u8 type, u8 regType, u8 regValue
%define NODE_IMMEDIATE   2 ; u8 type, u8 width, i64 value
%define NODE_ADDRESS     3 ; u8 type, u8 flags, u8 base, u8 index, u8 scale, i64 offset

; struct Token (16 bytes)
%define TOKEN_NONE       0
%define TOKEN_IDENTIFIER 1 ; u8 type, u8 length, if(length <= 8) char[8] string else char* string (nt)
%define TOKEN_SYMBOL     2 ; u8 type, u8 symbolOrdinal
%define TOKEN_INT        3 ; u8 type, i64 value
%define TOKEN_CHAR       4 ; u8 type, u32 value
%define TOKEN_STRING     5 ; u8 type, u32 length, if(length <= 8) char[8] string else char* string (nt)
%define TOKEN_REGISTER   6 ; u8 type, u8 regType, u8 register
%define TOKEN_MNEMONIC   7 ; u8 type, u16 mnemonicOrdinal

; enum Symbol (1 byte)
%define SYMBOL_EXCLAMATION 0
%define SYMBOL_AT 1
%define SYMBOL_HASH 2
%define SYMBOL_DOLLAR 3
%define SYMBOL_PERCENT 4
%define SYMBOL_CARET 5
%define SYMBOL_AMPERSAND 6
%define SYMBOL_ASTERISK 7
%define SYMBOL_LEFT_PAREN 8
%define SYMBOL_RIGHT_PAREN 9
%define SYMBOL_DASH 10
%define SYMBOL_EQUALS 11
%define SYMBOL_PLUS 12
%define SYMBOL_LEFT_BRACKET 13
%define SYMBOL_LEFT_BRACE 14
%define SYMBOL_RIGHT_BRACKET 15
%define SYMBOL_RIGHT_BRACE 16
%define SYMBOL_BACKSLASH 17
%define SYMBOL_VERT_SLASH 18
%define SYMBOL_SEMICOLON 19
%define SYMBOL_COLON 20
%define SYMBOL_APOSTROPHE 21
%define SYMBOL_QUOTES 22
%define SYMBOL_COMMA 23
%define SYMBOL_LEFT_ANGLE 24
%define SYMBOL_PERIOD 25
%define SYMBOL_RIGHT_ANGLE 26
%define SYMBOL_SLASH 27
%define SYMBOL_QUESTION 28
%define SYMBOL_BACKTICK 39
%define SYMBOL_TILDE 30
%define SYMBOL_NEWLINE 31



section .bss
	tokens resb 8192
	strings resb 8192
	nodes resb 8192



section .data
	stringsPos dq strings



section .text



input:
db "add rax, rcx"
dq 0



main:
	sub rsp, 40
	call lex_init
	call lex_input
	call print_tokens
	call parse_init
	call parse
	call print_nodes
	call ExitProcess



lex_init:
	lea rsi, [input]
	lea r12, [tokens]
	xor r13, r13
	ret



parse_init:
	lea rsi, [tokens]
	lea r12, [nodes]
	ret


; rcx: u64 length
; rax: char* string
alloc_string:
	mov rax, [stringsPos]
	mov rdx, rax
	add rdx, rcx
	add rdx, 7
	and rdx, -8
	mov [stringsPos], rdx
	ret



; rcx: pToken
print_token:
	push rdi
	sub rsp, 32

	mov rdi, rcx  ; rdi = pToken
	movzx rdx, byte [rdi] ; rdx = type
	movzx rcx, byte [.jump_table + rdx]
	add rcx, .jump_start
	jmp rcx

.jump_table:
	db .none - .jump_start
	db .identifier - .jump_start
	db .symbol - .jump_start
	db .int - .jump_start
	db .char - .jump_start
	db .string - .jump_start

.jump_start:
.none:
	lea rcx, [formatTokenNone]
	call printf
	jmp .end
.identifier:
	cmp byte [rdi + 1], 8
	ja .identifier_long
.identifier_short:
	lea rcx, [formatTokenIdShort]
	lea rdx, [rdi + 8]
	jmp .identifier_end
.identifier_long:
	lea rcx, [formatTokenIdLong]
	mov rdx, qword [rdi + 8]
.identifier_end:
	call printf
	jmp .end
.symbol:
	lea rcx, [formatTokenSymbol]
	movzx rdx, byte [rdi + 1]
	lea rdx, [symbolStrings + rdx * 8]
	call printf
	jmp .end
.int:
	lea rcx, [formatTokenInt]
	mov rdx, qword [rdi + 8]
	call printf
	jmp .end
.char:
	lea rcx, [formatTokenChar]
	mov edx, dword [rdi + 8]
	call printf
	jmp .end
.string:
	lea rcx, [formatTokenString]
	mov rdx, qword [rdi + 8]
	call printf
	jmp .end
.end:
	add rsp, 32
	pop rdi
	ret



print_tokens:
	push rdi
	sub rsp, 32
	lea rsi, [tokens]
.loop:
	cmp byte [rsi], TOKEN_NONE
	je .end
	mov rcx, rsi
	call print_token
	add rsi, 16
	jmp .loop
.end:
	add rsp, 32
	pop rdi
	ret



print_nodes:
	sub rsp, 40
	add rsp, 40
	ret



; LEX procedure
; rsi: source position
; rdi: current char
; r12: token stream position
; r13: line index



lex_error:
	sub rsp, 40
	lea rcx, [errorLex]
	mov rdx, r13
	call printf
	add rsp, 40
	ret



lex_error_invalid_char:
	sub rsp, 40
	call lex_error
	lea rcx, [errorInvalidChar]
	mov rdx, rdi
	call printf
	call ExitProcess



read_number:
	sub rsp, 40
	cmp dil, '0'
	jne .decimal
	mov dil, [rsi]
	cmp dil, 'x'
	je .hex
	cmp dil, 'b'
	je .binary
	jmp .decimal
	.hex:
		add rsi, 1
		xor eax, eax
		.hex_loop:
			mov cl, [rsi]
			add rsi, 1
			sub cl, '0'
			cmp cl, 9
			jbe .l0
			sub cl, 'A' - '0' - 10
			cmp cl, 15
			jbe .l0
			sub cl, 'a' - 'A'
			cmp cl, 15
			ja .end
		.l0:
			shl rax, 4
			or al, cl
			jmp .hex_loop
	.binary:
		add rsi, 1
		xor eax, eax
		.binary_loop:
			mov cl, [rsi]
			add rsi, 1
			cmp cl, '_'
			je .binary_loop
			sub cl, '0'
			cmp cl, 1
			ja .end
			shl rax, 1
			or al, cl
			jmp .binary_loop
	.decimal:
		sub rsi, 1
		xor eax, eax
		mov ecx, 10
		.decimal_loop:
			movzx rdi, byte [rsi]
			add rsi, 1
			cmp rdi, '_'
			je .decimal_loop
			sub rdi, '0'
			cmp rdi, 9
			ja .end
			mul rcx
			add rax, rdi
			jmp .decimal_loop
	.end:
		mov qword [r12], TOKEN_INT
		mov qword [r12 + 8], rax
		add r12, 16
		sub rsi, 1
		add rsp, 40
		ret
.read_number_end:



read_identifier:
	sub rsp, 40
	sub rsi, 1
	xor ecx, ecx
	lea rdx, [idCharMap]

	.length_loop:
		movzx edi, byte [rsi + rcx]
		mov edx, edi
		shr edx, 4
		mov dx, [idCharMap + rdx * 2]
		bt dx, di
		jnc .length_end
		add ecx, 1
		jmp .length_loop
	.length_end:
		mov eax, ecx
		shl eax, 8
		or eax, TOKEN_IDENTIFIER
		mov qword [r12], rax
		add r12, 8
		cmp ecx, 8
		ja .string_copy_indirect
	.string_copy_direct:
		mov rax, [rsi]
		mov edx, ecx
		neg cl
		add cl, 8
		shl cl, 3
		shl rax, cl
		shr rax, cl
		mov ecx, edx
		mov qword [r12], rax
		add r12, 8
		jmp .end
	.string_copy_indirect:
		mov edi, ecx ; save rcx
		add ecx, 1 ; null-terminator
		call alloc_string ; rax = char* string
		mov ecx, edi
		xor edx, edx
	.string_copy_loop:
		mov dil, [rsi + rdx]
		mov [rax + rdx], dil
		add edx, 1
		cmp edx, ecx
		jb .string_copy_loop
	.string_copy_end:
		mov byte [rax + rcx], 0 ; null-terminator
		mov qword [r12], rax
		add r12, 8
	.end:
		add rsi, rcx ; advance by length of string
		add rsp, 40
		ret
.read_identifier_end:



lex_input:
	sub rsp, 40

	lea rsi, [input]
	lea r12, [tokens]
	mov r13, 0

.loop:
	movzx rdi, byte [rsi]
	add rsi, 1
	movzx rax, byte [.jump_table + rdi]
	add rax, .eos
	jmp rax

	.eos:
		add rsp, 40
		ret
	.symbol:
		mov byte [r12], TOKEN_SYMBOL
		mov byte [r12 + 1], cl
		add r12, 16
		jmp .loop
	.letter:
		call read_identifier
		jmp .loop
	.digit:
		call read_number
		jmp .loop
	.invalid:
		jmp lex_error_invalid_char
	.whitespace:
		jmp .loop
	.newline:
		add r13, 1
		jmp .loop
	.exclamation:
		mov cl, SYMBOL_EXCLAMATION
		jmp .symbol
	.slash:
		mov cl, SYMBOL_SLASH
		jmp .symbol
	.quotes:
		mov cl, SYMBOL_QUOTES
		jmp .symbol
	.hash:
		mov cl, SYMBOL_HASH
		jmp .symbol
	.dollar:
		mov cl, SYMBOL_DOLLAR
		jmp .symbol
	.percent:
		mov cl, SYMBOL_PERCENT
		jmp .symbol
	.ampersand:
		mov cl, SYMBOL_AMPERSAND
		jmp .symbol
	.apostrophe:
		mov cl, SYMBOL_APOSTROPHE
		jmp .symbol
	.left_paren:
		mov cl, SYMBOL_LEFT_PAREN
		jmp .symbol
	.right_paren:
		mov cl, SYMBOL_RIGHT_PAREN
		jmp .symbol
	.asterisk:
		mov cl, SYMBOL_ASTERISK
		jmp .symbol
	.plus:
		mov cl, SYMBOL_PLUS
		jmp .symbol
	.comma:
		mov cl, SYMBOL_COMMA
		jmp .symbol
	.dash:
		mov cl, SYMBOL_DASH
		jmp .symbol
	.period:
		mov cl, SYMBOL_PERIOD
		jmp .symbol
	.colon:
		mov cl, SYMBOL_COLON
		jmp .symbol
	.semicolon:
		mov cl, SYMBOL_SEMICOLON
		jmp .symbol
	.left_angle:
		mov cl, SYMBOL_LEFT_ANGLE
		jmp .symbol
	.equals:
		mov cl, SYMBOL_EQUALS
		jmp .symbol
	.right_angle:
		mov cl, SYMBOL_RIGHT_ANGLE
		jmp .symbol
	.question:
		mov cl, SYMBOL_QUESTION
		jmp .symbol
	.at:
		mov cl, SYMBOL_AT
		jmp .symbol
	.left_bracket:
		mov cl, SYMBOL_LEFT_BRACKET
		jmp .symbol
	.backslash:
		mov cl, SYMBOL_BACKSLASH
		jmp .symbol
	.right_bracket:
		mov cl, SYMBOL_RIGHT_BRACKET
		jmp .symbol
	.caret:
		mov cl, SYMBOL_CARET
		jmp .symbol
	.backtick:
		mov cl, SYMBOL_BACKTICK
		jmp .symbol
	.left_brace:
		mov cl, SYMBOL_LEFT_BRACE
		jmp .symbol
	.vert_slash:
		mov cl, SYMBOL_VERT_SLASH
		jmp .symbol
	.right_brace:
		mov cl, SYMBOL_RIGHT_BRACE
		jmp .symbol
	.tilde:
		mov cl, SYMBOL_TILDE
		jmp .symbol

	.jump_table:
		db .eos - .eos ; 00
		db .invalid - .eos ; 01
		db .invalid - .eos ; 02
		db .invalid - .eos ; 03
		db .invalid - .eos ; 04
		db .invalid - .eos ; 05
		db .invalid - .eos ; 06
		db .invalid - .eos ; 07
		db .invalid - .eos ; 08
		db .whitespace - .eos ; 09
		db .newline - .eos    ; 0A
		db .invalid - .eos    ; 0B
		db .invalid - .eos    ; 0C
		db .whitespace - .eos ; 0D
		db .invalid - .eos ; 0E
		db .invalid - .eos ; 0F
		db .invalid - .eos ; 10
		db .invalid - .eos ; 11
		db .invalid - .eos ; 12
		db .invalid - .eos ; 13
		db .invalid - .eos ; 14
		db .invalid - .eos ; 15
		db .invalid - .eos ; 16
		db .invalid - .eos ; 17
		db .invalid - .eos ; 18
		db .invalid - .eos ; 19
		db .invalid - .eos ; 1A
		db .invalid - .eos ; 1B
		db .invalid - .eos ; 1C
		db .invalid - .eos ; 1D
		db .invalid - .eos ; 1E
		db .invalid - .eos ; 1F
		db .whitespace - .eos  ; 20
		db .exclamation - .eos ; 21
		db .quotes - .eos      ; 22
		db .hash - .eos        ; 23
		db .dollar - .eos      ; 24
		db .percent - .eos     ; 25
		db .ampersand - .eos   ; 26
		db .apostrophe - .eos  ; 27
		db .left_paren - .eos  ; 28
		db .right_paren - .eos ; 29
		db .asterisk - .eos    ; 2A
		db .plus - .eos        ; 2B
		db .comma - .eos       ; 2C
		db .dash - .eos        ; 2D
		db .period - .eos      ; 2E
		db .slash - .eos ; 2F
		db .digit - .eos ; 30
		db .digit - .eos ; 31
		db .digit - .eos ; 32
		db .digit - .eos ; 33
		db .digit - .eos ; 34
		db .digit - .eos ; 35
		db .digit - .eos ; 36
		db .digit - .eos ; 37
		db .digit - .eos ; 38
		db .digit - .eos ; 39
		db .colon - .eos ; 3A
		db .semicolon - .eos   ; 3B
		db .left_angle - .eos  ; 3C
		db .equals - .eos      ; 3D
		db .right_angle - .eos ; 3E
		db .question - .eos    ; 3F
		db .at - .eos          ; 40
		db .letter - .eos ; 41
		db .letter - .eos ; 42
		db .letter - .eos ; 43
		db .letter - .eos ; 44
		db .letter - .eos ; 45
		db .letter - .eos ; 46
		db .letter - .eos ; 47
		db .letter - .eos ; 48
		db .letter - .eos ; 49
		db .letter - .eos ; 4A
		db .letter - .eos ; 4B
		db .letter - .eos ; 4C
		db .letter - .eos ; 4D
		db .letter - .eos ; 4E
		db .letter - .eos ; 4F
		db .letter - .eos ; 50
		db .letter - .eos ; 51
		db .letter - .eos ; 52
		db .letter - .eos ; 53
		db .letter - .eos ; 54
		db .letter - .eos ; 55
		db .letter - .eos ; 56
		db .letter - .eos ; 57
		db .letter - .eos ; 58
		db .letter - .eos ; 59
		db .letter - .eos ; 5A
		db .left_bracket - .eos  ; 5B
		db .backslash - .eos     ; 5C
		db .right_bracket - .eos ; 4D
		db .caret - .eos         ; 5E
		db .letter - .eos        ; 5F
		db .backtick - .eos      ; 60
		db .letter - .eos ; 61
		db .letter - .eos ; 62
		db .letter - .eos ; 63
		db .letter - .eos ; 64
		db .letter - .eos ; 65
		db .letter - .eos ; 66
		db .letter - .eos ; 67
		db .letter - .eos ; 68
		db .letter - .eos ; 69
		db .letter - .eos ; 6A
		db .letter - .eos ; 6B
		db .letter - .eos ; 6C
		db .letter - .eos ; 6D
		db .letter - .eos ; 6E
		db .letter - .eos ; 6F
		db .letter - .eos ; 70
		db .letter - .eos ; 71
		db .letter - .eos ; 72
		db .letter - .eos ; 73
		db .letter - .eos ; 74
		db .letter - .eos ; 75
		db .letter - .eos ; 76
		db .letter - .eos ; 77
		db .letter - .eos ; 78
		db .letter - .eos ; 79
		db .letter - .eos ; 7A
		db .left_brace - .eos  ; 7B
		db .vert_slash - .eos  ; 7C
		db .right_brace - .eos ; 7D
		db .tilde - .eos       ; 7E
		db .invalid - .eos     ; 7F
lex_input_end:



;;;;;;;;;;;;;;;;;;;
; PARSE PROCEDURE ;
;;;;;;;;;;;;;;;;;;;
; rsi: pTokens    ;
; r12: pNodes     ;
;;;;;;;;;;;;;;;;;;;



parse:
	sub rsp, 40

	cmp byte [rsi], TOKEN_IDENTIFIER
	jne .error
	cmp byte [rsi + 1], 8
	ja .error

	mov rcx, [rsi + 8]
	cmp rcx, "add"
	jne .error

	add rsp, 40
	ret
.error:
	lea rcx, [stringFatalError]
	call printf
	call ExitProcess