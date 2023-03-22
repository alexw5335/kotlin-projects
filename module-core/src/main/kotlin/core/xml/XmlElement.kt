package core.xml

class XmlElement(
	val type     : String,
	val attribs  : Map<String, String>,
	val children : List<XmlElement>,
	var text     : String?
): Iterable<XmlElement> by children {


	/*
	Elements
	 */



	fun child(type: String) = children.firstOrNull { it.type == type } ?: error("No child of type '$type' in $this")

	fun children(type: String) = children.filter { it.type == type }

	fun childOrNull(type: String) = children.firstOrNull { it.type == type }

	fun childText(type: String) = child(type).text ?: error("No text for child '$type' in $this")



	/*
	Attributes
	 */



	fun attrib(key: String) = attribs[key] ?: error("No attribute '$key' in '$this'")

	operator fun get(key: String) = attribs[key]



	/*
	String formatting
	 */



	fun printAll(prefix: String = "") {
		print(prefix)
		println(toString())

		for(c in children)
			c.printAll(prefix + "\t")
	}



	override fun toString() = buildString {
		val reset  = "\u001B[0m"
		val red    = "\u001B[31m"
		val green  = "\u001B[32m"
		val yellow = "\u001B[33m"

		append(type)
		append(' ')

		for((name, value) in attribs)
			append("$red$name$reset=$yellow$value$reset ")

		if(text != null)
			append("$green\"$text\"$reset")
	}


}