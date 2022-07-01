%ifndef MACROS_INCLUDE
%define MACROS_INCLUDE



extern printf
extern ExitProcess
extern WriteFile



%define TRUE 1
%define FALSE 0
%define NULL 0



section .data
	newlinePrintFormat  db 10, 0
	stringFinished      db "Finished", 0
	string_Finished     db "Finished", 0
	stringHere          db "here", 0

	intPrintFormat1     db "%d", 10, 0
	longPrintFormat1    db "%lld", 10, 0
	stringPrintFormat1  db "%s", 10, 0
	charPrintFormat1    db "%c", 10, 0
	pointerPrintFormat1 db "%p", 10, 0
	uintPrintFormat1    db "%u", 10, 0

	intPrintFormat2     db "%s: %d", 10, 0
	longPrintFormat2    db "%s: %lld", 10, 0
	stringPrintFormat2  db "%s: %s", 10, 0
	charPrintfromat2    db "%s: %c", 10, 0
	pointerPrintFormat2 db "%s: %p", 10, 0
	uintPrintFormat2    db "%s: %u", 10, 0



; PRINT



%macro pushAll 0
	push rax
	push rcx
	push rdx
	push rbx
	push rbp
	push rsp
	push rdi
	push rsi
	push r8
	push r9
	push r10
	push r11
	push r12
	push r13
	push r14
	push r15
%endmacro



%macro popAll 0
	pop r15
	pop r14
	pop r13
	pop r12
	pop r11
	pop r10
	pop r9
	pop r8
	pop rsi
	pop rdi
	pop rsp
	pop rbp
	pop rbx
	pop rdx
	pop rcx
	pop rax
%endmacro



%macro print2 3
	pushAll

	mov r8, %3
	lea rcx, [%1]
	lea rdx, [%2]
	call printf

	popAll
%endmacro



%macro print1 2
	pushAll

	mov rdx, %2
	lea rcx, [%1]
	call printf

	popAll
%endmacro



%macro printInt 1
	print1 intPrintFormat1, %1
%endmacro

%macro printInt 2
	print2 intPrintFormat2, %1, %2
%endmacro



%macro printLong 1
	print1 longPrintFormat1, %1
%endmacro

%macro printLong 2
	print2 longPrintFormat2, %1, %2
%endmacro



%macro printChar 1
	print1 charPrintFormat1, %1
%endmacro

%macro printChar 2
	print2 charPrintFormat2, %1, %2
%endmacro



%macro printString 1
	print1 stringPrintFormat1, %1
%endmacro

%macro printString 2
	print2 stringPrintFormat2, %1, %2
%endmacro



%macro printPointer 1
	print1 pointerPrintFormat1, %1
%endmacro

%macro printPointer 2
	print2 pointerPrintFormat2, %1, %2
%endmacro



%macro printUInt 1
	print1 uintPrintFormat1, %1
%endmacro

%macro printUInt 2
	print2 uintPrintFormat2, %1, %2
%endmacro



%macro println 0
	print1 newlinePrintFormat
%endmacro



%macro printFinished 0
	printString stringFinished
%endmacro



%macro printHere 0
	lea rcx, [stringHere]
	call printf
%endmacro



; STACK



%macro stack_enter 0
	push rbp
	mov rbp, rsp
	sub rsp, 32
%endmacro

%macro stack_enter 1
	push rbp
	mov rbp, rsp
	sub rsp, 32 + ((%1 + 7) & -8)
%endmacro

%macro stack_exit 0
	mov rsp, rbp
	pop rbp
%endmacro

%macro stack_ret 0
	mov rsp, rbp
    pop rbp
	ret
%endmacro

%define stack_align16 and rsp, -16

%macro push_non_volatile 0
	push rsi
	push rdi
	push rbx
	push r12
	push r13
	push r14
	push r15
%endmacro

%macro pop_non_volatile 0
	pop r15
	pop r14
	pop r13
	pop r12
	pop rbx
	pop rdi
	pop rsi
%endmacro



; ALIGNMENT



%define align8(a) ((a + 7) & -8)

%define align16(a) ((a + 15) & -16)

%define align32(a) ((a + 31) & -32)

%macro dba 2+
	align %1
	db %2
%endmacro

%macro db8 1+
	align 8
	db %1
%endmacro

%macro db16 1+
	align 16
	db %1
%endmacro



; ERRORS



%macro exit_fatal 1
	printString %1
	call ExitProcess
%endmacro



%endif