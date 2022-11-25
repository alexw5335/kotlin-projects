package eyre

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap



class Intern(val id: Int, val string: String): Comparable<Intern> {
	override fun equals(other: Any?) = other === this
	override fun hashCode() = id
	override fun toString() = string
	override fun compareTo(other: Intern) = when { id < other.id -> -1; id > other.id -> 1; else -> 0 }
}



class InternArray(val array: IntArray) {
	override fun equals(other: Any?) = other is InternArray && array.contentEquals(other.array)
	override fun hashCode() = array.contentHashCode()
}



@JvmInline
value class InternInt(val id: Int) {
	val get get() = Interner[id]
}



object Interner {


	private var count = 0

	private val list = ArrayList<Intern>()

	private val map = HashMap<String, Intern>()

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

	val MAIN = "main".intern

	val RES = "res".intern

}