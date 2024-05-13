package vkgen

import core.gen.CWriter
import core.gen.CodeWriter
import core.gen.KWriter
import core.gen.procedural.CFunction
import core.gen.procedural.KFunction
import java.nio.file.Files
import java.nio.file.Path

class VkGenerator(
	private val parser      : VkParser,
	private val directory   : Path,
	private val cDirectory  : Path,
	private val packageName : String
) {

	private val jniPrefix = "Java_" + packageName.replace('.', '_')
	private class GenInfo(val genName: String, val shouldGen: Boolean)
	private val infoMap = HashMap<String, GenInfo>()
	private val Named.genName get() = infoMap[name]!!.genName
	private val Named.shouldGen get() = infoMap[name]!!.shouldGen
	private fun Named.add(genName: String) = infoMap.put(name, GenInfo(genName, true))
	private fun Named.remove(genName: String = name) = infoMap.put(name, GenInfo(genName, false))
	val commands = ArrayList<VkCommand>()
	private data class CommandMask(val retType: Primitive, val params: List<Primitive>)
	private val commandMasks = LinkedHashSet<CommandMask>()
	private val extensions = ArrayList<VkExtension>()



	private val extensionsToGen = setOf(
		"VK_KHR_swapchain",
		"VK_VERSION_1_0",
		"VK_VERSION_1_1",
		"VK_VERSION_1_2",
		"VK_VERSION_1_3",
		"VK_EXT_debug_utils"
	)



	fun gen() {
		Files.createDirectories(directory)
		Files.createDirectories(cDirectory)

		for(extension in parser.extensions)
			if(extension.name in extensionsToGen)
				extensions.add(extension)

		for(type in parser.types) {
			when(type) {
				is VkBitmask ->
					if(type.enum == null)
						type.remove(if(type.is64Bit) "Long" else "Int")
					else
						type.add(type.name.drop(2))

				is VkEnum -> {
					if(type.isFlagBits)
						type.remove()
					else
						type.add(type.name.drop(2))

					val prefix = when {
						!type.isFlagBits -> VkPostfix.drop(type.name).camelToSnakeCase + "_"
						!type.is64Bit    -> VkPostfix.drop(type.name).dropLast(8).camelToSnakeCase + "_"
						else             -> VkPostfix.drop(type.name).dropLast(9).camelToSnakeCase + "_2_"
					}

					for(entry in type.entries) {
						if(entry.alias != null) {
							entry.remove()
							continue
						}
						val n = if(entry.name.startsWith(prefix))
							entry.name.drop(prefix.length)
						else
							entry.name.drop(3)
						if(n[0].isDigit())
							entry.add("_$n")
						else
							entry.add(n)
					}
				}

				is VkHandle -> type.add(type.name.drop(2) + 'H')
				is VkStruct -> type.add(type.name.drop(2))
				is VkMiscType -> type.remove()
				is VkVoid -> type.remove()
				is VkAliasedType -> type.remove()
			}
		}

		for(command in parser.commands)
			if(command.alias != null)
				command.remove()
			else
				command.add(command.name.drop(2).replaceFirstChar { it.lowercase() })

		for(constant in parser.constants)
			if(constant.isAlias)
				constant.remove()
			else
				constant.add(constant.name.drop(3))

		genConstants()
		genCommands()
		for(e in extensions) genExtension(e)
	}



	private fun genExtension(extension: VkExtension) = KWriter.write(directory, extension.name) {
		fileSuppressUnused()
		package_(packageName)
		declaration("import core.mem.*")
		for(type in extension.types) {
			if(!type.shouldGen) continue
			if(type is VkEnum) genEnum(type) else if(type is VkBitmask) genBitmask(type)
		}
		for(type in extension.types) {
			if(!type.shouldGen) continue
			if(type is VkStruct) genStruct(type)
		}
	}



	private fun genConstants() = KWriter.write(directory, "Constants") {
		fileSuppressUnused()
		package_(packageName)
		for(constant in parser.constants) {
			if(!constant.shouldGen) continue
			val value: String = when(constant.type) {
				VkConstant.Type.INT -> constant.intValue.toString()
				VkConstant.Type.LONG -> constant.intValue.toString() + "L"
				VkConstant.Type.FLOAT -> constant.floatValue.toString() + "F"
			}
			declaration("const val ${constant.genName} = $value")
		}
	}



	// ENUMS



	private fun KWriter.genEnum(enum: VkEnum) {
		val name = enum.genName
		val type = enum.primitive.kName

		val entries = enum.entries.filter { it.shouldGen }

		annotation("JvmInline")
		class_("value class $name(val value: $type)", CodeWriter.Style(0, 0)) {
			companion_(CodeWriter.Style(0, 0)) {
				for(entry in entries)
					declaration("val ${entry.genName} = $name(${entry.value})")
			}
		}
	}



	private fun KWriter.genBitmask(bitmask: VkBitmask) {
		val name = bitmask.genName
		val enum = bitmask.enum
		val type = bitmask.primitive.kName

		if(enum == null) {
			annotation("JvmInline")
			declaration("value class $name(val value: $type)")
			return
		}

		val entries = enum.entries.filter { it.shouldGen }

		annotation("JvmInline")
		class_("value class $name(val value: $type)", CodeWriter.Style(0, 0)) {
			companion_(CodeWriter.Style(0, 0)) {
				for(entry in entries)
					declaration("val ${entry.genName} = $name(${entry.value})")
			}
			declaration("operator fun plus(mask: $name) = $name(value or mask.value)")
			declaration("operator fun contains(mask: $name) = value and mask.value == mask.value")
		}
	}



	// STRUCT


	
	private fun KWriter.genStruct(struct: VkStruct) {
		val name = struct.genName

		annotation("JvmInline")
		class_("value class $name(override val address: Long) : Addressable", CodeWriter.Style(1, 1)) {
			// Members
			group(1) {
				for(m in struct.members) declaration {
					val offset = struct.layout!!.offsets[m.index]

					if(m.isPointer && !m.name.startsWith('p')) println(m.name)

					if(m.isArray) {
						val type = "${m.type.primitive.kName}Ptr"
						writeln("val ${m.name}: $type")
						writeln("\tget() = $type(address + $offset)")
					} else {
						writeln("var ${m.name}: ${m.primitive.kName}")
						writeln("\tget()  = Unsafe.get${m.primitive.kName}(address + $offset)")
						writeln("\tset(v) = Unsafe.set${m.primitive.kName}(address + $offset, v)")
					}
				}
			}

			// Buffer
			if(struct.requiresBuffer) {
				class_("class Buffer(override val address: Long, override val capacity: Int) : DirectBuffer", style(0, 0)) {
					declaration("constructor(address: Long, capacity: Long) : this(address, capacity.toInt())")
					declaration("override val elementSize get() = ${struct.size}")
					declaration("operator fun get(index: Int) = $name(offset(index))")
					declaration("operator fun set(index: Int, value: $name) = Unsafe.copy(value.address, offset(index), ${struct.size})")
					declaration("inline fun forEach(block: ($name) -> Unit) = repeat(capacity) { block(get(it)) }")
					declaration("inline fun<R> map(block: ($name) -> R) = List(capacity) { block(get(it)) }")
					declaration("inline fun<R> mapIndexed(block: (Int, $name) -> R) = List(capacity) { block(it, get(it)) }")
				}

				declaration("val asBuffer get() = Buffer(address, 1)")
			}
		}

		if(struct.sType >= 0) {
			declaration("inline fun AllocatorBase.$name(block: ($name) -> Unit) = $name(calloc(${struct.size})).apply(block).also { it.sType = ${struct.size} }")
			if(struct.requiresBuffer)
				declaration("inline fun AllocatorBase.$name(capacity: Int, block: ($name.Buffer) -> Unit) = $name.Buffer(calloc(capacity * ${struct.size}), capacity).apply(block).apply { forEach { it.sType = ${struct.sType} } }")
		} else {
			declaration("inline fun AllocatorBase.$name(block: ($name) -> Unit) = $name(calloc(${struct.size})).apply(block)")
			if(struct.requiresBuffer)
				declaration("inline fun AllocatorBase.$name(capacity: Int, block: ($name.Buffer) -> Unit) = $name.Buffer(calloc(capacity * ${struct.size}), capacity).apply(block)")
		}

	}



	/*
	COMMANDS
	- Global commands have the same args
	- Instance commands have the first parameter replaced by gInstance
	- Device commands have the first parameter replaced by gDevice
	 */



	private fun genCommands() {
		for(c in parser.commands) {
			if(!c.shouldGen) continue
			c.genIndex = commands.size
			commands.add(c)
		}

		for(c in commands) {
			val mask = CommandMask(c.retType.primitive, c.params.map { it.primitive })
			commandMasks.add(mask)
			c.maskIndex = commandMasks.indexOf(mask)
		}

		genCommandsC()
		genCommandsK()
	}



	private fun genCommandsK() {
		KWriter.write(directory, "Commands") {
			fileSuppressUnused()
			package_(packageName)
			object_("object Commands", CodeWriter.Style(0, 0)) {
				declaration("external fun loadVulkan(): Int")
				declaration("external fun loadInstance(instance: Long)")
				declaration("external fun loadDevice(device: Long)")
				for(c in commands) function(KFunction(
					name       = c.genName,
					returnType = if(c.retType == VkVoid) null else c.retType.primitive.kName,
					modifiers  = listOf("external"),
					params     = buildList {
						for((i, p) in c.params.withIndex())
							if(i == 0 && !c.isGlobal)
								continue
							else
								add(p.name to p.primitive.kName)
					},
				))
			}
		}
	}



	private fun genCommandsC() {
		CWriter.write(cDirectory, "vk_commands") {
			multilineDeclaration("""
				extern void* LoadLibraryA(char* libName);
				extern void* GetProcAddress(void* lib, char* name);
				void* addresses[${commands.size}];
				void* gInstance;
				void* gDevice;
				typedef void* (*pfnGetAddr)(void* handle, char* name);
				pfnGetAddr instanceProc;
				pfnGetAddr deviceProc;
			""")

			function("int ${jniPrefix}_Commands_loadVulkan(void* env, void* obj)") {
				writeln("void* vulkan = LoadLibraryA(\"vulkan-1.dll\");")
				writeln("if(!vulkan) return 0;")
				writeln("instanceProc = GetProcAddress(vulkan, \"vkGetInstanceProcAddr\");")
				writeln("if(!instanceProc) return 0;")
				for(c in commands)
					if(c.isGlobal)
						writeln("addresses[${c.genIndex}] = instanceProc(0, \"${c.name}\");")
				writeln("return 1;")
			}

			function("void ${jniPrefix}_Commands_loadInstance(void* any, void* obj, void* instance)") {
				writeln("gInstance = instance;")
				for(c in commands)
					if(c.isInstance)
						writeln("addresses[${c.genIndex}] = instanceProc(instance, \"${c.name}\");")

			}

			function("void ${jniPrefix}_Commands_loadDevice(void* any, void* obj, void* device)") {
				writeln("gInstance = device;")
				for(c in commands)
					if(c.isDevice)
						writeln("addresses[${c.genIndex}] = deviceProc(device, \"${c.name}\");")

			}

			group(0) {
				for((i, mask) in commandMasks.withIndex()) {
					declaration {
						write("typedef ${mask.retType.cName} (*pfn$i)(")
						for((j, p) in mask.params.withIndex()) {
							write(p.cName)
							if(j != mask.params.lastIndex) write(", ")
						}
						writeln(");")
					}
				}
			}

			for((i, c) in commands.withIndex()) function(CFunction(
				name    = "${jniPrefix}_Commands_${c.genName}",
				retType = c.retType.primitive.cName,
				params  = buildList {
					add("env" to "void*")
					add("obj" to "void*")
					for((j, p) in c.params.withIndex()) {
						if(j == 0 && !c.isGlobal) continue
						add("p$j" to p.primitive.cName)
					}
				}
			)) {
				val args = buildList {
					when(c.scope) {
						VkCommand.Scope.GLOBAL   -> add("p0")
						VkCommand.Scope.INSTANCE -> add("gInstance")
						VkCommand.Scope.DEVICE   -> add("gDevice")
					}
					for(j in 1 ..< c.params.size)
						add("p$j")
				}
				functionCall("((pfn${c.maskIndex}) addresses[$i])", args, c.retType != VkVoid)
			}
		}
	}


}