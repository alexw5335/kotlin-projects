package eyre

import java.nio.file.Paths

class Compiler(val srcSet: SrcSet) {


	constructor(singleDirPath: String) : this(SrcSet(Paths.get(singleDirPath)))



	private val globalNamespace = Namespace(Interns.GLOBAL, Visibility.PUBLIC, SymbolTable())



	fun compile() {
		for(s in srcSet.files) {
			s.lexOutput = Lexer(s).lex()
			s.parseOutput = Parser(s, globalNamespace).parse()
		}

		for(s in srcSet.files)
			resolveFile(s)
	}



	private fun resolveFile(srcFile: SrcFile) {
		if(srcFile.resolving)
			error("Circular dependency found. Currently resolving files: ${srcSet.files.filter { it.resolving }}")
		else if(srcFile.resolved)
			return

		srcFile.resolving = true

		for(import in srcFile.parseOutput.fileImports) {
			val imported = srcSet[import] ?: error("Invalid file import: $import")
			resolveFile(imported)
		}

		Resolver(srcFile).resolve()
		srcFile.resolved = true
		srcFile.resolving = false
	}


}