package rome2

import core.binary.BinaryWriter
import java.nio.file.Path

@Suppress("Unused")
class PackWriter(private val outputPath: Path, private val data: Map<Table, List<Rome2Object>>) {


	private val writer = BinaryWriter()



	fun write() {
		writer.ascii("PFH4")
		writer.u32(2) // flags/fileType
		writer.u32(0) // packFileCount
		writer.u32(0) // packFileIndexSize
		writer.u32(data.size) // packedFileCount
		writer.u32(0) // packedFileIndexSize
		writer.u32(1543933775) // timeStamp

		for(table in data.keys) {
			writer.u32(0)
			writer.ascii(table.name)
			writer.u8(0)
		}
	}


}