default rel

%include "macros.asm"

global main
extern printf
extern ExitProcess
extern CreateFileA
extern ReadFile
extern GetLastError
extern CloseHandle
extern malloc





section .bss
	strings resb 8192



section .data
	stringsPos dq strings



section .text



; rcx: u64 length
; rax: char*
alloc_string:
	mov rax, [stringsPos]
	mov rdx, rax
	add rdx, rcx
	add rdx, 7
	and rdx, -8
	mov [stringsPos], rdx
	ret



main:
	sub rsp, 40
	call ExitProcess