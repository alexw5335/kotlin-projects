package pefile



inline fun DllFlags(block: DllFlags.Companion.() -> DllFlags) = block(DllFlags)

inline fun CoffFlags(block: CoffFlags.Companion.() -> CoffFlags) = block(CoffFlags)

inline fun SectionFlags(block: SectionFlags.Companion.() -> SectionFlags) = block(SectionFlags.Companion)



/**
 * Wrapper type for the optional header dllCharacteristics field.
 */
@JvmInline
value class DllFlags(val value: Int) {


	companion object {

		/**
		 * Image can handle a high-entropy 64-bit virtual address space.
		 */
		val HIGH_ENTROPY_VA = DllFlags(1 shl 5)

		/**
		 * DLL can be relocated at load time.
		 */
		val DYNAMIC_BASE = DllFlags(1 shl 6)

		/**
		 * Code integrity checks are enforced.
		 */
		val FORCE_INTEGRITY = DllFlags(1 shl 7)

		/**
		 * Iamge is NX compatible.
		 */
		val NX_COMPAT = DllFlags(1 shl 8)

		/**
		 * Isolation aware, but do not isolate image.
		 */
		val NO_ISOLATION = DllFlags(1 shl 9)

		/**
		 * Does not use structured exception handling. No SE handler may be called in this image.
		 */
		val NO_SEH = DllFlags(1 shl 10)

		/**
		 * Do not bind the image.
		 */
		val NO_BIND = DllFlags(1 shl 11)

		/**
		 * Image must execute in an AppContainer
		 */
		val APP_CONTAINER = DllFlags(1 shl 12)

		/**
		 * A WDM driver
		 */
		val WDM_DRIVER = DllFlags(1 shl 13)

		/**
		 * Image supports Control Flow Guard.
		 */
		val GUARD_CF = DllFlags(1 shl 14)

		/**
		 * Terminal Server aware.
		 */
		val TERMINAL_SERVER_AWARE = DllFlags(1 shl 15)

	}



	operator fun plus(other: DllFlags) = DllFlags(value or other.value)

	operator fun contains(other: DllFlags) = value and other.value == other.value

	override fun toString() = buildString {
		append("{ ")
		if(contains(HIGH_ENTROPY_VA)) append("HIGH_ENTROPY_VA, ")
		if(contains(DYNAMIC_BASE)) append("DYNAMIC_BASE, ")
		if(contains(FORCE_INTEGRITY)) append("FORCE_INTEGRITY, ")
		if(contains(NX_COMPAT)) append("NX_COMPAT, ")
		if(contains(NO_ISOLATION)) append("NO_ISOLATION, ")
		if(contains(NO_SEH)) append("NO_SEH, ")
		if(contains(NO_BIND)) append("NO_BIND, ")
		if(contains(APP_CONTAINER)) append("APP_CONTAINER, ")
		if(contains(WDM_DRIVER)) append("WDM_DRIVER, ")
		if(contains(GUARD_CF)) append("GUARD_CF, ")
		if(contains(TERMINAL_SERVER_AWARE)) append("TERMINAL_SERVER_AWARE, ")
		if(length == 2) append("NONE") else setLength(length - 2)
		append(" }")
	}


}



/**
 * Wrapper type for the COFF header characteristics field
 */
@JvmInline
value class CoffFlags(val value: Int) {


	companion object {

		/**
		 * Image only, Windows CE, and Microsoft Windows NT and later. This indicates that the file does not contain
		 * base relocations and must therefore be loaded at its preferred base address. If the base address is not
		 * available, the loader reports an error. The default behavior of the linker is to strip base relocations from
		 * executable (EXE) files.
		 */
		val RELOCS_STRIPPED = CoffFlags(1 shl 0)

		/**
		 * Image only. This indicates that the image file is valid and can be run. If this flag is not set, it indicates
		 * a linker error.
		 */
		val EXECUTABLE_IMAGE = CoffFlags(1 shl 1)

		/**
		 * Deprecated, should be zero.
		 */
		val LINE_NUMBERS_STRIPPED = CoffFlags(1 shl 2)

		/**
		 * Deprecated, should be zero.
		 */
		val LOCAL_SYMBOLS_STRIPPED = CoffFlags(1 shl 3)

		/**
		 * Deprecated, should be zero.
		 */
		val AGGRESSIVE_WS_TRIM = CoffFlags(1 shl 4)

		/**
		 * Application can handle > 2GB addresses.
		 */
		val LARGE_ADDRESS_AWARE = CoffFlags(1 shl 5)

		/**
		 * Deprecated, should be zero.
		 */
		val BYTES_REVERSED_LOW = CoffFlags(1 shl 7)

		/**
		 * Machine is based on a 32-bit-word architecture.
		 */
		val MACHINE_32_BIT = CoffFlags(1 shl 8)

		/**
		 * Debugging information is removed from the image file.
		 */
		val DEBUG_STRIPPED = CoffFlags(1 shl 9)

		/**
		 * If the image is on removable media, fully load it and copy it to the swap file.
		 */
		val REMOVABLE_RUN_FROM_SWAP = CoffFlags(1 shl 10)

		/**
		 * If the image is on network media, fully load it and copy it to the swap file.
		 */
		val NET_RUN_FROM_SWAP = CoffFlags(1 shl 11)

		/**
		 * The image file is a system file, not a user program.
		 */
		val SYSTEM = CoffFlags(1 shl 12)

		/**
		 * The image file is a dynamic-link library (DLL). Such files are considered executable files for almost all
		 * purposes, although they cannot be directly run.
		 */
		val DLL = CoffFlags(1 shl 13)

		/**
		 * The file should be run only on a uni-processor machine.
		 */
		val SYSTEM_ONLY = CoffFlags(1 shl 14)

		/**
		 * Deprecated, should be zero.
		 */
		val BYTES_REVERSED_HIGH = CoffFlags(1 shl 15)

	}



	operator fun plus(other: CoffFlags) = CoffFlags(value or other.value)

	operator fun contains(other: CoffFlags) = value and other.value == other.value


	
	override fun toString() = buildString {
		append("{ ")
		if(contains(RELOCS_STRIPPED)) append("RELOCS_STRIPPED, ")
		if(contains(EXECUTABLE_IMAGE)) append("EXECUTABLE_IMAGE, ")
		if(contains(LINE_NUMBERS_STRIPPED)) append("LINE_NUMBERS_STRIPPED, ")
		if(contains(LOCAL_SYMBOLS_STRIPPED)) append("LOCAL_SYMBOLS_STRIPPED, ")
		if(contains(AGGRESSIVE_WS_TRIM)) append("AGGRESSIVE_WS_TRIM, ")
		if(contains(LARGE_ADDRESS_AWARE)) append("LARGE_ADDRESS_AWARE, ")
		if(contains(BYTES_REVERSED_LOW)) append("BYTES_REVERSED_LOW, ")
		if(contains(MACHINE_32_BIT)) append("MACHINE_32_BIT, ")
		if(contains(DEBUG_STRIPPED)) append("DEBUG_STRIPPED, ")
		if(contains(REMOVABLE_RUN_FROM_SWAP)) append("REMOVABLE_RUN_FROM_SWAP, ")
		if(contains(NET_RUN_FROM_SWAP)) append("NET_RUN_FROM_SWAP, ")
		if(contains(SYSTEM)) append("SYSTEM, ")
		if(contains(DLL)) append("DLL, ")
		if(contains(SYSTEM_ONLY)) append("SYSTEM_ONLY, ")
		if(contains(BYTES_REVERSED_HIGH)) append("BYTES_REVERSED_HIGH, ")
		if(length == 2) append("NONE") else setLength(length - 2)
		append(" }")
	}


}



/**
 * Wrapper type for the section header characteristics field.
 */
@JvmInline
value class SectionFlags(val value: Int) {


	companion object {

		/**
		 * This section should not be padded to the next boundary. This flag is obsolete. Valid only for object files.
		 */
		val NO_PAD = SectionFlags(1 shl 3)

		/**
		 * The section contains executable code.
		 */
		val CODE = SectionFlags(1 shl 5)

		/**
		 * The section contains initialised data.
		 */
		val INITIALISED_DATA = SectionFlags(1 shl 6)

		/**
		 * The section contains uninitialised data.
		 */
		val UNINITIALISED_DATA = SectionFlags(1 shl 6)

		/**
		 * The section contains comments or other information. The .drectve section has this type. Valid for object
		 * files only.
		 */
		val INFO = SectionFlags(1 shl 7)

		/**
		 * The section will not become part of the image. Valid only for object files.
		 */
		val REMOVE = SectionFlags(1 shl 9)

		/**
		 * The section contains COMDAT data. Valid only for object files.
		 */
		val COMDAT = SectionFlags(1 shl 10)

		/**
		 * The section contains data referenced through the global pointer (GP).
		 */
		val GPREL = SectionFlags(1 shl 13)

		/**
		 * Align data on a 1-byte boundary. Valid only for object files.
		 */
		val ALIGN_1 = SectionFlags(0x00100000)

		/**
		 * Align data on a 2-byte boundary. Valid only for object files.
		 */
		val ALIGN_2 = SectionFlags(0x00200000)

		/**
		 * Align data on a 4-byte boundary. Valid only for object files.
		 */
		val ALIGN_4 = SectionFlags(0x00300000)

		/**
		 * Align data on a 8-byte boundary. Valid only for object files.
		 */
		val ALIGN_8 = SectionFlags(0x00400000)

		/**
		 * Align data on a 16-byte boundary. Valid only for object files.
		 */
		val ALIGN_16 = SectionFlags(0x00500000)

		/**
		 * Align data on a 32-byte boundary. Valid only for object files.
		 */
		val ALIGN_32 = SectionFlags(0x00600000)

		/**
		 * Align data on a 64-byte boundary. Valid only for object files.
		 */
		val ALIGN_64 = SectionFlags(0x00700000)

		/**
		 * Align data on a 128-byte boundary. Valid only for object files.
		 */
		val ALIGN_128 = SectionFlags(0x00800000)

		/**
		 * Align data on a 256-byte boundary. Valid only for object files.
		 */
		val ALIGN_256 = SectionFlags(0x00900000)

		/**
		 * Align data on a 512-byte boundary. Valid only for object files.
		 */
		val ALIGN_512 = SectionFlags(0x00A00000)
		
		/**
		 * Align data on a 1024-byte boundary. Valid only for object files.
		 */
		val ALIGN_1024 = SectionFlags(0x00B00000)
		
		/**
		 * Align data on a 2048-byte boundary. Valid only for object files.
		 */
		val ALIGN_2048 = SectionFlags(0x00C00000)
		
		/**
		 * Align data on a 4096-byte boundary. Valid only for object files.
		 */
		val ALIGN_4096 = SectionFlags(0x00D00000)

		/**
		 * Align data on a 8192-byte boundary. Valid only for object files.
		 */
		val ALIGN_8192 = SectionFlags(0x00E00000)

		/**
		 * Section contains extended relocations.
		 */
		val NRELOC_OVFL = SectionFlags(1 shl 24)

		/**
		 * Section can be discarded as needed.
		 */
		val DISCARDABLE = SectionFlags(1 shl 25)

		/**
		 * Section cannot be cached.
		 */
		val NOT_CACHED = SectionFlags(1 shl 26)

		/**
		 * Section is not pageable.
		 */
		val NOT_PAGED = SectionFlags(1 shl 27)

		/**
		 * Section can be shared in memory.
		 */
		val SHARED = SectionFlags(1 shl 28)

		/**
		 * Section can be executed as code.
		 */
		val EXECUTE = SectionFlags(1 shl 29)

		/**
		 * Section can be read.
		 */
		val READ = SectionFlags(1 shl 30)

		/**
		 * Section can be written to.
		 */
		val WRITE = SectionFlags(1 shl 31)

	}



	operator fun plus(other: SectionFlags) = SectionFlags(value or other.value)

	operator fun contains(other: SectionFlags) = value and other.value == other.value



	override fun toString() = buildString {
		append("{ ")
		if(contains(NO_PAD)) append("NO_PAD, ")
		if(contains(CODE)) append("CODE, ")
		if(contains(INITIALISED_DATA)) append("INITIALISED_DATA, ")
		if(contains(UNINITIALISED_DATA)) append("UNINITIALISED_DATA, ")
		if(contains(INFO)) append("INFO, ")
		if(contains(REMOVE)) append("REMOVE, ")
		if(contains(COMDAT)) append("COMDAT, ")
		if(contains(GPREL)) append("GPREL, ")
		if(contains(ALIGN_1)) append("ALIGN_1, ")
		if(contains(ALIGN_2)) append("ALIGN_2, ")
		if(contains(ALIGN_4)) append("ALIGN_4, ")
		if(contains(ALIGN_8)) append("ALIGN_8, ")
		if(contains(ALIGN_16)) append("ALIGN_16, ")
		if(contains(ALIGN_32)) append("ALIGN_32, ")
		if(contains(ALIGN_64)) append("ALIGN_64, ")
		if(contains(ALIGN_128)) append("ALIGN_128, ")
		if(contains(ALIGN_256)) append("ALIGN_256, ")
		if(contains(ALIGN_512)) append("ALIGN_512, ")
		if(contains(ALIGN_1024)) append("ALIGN_1024, ")
		if(contains(ALIGN_2048)) append("ALIGN_2048, ")
		if(contains(ALIGN_4096)) append("ALIGN_4096, ")
		if(contains(ALIGN_8192)) append("ALIGN_8192, ")
		if(contains(NRELOC_OVFL)) append("NRELOC_OVFL, ")
		if(contains(DISCARDABLE)) append("DISCARDABLE, ")
		if(contains(NOT_CACHED)) append("NOT_CACHED, ")
		if(contains(NOT_PAGED)) append("NOT_PAGED, ")
		if(contains(SHARED)) append("SHARED, ")
		if(contains(EXECUTE)) append("EXECUTE, ")
		if(contains(READ)) append("READ, ")
		if(contains(WRITE)) append("WRITE, ")
		if(length == 2) append("NONE") else setLength(length - 2)
		append(" }")
	}


}



enum class StorageClass(val value: Int) {

	END_OF_FUNCTION(-1),
	NULL(0),
	AUTOMATIC(1),
	EXTERNAL(2),
	STATIC(3),
	REGISTER(4),
	EXTERNAL_DEF(5),
	LABEL(6),
	UNDEFINED_LABEL(7),
	MEMBER_OF_STRUCT(8),
	ARGUMENT(9),
	STRUCT_TAG(10),
	MEMBER_OF_UNION(11),
	UNION_TAG(12),
	TYPE_DEFINITION(13),
	UNDEFINED_STATIC(14),
	ENUM_TAG(15),
	MEMBER_OF_ENUM(16),
	REGISTER_PARAM(17),
	BIT_FIELD(18),
	BLOCK(100),
	FUNCTION(101),
	END_OF_STRUCT(102),
	FILE(103),
	SECTION(104),
	WEAK_EXTERNAL(105),
	CLR_TOKEN(107);

	companion object {
		val values = values()
		fun get(value: Int) = values().first { it.value == value }
	}

}