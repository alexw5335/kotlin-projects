package rome2

import core.*
import core.binary.BinaryReader
import java.nio.file.Paths

/**
 * https://sourceforge.net/p/packfilemanager/code/HEAD/tree/trunk/Filetypes/DB/DBFileCodec.cs#l164
 */
class PackReader(private val reader: BinaryReader) {


	private val schema = readSchema(BinaryReader(Core.readResourceBytes("/schema.bin")))



	fun read(): List<PackTable> {
		if(reader.ascii(4) != "PFH4") error("Expecting version PFH4")

		val flags = reader.u32()
		val packFileCount = reader.u32()
		val packFileIndexSize = reader.u32()
		val packedFileCount = reader.u32()
		val packedFileIndexSize = reader.u32()
		val timeStamp = reader.u32()

		val headers = List(packedFileCount) { Pair(reader.u32(), reader.asciiNt()) }
		val tables = ArrayList<PackTable>()

		for((size, path) in headers) {
			if(!path.startsWith("db")) {
				reader.pos += size
				continue
			}

			val name = path.split('\\')[1]
			val table = schema.tables[name]

			if(table == null || name.startsWith("models_")) {
				reader.pos += size
				continue
			}

			while(true) {
				when(reader.u8()) {
					1    -> break
					0xFD -> { reader.pos += 3; val length = reader.u16(); reader.pos += length * 2 }
					0xFC -> { reader.pos += 7 }
				}
			}

			val numEntries = reader.u32()

			val entries = List(numEntries) {
				PackEntry(table.fields.map { it.decode() })
			}

			tables.add(PackTable(entries, table))
		}

		return tables
	}



	private fun SchemaField.decode(): PackField = when(type) {
		SchemaFieldType.ASCII -> {
			val length = reader.u16()
			val string = reader.string(length, Charsets.UTF_8)
			PackFieldString(string)
		}

		SchemaFieldType.ASCII_OPTIONAL -> {
			if(reader.u8() == 0) {
				PackFieldString("")
			} else {
				val length = reader.u16()
				val string = reader.string(length, Charsets.UTF_8)
				PackFieldString(string)
			}
		}

		SchemaFieldType.STRING -> {
			val length = reader.u16()
			val string = reader.string(length * 2, Charsets.UTF_16LE)
			PackFieldString(string)
		}

		SchemaFieldType.STRING_OPTIONAL -> {
			if(reader.u8() == 0) {
				PackFieldString("")
			} else {
				val length = reader.u16()
				val string = reader.string(length * 2, Charsets.UTF_16LE)
				PackFieldString(string)
			}
		}

		SchemaFieldType.BOOLEAN -> PackFieldBoolean(reader.u8() == 1)

		SchemaFieldType.FLOAT -> PackFieldFloat(reader.f32())

		SchemaFieldType.INT -> PackFieldInt(reader.s32())

		SchemaFieldType.LIST -> error("List types unsupported")
	}



	fun readPrint() {
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
		val headers = List(packedFileCount) { Pair(reader.u32(), reader.asciiNt()) }

		val tables = ArrayList<PackTable>()

		var tableIndex = 0
		for((size, path) in headers) {
			if(!path.startsWith("db")) {
				reader.pos += size
				continue
			}

			val name = path.split('\\')[1]
			val table = schema.tables[name]

			if(table == null || name.startsWith("models_")) {
				reader.pos += size
				continue
			}

			if(tableIndex > 900) break

			var version = 0
			while(true) {
				when(reader.u8()) {
					1    -> break
					0xFD -> { reader.pos += 3; val length = reader.u16(); reader.pos += length * 2 }
					0xFC -> { reader.pos += 3; version = reader.u32() }
				}
			}

			val numEntries = reader.u32()
			println()
			println("TABLE ${tableIndex++}: ${table.name}, version=$version, pos=${reader.pos.hexFull}, size=$size, numEntries=$numEntries")
			if(numEntries != 0) {
				for(i in 0 until numEntries) {
					if(i == 0) {
						for(field in table.fields) {
							val pos = reader.pos
							println("\t${pos.hex32} ${field.type} ${field.name} = ")
							print("\t\t")
							println(field.decode())
						}
					} else {
						for(field in table.fields) field.decode()
					}
				}
			}
		}
	}



	private fun String.check(): String {
		// Edge case for "ACHIEVEMENT_INV_CONQUER_CARTHAGE"
		val string = if(lastOrNull() == Char(0xA0))
			trimEnd(Char(0xc2), Char(0xa0))
		else
			this

		string.firstOrNull {
			(it.code == 0 || it.code > 255) &&
			it.code != 0x2026 && it.code != 0x2013 && it.code != 0x201c  && it.code != 0x201d && it.code != 0x2019
		}?.let {
			error("Invalid char (${it.code.hex16}) at end pos ${reader.pos.hex} in string: \"$string\"")
		}

		return string
	}



	private fun SchemaField.decodePrint(): String = when(type) {
		SchemaFieldType.ASCII -> {
			val length = reader.u16()
			val string = reader.string(length, Charsets.UTF_8).check()
			"\"$string\""
		}

		SchemaFieldType.ASCII_OPTIONAL -> {
			if(reader.u8() == 0) {
				"null"
			} else {
				val length = reader.u16()
				val string = reader.string(length, Charsets.UTF_8).check()
				"\"$string\""
			}
		}

		SchemaFieldType.STRING -> {
			val length = reader.u16()
			val string = reader.string(length * 2, Charsets.UTF_16LE).check()
			"\"$string\""
		}

		SchemaFieldType.STRING_OPTIONAL -> {
			if(reader.u8() == 0) {
				"null"
			} else {
				val length = reader.u16()
				val string = reader.string(length * 2, Charsets.UTF_16LE).check()
				"\"$string\""
			}
		}

		SchemaFieldType.BOOLEAN -> if(reader.u8() == 1) "true" else "false"

		SchemaFieldType.FLOAT -> reader.f32().toString()

		SchemaFieldType.INT -> reader.s32().toString()

		SchemaFieldType.LIST -> error("List types unsupported")
	}


}
