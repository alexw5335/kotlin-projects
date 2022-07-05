

section .bss
	tokens resb 8192
	strings resb 8192
	nodes resb 8192



section .data
	stringsPos dq strings
	tokenCount dq 0
	nodeCount dq 0



section .text



input:
	db "add rax, rcx"
	dq 0



main:
	sub rsp, 40
	call lex_init
	call lex
	call print_tokens
	call parse_init
	call parse
	;call print_nodes
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
		db .register - .jump_start
		db .mnemonic - .jump_start

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
		mov edx, dword [rdi + 4]
		call printf
		jmp .end

	.string:
		cmp word [rdi + 2], 0
		ja .string_long
	.string_short:
		lea rcx, [formatTokenStringShort]
		lea rdx, [rdi + 8]
		jmp .string_end
	.string_long:
		lea rcx, [formatTokenStringLong]
		mov rdx, qword [rdi + 8]
	.string_end:
		call printf
		jmp .end

	.register:
		movzx ecx, word [rdi + 2]
		call get_register_name
		mov rdx, rax
		lea rcx, [formatTokenRegister]
		call printf
		jmp .end

	.mnemonic:
		movzx ecx, word [rdi + 2]
		call get_mnemonic_name
		mov rdx, rax
		lea rcx, [formatTokenMnemonic]
		call printf
		jmp .end

	.end:
		add rsp, 32
		pop rdi
		ret
.print_token_end:



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






;;;;;;;;;;;;;;;;;
;      LEX      ;
;;;;;;;;;;;;;;;;;
; rsi: pSrc
; rdi: current char
; r12: pTokens
; r13: line number
;;;;;;;;;;;;;;;;;





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
	push rbx    ; char[8] string (if length <= 8)
	sub rsp, 32
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
		cmp ecx, 8
		ja .string_copy_indirect

		mov rbx, [rsi] ; store entire string in rbx
		mov edx, ecx   ; save ecx
		neg cl
		add cl, 8
		shl cl, 3
		shl rbx, cl
		shr rbx, cl
		mov ecx, edx   ; restore ecx

		mov edi, ecx ; save ecx
		mov rcx, rbx
		call resolve_mnemonic
		mov ecx, edi ; restore ecx
		cmp eax, -1
		jne .mnemonic

		mov edi, ecx ; save ecx
		mov rcx, rbx
		call resolve_register
		mov ecx, edi ; restore ecx
		cmp eax, -1
		jne .register

		jmp .string_copy_direct

		; need to resolve registers as well
	.mnemonic:
		mov byte [r12], TOKEN_MNEMONIC
		add r12, 2
		mov word [r12], ax
		add r12, 14
		jmp .end

	.register:
		mov byte [r12], TOKEN_REGISTER
		add r12, 2
		mov word [r12], ax
		add r12, 14
		jmp .end

	.string_copy_direct:
		mov byte [r12], TOKEN_IDENTIFIER
		add r12, 1
		mov byte [r12], cl
		add r12, 7
		mov qword [r12], rbx
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
		mov byte [r12], TOKEN_IDENTIFIER
		add r12, 1
		mov byte [r12], cl
		add r12, 7
		mov qword [r12], rax
		add r12, 8
	.end:
		add rsi, rcx ; advance by length of string
		add rsp, 32
		pop rbx
		ret
.read_identifier_end:



read_string_literal:
	sub rsp, 40
	xor ecx, ecx

	.length_loop:
		mov al, [rsi + rcx]
		add ecx, 1
		cmp al, '"'
		jne .length_loop
	.length_loop_end:
		sub ecx, 1
		mov byte [r12], TOKEN_STRING
		add r12, 2
		mov word [r12], cx
		add r12, 6
		cmp ecx, 8
		ja .copy_indirect
	.copy_direct:
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
	.copy_indirect:
		mov edi, ecx ; save ecx
		add ecx, 1 ; null-terminator
		call alloc_string ; rax = char* string
		mov ecx, edi
		xor edx, edx
	.copy_indirect_loop:
		mov dil, [rsi + rdx]
		mov [rax + rdx], dil
		add edx, 1
		cmp edx, ecx
		jb .copy_indirect_loop
	.copy_indirect_loop_end:
		mov byte [rax + rcx], 0 ; null-terminator
		mov qword [r12], rax
		add r12, 8
	.end:
		add rsi, rcx
		add rsi, 1
		add rsp, 40
		ret
read_string_literal_end:



read_char_literal:
	xor eax, eax
	mov al, [rsi]
	add rsi, 2
	mov byte [r12], TOKEN_CHAR
	add r12, 4
	mov dword [r12], eax
	add r12, 12
	ret



lex:
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
		call read_string_literal
		jmp .loop
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
		call read_char_literal
		jmp .loop
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
lex_end:



; rcx: char[8] string
resolve_mnemonic:
	sub rsp, 40
	mov r8, rcx
	lea rcx, [mnemonicSearchTable]
	mov rdx, NUM_MNEMONICS
	call binary_search_u64
	cmp rax, -1
	je .end
	mov ax, [mnemonicIndexTable + rax * 2]
.end:
	add rsp, 40
	ret



; rcx: char[8] string
resolve_register:
	sub rsp, 40
	mov r8, rcx
	lea rcx, [registerSearchTable]
	mov rdx, NUM_REGISTERS
	call binary_search_u64
	cmp rax, -1
	je .end
	mov ax, [registerValueTable + rax * 2]
.end:
	add rsp, 40
	ret



; rcx: u16 register
get_register_name:
	mov eax, ecx
	shr eax, 8
	shl eax, 5    ; eax = register.type * 32
	and ecx, 0xFF ; ecx = register.value
	movzx eax, byte [registerReverseIndexTable + rax + rcx]
	lea rax, [registerSearchTable + rax * 8]
	ret



; rcx: u8 mnemonic
get_mnemonic_name:
	movzx eax, byte [mnemonicReverseIndexTable + rcx * 2]
	lea rax, [mnemonicSearchTable + rax * 8]
	ret






;;;;;;;;;;;;;;;;;;;
;      PARSE      ;
;;;;;;;;;;;;;;;;;;;
; rsi: pTokens    ;
; r12: pNodes     ;
;;;;;;;;;;;;;;;;;;;






parse:
	sub rsp, 40

	.loop:
		movzx edi, byte [rsi]
		add rsi, 16
		movzx eax, byte [.jump_table + rdi]
		add rax, .eos
		jmp rax
	.eos:
		add rsp, 40
		ret
	.identifier:
		jmp .loop
	.mnemonic:
		jmp .loop
	.error:
		lea rcx, [stringFatalError]
		call printf
		call ExitProcess

	.jump_table:
		db .eos - .eos         ; NONE
		db .error - .eos       ; IDENTIFIER
		db .error - .eos       ; SYMBOL
		db .error - .eos       ; INT
		db .error - .eos       ; CHAR
		db .error - .eos       ; STRING
		db .error - .eos       ; REGISTER
		db .mnemonic - .eos    ; MNEMONIC
.parse_end: