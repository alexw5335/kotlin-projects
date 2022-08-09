package assembler

class InstructionGroup(
	val list                : List<InstructionEncoding>,
	val encodingFlags       : Long,
	val specialisationFlags : Int
)