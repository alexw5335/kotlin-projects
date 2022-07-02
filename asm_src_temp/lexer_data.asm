section .data


errorLex db "Lexer error on line %d: ", 0
errorInvalidChar db "Invalid char: %d", 10, 0



idCharMap:
	db 0b00000000 ; 00
	db 0b00000000 ; 08
	db 0b00000000 ; 10
	db 0b00000000 ; 18

	db 0b00000000 ; 20
	db 0b00000000 ; 28
	db 0b11111111 ; 30
	db 0b00000011 ; 38

	db 0b11111110 ; 40
	db 0b11111111 ; 48
	db 0b11111111 ; 50
	db 0b00000111 ; 58

	db 0b11111110 ; 60
	db 0b11111111 ; 68
	db 0b11111111 ; 70
	db 0b00000111 ; 78



symbolStrings:
	dq "!"
	dq "@"
	dq "#"
	dq "$"
	dq "%"
    dq "^"
    dq "&"
    dq "*"
    dq "("
    dq ")"
    dq "-"
    dq "="
    dq "+"
    dq "["
    dq "{"
    dq "]"
    dq "}"
    dq "\\"
    dq "|"
    dq ";"
    dq ":"
    dq "'"
    dq 34
    dq ","
    dq "<"
    dq "."
    dq ">"
    dq "/"
    dq "?"
    dq "`"
    dq "~"
    dq ""
    dq "=="
    dq "!="
    dq "&&"
    dq "||"
    dq "<<"
    dq ">>"
    dq "+="
	dq "-="
	dq "/="
	dq "*="
	dq "++"
	dq "--"
	dq "==="
	dq "!=="



tokenFormats:
	formatTokenNone       db "Invalid token", 10, 0
	formatTokenIdShort    db "Identifier:  %.8s", 10, 0
	formatTokenIdLong     db "Identifier:  %s", 10, 0
	formatTokenSymbol     db "Symbol:      %s", 10, 0
	formatTokenInt        db "Literal:     %llu", 10, 0
	formatTokenChar       db "Literal:     '%c'", 10, 0
	formatTokenString     db "Literal:     ", 0x22, "%s", 0x22, 10, 0



;;;;;;;;;;;;;
; REGISTERS ;
;;;;;;;;;;;;;



;enum RegType (1 byte)
%define REGTYPE_GP8 0
%define REGTYPE_GP16 1
%define REGTYPE_GP32 2
%define REGTYPE_GP64 3



;enum Register (2 bytes)
%define REGISTER_RAX 768
%define REGISTER_RCX 769
%define REGISTER_RDX 770
%define REGISTER_RBX 771
%define REGISTER_RSP 772
%define REGISTER_RBP 773
%define REGISTER_RSI 774
%define REGISTER_RDI 775
%define REGISTER_R8 776
%define REGISTER_R9 777
%define REGISTER_R10 778
%define REGISTER_R11 779
%define REGISTER_R12 780
%define REGISTER_R13 781
%define REGISTER_R14 782
%define REGISTER_R15 783
%define REGISTER_EAX 512
%define REGISTER_ECX 513
%define REGISTER_EDX 514
%define REGISTER_EBX 515
%define REGISTER_ESP 516
%define REGISTER_EBP 517
%define REGISTER_ESI 518
%define REGISTER_EDI 519
%define REGISTER_R8D 520
%define REGISTER_R9D 521
%define REGISTER_R10D 522
%define REGISTER_R11D 523
%define REGISTER_R12D 524
%define REGISTER_R13D 525
%define REGISTER_R14D 526
%define REGISTER_R15D 527
%define REGISTER_AX 256
%define REGISTER_CX 257
%define REGISTER_DX 258
%define REGISTER_BX 259
%define REGISTER_SP 260
%define REGISTER_BP 261
%define REGISTER_SI 262
%define REGISTER_DI 263
%define REGISTER_R8W 264
%define REGISTER_R9W 265
%define REGISTER_R10W 266
%define REGISTER_R11W 267
%define REGISTER_R12W 268
%define REGISTER_R13W 269
%define REGISTER_R14W 270
%define REGISTER_R15W 271
%define REGISTER_AL 0
%define REGISTER_CL 1
%define REGISTER_DL 2
%define REGISTER_BL 3
%define REGISTER_AH 4
%define REGISTER_CH 5
%define REGISTER_DH 6
%define REGISTER_BH 7
%define REGISTER_R8B 8
%define REGISTER_R9B 9
%define REGISTER_R10B 10
%define REGISTER_R11B 11
%define REGISTER_R12B 12
%define REGISTER_R13B 13
%define REGISTER_R14B 14
%define REGISTER_R15B 15



registerSearchTable:
	dq "r8"
	dq "r9"
	dq "ah"
	dq "bh"
	dq "ch"
	dq "dh"
	dq "di"
	dq "si"
	dq "dl"
	dq "al"
	dq "bl"
	dq "cl"
	dq "bp"
	dq "sp"
	dq "ax"
	dq "bx"
	dq "cx"
	dq "dx"
	dq "r10"
	dq "r11"
	dq "r12"
	dq "r13"
	dq "r14"
	dq "r15"
	dq "r8b"
	dq "r9b"
	dq "r8d"
	dq "r9d"
	dq "edi"
	dq "rdi"
	dq "rsi"
	dq "esi"
	dq "rbp"
	dq "ebp"
	dq "rsp"
	dq "esp"
	dq "r8w"
	dq "r9w"
	dq "rax"
	dq "eax"
	dq "rbx"
	dq "ebx"
	dq "rcx"
	dq "ecx"
	dq "edx"
	dq "rdx"
	dq "r10b"
	dq "r12b"
	dq "r11b"
	dq "r13b"
	dq "r14b"
	dq "r15b"
	dq "r10d"
	dq "r14d"
	dq "r11d"
	dq "r15d"
	dq "r12d"
	dq "r13d"
	dq "r10w"
	dq "r11w"
	dq "r12w"
	dq "r13w"
	dq "r14w"
	dq "r15w"



registerValueMap:
	dw 776
	dw 777
	dw 4
	dw 7
	dw 5
	dw 6
	dw 263
	dw 262
	dw 2
	dw 0
	dw 3
	dw 1
	dw 261
	dw 260
	dw 256
	dw 259
	dw 257
	dw 258
	dw 778
	dw 779
	dw 780
	dw 781
	dw 782
	dw 783
	dw 8
	dw 9
	dw 520
	dw 521
	dw 519
	dw 775
	dw 774
	dw 518
	dw 773
	dw 517
	dw 772
	dw 516
	dw 264
	dw 265
	dw 768
	dw 512
	dw 771
	dw 515
	dw 769
	dw 513
	dw 514
	dw 770
	dw 10
	dw 12
	dw 11
	dw 13
	dw 14
	dw 15
	dw 522
	dw 526
	dw 523
	dw 527
	dw 524
	dw 525
	dw 266
	dw 267
	dw 268
	dw 269
	dw 270
	dw 271



registerReverseIndexMap:
	align 32
	db 9
	db 11
	db 8
	db 10
	db 2
	db 4
	db 5
	db 3
	db 24
	db 25
	db 46
	db 48
	db 47
	db 49
	db 50
	db 51
	align 32
	db 14
	db 16
	db 17
	db 15
	db 13
	db 12
	db 7
	db 6
	db 36
	db 37
	db 58
	db 59
	db 60
	db 61
	db 62
	db 63
	align 32
	db 39
	db 43
	db 44
	db 41
	db 35
	db 33
	db 31
	db 28
	db 26
	db 27
	db 52
	db 54
	db 56
	db 57
	db 53
	db 55
	align 32
	db 38
	db 42
	db 45
	db 40
	db 34
	db 32
	db 30
	db 29
	db 0
	db 1
	db 18
	db 19
	db 20
	db 21
	db 22
	db 23



;;;;;;;;;;;;;
; MNEMONICS ;
;;;;;;;;;;;;;



; enum Mnemonic (2 bytes)
%define NUM_MNEMONICS 8
%define MNEMONIC_ADD 0
%define MNEMONIC_OR 1
%define MNEMONIC_ADC 2
%define MNEMONIC_SBB 3
%define MNEMONIC_AND 4
%define MNEMONIC_SUB 5
%define MNEMONIC_XOR 6
%define MNEMONIC_CMP 7



mnemonicSearchTable:
	dq "OR"
	dq "SBB"
	dq "SUB"
	dq "ADC"
	dq "ADD"
	dq "AND"
	dq "CMP"
	dq "XOR"



mnemonicIndexTable:
	dw 4
	dw 0
	dw 3
	dw 1
	dw 5
	dw 2
	dw 7
	dw 6



mnemonicReverseIndexTable:
	dw 1
	dw 3
	dw 5
	dw 2
	dw 0
	dw 4
	dw 7
	dw 6