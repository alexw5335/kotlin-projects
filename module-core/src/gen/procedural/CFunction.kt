package core.gen.procedural

class CFunction(
	val name       : String,
	val retType    : String?                    = null,
	val params     : List<Pair<String, String>> = emptyList(),
	val contents   : String?                    = null,
	val modifiers  : List<String>               = emptyList(),
	val modifiers2 : List<String>               = emptyList()
)