package eyre

class EyreContext(val srcFiles: List<SrcFile>) {

	val globalNamespace = Namespace(Interns.GLOBAL, SymTable())

	val types = ArrayList<Type>()

	init {
		globalNamespace.symbols.add(VoidType)
		globalNamespace.symbols.add(ByteType)
		globalNamespace.symbols.add(WordType)
		globalNamespace.symbols.add(DWordType)
		globalNamespace.symbols.add(QWordType)
		globalNamespace.symbols.add(Interns.I8, ByteType)
		globalNamespace.symbols.add(Interns.I16, WordType)
		globalNamespace.symbols.add(Interns.I32, DWordType)
		globalNamespace.symbols.add(Interns.I64, QWordType)
	}

}