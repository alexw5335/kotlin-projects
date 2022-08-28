package rome2

import binary.BinaryReader
import core.binary.BinaryWriter
import core.xml.XmlParser
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths



enum class SchemaFieldType(val string: String) {

	ASCII("string_ascii"),
	STRING_OPTIONAL("optstring"),
	BOOLEAN("boolean"),
	STRING("string"),
	FLOAT("float"),
	ASCII_OPTIONAL("optstring_ascii"),
	LIST("list"),
	INT("int");

	companion object {
		val values = values()
		val map = values.associateBy { it.string }
	}

}



data class SchemaField(val name: String, val type: SchemaFieldType, val isKey: Boolean)

data class SchemaTable(val name: String, val version: Int, val guid: String?, val fields: List<SchemaField>)

data class Schema(val tables: Map<String, SchemaTable>)



fun readSchema(reader: BinaryReader): Schema {
	val tableCount = reader.u32()

	val tables = List(tableCount) {
		val tableNameLength = reader.u8()
		val tableName = reader.ascii(tableNameLength)
		val version = reader.u8()
		val guidLength = reader.u8()
		val guid = if(guidLength > 0) reader.ascii(guidLength) else null
		val fieldCount = reader.u8()

		val fields = List(fieldCount) {
			val fieldNameLength = reader.u8()
			val fieldName = reader.ascii(fieldNameLength)
			val fieldTypeByte = reader.u8()
			val fieldType = SchemaFieldType.values[fieldTypeByte and 0b01111111]
			val isKey = fieldTypeByte and 0b10000000 != 0

			SchemaField(fieldName, fieldType, isKey)
		}

		SchemaTable(tableName, version, guid, fields)
	}

	return Schema(tables.associateBy { it.name })
}



fun writeSchema(path: Path, schema: Schema) {
	val writer = BinaryWriter()
	writer.u32(schema.tables.size)

	for(table in schema.tables.values) {
		writer.u8(table.name.length)
		writer.ascii(table.name)
		writer.u8(table.version)
		writer.u8(table.guid?.length ?: 0)
		table.guid?.let { writer.ascii(it) }
		writer.u8(table.fields.size)

		for(field in table.fields) {
			writer.u8(field.name.length)
			writer.ascii(field.name)
			writer.u8(field.type.ordinal or ((if(field.isKey) 1 else 0) shl 7))
		}
	}

	Files.write(path, writer.trimmedBytes())
}




const val ROME_2_DATA_PACK_PATH =
	"C:\\Program Files (x86)\\Steam\\steamapps\\common\\Total War Rome II\\data\\data_rome2.pack"



fun readRome2Schemas(
	rome2DataPath: Path = Paths.get(ROME_2_DATA_PACK_PATH),
	masterSchemaPath: Path = Paths.get("master_schema.xml")
): Schema {
	val reader = BinaryReader(rome2DataPath)
	reader.pos += 16
	val packedFileCount = reader.u32()
	reader.pos += 8

	val indices = List(packedFileCount) { Pair(reader.u32(), reader.ascii()) }

	var dataPos = reader.pos

	class PackedFileInfo(val path: String, val guid: String?, val version: Int)

	val infos = ArrayList<PackedFileInfo>()

	for((size, path) in indices) {
		if(!path.startsWith("db")) {
			dataPos += size
			continue
		}

		reader.pos = dataPos
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

		dataPos += size

		infos.add(PackedFileInfo(path, guid, version))
	}

	val infoMap = infos.associateBy { it.path.split('\\')[1] }
	val schema = XmlParser.parse(masterSchemaPath)
	val tables = ArrayList<SchemaTable>()

	for(tableTag in schema.children) {
		val name = tableTag.attrib("table_name")
		val info = infoMap[name] ?: continue
		val version = tableTag.attrib("table_version").toInt()
		if(version != info.version) continue
		val fields = tableTag.children.map {
			SchemaField(
				name  = it.attrib("name"),
				type  = SchemaFieldType.map[it.attrib("type")]!!,
				isKey = it["pk"] != null
			)
		}

		tables.add(SchemaTable(name, version, info.guid, fields))
	}

	return Schema(tables.associateBy { it.name })
}