package eyre

class Resolver(private val srcFile: SrcFile) {


	fun resolve() {
		println("resolving file: ${srcFile.relPath}")

	}


}