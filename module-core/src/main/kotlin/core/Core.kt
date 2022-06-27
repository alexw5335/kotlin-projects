package core

object Core {


	fun readResourceText(path: String) = this::class.java
		.getResourceAsStream(path)!!
		.reader()
		.readText()
		.toCharArray()


}