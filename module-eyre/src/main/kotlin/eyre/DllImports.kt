package eyre

class DllImports {

	class Import(val dll: Intern, val symbols: HashMap<Intern, DllSymbol>)

	private val map = HashMap<Intern, Import>()

	fun add(dll: Intern, import: Intern) = map
		.getOrPut(dll) { Import(dll, HashMap()) }
		.symbols
		.getOrPut(import) { DllSymbol(dll, import, Section.RDATA) }


	val imports get(): Collection<Import> = map.values

}