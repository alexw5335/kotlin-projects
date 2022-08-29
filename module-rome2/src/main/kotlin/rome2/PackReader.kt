package rome2

import binary.BinaryReader
import core.hexFull

/**
 * https://sourceforge.net/p/packfilemanager/code/HEAD/tree/trunk/Filetypes/DB/DBFileCodec.cs#l164
 */
class PackReader(private val reader: BinaryReader) {


	class PackedFileTable(val schemaTable: SchemaTable, val entries: List<Any>)



	fun read() {
		if(reader.ascii(4) != "PFH4") error("Expecting version PFH4")

		val flags = reader.u32()
		val packFileCount = reader.u32()
		val packFileIndexSize = reader.u32()
		val packedFileCount = reader.u32()
		val packedFileIndexSize = reader.u32()
		val timeStamp = reader.u32()

		println("""
			flags:               $flags
			packFileCount:       $packFileCount
			packFileIndexSize:   $packFileIndexSize
			packedFileCount:     $packedFileCount
			packedFileIndexSize: $packedFileIndexSize
			timeStamp:           $timeStamp
		""".trimIndent())

		// u32 size, char* path
		val headers = List(packedFileCount) { Pair(reader.u32(), reader.ascii()) }

		var pos = reader.pos

		var headerCount = 0
		for((size, path) in headers) {
			if(!path.startsWith("db")) {
				pos += size
				continue
			}

			if(headerCount++ == 26) break

			reader.pos = pos

			while(true) {
				when(reader.u8()) {
					1    -> break
					0xFD -> { reader.pos += 3; val length = reader.u16(); reader.pos += length * 2 }
					0xFC -> { reader.pos += 7 }
				}
			}

			val name = path.split('\\')[1]
			val table = Tables.SCHEMA.tables[name] ?: continue
			val numEntries = reader.u32()
			//println()
			println("TABLE: ${table.name}, version=${table.version}, pos=${pos.hexFull}, size=$size, numEntries=$numEntries")
			for(field in table.fields) {
				//println("$count ${reader.pos.hexFull} ${field.type} ${field.name}: ")
				//println("\t${field.decode()}")
				count++
			}

			pos += size
		}
	}



	private var count = 0



	private fun SchemaField.decode(): String = when(type) {
		SchemaFieldType.ASCII -> {
			val length = reader.u16()
			val string = reader.ascii(length)
			"\"$string\""
		}

		SchemaFieldType.STRING_OPTIONAL -> TODO()

		SchemaFieldType.BOOLEAN -> {
			val value = reader.u8() == 1
			value.toString()
		}

		SchemaFieldType.STRING -> {
			val length = reader.u16()
			val string = reader.string(length, Charsets.UTF_8)
			"\"$string\""
		}

		SchemaFieldType.FLOAT -> reader.f32().toString()

		SchemaFieldType.ASCII_OPTIONAL -> {
			if(reader.u8() == 0) {
				"null"
			} else {
				val length = reader.u16()
				val string = reader.ascii(length)
				"\"$string\""
			}
		}

		SchemaFieldType.LIST -> TODO()

		SchemaFieldType.INT -> reader.s32().toString()
	}

}
