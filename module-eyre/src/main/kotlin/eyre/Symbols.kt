package eyre

import java.util.LinkedList



interface MutableScope : Scope {
	fun add(symbol: Symbol): Symbol?
	operator fun plusAssign(symbol: Symbol) { add(symbol) }
}



interface Scope {
	operator fun get(intern: Intern): Symbol?
	fun isEmpty(): Boolean
}



object EmptyScope : Scope {
	override operator fun get(intern: Intern) = null
	override fun isEmpty() = true
}



class SymTable(val map: MutableMap<Intern, Symbol> = HashMap()) : MutableScope {

	override fun get(intern: Intern) = map[intern]

	override fun add(symbol: Symbol) = map.put(symbol.name, symbol)

	override fun isEmpty() = map.isEmpty()

	override fun toString() = map.toString()

	operator fun contains(intern: Intern) = map.contains(intern)

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



sealed interface Symbol {
	val name: Intern
}



sealed interface Ref {
	var pos: Int
	var section: Section
}



private data class RefImpl(
	override var section : Section,
	override var pos     : Int
) : Ref



fun Ref(section: Section, pos: Int): Ref = RefImpl(section, pos)



data class Namespace(
	override val name : Intern,
	val symbols       : SymTable
) : Symbol



data class DllSymbol(
	val dll              : Intern,
	override val name    : Intern,
	override var section : Section,
	override var pos     : Int = 0
) : Symbol, Ref {

	override fun equals(other: Any?) = other is DllSymbol && other.dll === dll && other.name === name
	override fun hashCode() = name.hashCode()
}



data class VarSymbol(
	override val name    : Intern,
	override var section : Section,
	override var pos     : Int = 0,
	val size             : Int
) : Symbol, Ref



data class ResSymbol(
	override val name    : Intern,
	override var section : Section,
	override var pos     : Int = 0,
	var size             : Int = 0
) : Symbol, Ref



data class LabelSymbol(
	override val name    : Intern,
	override var section : Section,
	override var pos     : Int = 0
) : Symbol, Ref



data class ProcSymbol(
	override val name    : Intern,
	override var section : Section,
	override var pos     : Int = 0,
	val symbols          : SymTable
) : Symbol, Ref



/*
Const
 */



data class IntSymbol(
	override val name       : Intern,
	val file                : SrcFile,
	var value               : Long = 0,
	var resolved            : Boolean = false
) : Symbol