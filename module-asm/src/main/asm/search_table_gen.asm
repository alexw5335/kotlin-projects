default rel

%include "stdio.asm"
%include "data.asm"
%include "macros.asm"

global main
extern printf
extern ExitProcess



%define CONVERT_TO_LOWERCASE



section .bss
	fileHandle resb 8
	fileSize resb 8
	fileData resb 1 << 16
	strings resb 1 << 16
	stringCount resb 8
	output resb 1 << 16
	outputLength resb 8



section .data
	fileName db "module-asm/src/main/resources/mnemonics.txt", 0
	outputFileName db "module-asm/src/main/resources/output.txt", 0



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



make_lowercase:
	cmp cl, 'A'
	jb .end
	cmp cl, 'Z'
	ja .end
	add cl, 'a' - 'A'
.end:
	ret



read_strings:
	push rdi ; u64   string
	push rsi ; void* fileData
	push rbp ; u64*  buffer
	sub rsp, 40

	mov qword [stringCount], 0
	lea rbp, [strings]
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

%ifdef CONVERT_TO_LOWERCASE
	cmp al, 'A'
    jb .case_convert_end
    cmp al, 'Z'
    ja .case_convert_end
    add al, 'a' - 'A'
.case_convert_end:
%endif

	shl ecx, 3
	shl rax, cl
	or rdi, rax
	shr ecx, 3
	add ecx, 1

	jmp .loop
.carriage:
	add rsi, 1
.newline:
	cmp ecx, 8
	ja .continue
	cmp ecx, 0
	je .continue
	add dword [stringCount], 1
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



sort_strings:
	sub rsp, 40
	lea rcx, [strings]
	mov edx, [stringCount]
	call bubble_sort_u64
	add rsp, 40
	ret



gen_output:
	push rbx    ; loop index
	push rdi    ; void* buffer
	sub rsp, 40
	mov rbx, 0
	lea rdi, [output]

.loop:
	mov dword [rdi], "    "
	add rdi, 4
	mov dword [rdi], "dq "
	add rdi, 3
	mov byte [rdi], 34
	add rdi, 1
	mov rdx, [strings + rbx * 8]
	mov rcx, rdx
	call nt_length8
	mov qword [rdi], rdx
	add rdi, rax
	mov byte [rdi], 34
	add rdi, 1
	mov byte [rdi], 10
	add rdi, 1
	add rbx, 1
	cmp rbx, [stringCount]
	jb .loop

	lea rcx, [output]
	sub rdi, rcx
	mov qword [outputLength], rdi

	add rsp, 40
	pop rdi
	pop rbx
	ret



print_output:
	push rbx
	sub rsp, 32

	lea rcx, [outputFileName]
	call create_file_for_writing
	mov rbx, rax

	mov rcx, rbx
	lea rdx, [output]
	mov r8, [outputLength]
	call write_file

	mov rcx, rbx
	call close_file

	add rsp, 32
	pop rbx
	ret



main:
	sub rsp, 56

	call init
	call read_strings
	call sort_strings
	call gen_output
	call print_output

	printFinished
	call ExitProcess