package rome2

import binary.BinaryReader
import core.printHex

/**
 * https://sourceforge.net/p/packfilemanager/code/HEAD/tree/trunk/Filetypes/DB/DBFileCodec.cs#l164
 */
class PackReader(private val reader: BinaryReader) {


	data class PackedFileIndex(val size: Int, val path: String) {
		var pos = 0
	}



	data class PackedFileInfo(val path: String, val guid: String?, val version: Int)



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

		val packedFileIndices = List(packedFileCount) {
			val size = reader.u32()
			val path = reader.ascii()
			PackedFileIndex(size, path)
		}

		var dataPos = reader.pos
		for(p in packedFileIndices) {
			p.pos = dataPos
			dataPos += p.size
		}

		val dbIndices = packedFileIndices.filter { it.path.startsWith("db") }

		val infos = dbIndices.map { index ->
			reader.pos = index.pos

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

			PackedFileInfo(index.path, guid, version)
		}

		for(p in infos)
			println(p)

/*		for(packedFileIndex in packedFileIndices) {
			var marker = reader.u32().toUInt().toLong()

			var guid: ByteArray? = null
			var version = -1

			while(marker != 1L) {
				if(marker == 0xFF_FC_FE_FD) {
					val length = reader.u16()
					guid = reader.bytes(length * 2)
				} else if(marker == 0xFF_FE_FD_FC) {
					version = reader.u32()
				} else {
					reader.pos -= 3
					break
				}
			}
		}

		var version = 0

		println(packedFileIndices[0])

		while(true) {
			val marker = reader.u32().toUInt().toLong()

			if(marker == 0xFF_FC_FE_FD) {
				val length = reader.u16()
				val string = reader.ascii(length * 2)
				println("GUID: $string")
			} else if(marker == 0xFF_FE_FD_FC) {
				version = reader.u32()
				println("VERSION: $version")
			} else {
				reader.pos -= 3
				break
			}
		}*/

/*		val entryCount = reader.u32()
		println("entry count: $entryCount")
		val table = schema[packedFileIndices[0].path.split('\\')[1]]!!
		println("table: $table")

		for(field in table.fields) {
			when(field.type) {
				SchemaFieldType.ASCII -> println("ASCII")
				SchemaFieldType.STRING_OPTIONAL -> println("optional string")
				SchemaFieldType.BOOLEAN -> println("boolean")
				SchemaFieldType.STRING -> println("string")
				SchemaFieldType.FLOAT -> println("float")
				SchemaFieldType.ASCII_OPTIONAL -> println("optional ascii")
				SchemaFieldType.LIST -> println("list")
				SchemaFieldType.INT -> println("int")
			}
		}*/
	}


}
