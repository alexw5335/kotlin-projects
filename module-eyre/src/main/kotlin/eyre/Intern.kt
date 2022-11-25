package eyre

import kotlin.collections.ArrayList
import kotlin.collections.HashMap



class Intern(val id: Int, val string: String): Comparable<Intern> {
	override fun equals(other: Any?) = other === this
	override fun hashCode() = id
	override fun toString() = string
	override fun compareTo(other: Intern) = when { id < other.id -> -1; id > other.id -> 1; else -> 0 }
}



class InternArray(val array: IntArray) {
	constructor(strings: List<String>) : this(IntArray(strings.size) { Interner.add(strings[it]).id })
	override fun equals(other: Any?) = other is InternArray && array.contentEquals(other.array)
	override fun hashCode() = array.contentHashCode()
}



object Interner {


	private var count = 0

	private val list = ArrayList<Intern>()

	private val map = HashMap<String, Intern>()



	private val keywordRange = addRanged(Keyword.values, Keyword::string)

	private val widthRange = addRanged(Width.values, Width::string)

	private val varWidthRange = addRanged(Width.values, Width::varString)

	private val registerRange = addRanged(Register.values, Register::string)

	private val prefixRange = addRanged(Prefix.values, Prefix::string)



	fun isKeyword(intern: Intern) = intern.id in keywordRange

	fun isWidth(intern: Intern) = intern.id in widthRange

	fun isVarWidth(intern: Intern) = intern.id in varWidthRange

	fun isRegister(intern: Intern) = intern.id in registerRange

	fun isPrefix(intern: Intern) = intern.id in prefixRange



	fun keyword(intern: Intern) = Keyword.values[intern.id - keywordRange.first]

	fun width(intern: Intern) = Width.values[intern.id - widthRange.first]

	fun varWidth(intern: Intern) = Width.values[intern.id - varWidthRange.first]

	fun register(intern: Intern) = Register.values[intern.id - registerRange.first]

	fun prefix(intern: Intern) = Prefix.values[intern.id - prefixRange.first]



	fun<T> addRanged(elements: Array<T>, block: (T) -> String): IntRange {
		val start = count
		for(e in elements) add(block(e))
		return IntRange(start, start + elements.size)
	}



	fun add(string: String): Intern {
		val intern = Intern(count++, string)
		map[string]?.let { return it }
		list.add(intern)
		map[string] = intern
		return intern
	}



	operator fun get(id: Int) = list[id]


}



object Interns {

	private val String.intern get() = Interner.add(this)

	val RES   = "res"   .intern
	val MAIN  = "main"  .intern
	val SHORT = "short" .intern
	val DLL   = "dll"   .intern
	val NULL  = "null"  .intern
	val EMPTY = ""      .intern

}