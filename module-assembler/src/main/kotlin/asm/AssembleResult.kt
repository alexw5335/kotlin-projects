package asm

class AssembleResult(
	val text        : ByteArray,
	val imports     : List<DllImport>,
	val relocations : List<Relocation>
)