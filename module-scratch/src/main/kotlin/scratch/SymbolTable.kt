package scratch

import java.util.*
import kotlin.collections.HashMap

private class Symbol(name: Int)

private class ScopeName(val id: Int, val components: IntArray)

private class SymbolTable {
	private val map = HashMap<ScopeName, Symbol>()
	operator fun get(scopeName: ScopeName) = map[scopeName]
	operator fun set(scopeName: ScopeName, symbol: Symbol) = map.put(scopeName, symbol)
}

fun main() {

}