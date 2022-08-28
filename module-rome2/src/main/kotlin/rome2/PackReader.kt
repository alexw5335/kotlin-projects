package rome2

import binary.BinaryReader

/**
 * https://sourceforge.net/p/packfilemanager/code/HEAD/tree/trunk/Filetypes/DB/DBFileCodec.cs#l164
 */
class PackReader(private val reader: BinaryReader) {


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

		for((size, path) in headers) {
			if(!path.startsWith("db")) break

			var guid: String? = null
			var version = 0

			while(true) {
				when(reader.u8()) {
					1 -> break
					0xFD -> {
						reader.pos += 3
						val guidLength = reader.u16()
						guid = reader.string(guidLength, Charsets.UTF_16LE)
					}
					0xFC -> {
						reader.pos += 3
						version = reader.u32()
					}
				}
			}

			val name = path.split('\\')[1]
			val table = Tables.SCHEMA.tables[name] ?: continue
			println(table)
			val numEntries = reader.u32()
			println("num entries: $numEntries")
			for(field in table.fields)
				field.decode()
		}
	}


	private fun SchemaField.decode() {
		when(type) {
			SchemaFieldType.ASCII -> {
				val length = reader.u16()
				val string = reader.ascii(length)
				println("string $name = \"$string\"")
			}
			SchemaFieldType.STRING_OPTIONAL -> TODO()
			SchemaFieldType.BOOLEAN -> {
				val value = reader.u8() == 1
				println("boolean $name = $value")
			}
			SchemaFieldType.STRING -> TODO()
			SchemaFieldType.FLOAT -> TODO()
			SchemaFieldType.ASCII_OPTIONAL -> {
				if(reader.u8() == 0) {
					println("string? $name = null")
					return
				}
				val length = reader.u16()
				val string = reader.ascii(length)
				println("string? $name = \"$string\"")
			}
			SchemaFieldType.LIST -> TODO()
			SchemaFieldType.INT -> {
				val value = reader.u32()
				println("u32 $name = $value")
			}
		}
	}


}
