package assembler

import kotlin.reflect.KClass



class Symbol<T : SymbolData>(
	val name: String,
	val type: KClass<T>,
	var data: T? = null
)



interface SymbolData



class IntSymbolData(val value: Long) : SymbolData



class EnumSymbolData(val name: String, val entries: List<EnumEntry>)



class EnumEntry(val name: String, val value: AstNode?)