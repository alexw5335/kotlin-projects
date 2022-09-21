package core

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*
import kotlin.system.exitProcess

@Suppress("Unused", "MemberVisibilityCanBePrivate")
object Core {


	fun run(command: String, output: Boolean = true): Boolean {
		val process = Runtime.getRuntime().exec(command)
		process.waitFor()

		val errorText = process.errorReader().readText()
		if(errorText.isNotEmpty() && output) {
			print("\u001B[31m")
			print(errorText)
			print("\u001B[0m")
			return false
		}

		val outputText = process.inputReader().readText()
		if(outputText.isNotEmpty() && output)
			print(outputText)

		return true
	}



	fun runOrExit(command: String) {
		if(!run(command)) exitProcess(1)
	}



	fun resourceBufferedReader(path: String) = this::class.java.getResourceAsStream(path)!!.bufferedReader()

	fun readResourceText(path: String) = this::class.java
		.getResourceAsStream(path)!!
		.reader()
		.readText()
		.toCharArray()



	fun readResourceLines(path: String) = this::class.java
		.getResourceAsStream(path)!!
		.bufferedReader()
		.readLines()



	fun readResourceBytes(path: String) = this::class.java
		.getResourceAsStream(path)!!
		.readAllBytes()



	fun nasmBuild(src: String, out: String, vararg args: String) {
		runOrExit("nasm -fwin64 $src.asm -o $out.obj ${args.joinToString(" ") }")
		runOrExit("gcc $out.obj -o $out.exe")
	}



	fun nasmRun(src: String, out: String, vararg args: String) {
		nasmBuild(src, out, *args)
		runOrExit("./$out.exe")
	}


	fun nasmBuild(directory: String, fileName: String) {
		val outDir = Paths.get("$directory/out")

		if(!outDir.exists()) outDir.createDirectory()

		val srcDir = Paths.get("$directory/src")

		val includes = srcDir
			.listDirectoryEntries()
			.filter { it.isDirectory() }
			.map { it.toString() }
			.toMutableList()
			.also { it.add(srcDir.toString()) }
			.joinToString(separator = " -I ", prefix = "-I ")

		nasmBuild("$directory/src/$fileName", "$directory/out/$fileName", includes)
	}



	fun nasmRun(directory: String, fileName: String) {
		nasmBuild(directory, fileName)
		runOrExit("./out.exe")
	}



	fun nasmAssemble(code: String): ByteArray {
		fun ByteArray.int32(pos: Int) =
			this[pos].toInt() or
				(this[pos + 1].toInt() shl 8) or
				(this[pos + 2].toInt() shl 16) or
				(this[pos + 3].toInt() shl 24)

		val temp = Files.createFile(Paths.get("nasmTemp.asm"))
		temp.writeText(code)
		try {
			runOrExit("nasm -fwin64 nasmTemp.asm -o nasmTemp.obj")
		} finally {
			Files.delete(temp)
		}
		val path = Paths.get("nasmTemp.obj")
		val bytes = Files.readAllBytes(path)
		Files.delete(path)
		return bytes.copyOfRange(bytes.int32(40), bytes.int32(40) + bytes.int32(36))
	}



	fun nasmPrint(code: String) {
		nasmAssemble(code).forEach { println("${it.hex8}  ${it.bin233}") }
	}



	fun toPackedInts(bytes: ByteArray): IntArray {
		val ints = IntArray(bytes.size shr 2)

		for(i in bytes.indices)
			ints[i shr 2] = ints[i shr 2] or (bytes[i].toInt() shl ((4 - (i + 3) and -4) shl 3))

		return ints
	}



	fun writeLines(path: Path, lines: List<String>) {
		if(!path.parent.exists())
			path.parent.createDirectories()
		Files.write(path, lines)
	}



	fun writeLines(path: String, lines: List<String>) {
		writeLines(Paths.get(path), lines)
	}


}