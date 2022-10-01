package asm

enum class SRegister {

	CS,
	SS,
	DS,
	ES,
	FS,
	GS;

	val string = name.lowercase()

}