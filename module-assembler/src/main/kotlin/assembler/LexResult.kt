package assembler

class LexResult(val tokens: List<Token>, val newlines: NewlineList) {


	fun printTokens() {
		for(t in tokens) {
			if(t is EndToken) break
			println(t.printableString)
		}
	}


}