package assembler



class Interned(val type: InternType, val name: String)



enum class InternType {
	NONE,
	KEYWORD,
	MEM_SIZE,
	MNEMONIC;
}



object Intern {

	private val map = HashMap<String, Interned>()

	fun add(type: InternType, string: String) = map.put(string, Interned(type, string))

	fun add(string: String) = map.put(string, Interned(InternType.NONE, string))

	fun addAndGet(string: String) = map.getOrPut(string) { Interned(InternType.NONE, string) }

}