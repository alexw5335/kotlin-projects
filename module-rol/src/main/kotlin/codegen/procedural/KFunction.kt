package codegen.procedural

class KFunction(
	val name       : String,
	val returnType : String?                    = null,
	val params     : List<Pair<String, String>> = emptyList(),
	val contents   : String?                    = null,
	val modifiers  : List<String>               = emptyList(),
	val generics   : String?                    = null,
)