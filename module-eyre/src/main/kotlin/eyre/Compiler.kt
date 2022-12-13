package eyre

import core.Core
import java.nio.file.Files
import java.nio.file.Paths

class Compiler(private val srcSet: SrcSet) {


	constructor(singleDirPath: String) : this(SrcSet(Paths.get(singleDirPath)))



	val globalNamespace = Namespace(Interns.GLOBAL, SymTable())

	val dllImports = DllImports()

	var entryPoint: LabelSymbol? = null



	fun compile() {
		for(s in srcSet.files) {
			Lexer(s).lex()
			Parser(this, s).parse()
		}

		for(s in srcSet.files) {
			println("AST for file: ${s.path}")
			for(n in s.nodes)
				println(n.printableString)
		}

		Resolver(srcSet, globalNamespace).resolve()

		val assemblerOutput = Assembler(srcSet).assemble()

		val linkerOutput = Linker(this, assemblerOutput).link()

		Files.write(Paths.get("test.bin"), assemblerOutput.text)
		Files.write(Paths.get("test.exe"), linkerOutput)

		println("\nRunning:")
		Core.runPrint("./test.exe")
	}


}