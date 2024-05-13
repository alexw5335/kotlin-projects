package vkgen

import core.mem.StructLayout
import java.util.regex.Pattern



val camelCaseRegex = Pattern.compile("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])").toRegex()

val String.camelToSnakeCase get() = replace(camelCaseRegex, "_").uppercase()



interface Named {
	val name: String
}



class NamedList<T : Named>(val map: MutableMap<String, T> = LinkedHashMap()) : Collection<T> by map.values {
	fun add(value: T) = map.put(value.name, value)
	fun fromName(name: String) = map[name] ?: error("No such element: $name")
	operator fun get(name: String) = map[name]
	operator fun contains(name: String) = map[name] != null
	fun remove(name: String) = map.remove(name)
}



class VkRegistry {
	val types = NamedList<VkType>()
	val commands = NamedList<VkCommand>()
	val constants = NamedList<VkConstant>()
	val extensions = NamedList<VkExtension>()
}



sealed interface VkType : Named {
	val primitive: Primitive
}



data class VkConstant(
	override val name: String,
	val isAlias: Boolean,
	val type: Type,
	val intValue: Long,
	val floatValue: Float
) : Named {
	enum class Type {
		INT,
		LONG,
		FLOAT,
	}
}

data class VkVar(
	override val name: String,
	val typeString: String,
	val optional: Boolean,
	val pointerCount: Int,
	val index: Int,
	val len: String?,
	val altLen: String?,
	val varLen: String?,
	val constLen: Int?,
	val sTypeString: String?
) : Named {
	var struct: VkStruct? = null
	lateinit var type: VkType
	lateinit var primitive: Primitive
	var varLenVariable: VkVar? = null
	val isPointer = pointerCount > 0
	val isPointer2 = pointerCount == 2
	val isArray = constLen != null || varLen != null
	val isArrayPointer = isPointer && isArray
	val isCharPointer = len == "null-terminated"
}



class VkCommand(
	override val name : String,
	var scope         : Scope,
	val retType       : VkType,
	val params        : List<VkVar>,
	val alias         : String? = null
) : Named {
	var genIndex = 0
	var maskIndex = 0
	enum class Scope { GLOBAL, INSTANCE, DEVICE }
	val isGlobal get() = scope == Scope.GLOBAL
	val isInstance get() = scope == Scope.INSTANCE
	val isDevice get() = scope == Scope.DEVICE
}

class VkEnumEntry(
	override val name: String,
	var value: String?,
	val alias: String?,
	val extension: String?,
) : Named

class VkEnum(
	override val name: String,
	val is64Bit: Boolean,
	val isFlagBits: Boolean
) : VkType {
	override val primitive = if(is64Bit) Primitive.LONG else Primitive.INT
	val entries = NamedList<VkEnumEntry>()
}

class VkBitmask(
	override val name: String,
	val is64Bit: Boolean,
	val enumName: String?,
) : VkType {
	var enum: VkEnum? = null
	override val primitive = if(is64Bit) Primitive.LONG else Primitive.INT
}

class VkStruct(
	override val name: String,
	val isUnion: Boolean,
	val members: List<VkVar>,
	val extends: List<String>
) : VkType {
	var sType = -1
	var requiresBuffer = false
	var layout: StructLayout? = null
	val size get() = layout!!.size
	override val primitive = Primitive.LONG
}

class VkHandle(override val name: String) : VkType {
	override val primitive = Primitive.LONG
}

data object VkVoid : VkType { override val name = "void"; override val primitive = Primitive.LONG }

class VkMiscType(override val name: String, override val primitive: Primitive) : VkType

class VkAliasedType(override val name: String, val typeName: String) : VkType {
	lateinit var type: VkType
	override val primitive get() = type.primitive
}

enum class Primitive(val kName: String, val cName: String, val size: Int) {
	VOID("null", "void", 0),
	BYTE("Byte", "char", 1),
	SHORT("Short", "short", 2),
	INT("Int", "int", 4),
	LONG("Long", "void*", 8),
	FLOAT("Float", "float", 4),
	DOUBLE("Double", "double", 8);
}

class VkExtension(
	override val name : String,
	val types         : NamedList<VkType>,
	val commands      : NamedList<VkCommand>,
	val isFeature     : Boolean,
	// Everything below here is only for extensions (isFeature == false)
	val number        : Int,
	val deprecatedBy  : String?,
	val promotedTo    : String?,
	val disabled      : Boolean,
) : Named


