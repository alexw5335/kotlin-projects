%ifndef PERSISTENT_INCLUDE
%define PERSISTENT_INCLUDE



extern memset
extern printf
extern ExitProcess



%ifndef PERSISTENT_SIZE
%define PERSISTENT_SIZE 1 << 20 ; 1 MB
%endif



segment .data
	errorPersistentOverflow db "ERROR: Persistent memory overflow.", 10, 0



segment .bss
	persistentData resb PERSISTENT_SIZE
	persistentPos resb 8



segment .text



; rcx: u64 size
; rax: void* pAllocation (NULL if out of memory)
palloc:
	sub rsp, 40

	lea rax, [persistentData]
	add rax, [persistentPos]
	add rcx, 7
	and rcx, -8 ; align up to nearest multiple of 8
	add [persistentPos], rcx
	cmp qword [persistentPos], PERSISTENT_SIZE
	jle .continue

	lea rcx, [errorPersistentOverflow]
	call printf
	mov rax, 0
.continue:
	add rsp, 40
	ret



; rcx: u64 size
; rax: void* pAllocation (NULL if out of memory)
pcalloc:
	push rsi ; pAllocation
	push rdi ; allocationSize
	sub rsp, 40

	mov rdi, rcx

	call palloc
	mov rsi, rax

	mov rcx, rsi
	mov rdx, 0
	mov r8, rdi
	call memset

	mov rax, rsi

	add rsp, 40
	pop rsi
	pop rdi
	ret



%endif