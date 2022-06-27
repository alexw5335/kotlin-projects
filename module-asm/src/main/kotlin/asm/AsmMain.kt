package asm

import core.Processes



private const val fileName = "mnemonic_generator"

private const val directory = "module-asm/src/main/asm"

private const val asmSrc = "$directory/$fileName.asm"

private const val asmOut = "$directory/out/$fileName.obj"

private const val exeOut = "$directory/out/$fileName.exe"



fun main() {
	Processes.runOrExit("nasm -fwin64 $asmSrc -o $asmOut -I$directory/utils -I$directory")
	Processes.runOrExit("gcc $asmOut -o $exeOut")
	Processes.runOrExit("./$exeOut")
}