package assembler

import kotlin.system.exitProcess


const val directory = "module-assembler/src/main/asm"

const val fileName = "mnemonic_generator"

const val asmSrc = "$directory/$fileName.asm"
const val asmOut = "$directory/out/$fileName.obj"
const val exeOut = "$directory/out/$fileName.exe"
const val includeDirs = "-I$directory/utils -I$directory"



private fun run(command: String): Boolean {
	val process = Runtime.getRuntime().exec(command)
	process.waitFor()

	val errorText = process.errorReader().readText()
	if(errorText.isNotEmpty()) {
		print("\u001B[31m")
		print(errorText)
		print("\u001B[0m")
		return false
	}

	val outputText = process.inputReader().readText()
	if(outputText.isNotEmpty())
		print(outputText)

	return true
}



private fun Boolean.check() {
	if(!this) exitProcess(1)
}



fun main() {
	run("nasm -fwin64 $asmSrc -o $asmOut $includeDirs").check()
	run("gcc $asmOut -o $exeOut").check()
	run("./$exeOut").check()
}