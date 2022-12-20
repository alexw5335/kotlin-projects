package eyre



class DllImport(
	val dll    : Intern,
	val name   : Intern,
	var symbol : DllSymbol = DllSymbol(dll, name, Section.RDATA, 0)
)



class Dll(
	val name: Intern,
	val map: HashMap<Intern, DllImport> = HashMap()
)



class DllImports {

	val dllMap = HashMap<Intern, Dll>()

	val defaultDlls = ArrayList<Dll>()

	fun add(dllName: Intern, importName: Intern) =
		dllMap
		.getOrPut(dllName) { Dll(dllName) }
		.map
		.getOrPut(importName) { DllImport(dllName, importName) }

	fun get(importName: Intern) = defaultDlls.firstNotNullOfOrNull { it.map[importName] }

}



object DefaultDlls {

	fun create(dllName: String, vararg importNames: String): Dll {
		val dllNameIntern = Interner.add(dllName)
		val dll = Dll(dllNameIntern)

		for(importName in importNames) {
			val importNameIntern = Interner.add(importName)
			dll.map[importNameIntern] = DllImport(dllNameIntern, importNameIntern)
		}

		return dll
	}

}