package core.xml

class XmlElement(
	val type     : String,
	val attribs  : Map<String, String>,
	val children : List<XmlElement>,
	val text     : String?
): Iterable<XmlElement> by children {


	/*
	Elements
	 */



	fun child(type: String) = children.first { it.type == type }

	fun children(type: String) = children.filter { it.type == type }

	fun childOrNull(type: String) = children.firstOrNull { it.type == type }



	/*
	Attributes
	 */



	fun attrib(key: String) = attribs[key] ?: throw IllegalArgumentException("No attribute '$key' in '$this'")

	fun attribOrNull(key: String) = attribs[key]

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