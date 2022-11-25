package eyre

import java.nio.file.Path

class SrcFile(
	val name     : Intern,
	val path     : Path,
	val relPath  : Path,
	val relParts : InternArray,
	val contents : CharArray
)