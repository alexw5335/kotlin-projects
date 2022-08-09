default rel

extern ExitProcess
global main

section .text

main:
	sub rsp, 40
	call ExitProcess