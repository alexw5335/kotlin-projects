package core.binary

object BigEndian : Endianness {


	override fun s16(reader: BinaryReader) = reader.s16BE()

	override fun u16(reader: BinaryReader) = reader.u16BE()

	override fun s32(reader: BinaryReader) = reader.s32BE()

	override fun u32(reader: BinaryReader) = reader.u32BE()

	override fun s64(reader: BinaryReader) = reader.s64BE()

	override fun u64(reader: BinaryReader) = reader.u64BE()

	override fun f32(reader: BinaryReader) = reader.f32BE()

	override fun f64(reader: BinaryReader) = reader.f64BE()



	override fun s16(writer: BinaryWriter, value: Int) = writer.s16BE(value)

	override fun u16(writer: BinaryWriter, value: Int) = writer.u16BE(value)

	override fun s32(writer: BinaryWriter, value: Int) = writer.s32BE(value)

	override fun u32(writer: BinaryWriter, value: Int) = writer.u32BE(value)

	override fun s64(writer: BinaryWriter, value: Long) = writer.s64BE(value)

	override fun u64(writer: BinaryWriter, value: Long) = writer.u64BE(value)

	override fun f32(writer: BinaryWriter, value: Float) = writer.f32BE(value)

	override fun f64(writer: BinaryWriter, value: Double) = writer.f64BE(value)



	override fun s16(writer: BinaryWriter, pos: Int, value: Int) = writer.s16BE(pos, value)

	override fun u16(writer: BinaryWriter, pos: Int, value: Int) = writer.u16BE(pos, value)

	override fun s32(writer: BinaryWriter, pos: Int, value: Int) = writer.s32BE(pos, value)

	override fun u32(writer: BinaryWriter, pos: Int, value: Int) = writer.u32BE(pos, value)

	override fun s64(writer: BinaryWriter, pos: Int, value: Long) = writer.s64BE(pos, value)

	override fun u64(writer: BinaryWriter, pos: Int, value: Long) = writer.u64BE(pos, value)

	override fun f32(writer: BinaryWriter, pos: Int, value: Float) = writer.f32BE(pos, value)

	override fun f64(writer: BinaryWriter, pos: Int, value: Double) = writer.f64BE(pos, value)


}