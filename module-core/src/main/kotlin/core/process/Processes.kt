package core.process

object Processes {


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


}