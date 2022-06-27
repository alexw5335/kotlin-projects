%ifndef ALLOCATION_INCLUDE
%define ALLOCATION_INCLUDE



extern memset
extern printf
extern ExitProcess



struc LinearAllocator
	.data resb 8
	.capacity resb 8
	.pos resb 8
	.parent resb 8 ; if null, then this allocator cannot be expanded.
endstruc



segment .data
	errorLinearAllocatorOverflow db "ERROR: Linear allocator overflow.", 10, 0



segment .text



; rcx: Allocator* allocator
; rcx: Allocator* parent
; rdx: u64 initialCapacity
; r8:  boolean dynamic
create_linear_allocator:
	sub rsp, 40

	add rsp, 40
	ret



; rcx: LinearAllocator* allocator
; rdx: u64 allocationSize
; rax: void* pAllocation (NULL if out of memory)
linear_alloc:
	sub rsp, 40

	lea rax, [rcx]
	add rax, [rcx + 12] ; rax = allocator.data + allocator.pos

	add rdx, 7
	and rdx, -8 ; align allocationSize up to nearest multiple of 8

	mov r8, qword [rcx + 12]
	add r8, rdx ; r8 = allocator.pos + allocationSize
	mov qword [rcx + 12], r8 ; allocator.pos += allocationSize

	cmp r8, qword [rcx + 8]
	ja .overflow ; if(allocator.pos > allocator.capacity)

	add rsp, 40
	ret
.overflow:
	lea rcx, [errorLinearAllocatorOverflow]
	call printf
	call ExitProcess



; rcx: LinearAllocator* allocator
; rdx: u64 allocationSize
; rax: void* pAllocation (NULL if out of memory)
linear_calloc:
	sub rsp, 40

	mov [rsp], rdx ; [rsp] = allocationSize

	call linear_alloc
	mov [rsp + 8], rax ; [rsp + 8] = pAllocation

	mov rcx, [rsp + 8]
	mov rdx, 0
	mov r8, [rsp]
	call memset

	mov rax, [rsp + 8]

	add rsp, 40
	ret



%endif