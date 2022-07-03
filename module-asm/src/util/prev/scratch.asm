default rel

%include "macros.asm"

global main
extern printf
extern ExitProcess

%ifndef STACK_SIZE
%define STACK_SIZE 128
%endif

%macro SCRATCH_START 0
section .text
main:
	sub rsp, STACK_SIZE
	and rsp, -16
%endmacro

%macro SCRATCH_END 0
	printFinished
	call ExitProcess
%endmacro