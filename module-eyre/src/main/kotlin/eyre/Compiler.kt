package eyre

class Compiler(private val context: EyreContext) {


	fun compile() {
		for(srcFile in context.srcFiles)
			Lexer(srcFile).lex()

		for(srcFile in context.srcFiles)
			Parser(context, srcFile).parse()

		//Resolver(context).resolve()
		//context.types.forEach(::println)
	}



	fun printAst() {
		for((i, file) in context.srcFiles.withIndex()) {
			println("\u001B[32mFILE AST (${file.relPath})\u001B[0m\n")

			for(node in file.nodes)
				println(node.printString + "\n")

			if(i != context.srcFiles.size - 1)
				println()
		}
	}


}