package assembler

enum class Section {

	/** Section not yet chosen */
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