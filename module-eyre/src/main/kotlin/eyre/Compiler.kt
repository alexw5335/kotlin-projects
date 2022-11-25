package eyre

import java.nio.file.Paths

class Compiler(val srcSet: SrcSet) {


	constructor(singleDirPath: String) : this(SrcSet(Paths.get(singleDirPath)))



	fun compile() {
		val lexOutputs = srcSet.files.map { Lexer(it).lex() }
		val parseOutputs = lexOutputs.map { Parser(it).parse() }

		for(p in parseOutputs)
			for(t in p.fileImports)
				t.array.contentToString().let(::println)

		println("test")
		for(i in srcSet.files)
			i.relParts.array.contentToString().let(::println)
	}


}