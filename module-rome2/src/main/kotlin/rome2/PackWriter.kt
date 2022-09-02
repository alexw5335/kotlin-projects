package rome2

import core.binary.BinaryWriter
import kotlin.reflect.KClass

class PackWriter(private val modMap: HashMap<KClass<*>, HashSet<EntryType>>, val prefix: String) {


	private val writer = BinaryWriter()

	fun write(): ByteArray {
		if(modMap.isEmpty()) error("No mods to write")

		writer.ascii("PFH4")

		writer.u32(2) // flags
		writer.u32(0) // packFileCount
		writer.u32(0) // packFileIndexSize
		writer.u32(modMap.size) // packedFileCount
		writer.u32(0) // packedFileIndexSize, fill in later
		writer.u32(0) // timeStamp

		val sizePositions = ArrayList<Int>()

		for((c, _) in modMap) {
			val table = Tables.get(c)
			val path = "db\\${table.schema.name}\\$prefix${table.schema.name.dropLast(7)}"
			sizePositions.add(writer.pos)
			writer.u32(0) // size, fill in later
			writer.ascii(path)
			writer.u8(0)
		}

		val packedFileIndexSize = writer.pos - 28
		writer.u32(20, packedFileIndexSize)

		var tableIndex = 0

		for((c, types) in modMap) {
			val startPos = writer.pos

			val table = Tables.get(c)
			writer.u8(0xFC)
			writer.u8(0xFD)
			writer.u8(0xFE)
			writer.u8(0xFF)
			writer.u32(table.schema.version)
			writer.u8(1)
			writer.u32(types.size)

			for(type in types) {
				val entry = type.entry

				for(i in entry.fields.indices) {
					val field = entry.fields[i]
					val fieldSchema = table.schema.fields[i]

					when(fieldSchema.type) {
						SchemaFieldType.ASCII -> {
							val string = (field as PackFieldString).value
							writer.u16(string.length)
							writer.stringu8(string)
						}

						SchemaFieldType.ASCII_OPTIONAL -> {
							val string = (field as PackFieldString).value
							if(string.isEmpty()) {
								writer.u8(0)
							} else {
								writer.u8(1)
								writer.u16(string.length)
								writer.stringu8(string)
							}
						}

						SchemaFieldType.STRING_OPTIONAL -> {
							val string = (field as PackFieldString).value
							if(string.isEmpty()) {
								writer.u8(0)
							} else {
								writer.u8(1)
								writer.u16(string.length)
								writer.stringu16(string)
							}
						}

						SchemaFieldType.STRING -> {
							val string = (field as PackFieldString).value
							writer.u16(string.length)
							writer.stringu16(string)
						}

						SchemaFieldType.BOOLEAN -> writer.u8(if((field as PackFieldBoolean).value) 1 else 0)

						SchemaFieldType.FLOAT -> writer.f32((field as PackFieldFloat).value)

						SchemaFieldType.INT -> writer.u32((field as PackFieldInt).value)

						SchemaFieldType.LIST -> error("List types unsupported")
					}
				}
			}

			writer.u32(sizePositions[tableIndex++], writer.pos - startPos)
		}

		return writer.trimmedBytes()
	}


}