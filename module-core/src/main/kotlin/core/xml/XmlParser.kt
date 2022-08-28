package core.xml

import java.nio.file.Files
import java.nio.file.Path

class XmlParser(private val chars: CharArray) {


	companion object {
		fun parse(file: Path) = XmlParser(Files.readString(file, Charsets.UTF_8).toCharArray()).parse()
	}



	private var pos = 0



	fun parse(): XmlElement {
		pos = 0

		// Skip to the prolog or root element
		while(chars[pos] != '<') pos++

		// Skip the prolog if it is present
		if(chars[pos] == '?')
			while(chars[pos++] != '>') Unit

		// Skip any comments after prolog
		while(chars[pos] != '<' || chars[pos + 1] == '!') pos++

		// Recursively read the root element.
		return readElement()
	}



	private inline fun readUntil(predicate: (Char) -> Boolean): String {
		val startPos = pos
		while(!predicate(chars[pos])) pos++
		return String(chars, startPos, pos - startPos)
	}



	private fun appendText(builder: StringBuilder) {
		// Skip leading whitespace
		while(chars[pos].isWhitespace()) pos++

		// Read until the next tag opening
		while(chars[pos] != '<')
			builder.append(chars[pos++])

		// Skip comments, continue reading between and after comments
		if(chars[pos + 1] == '!') {
			pos += 4 // Skip <!--
			while(chars[pos++] != '-' || chars[pos++] != '-') continue // Skip until '--'
			pos++ // skip the last character of '-->'
			appendText(builder)
		}
	}



	private fun readElement(): XmlElement {
		while(chars[pos++] != '<') Unit

		val type = readUntil { it.isWhitespace() || it == '>' || it == '/' }

		var attributes: MutableMap<String, String>? = null
		var children: MutableList<XmlElement>? = null

		while(true) {
			val char = chars[pos++]

			when {
				// End of opening tag
				char == '>' -> break

				// End of opening tag, no closing tag
				char == '/' -> {
					pos++ // skip '>'
					return XmlElement(type, attributes ?: emptyMap(), emptyList(), null)
				}

				// Ignore whitespace between attributes.
				char.isWhitespace() -> continue

				// Read attribute
				else -> {
					pos--
					val attribName = readUntil { it == ' ' || it == '=' }
					while(chars[pos] != '\'' && chars[pos] != '"') pos++
					if(attributes == null) attributes = HashMap()
					val quoteMark = chars[pos++] // Strings represented by either ' or "
					attributes[attribName] = readUntil { it == quoteMark }
					pos++
				}
			}
		}

		val builder = StringBuilder()

		while(true) {
			// Read the text between child tags and comments
			appendText(builder)

			// next char is '<', either a closing tag or a child's opening tag.
			if(chars[pos + 1] == '/') {
				pos++
				while(chars[pos++] != '>') Unit
				val text = if(builder.isEmpty()) null else builder.toString().trimEnd()
				return XmlElement(type, attributes ?: emptyMap(), children ?: emptyList(), text)
			} else {
				if(children == null) children = ArrayList()
				children.add(readElement())
			}
		}
	}


}