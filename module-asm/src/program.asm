default rel



global main
extern ExitProcess
extern LoadLibraryA
extern GetProcAddress



%include "std.asm"



section .data

errorCommandFailed db "Command %s failed with result %d", 10, 0
errorVulkanLoad db "Failed to load the Vulkan dynamic library", 10, 0
errorVulkanProcAddr db "Failed to load the vkGetInstanceProcAddr function", 10, 0
vulkanLibraryName db "vulkan-1.dll", 0
string_vkGetInstanceProcAddr db "vkGetInstanceProcAddr", 0
string_vkEnumerateInstanceVersion db "vkEnumerateInstanceVersion", 0
string_vkEnumerateInstanceExtensionProperties db "vkEnumerateInstanceExtensionProperties", 0
string_vkEnumerateInstanceLayerProperties db "vkEnumerateInstanceLayerProperties", 0
string_vkCreateInstance db "vkCreateInstance", 0

section .bss

vulkanHModule resb 8
vkVersion resb 8
vkGetInstanceProcAddr resb 8
vkEnumerateInstanceVersion resb 8
vkEnumerateInstanceExtensionProperties resb 8
vkEnumerateInstanceLayerProperties resb 8
vkCreateInstance resb 8

section .text



load_vulkan:
	sub rsp, 40

	lea rcx, [vulkanLibraryName]
	call LoadLibraryA
	cmp rax, 0
	je .error
	mov [vulkanHModule], rax

	mov rcx, [vulkanHModule]
	lea rdx, [string_vkGetInstanceProcAddr]
	call GetProcAddress
	cmp rax, 0
	je .error2
	mov [vkGetInstanceProcAddr], rax

	xor ecx, ecx
	lea rdx, [string_vkEnumerateInstanceVersion]
	call [vkGetInstanceProcAddr]
	mov [vkEnumerateInstanceVersion], rax

	xor ecx, ecx
	lea rdx, [string_vkEnumerateInstanceExtensionProperties]
	call [vkGetInstanceProcAddr]
	mov [vkEnumerateInstanceExtensionProperties], rax

	xor ecx, ecx
    lea rdx, [string_vkEnumerateInstanceLayerProperties]
    call [vkGetInstanceProcAddr]
    mov [vkEnumerateInstanceLayerProperties], rax

    xor ecx, ecx
    lea rdx, [string_vkCreateInstance]
    call [vkGetInstanceProcAddr]
    mov [vkCreateInstance], rax

	lea rcx, [vkVersion]
	call [vkEnumerateInstanceVersion]
	mov rcx, [vkVersion]

	add rsp, 40
	ret
.error:
	lea rcx, [errorVulkanLoad]
	call printf
	call ExitProcess
.error2:
	lea rcx, [errorVulkanProcAddr]
	call printf
	call ExitProcess



create_instance:
	sub rsp, 40
	add rsp, 40
	ret



main:
	sub rsp, 40

	call load_vulkan
	call create_instance

	lea rcx, [stringFinished]
	call print_string
	call ExitProcess