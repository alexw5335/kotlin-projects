package core.binary

sealed interface Endianness {


	fun s16(reader: BinaryReader): Int

	fun u16(reader: BinaryReader): Int

	fun s32(reader: BinaryReader): Int

	fun u32(reader: BinaryReader): Int

	fun s64(reader: BinaryReader): Long

	fun u64(reader: BinaryReader): Long

	fun f32(reader: BinaryReader): Float

	fun f64(reader: BinaryReader): Double



	fun s16(writer: BinaryWriter, value: Int)

	fun u16(writer: BinaryWriter, value: Int)

	fun s32(writer: BinaryWriter, value: Int)

	fun u32(writer: BinaryWriter, value: Int)

	fun s64(writer: BinaryWriter, value: Long)

	fun u64(writer: BinaryWriter, value: Long)

	fun f32(writer: BinaryWriter, value: Float)

	fun f64(writer: BinaryWriter, value: Double)



	fun s16(writer: BinaryWriter, pos: Int, value: Int)

	fun u16(writer: BinaryWriter, pos: Int, value: Int)

	fun s32(writer: BinaryWriter, pos: Int, value: Int)

	fun u32(writer: BinaryWriter, pos: Int, value: Int)

	fun s64(writer: BinaryWriter, pos: Int, value: Long)

	fun u64(writer: BinaryWriter, pos: Int, value: Long)

	fun f32(writer: BinaryWriter, pos: Int, value: Float)

	fun f64(writer: BinaryWriter, pos: Int, value: Double)


}