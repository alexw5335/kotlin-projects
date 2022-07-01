package core

import kotlin.system.exitProcess

object Core {


	fun run(command: String): Boolean {
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



	fun runOrExit(command: String) {
		if(!run(command)) exitProcess(1)
	}



	fun readResourceText(path: String) = this::class.java
		.getResourceAsStream(path)!!
		.reader()
		.readText()
		.toCharArray()



	fun nasmBuild(src: String, out: String, vararg args: String) {
		runOrExit("nasm -fwin64 $src.asm -o $out.obj ${args.joinToString(" ") }")
		runOrExit("gcc $out.obj -o $out.exe")
	}



	fun nasmRun(src: String, out: String, vararg args: String) {
		nasmBuild(src, out, *args)
		runOrExit("./$out.exe")
	}


}