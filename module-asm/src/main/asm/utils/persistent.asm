%ifndef PERSISTENT_INCLUDE
%define PERSISTENT_INCLUDE



extern memset
extern printf
extern ExitProcess



%ifndef PERSISTENT_SIZE
%define PERSISTENT_SIZE 1 << 20 ; 1 MB
%endif



segment .data
	persistentOverflowMsg db "ERROR: Persistent memory overflow.", 10, 0



segment .bss
	persistentData resb PERSISTENT_SIZE
	persistentPos resb 8



segment .text



; rcx: u64 size
; rax: void* pAllocation (NULL if out of memory)
palloc:
	push rbp
	mov rbp, rsp
	sub rsp, 32

	lea rax, [persistentData]
	add rax, [persistentPos]
	add rcx, 7
	and rcx, -8 ; align up to nearest multiple of 8
	add [persistentPos], rcx
	cmp qword [persistentPos], PERSISTENT_SIZE
	jle .continue

	lea rcx, [persistentOverflowMsg]
	call printf
	mov rax, 0
.continue:
	leave
	ret



; rcx: u64 size
; rax: void* pAllocation (NULL if out of memory)
pcalloc:
	push rsi ; pAllocation
	push rdi ; allocationSize
	push rbp
	mov rbp, rsp
	sub rsp, 32

	mov rdi, rcx

	call palloc
	mov rsi, rax

	mov rcx, rsi
	mov rdx, 0
	mov r8, rdi
	call memset

	mov rax, rsi

	leave
	pop rsi
	pop rdi
	ret



%endif