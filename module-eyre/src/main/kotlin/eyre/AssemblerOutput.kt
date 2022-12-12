package eyre

class AssemblerOutput(
	val text        : ByteArray,
	val data        : ByteArray,
	val bssSize     : Int,
	val relocations : List<Relocation>
)