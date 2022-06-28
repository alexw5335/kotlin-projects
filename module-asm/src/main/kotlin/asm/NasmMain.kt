package asm

import core.hex8
import core.Processes
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.writeText



const val input = """
	pop qword fs
"""



fun main() {
	val temp = Files.createFile(Paths.get("nasmTemp.asm"))
	temp.writeText(input)
	val succeeded = Processes.run("nasm -fwin64 nasmTemp.asm -o nasmTemp.obj")
	Files.delete(temp)
	if(!succeeded) return
	val path = Paths.get("nasmTemp.obj")
	val bytes = Files.readAllBytes(path)
	val data = bytes.copyOfRange(bytes.int32(40), bytes.int32(40) + bytes.int32(36))
	data.forEach { println(it.hex8) }
	Files.delete(path)
}



private fun ByteArray.int32(pos: Int) =
	(this[pos].toInt()      shl 0) or
	(this[pos + 1].toInt()  shl 8) or
	(this[pos + 2].toInt()  shl 16) or
	(this[pos + 3].toInt()  shl 24)