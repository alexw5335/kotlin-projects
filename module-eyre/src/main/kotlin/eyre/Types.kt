package eyre



/*
All members of a type except for its name should be mutable
 */



sealed interface Type {
	val name: Intern
}

sealed interface ScopedType : Type {
	val symbols: SymTable
}

object IntType : Type {
	override val name = Interns.INT
}

class StructType(override val name: Intern) : ScopedType {
	override val symbols = SymTable()
}



class EnumEntryType(override val name: Intern) : ScopedType {
	override val symbols = SymTable()
}

class EnumType(override val name: Intern) : ScopedType {
	override val symbols = SymTable()
}