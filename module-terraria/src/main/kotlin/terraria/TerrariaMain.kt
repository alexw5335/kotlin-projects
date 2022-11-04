package terraria

import core.binary.BinaryReader
import core.Core
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
	convertToMasterMode("/Parity_plot.wld")
}



fun convertToMasterMode(path: String) {
	val reader = BinaryReader(Core.readResourceBytes(path))
	reader.pos += 24
	val sectionPointers = IntArray(reader.u16()) { reader.u32() }
	reader.pos = sectionPointers[0]
	reader.pos += 1 + reader.u8()
	reader.pos += 1 + reader.u8()
	reader.pos += 16 + 8 + 4 + 4 + 4 + 4 + 4 + 4 + 4
	val gameModePos = reader.pos
	val bytes = reader.bytes
	bytes[gameModePos] = 2
	Files.write(Paths.get("output.wld"), bytes)
}