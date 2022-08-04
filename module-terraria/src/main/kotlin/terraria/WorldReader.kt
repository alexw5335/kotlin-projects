package terraria

import binary.BinaryReader
import core.Core

class WorldReader(private val reader: BinaryReader) {


	fun read() {
		reader.pos += 24
		val sectionPointers = IntArray(reader.u16()) { reader.u32() }
		val importantTileFrames = reader.bytes(reader.u16() / 8)

		reader.pos = sectionPointers[0]
		val worldName = reader.ascii(reader.u8())
		val worldSeed = reader.ascii(reader.u8())

	}


}