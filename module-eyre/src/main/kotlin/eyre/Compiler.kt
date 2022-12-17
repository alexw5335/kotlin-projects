package eyre

import core.Core
import java.nio.file.Files
import java.nio.file.Paths

class Compiler(private val srcSet: SrcSet) {


	constructor(singleDirPath: String) : this(SrcSet.create(Paths.get(singleDirPath)))



	val globalNamespace = Namespace(Interns.GLOBAL, SymTable())

	val dllImports = DllImports()

	var entryPoint: LabelSymbol? = null



	fun run() {
		compile()
		Core.runPrint("./test.exe")
	}



	fun compile() {
		for(s in srcSet.srcFiles) {
			Lexer(s).lex()
			Parser(this, s).parse()
		}

		Resolver(srcSet, globalNamespace).resolve()

		val assemblerOutput = Assembler(srcSet).assemble()

		val linkerOutput = Linker(this, assemblerOutput).link()

		Files.write(Paths.get("test.bin"), assemblerOutput.text)
		Files.write(Paths.get("test.exe"), linkerOutput)
	}



	fun printAst() {
		for(s in srcSet.srcFiles) {
			println("AST for file: ${s.path}")
			for(n in s.nodes)
				println(n.printableString)
		}
	}


	companion object {
		fun createFromResources(root: String, vararg files: String) =
			Compiler(SrcSet.create(Core.getResourcePath(root), *files))
	}

}