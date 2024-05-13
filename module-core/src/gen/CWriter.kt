package core.gen

import core.gen.procedural.CFunction
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.div


@Suppress("unused")
class CWriter(writer: Writer) : CodeWriter(writer) {


	constructor(path: Path) : this(Files.newBufferedWriter(path))

	constructor(path: String) : this(Paths.get(path))



	companion object {

		inline fun write(directory: Path, fileName: String, block: CWriter.() -> Unit) {
			CWriter(directory / "$fileName.c").use(block)
		}

		inline fun writeHeader(directory: Path, fileName: String, block: CWriter.() -> Unit) {
			CWriter(directory / "$fileName.h").use(block)
		}

	}




	// PREPROCESSOR



	fun includes(includes: Iterable<String>) = declaration {
		for(i in includes)
			writeln("#include <$i>")
	}

	fun includes(vararg includes: String) = declaration {
		for(i in includes)
			writeln("#include <$i>")
	}

	fun define(string: String) {
		declaration("#define $string")
	}

	fun ifdef(string: String) {
		declaration("#ifdef $string")
	}

	fun endif() {
		declaration("#endif")
	}



	// BLOCK DECLARATIONS



	inline fun function(signature: String, block: () -> Unit) = declaration(noStyle) {
		write(signature)
		braced(block)
	}



	// COMMENTS



	override fun comment(comment: String) {
		declaration("// $comment")
	}

	override fun multilineComment(lines: List<String>) {
		declaration {
			writeln("/*")
			for(l in lines) writeln(" * $l")
			writeln(" */")
		}
	}

	override fun doc(lines: List<String>) {
		declaration {
			writeln("/*")
			for(l in lines) writeln(" * $l")
			writeln(" */")
		}

		spacing = 0
	}



	// PROCEDURAL DECLARATIONS



	fun function(function: CFunction, block: () -> Unit) = declaration(noStyle) {
		function.writeHeader()
		braced(block)
	}

	fun functionCall(name: String, args: Iterable<String>, ret: Boolean = false) = declaration(noStyle) {
		if(ret) write("return ")
		write(name)
		write('(')
		val iterator = args.iterator()
		if(iterator.hasNext()) {
			while(true) {
				write(iterator.next())
				if(iterator.hasNext()) write(", ") else break
			}
		}
		writeln(");")
	}

	private fun CFunction.writeHeader() {
		for(m in modifiers) { write(m); write(' ') }
		write(retType ?: "void"); write(' ')
		for(m in modifiers2) { write(m); write(' ') }
		write(name); write('(')
		for((index, param) in params.withIndex()) {
			write(param.second); write(' '); write(param.first)
			if(index != params.lastIndex) write(", ")
		}
		write(')')
	}

	fun function(function: CFunction) = declaration(noStyle) {
		function.writeHeader()
		if(function.contents != null) braced { writeMultiline(function.contents) } else { newline() }
	}


}