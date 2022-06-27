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



; rcx: u64* array
; rdx: u64 arrayLength
; r8: u64 value
; uses: rax, rcx, rdx, r8, r9, r10
binary_search_u64:
	xor r9, r9 ; lower
	mov r10, rdx
	sub r10, 1 ; upper
.loop:
	cmp r9, r10
	jg .not_found
	mov rax, r9
	add rax, r10
	shr rax, 1
	cmp [rcx + rax * 8], r8
	jb .less
	ja .greater
	jmp .found
.less:
	mov r9, rax
	add r9, 1
	jmp .loop
.greater:
	mov r10, rax
	sub r10, 1
	jmp .loop
.not_found:
	mov rax, -1
.found:
	ret


%endif