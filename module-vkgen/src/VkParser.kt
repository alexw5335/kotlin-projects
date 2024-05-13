package vkgen

import core.mem.StructLayout
import core.mem.StructLayoutBuilder
import core.xml.XmlElement
import java.rmi.registry.Registry

class VkParser(val root: XmlElement) {


	private fun err(element: XmlElement): Nothing = error(element.toString())

	private val registry = VkRegistry()
	val types = registry.types
	val commands = registry.commands
	val extensions = registry.extensions
	val constants = registry.constants



	init {
		// requires="vk_platform"
		types.add(VkVoid)
		types.add(VkMiscType("char", Primitive.BYTE))
		types.add(VkMiscType("int8_t", Primitive.BYTE))
		types.add(VkMiscType("uint8_t", Primitive.BYTE))
		types.add(VkMiscType("int16_t", Primitive.SHORT))
		types.add(VkMiscType("uint16_t", Primitive.SHORT))
		types.add(VkMiscType("int", Primitive.INT))
		types.add(VkMiscType("int32_t", Primitive.INT))
		types.add(VkMiscType("uint32_t", Primitive.INT))
		types.add(VkMiscType("uint64_t", Primitive.LONG))
		types.add(VkMiscType("int64_t", Primitive.LONG))
		types.add(VkMiscType("size_t", Primitive.LONG))
		types.add(VkMiscType("float", Primitive.FLOAT))
		types.add(VkMiscType("double", Primitive.DOUBLE))
		// category="basetype"
		types.add(VkMiscType("ANativeWindow", Primitive.LONG))
		types.add(VkMiscType("AHardwareBuffer", Primitive.LONG))
		types.add(VkMiscType("CAMetalLayer", Primitive.LONG))
		types.add(VkMiscType("VkSampleMask", Primitive.LONG))
		types.add(VkMiscType("VkBool32", Primitive.INT))
		types.add(VkMiscType("VkFlags", Primitive.INT))
		types.add(VkMiscType("VkFlags64", Primitive.LONG))
		types.add(VkMiscType("VkDeviceSize", Primitive.LONG))
		types.add(VkMiscType("VkDeviceAddress", Primitive.LONG))
		types.add(VkMiscType("VkRemoteAddressNV", Primitive.LONG))
		types.add(VkMiscType("MTLDevice_id", Primitive.LONG))
		types.add(VkMiscType("MTLCommandQueue_id", Primitive.LONG))
		types.add(VkMiscType("MTLBuffer_id", Primitive.LONG))
		types.add(VkMiscType("MTLTexture_id", Primitive.LONG))
		types.add(VkMiscType("MTLSharedEvent_id", Primitive.LONG))
		types.add(VkMiscType("IOSurfaceRef", Primitive.LONG))
		// requires="windows.h"
		types.add(VkMiscType("HINSTANCE", Primitive.LONG))
		types.add(VkMiscType("HWND", Primitive.LONG))
		types.add(VkMiscType("HMONITOR", Primitive.LONG))
		types.add(VkMiscType("HANDLE", Primitive.LONG))
		types.add(VkMiscType("SECURITY_ATTRIBUTES", Primitive.LONG))
		types.add(VkMiscType("DWORD", Primitive.INT))
		types.add(VkMiscType("LPCWSTR", Primitive.LONG))
	}



	fun parse(): VkRegistry {
		// The first enums block contains API constants
		// This must be parsed first since some of the types reference constants
		for(child in root.children.first { it.type == "enums" })
			constants.add(parseConstant(child))

		for(element in root) {
			when(element.type) {
				"types" -> element.forEach {
					if(it.type == "type") parseType(it)?.let(types::add)
					else if(it.type != "comment") err(element)
				}
				"commands" -> element.forEach {
					if(it.type == "command") parseCommand(it)?.let(commands::add)
					else if(it.type != "comment") err(element)
				}
				"extensions" -> element.forEach {
					if(it.type == "extension") extensions.add(parseExtension(it, false))
					else if(it.type != "comment") err(element)
				}
				"feature" -> extensions.add(parseExtension(element, true))
				"enums" -> parseEnum(element)
				"platforms",
				"comment",
				"tags",
				"formats",
				"spirvextensions",
				"spirvcapabilities",
				"sync" -> continue
				else -> err(element)
			}
		}

		val sType = types.fromName("VkStructureType") as VkEnum

		for(type in types) {
			when(type) {
				is VkStruct -> {
					type.members[0].sTypeString?.let {
						type.sType = sType.entries.fromName(it).value!!.toInt()
					}
					type.members.forEach { processVar(it, type) }
				}
				is VkAliasedType -> type.type = types.fromName(type.typeName)
				is VkEnum -> {
					fun value(entry: VkEnumEntry): String = entry.value
						?: value(type.entries.fromName(entry.alias!!))
					for(entry in type.entries) entry.value = value(entry)
				}
				is VkBitmask -> type.enum = type.enumName?.let { types.fromName(it) as VkEnum }
				else -> continue
			}
		}

		for(c in commands)
			c.params.forEach { processVar(it, null) }

		for(type in types)
			if(type is VkStruct)
				structLayout(type)

		return registry
	}

	
	
	private fun processVar(v: VkVar, struct: VkStruct?) {
		v.type = types.fromName(v.typeString)
		v.primitive = if(v.isPointer || v.isArray) Primitive.LONG else v.type.primitive
		v.struct = struct
		if(struct != null && v.varLen != null)
				v.varLenVariable = struct.members.first { it.name == v.varLen }
		if(v.type is VkStruct && v.isArray)
			(v.type as VkStruct).requiresBuffer = true
	}



	private fun structLayout(struct: VkStruct): StructLayout {
		struct.layout?.let { return it }
		struct.layout = StructLayoutBuilder().build(struct.isUnion) {
			for(m in struct.members) {
				when {
					m.isPointer -> member(8)
					m.isArray && m.type is VkStruct -> array(structLayout(m.type as VkStruct), m.constLen!!)
					m.isArray -> array(m.type.primitive.size, m.constLen!!)
					m.type is VkStruct -> member(structLayout(m.type as VkStruct))
					else -> member(m.type.primitive.size)
				}
			}
		}
		return struct.layout!!
	}



	private fun parseCommand(element: XmlElement): VkCommand? {
		if(element["api"] == "vulkansc") return null

		element["alias"]?.let {
			val aliased = commands.fromName(it)
			return VkCommand(element.attrib("name"), aliased.scope, aliased.retType, aliased.params, it)
		}

		val proto      = element.child("proto")
		val name       = proto.child("name").text!!
		val returnType = proto.child("type").text!!
		val params     = element.children("param").filter { it["api"] != "vulkansc" }.mapIndexed(::parseVar)

		val scope: VkCommand.Scope = when {
			name == "vkGetInstanceProcAddr"            -> VkCommand.Scope.GLOBAL
			name == "vkGetDeviceProcAddr"              -> VkCommand.Scope.INSTANCE
			params[0].typeString == "VkInstance"       -> VkCommand.Scope.INSTANCE
			params[0].typeString == "VkPhysicalDevice" -> VkCommand.Scope.INSTANCE
			params[0].typeString == "VkDevice"         -> VkCommand.Scope.DEVICE
			params[0].typeString == "VkQueue"          -> VkCommand.Scope.DEVICE
			params[0].typeString == "VkCommandBuffer"  -> VkCommand.Scope.DEVICE
			else                                       -> VkCommand.Scope.GLOBAL
		}

		return VkCommand(name, scope, types.fromName(returnType), params, null)
	}




	private fun parseType(element: XmlElement): VkType? {
		val name = element["name"] ?: element.child("name").text ?: err(element)

		element["alias"]?.let { return VkAliasedType(name, it) }

		return when(val category = element["category"]) {
			"define", "include" -> VkMiscType(name, Primitive.LONG)
			"enum" -> VkEnum(name, name.contains("FlagBits2"), name.contains("FlagBits"))
			"bitmask" -> VkBitmask(name, "bitvalues" in element, element["requires"] ?: element["bitvalues"])
			"basetype" -> if(name !in types) err(element) else null
			"struct", "union" -> VkStruct(
				name,
				category == "union",
				element.children("member").filter { it["api"] != "vulkansc" }.mapIndexed(::parseVar),
				element["structextends"]?.split(',') ?: emptyList()
			)
			"handle" -> VkHandle(name)
			"funcpointer" -> VkMiscType(name, Primitive.LONG)
			null -> {
				if(name in types) return null // C types
				types.add(VkMiscType(name, Primitive.LONG))
				null // Platform-specific types, fill in later
			}
			else -> err(element)
		}
	}



	private fun parseConstant(element: XmlElement) : VkConstant {
		val name = element["name"] ?: err(element)

		element["alias"]?.let {
			val alias = constants[it] ?: err(element)
			return VkConstant(name, true, alias.type, alias.intValue, alias.floatValue)
		}

		val value = element.attrib("value")

		when(value) {
			"(~0ULL)" -> return VkConstant(name, false, VkConstant.Type.LONG, -1, 0F)
			"(~0U)"   -> return VkConstant(name, false, VkConstant.Type.INT, -1, 0F)
			"(~0U-1)" -> return VkConstant(name, false, VkConstant.Type.INT, -2, 0F)
			"(~0U-2)" -> return VkConstant(name, false, VkConstant.Type.INT, -3, 0F)
			"(~1U)"   -> return VkConstant(name, false, VkConstant.Type.INT, -2, 0F)
			"(~2U)"   -> return VkConstant(name, false, VkConstant.Type.INT, -3, 0F)
		}

		return when(element["type"]) {
			"uint32_t" -> VkConstant(name, false, VkConstant.Type.INT, value.toLong(), 0F)
			"uint64_t" -> VkConstant(name, false, VkConstant.Type.LONG, value.toLong(), 0F)
			"float"    -> VkConstant(name, false, VkConstant.Type.FLOAT, 0, value.toFloat())
			else       -> err(element)
		}
	}



	private fun parseEnum(element: XmlElement) {
		val name = element["name"] ?: err(element)
		if(name == "API Constants") return
		val enum = types.fromName(name) as VkEnum
		for(child in element)
			if(child.type == "enum")
				enum.entries.add(parseEnumEntry(child, 0, null))
	}



	private fun parseEnumEntry(element: XmlElement, extNumber: Int, extension: String?): VkEnumEntry {
		val name = element["name"] ?: err(element)
		element["alias"]?.let { return VkEnumEntry(name, null, it, extension) }
		// See KhronosGroup/Vulkan-Docs/scripts/generator.py for the formula
		var value =
			element["value"]
				?: element["bitpos"]?.let {
					(1L shl it.toInt()).toString()
				}
				?: element["offset"]?.let {
					(1000000000 + ((element["extnumber"]?.toInt() ?: extNumber) - 1) * 1000 + it.toInt()).toString()
				}
				?: err(element)
		if(element["dir"] == "-") value = "-$value"
		return VkEnumEntry(name, value, null, extension)
	}



	/*
	Struct members and function variables
	 */



	private fun parseVar(index: Int, element: XmlElement) = VkVar(
		element.child("name").text?.let { if(it == "object") "object_" else it } ?: err(element),
		element.child("type").text ?: err(element),
		element["optional"]?.let { it == "true" } ?: false,
		element.text?.count { it == '*' } ?: 0,
		index,
		element["len"],
		element["altlen"],
		varLen(element["len"]),
		constArrayLength(element),
		element["values"]
	)



	private fun constArrayLength(element: XmlElement): Int? {
		val text = element.text

		// Edge case for VkAccelerationStructureVersionInfoKHR.
		if(element["len"] == "2*ename:VK_UUID_SIZE") return 32

		element["len"]?.let(::println)
		// No const array length.
		if(text == null || !text.contains('[')) return null

		// If [] with no specified array length, then the array length is given as an attribute named 'enum'.
		// The attribute refers to an API constant.
		return text.split('[').last().substringBefore(']').let {
			if(it.isEmpty())
				constants.fromName(element.child("enum").text!!).intValue.toInt()
			else
				it.toInt()
		}
	}


	private val constLenMap = mapOf(
		"[2]" to 2,
		"[3]" to 3,
		"[4]" to 4,
		"[3][4]" to 12,
		"const [2]" to 2,
		"const [4]" to 4,
	)



	/**
	 * Returns the name of the struct member that determines another member's array length.
	 */
	private fun varLen(len: String?): String? = when {
		// No variable length
		len == null -> null

		// Of the form "variable,null-terminated". Only 3 instances where this occurs:
		// ppEnabledLayerNames, ppEnabledExtensionNames, ppGeometries.
		// Only the first part of the len string matters for these.
		len.contains(',') 					-> len.split(',')[0]

		// Only 2 cases, too complex to create convenience functions for these.
		len.startsWith("latexmath") 		-> null

		// Handled as a constant array length (=32)
		len == "2*ename:VK_UUID_SIZE"		-> null

		// Only for char*. These are handled separately during generation.
		len == "null-terminated"			-> null

		// Edge-case - refers to a variable of a variable within the struct.
		len == "pBuildInfo-geometryCount" 	-> null

		// By this point, the length should refer to another variable in a struct.
		// Warning: This will not catch new edge-cases. New edge-cases will produce compile errors.
		else								-> len
	}



	/*
	Feature and Extension
	 */



	private fun parseExtension(element: XmlElement, isFeature: Boolean): VkExtension {
		val name = element["name"] ?: err(element)
		val number = if(isFeature) 0 else element["number"]?.toInt() ?: err(element)
		val types = NamedList<VkType>()
		val commands = NamedList<VkCommand>()

		for(require in element.children) {
			if(require.type != "require") continue

			for(element2 in require.children) {
				when(element2.type) {
					"type"    -> types.add(this.types.fromName(element2.attrib("name")))
					"command" -> commands.add(this.commands.fromName(element2.attrib("name")))
					"enum"    -> {
						val enum = this.types.fromName(element2["extends"] ?: continue) as VkEnum
						enum.entries.add(parseEnumEntry(element2, number, if(isFeature) null else name))
					}
				}
			}
		}

		if(isFeature) return VkExtension(name, types, commands, true, 0, null, null, false)

		return VkExtension(
			name,
			types,
			commands,
			false,
			number,
			element["deprecatedby"],
			element["promotedto"],
			element["supported"] == "disabled"
		)
	}


}