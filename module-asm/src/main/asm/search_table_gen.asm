default rel

%include "stdio.asm"
%include "data.asm"
%include "macros.asm"

global main
extern printf
extern ExitProcess



section .bss
	fileHandle resb 8
	fileSize resb 8
	fileData resb 1 << 14
	mnemonics resb 1 << 14
	mnemonicCount resb 8



section .data
	fileName db "module-assembler/src/main/resources/mnemonics.txt", 0
	mnemonicFormat db "dq ", 0x22, "%.8s", 0x22, 0xa, 0



section .text



init:
	push rbx ; HANDLE* fileHandle
	sub rsp, 32

	lea rcx, [fileName]
	call create_file_for_reading
	mov [fileHandle], rax
	mov rbx, rax

	mov rcx, rbx
	lea rdx, [fileData]
	mov r8, 1 << 14
	lea r9, [fileSize]
	call read_file

	mov rcx, rbx
	call close_file

	add rsp, 32
	pop rbx
	ret



read_mnemonics:
	push rdi ; mnemonic string
	push rsi ; fileData
	push rbp ; mnemonics buffer
	sub rsp, 40

	mov qword [mnemonicCount], 0
	lea rbp, [mnemonics]
	lea rsi, [fileData]
	xor ecx, ecx
	xor edi, edi

.loop:
	movzx rax, byte [rsi]
	add rsi, 1
	cmp al, '#'
	je .end
	cmp al, 0xa
	je .newline
	cmp al, 0xd
	je .carriage

	shl ecx, 3
	shl rax, cl
	xor rdi, rax
	shr ecx, 3
	add ecx, 1

	jmp .loop
.carriage:
	add rsi, 1
.newline:
	cmp ecx, 8
	ja .continue
	add dword [mnemonicCount], 1
	mov [rbp], rdi
	add rbp, 8
.continue:
	xor ecx, ecx
	xor edi, edi
	jmp .loop
.end:
	add rsp, 40
	pop rbp
	pop rsi
	pop rdi
	ret



sort_mnemonics:
	sub rsp, 40
	lea rcx, [mnemonics]
	mov edx, [mnemonicCount]
	call bubble_sort_u64
	add rsp, 40
	ret



print_mnemonics:
	push rbx
	sub rsp, 32
	mov rbx, 0 ; print in sections of 200, too many calls to WriteFile causes freezes?
.loop:
	mov rcx, "    dq "
	call print_ascii8
	mov rcx, 34
	call print_ascii8
	mov rcx, [mnemonics + rbx * 8]
	call print_ascii8
	mov rcx, 34
	call println_ascii8
	add rbx, 1
	cmp rbx, 200
	jb .loop
	add rsp, 32
	pop rbx
	ret



main:
	sub rsp, 56

	call init
	call read_mnemonics
	call sort_mnemonics
	call print_mnemonics

	printFinished
	call ExitProcess