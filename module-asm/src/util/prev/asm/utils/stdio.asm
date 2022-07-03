%ifndef FILEIO_INCLUDE
%define FILEIO_INCLUDE



%define CREATE_NEW         1
%define CREATE_ALWAYS      2
%define OPEN_EXISTING      3
%define OPEN_ALWAYS        4
%define TRUNCATE_EXISTING  5

%define FILE_SHARE_FLAG_NONE    0
%define FILE_SHARE_FLAG_READ    1
%define FILE_SHARE_FLAG_WRITE   2
%define FILE_SHARE_FLAG_DELETE  4

%define GENERIC_READ     0x80000000
%define GENERIC_WRITE    0x40000000
%define GENERIC_EXECUTE  0x20000000
%define GENERIC_ALL      0x10000000

%define FILE_ATTRIBUTE_READONLY   1
%define FILE_ATTRIBUTE_HIDDEN     2
%define FILE_ATTRIBUTE_SYSTEM     4
%define FILE_ATTRIBUTE_ARCHIVE    32
%define FILE_ATTRIBUTE_NORMAL     128
%define FILE_ATTRIBUTE_TEMPORARY  256
%define FILE_ATTRIBUTE_OFFLINE    4096
%define FILE_ATTRIBUTE_ENCRYPTED  16384



extern CloseHandle
extern CreateFileA
extern GetFileSize
extern ReadFile
extern GetLastError
extern WriteFile
extern printf
extern ExitProcess



section .data
	errorFileCreate db "ERROR: File creation failed with result: %d", 10, 0
	errorFileRead   db "ERROR: File read failed with result: %d", 10, 0
	errorFileClose  db "ERROR: File close failed with result: %d", 10, 0
	errorFileWrite  db "ERROR: File write failed with result: %d", 10, 0
	errorFileSize   db "ERROR: File size query failed with result: %d", 10, 0
	errorStdWrite   db "ERROR: Stdout write failed with result: %d", 10, 0



section .bss
	bytesWritten resb 8



section .text



; rcx: u64 string
; rax: null-terminated length of rcx or 8 if no null-terminator exists
; registers: rax, rcx
nt_length8:
	xor eax, eax
.loop:
	cmp cl, 0
	je .end
	add eax, 1
	shr rcx, 8
	cmp eax, 8
	jne .loop
.end:
	ret



; rcx: char* error message
stdio_error:
	sub rsp, 40
	call GetLastError
	mov rdx, rax
	call printf
	call ExitProcess



;;;;;;;;;;
; STDOUT ;
;;;;;;;;;;



; rcx: void* buffer
; rdx: u64 length (0 for null-terminated buffer)
; note: Program freezes if too many characters are printed in quick succession?
print:
	sub rsp, 56
	mov r8, rdx
	mov rdx, rcx
	mov rcx, -11 ; stdout
	lea r9, [rsp + 40]
	mov qword [rsp + 32], 0
	call WriteFile
	cmp eax, 0
	je .error
	add rsp, 56
	ret
.error:
	lea rcx, [errorStdWrite]
	call stdio_error



println:
   	sub rsp, 56
   	lea rcx, [rsp + 40]
   	mov qword [rcx], 0xA
   	mov rdx, 1
   	call print
   	add rsp, 56
   	ret



; rcx: u64 string
print_ascii8:
	sub rsp, 56
	mov rax, rcx ; rax = string
	lea rcx, [rsp + 40] ; rcx = buffer
	mov [rcx], rax
	xor edx, edx ; rdx = length
.loop:
	cmp al, 0
	je .end
	add edx, 1
	shr rax, 8
	cmp edx, 8
	jne .loop
.end:
	call print
	add rsp, 56
	ret



; rcx: u64 string
println_ascii8:
	sub rsp, 40
	call print_ascii8
	call println
	add rsp, 40
	ret



;;;;;;;;
; FILE ;
;;;;;;;;



; rcx: pFileName
; rdx: GENERIC_WRITE or GENERIC_READ
; r8: FILE_SHARE_FLAGS(NONE | READ | WRITE | DELETE)
; r9: CREATE_ALWAYS, CREATE_NEW, OPEN_EXISTING, OPEN_ALWAYS, or TRUNCATE_EXISTING
; rax: fileHandle
create_file:
	push rbp
	mov rbp, rsp
	sub rsp, 64

	mov qword [rsp + 32 + 0], r9
	mov qword [rsp + 32 + 8], FILE_ATTRIBUTE_NORMAL
	mov qword [rsp + 32 + 16], 0
	mov r9, 0
	call CreateFileA

	cmp rax, -1
	je .error

	leave
	ret
.error:
	call GetLastError
	lea rcx, [errorFileCreate]
	mov rdx, rax
	call printf
	call ExitProcess



; rcx: pFileName
; rax: fileHandle
create_file_for_writing:
	push rbp
	mov rbp, rsp
	sub rsp, 32

	mov rdx, GENERIC_WRITE
	mov r8, FILE_SHARE_FLAG_NONE
	mov r9, CREATE_ALWAYS
	call create_file

	leave
	ret



; rcx: pFileName
; rax: fileHandle
create_file_for_reading:
	sub rsp, 40

	mov rdx, GENERIC_READ
	mov r8, FILE_SHARE_FLAG_NONE
	mov r9, OPEN_EXISTING
	call create_file

	add rsp, 40
	ret



; rcx: fileHandle
close_file:
	sub rsp, 40
	call CloseHandle
	cmp rax, 0
	je .error
	add rsp, 40
	ret
.error:
	call GetLastError
	lea rcx, [errorFileClose]
	mov rdx, rax
	call printf
	call ExitProcess



; rcx: HANDLE* file
; rax: u32 fileSize
get_file_size:
	sub rsp, 40
	xor edx, edx
	call GetFileSize
	cmp eax, 0xFFFFFFFF ; INVALID_FILE_SIZE
	je .error
	add rsp, 40
	ret
.error:
	call GetLastError
	lea rcx, [errorFileSize]
	mov rdx, rax
	call printf
	call ExitProcess



; rcx: HANDLE* file
; rdx: void* buffer
; r8: u32 maxBytesToRead
; r9: u32* bytesRead
read_file:
	sub rsp, 56

	mov qword [rsp + 32], 0
	call ReadFile
	cmp eax, 0
	je .error

	add rsp, 56
	ret
.error:
	call GetLastError
	lea rcx, [errorFileRead]
	mov rdx, rax
	call printf
	call ExitProcess



; rcx: fileHandle
; rdx: pData
; r8: dataLength
write_file:
	push rbp
	mov rbp, rsp
	sub rsp, 48

	lea r9, [bytesWritten]
	mov qword [rsp + 32 + 0], 0 ; lpOverlapped, NULL
	call WriteFile
	cmp rax, 0
	je .error

	leave
	ret
.error:
	call GetLastError
	lea rcx, [errorFileWrite]
	mov rdx, rax
	call printf
	call ExitProcess



%endif