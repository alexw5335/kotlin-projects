package eyre

class Compiler(private val srcFiles: List<SrcFile>) {


	private val globalNamespace = Namespace(Interns.GLOBAL, SymTable())



	fun compile() {
		globalNamespace.symbols.addGlobalTypes()

		for(srcFile in srcFiles)
			Lexer(srcFile).lex()

		for(srcFile in srcFiles)
			Parser(globalNamespace, srcFile).parse()

		Resolver(globalNamespace, srcFiles).resolve()
	}



	private fun SymTable.addGlobalTypes() {
		add(VoidType)
		add(ByteType)
		add(WordType)
		add(DWordType)
		add(QWordType)
		add(Interner["i8"], ByteType)
		add(Interner["i16"], WordType)
		add(Interner["i32"], DWordType)
		add(Interner["i64"], QWordType)
	}



	fun printAst() {
		for((i, file) in srcFiles.withIndex()) {
			println("Ast for file: ${file.relPath}:")

			for(node in file.nodes)
				println(node.printString)

			if(i != srcFiles.size - 1)
				println()
		}
	}


}