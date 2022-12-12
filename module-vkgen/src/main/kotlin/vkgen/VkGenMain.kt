package vkgen

import core.Core
import vkgen.xml.XmlElement
import vkgen.xml.XmlParser



fun main() {
	VkXmlReader(Core.readResourceChars("/vk.xml")).read()
}



/**
 * https://registry.khronos.org/vulkan/specs/1.3/registry.html
 */
class VkXmlReader(chars: CharArray) {


	private val registry = XmlParser(chars).parse()

	private val consts = NamedList<Const>()

	private val types = NamedList<Type>()



	fun read() {
		for(element in registry.child("enums"))
			if(element.type == "enum")
				consts.add(readConst(element))

		for(element in registry.child("types"))
			if(element.type == "type")
				readType(element)?.let(types::add)

		for(t in types)
			if(t is NativeType || t is BaseType)
				println(t)

	}



	private fun readConst(element: XmlElement): Const {
		val name = element["name"]!!

		element["alias"]?.let {
			val aliased = consts[it]!!
			return Const(name, aliased, aliased.intValue, aliased.floatValue)
		}

		val value = element["value"]!!

		value.toLongOrNull()?.let {
			return Const(name, null, it, null)
		}

		value.toFloatOrNull()?.let {
			return Const(name, null, null, it)
		}

		val intValue = when(value) {
			"(~0U)"   -> 0.inv().toLong()
			"(~0ULL)" -> 0L.inv()
			"(~1U)"   -> 1.inv().toLong()
			"(~2U)"   -> 2.inv().toLong()
			else      -> error("Unhandled const string: $value")
		}

		return Const(name, null, intValue, null)
	}



	private fun readType(element: XmlElement): Type? {
		val category = element["category"]

		if(category == "funcpointer") return null

		val name = element["name"] ?: element.childText("name")

		element["alias"]?.let {
			return AliasedType(name, it)
		}

		when(category) {
			null       -> { }
			"define"   -> return null
			"include"  -> return null
			"basetype" -> return BaseType(name, element.childTextOrNull("type") ?: "void")
			"bitmask"  -> return BitmaskType(name, element["requires"] ?: element["bitvalues"], element["bitvalues"] != null)
			"handle"   -> return HandleType(name, element["parent"], element["objtypeenum"]!!)
			"enum"     -> return EnumType(name, name.contains("FlagBits2"), name.contains("FlagBits"))
			"struct"   -> return readStruct(name, element, false)
			"union"    -> return readStruct(name, element, true)
			else       -> error("Unhandled category: $category")
		}

		element["requires"]?.let {
			if(it == "vk_platform")
				return if(name == "void")
					VoidType
				else
					PrimitiveType(name)

			return NativeType(element.attrib("name"), it)
		}

		if(name == "int") return PrimitiveType("int")

		error("Unhandled type: $element")
	}



	private fun readStruct(name: String, element: XmlElement, isUnion: Boolean): StructType {
		val params = ArrayList<Var>()

		for((i, e) in element.children.withIndex())
			if(e.type == "member")
				params.add(readVar(i, e))

		return StructType(name, isUnion, params, element["structextends"]?.split(',') ?: emptyList())
	}



	private fun readEnums(element: XmlElement) {
		if(element["name"] == "API Constants") return

	}



	private fun readVar(index: Int, element: XmlElement): Var {
		var constLen: Int? = null
		var pType: PointerType? = null

		when(element.text) {
			null              -> { }
			":8"              -> { }
			":24"             -> { }
			"[2]"             -> constLen = 2
			"[3]"             -> constLen = 3
			"[4]"             -> constLen = 4
			"[3][4]"          -> constLen = 12
			"[]"              -> constLen = consts[element.childText("enum")]!!.intValue!!.toInt()
			"const struct *"  -> pType = PointerType.CONST_STRUCT
			"const * const*"  -> pType = PointerType.DOUBLE_CONST
			"*"               -> pType = PointerType.SINGLE
			"const *"         -> pType = PointerType.CONST
			"struct *"        -> pType = PointerType.STRUCT
			"const * const *" -> pType = PointerType.DOUBLE_CONST
			else              -> error("Unhandled struct member text: ${element.text}")
		}

		return Var(
			name     = element.childText("name"),
			type     = element.childText("type"),
			index    = index,
			optional = (element.childOrNull("optional")?.text ?: false) == "true",
			constLen = constLen,
			len      = element["len"],
			pType    = pType
		)
	}


}



class NamedList<T : Named>(
	val map: HashMap<String, T> = HashMap(),
	val list: ArrayList<T> = ArrayList()
) : List<T> by list {

	fun add(named: T) {
		list.add(named)
		map[named.name] = named
	}

	operator fun get(name: String) = map[name]

}



interface Named {
	val name: String
}

interface Type : Named

object VoidType : Type { override val name = "void" }

data class AliasedType(override val name: String, val alias: String) : Type

data class NativeType(override val name: String, val header: String) : Type

data class BaseType(override val name: String, val type: String) : Type

data class PrimitiveType(override val name: String) : Type

data class BitmaskType(override val name: String, val enumName: String?, val is64Bit: Boolean) : Type

data class EnumType(override val name: String, val is64Bit: Boolean, val isFlagBits: Boolean) : Type

data class HandleType(override val name: String, val parent: String?, val objTypeEnum: String) : Type

data class StructType(
	override val name: String,
	val isUnion: Boolean,
	val params: List<Var>,
	val extends: List<String>
) : Type

data class Const(
	override val name : String,
	val alias         : Const?,
	val intValue      : Long?,
	val floatValue    : Float?
) : Named

data class Var(
	val name     : String,
	val type     : String,
	val index    : Int,
	val optional : Boolean,
	val constLen : Int?,
	val len      : String?,
	val pType    : PointerType?,
)

enum class PointerType {
	CONST_STRUCT,
	DOUBLE_CONST,
	SINGLE,
	CONST,
	STRUCT;
}