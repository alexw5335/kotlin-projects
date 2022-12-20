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




class InternRange<T>(private val range: IntRange, private val elements: Array<T>) {
	operator fun contains(intern: Intern) = intern.id in range
	operator fun get(intern: Intern) = elements[intern.id - range.first]
}



object Interner {


	private var count = 0

	val list = ArrayList<Intern>()

	private val map = HashMap<String, Intern>()



	val keywords     = createRange(Keyword.values, Keyword::string)

	val widths       = createRange(Width.values, Width::string)

	val varWidths    = createRange(Width.values, Width::varString)

	val registers    = createRange(Register.values, Register::string)

	val prefixes     = createRange(Prefix.values, Prefix::string)

	val visibilities = createRange(Visibility.values, Visibility::string)
	
	val mnemonics    = createRange(Mnemonic.values, Mnemonic::string)


	
	private fun<T> createRange(elements: Array<T>, supplier: (T) -> String): InternRange<T> {
		val range = IntRange(count, count + elements.size)
		for(e in elements) add(supplier(e))
		return InternRange(range, elements)
	}



	fun add(string: String): Intern {
		map[string]?.let { return it }
		val intern = Intern(count++, string)
		list += intern
		map[string] = intern
		return intern
	}



	operator fun get(id: Int) = list[id]


}



object Interns {

	private val String.intern get() = Interner.add(this)

	val RES    = "res"   .intern
	val SHORT  = "short" .intern
	val NULL   = "null"  .intern
	val EMPTY  = ""      .intern
	val GLOBAL = "global".intern
	val MAIN   = "main"  .intern
	val ENDP   = "endp"  .intern
	val SIZEOF = "sizeof".intern
	val REL    = "rel"   .intern

}