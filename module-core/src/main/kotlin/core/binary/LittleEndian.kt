package core.binary

object LittleEndian : Endianness {


	override fun s16(reader: BinaryReader) = reader.s16LE()

	override fun u16(reader: BinaryReader) = reader.u16LE()

	override fun s32(reader: BinaryReader) = reader.s32LE()

	override fun u32(reader: BinaryReader) = reader.u32LE()

	override fun s64(reader: BinaryReader) = reader.s64LE()

	override fun u64(reader: BinaryReader) = reader.u64LE()

	override fun f32(reader: BinaryReader) = reader.f32LE()

	override fun f64(reader: BinaryReader) = reader.f64LE()



	override fun s16(writer: BinaryWriter, value: Int) = writer.s16LE(value)

	override fun u16(writer: BinaryWriter, value: Int) = writer.u16LE(value)

	override fun s32(writer: BinaryWriter, value: Int) = writer.s32LE(value)

	override fun u32(writer: BinaryWriter, value: Int) = writer.u32LE(value)

	override fun s64(writer: BinaryWriter, value: Long) = writer.s64LE(value)

	override fun u64(writer: BinaryWriter, value: Long) = writer.u64LE(value)

	override fun f32(writer: BinaryWriter, value: Float) = writer.f32LE(value)

	override fun f64(writer: BinaryWriter, value: Double) = writer.f64LE(value)



	override fun s16(writer: BinaryWriter, pos: Int, value: Int) = writer.s16LE(pos, value)

	override fun u16(writer: BinaryWriter, pos: Int, value: Int) = writer.u16LE(pos, value)

	override fun s32(writer: BinaryWriter, pos: Int, value: Int) = writer.s32LE(pos, value)

	override fun u32(writer: BinaryWriter, pos: Int, value: Int) = writer.u32LE(pos, value)

	override fun s64(writer: BinaryWriter, pos: Int, value: Long) = writer.s64LE(pos, value)

	override fun u64(writer: BinaryWriter, pos: Int, value: Long) = writer.u64LE(pos, value)

	override fun f32(writer: BinaryWriter, pos: Int, value: Float) = writer.f32LE(pos, value)

	override fun f64(writer: BinaryWriter, pos: Int, value: Double) = writer.f64LE(pos, value)


}