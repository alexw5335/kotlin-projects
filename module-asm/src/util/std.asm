%ifndef STD_INCLUDE
%define STD_INCLUDE

extern printf
extern ExitProcess

%define TRUE  1
%define FALSE 0
%define NULL  0



section .data
	intPrintFormat db "%d", 0
	longPrintFormat db "%lld", 0
	charPrintFormat db "%c", 0
	stringPrintFormat db "%s", 0
	newlinePrintFormat db 10, 0
	stringFatalError db "A fatal error has occurred", 10, 0



section .text



%macro PUSH_VOLATILE 0
	push rax
	push rcx
	push rdx
	push r8
	push r9
	push r10
	push r11
%endmacro



%macro POP_VOLATILE 0
	pop r11
	pop r10
	pop r9
	pop r8
	pop rdx
	pop rcx
	pop rax
%endmacro



%macro PRINT_PRIMITIVE 1
	PUSH_VOLATILE
	sub rsp, 32
	mov rdx, rcx
	lea rcx, [%1]
	call printf
	lea rcx, [newlinePrintFormat]
	call printf
	add rsp, 32
	POP_VOLATILE
%endmacro



print_int:
	PRINT_PRIMITIVE intPrintFormat
	ret

print_long:
	PRINT_PRIMITIVE longPrintFormat
	ret

print_char:
	PRINT_PRIMITIVE charPrintFormat
	ret

print_string:
	PRINT_PRIMITIVE stringPrintFormat
	ret

%macro PRINT_INT 1
	mov rcx, %1
	call print_long
	call ExitProcess
%endmacro

%endif