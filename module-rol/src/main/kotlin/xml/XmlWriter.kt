package xml

import codegen.CodeWriter
import codegen.KWriter
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.div

class XmlWriter(writer: Writer) : CodeWriter(writer) {


	constructor(path: Path) : this(Files.newBufferedWriter(path))

	constructor(path: String) : this(Paths.get(path))



	companion object {

		inline fun write(directory: Path, fileName: String, block: XmlWriter.() -> Unit) {
			XmlWriter(directory / "$fileName.xml").use(block)
		}

	}



	init {
		currentStyle = Style(0, 0)
	}



	fun writeProlog() {
		writeln("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>")
		newline()
	}



	fun writeElement(element: XmlElement) {
		write("<${element.type}")

		for((name, value) in element.attribs) {
			write(" $name=\"$value\"")
		}

		if(element.text == null && element.children.isEmpty()) {
			writeln("/>")
			return
		}

		write('>')

		if(element.text != null) {
			write(element.text!!)
			writeln("</${element.type}>")
			return
		}

		newline()

		indent++
		for(c in element.children) writeElement(c)
		indent--

		writeln("</${element.type}>")
	}


}