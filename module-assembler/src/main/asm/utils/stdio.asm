%ifndef STDIO_INCLUDE
%define STDIO_INCLUDE

extern WriteFile


section .bss
	tempLong resb 8



section .text


; rcx: void* buffer
; rdx: u64 length
print:
	sub rsp, 56
	mov r8, rdx
	mov rdx, rcx
	mov rcx, -11 ; stdout
	lea r9, [rsp + 40]
	mov qword [rsp + 32], 0
	call WriteFile
	cmp rax, 0
	je .error
	add rsp, 56
	ret
.error:



print_newline:
	sub rsp, 56
	lea rdx, [rsp + 40]
	mov qword [rdx], 0xa
	mov rcx, -11
	mov r8, 1
	lea r9, [rsp + 48]
	mov qword [rsp + 32], 0
	call WriteFile
	add rsp, 56
	ret



; rcx: u64 string
print_ascii8:
	sub rsp, 56

	lea rdx, [rsp + 40]
	mov [rdx], rcx
	xor r8, r8
.loop:
	cmp cl, 0
	je .end
	add r8, 1
	shr rcx, 8
	cmp r8, 8
	jne .loop
.end:
	mov rcx, -11
	lea r9, [tempLong]
	mov qword [rsp + 32], 0
	call WriteFile

	add rsp, 56
	ret



; rcx: u64 string
println_ascii8:
	sub rsp, 40
	call print_ascii8
	call print_newline
	add rsp, 40


%endif