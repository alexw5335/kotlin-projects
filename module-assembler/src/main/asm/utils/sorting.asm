%ifndef SORTING_INCLUDE
%define SORTING_INCLUDE



section .text



; rcx: u64* array
; rdx: u64 arrayLength
bubble_sort_u64:
.outer_loop:
	xor al, al ; do-while condition
	mov r8, 1 ; inner loop index
.inner_loop:
	mov r9, r8
	sub r9, 1               ; r8 - 1
	mov r10, [rcx + r9 * 8] ; array[i - 1]
	mov r11, [rcx + r8 * 8] ; array[i]
	cmp r10, r11
	ja .swap
	add r8, 1
    cmp r8, rdx
    jl .inner_loop
    cmp al, 0
    jne .outer_loop
    ret
.swap:
	mov [rcx + r9 * 8], r11
	mov [rcx + r8 * 8], r10
	mov al, 1
	add r8, 1
    cmp r8, rdx
    jl .inner_loop
	cmp al, 0
	jne .outer_loop
	ret


%endif