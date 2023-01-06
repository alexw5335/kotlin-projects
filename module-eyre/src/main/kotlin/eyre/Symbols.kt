package eyre



/*
Symbol Table
 */



open class SymTable(private val map: MutableMap<Intern, Symbol> = HashMap()) {
	operator fun get(intern: Intern) = map[intern]
	operator fun contains(intern: Intern) = map.contains(intern)
	operator fun plusAssign(symbol: Symbol) { add(symbol) }
	open fun add(symbol: Symbol) = map.put(symbol.name, symbol)
	open fun add(name: Intern, symbol: Symbol) = map.put(name, symbol)
	operator fun set(name: Intern, symbol: Symbol) = add(name, symbol)
}



object EmptySymTable : SymTable(HashMap()) {
	override fun add(symbol: Symbol) = error("Tried to add a symbol ($symbol) to an the singleton empty symbol table")
	override fun add(name: Intern, symbol: Symbol) = add(symbol)
}



class SymStack {

	private class Frame {
		lateinit var locals: SymTable
		val imports = ArrayList<SymTable>()
	}

	private var frames = arrayOfNulls<Frame>(10)

	var pos = -1

	fun push(locals: SymTable) {
		pos++
		if(pos >= frames.size)
			frames = frames.copyOf(frames.size shl 2)

		val frame = frames[pos] ?: Frame().also { frames[pos] = it }
		frame.locals = locals
		frame.imports.clear()
	}

	fun add(import: SymTable) {
		frames[pos]!!.imports.add(import)
	}

	fun pop() {
		if(pos-- < 0)
			error("Stack underflow")
	}

	operator fun get(name: Intern): Symbol? {
		for(i in pos downTo 0) {
			val frame = frames[i]!!
			frame.locals[name]?.let { return it }
			for(imports in frame.imports)
				imports[name]?.let { return it }
		}

		return null
	}

}



/*
Symbol interfaces
 */



interface Symbol {
	val name: Intern
}



interface Pos {
	var pos: Int
}



interface TypedSymbol : Symbol {
	var type: Type
}



interface ScopedSymbol : Symbol {
	val symbols: SymTable
}



interface Type : ScopedSymbol {
	val size: Int
	var resolved: Boolean
}



/*
Symbol instances
 */



abstract class PrimitiveType(override val name: Intern, override val size: Int) : Type {
	override val symbols = EmptySymTable
	override fun toString() = name.string
	override var resolved = true
}



object VoidType : PrimitiveType(Interner["void"], 0)

object ByteType : PrimitiveType(Interner["byte"], 1)

object WordType : PrimitiveType(Interner["word"], 2)

object DWordType : PrimitiveType(Interner["dword"], 4)

object QWordType : PrimitiveType(Interner["qword"], 8)



class Namespace(
	override val name: Intern,
	override val symbols: SymTable
) : ScopedSymbol

class ResSymbol(
	override val name : Intern,
	override var type : Type = VoidType,
	var size          : Int = 0,
	override var pos  : Int = 0
) : TypedSymbol, Pos

class VarSymbol(
	override val name : Intern,
	override var type : Type = VoidType,
	var size          : Int = 0,
	override var pos  : Int = 0
) : TypedSymbol, Pos

class StructMemberSymbol(
	override val name : Intern,
	val typeName      : Intern,
	override var type : Type = VoidType,
	var offset        : Int = 0
) : TypedSymbol

class StructSymbol(
	override val name     : Intern,
	override val symbols  : SymTable,
	val members           : List<StructMemberSymbol>,
	override var size     : Int = 0,
	override var resolved : Boolean = false
) : Type

class ConstSymbol(
	override val name : Intern,
	val srcFile       : SrcFile,
	var value         : Long = 0,
	var resolved      : Boolean = false
) : Symbol