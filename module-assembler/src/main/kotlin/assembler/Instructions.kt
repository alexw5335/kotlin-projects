package assembler

import assembler.Mnemonic.*
import assembler.*
import assembler.Operands.*



object Instructions {


	private val rawMap = HashMap<Mnemonic, MutableList<InstructionEncoding>>()
	
	private val map = HashMap<Mnemonic, InstructionGroup>()

	fun get(mnemonic: Mnemonic) = map[mnemonic]

	
	
	private fun add(
		opcode     : Int,
		opcodeType : OpcodeType,
		mnemonic   : Mnemonic,
		extension  : Int,
		operands   : Operands
	) = rawMap.getOrPut(mnemonic, ::ArrayList).add(InstructionEncoding(
		opcode,
		opcodeType,
		extension,
		operands
	))
	
	

	private fun single(
		opcode    : Int,
		mnemonic  : Mnemonic,
		operands  : Operands,
		extension : Int = 0
	) = add(opcode, OpcodeType.SINGLE, mnemonic, 0, operands)
	
	
	
	private fun double(
		opcode: Int,
		mnemonic: Mnemonic,
		operands: Operands,
		extension: Int = 0
	) = add(opcode, OpcodeType.DOUBLE, mnemonic, 0, operands)



	private fun addGroup1(mnemonic: Mnemonic, firstOpcode: Int, extension: Int) {
		single(firstOpcode + 0, mnemonic, RM8_R8)
		single(firstOpcode + 1, mnemonic, RM_R)
		single(firstOpcode + 2, mnemonic, R8_RM8)
		single(firstOpcode + 3, mnemonic, R_RM)
		single(firstOpcode + 4, mnemonic, AL_IMM8)
		single(firstOpcode + 5, mnemonic, A_IMM)
		single(0x80, mnemonic, RM8_IMM8, extension = extension)
		single(0x81, mnemonic, RM_IMM, extension = extension)
		single(0x83, mnemonic, RM_IMM8, extension = extension)
	}



	private fun addGroup2(mnemonic: Mnemonic, extension: Int) {
		single(0xD0, mnemonic, RM8_ONE,  extension = extension)
		single(0xD2, mnemonic, RM8_CL,   extension = extension)
		single(0xC0, mnemonic, RM8_IMM8, extension = extension)
		single(0xD1, mnemonic, RM_ONE,   extension = extension)
		single(0xD3, mnemonic, RM_CL,    extension = extension)
		single(0xC1, mnemonic, RM_IMM8,  extension = extension)
	}



	private fun addEncodings() {
		addGroup1(ADD, 0x00, 0)
		addGroup1(OR,  0x08, 1)
		addGroup1(ADC, 0x10, 2)
		addGroup1(SBB, 0x18, 3)
		addGroup1(AND, 0x20, 4)
		addGroup1(SUB, 0x28, 5)
		addGroup1(XOR, 0x30, 6)
		addGroup1(CMP, 0x38, 7)

		addGroup2(ROL, 0)
		addGroup2(ROR, 1)
		addGroup2(RCL, 2)
		addGroup2(RCR, 3)
		addGroup2(SHL, 4)
		addGroup2(SHR, 5)
		addGroup2(SAR, 7)
	}
	
	
	
	init {
		addEncodings()
		
		for((mnemonic, encodings) in rawMap) {
			val list = encodings.sortedBy { it.operands }
			var encodingFlags = 0L
			var specialisationFlags = 0

			for(encoding in encodings) {
				encodingFlags = encodingFlags or encoding.operands.bit
				specialisationFlags = specialisationFlags or encoding.operands.specialisation.bit
			}

			map[mnemonic] = InstructionGroup(list, encodingFlags, specialisationFlags)
		}
	}


}