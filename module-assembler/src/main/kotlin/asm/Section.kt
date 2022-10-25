package asm

enum class Section {

	/** Not yet chosen */
	NONE,

	/** .text, initialised/code */
	TEXT,

	/** .rdata or .rodata, initialised, read */
	RDATA,

	/** .data, initialised, read/write */
	DATA,

	/** .data, uninitialised, read/write */
	BSS;

}