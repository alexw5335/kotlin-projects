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
		single(firstOpcode + 0, mnemonic, R8_R8)
		single(firstOpcode + 0, mnemonic, M8_R8)
		single(firstOpcode + 1, mnemonic, R16_R16)
		single(firstOpcode + 1, mnemonic, M16_R16)
		single(firstOpcode + 1, mnemonic, R32_R32)
		single(firstOpcode + 1, mnemonic, M32_R32)
		single(firstOpcode + 1, mnemonic, R64_R64)
		single(firstOpcode + 1, mnemonic, M64_R64)
		single(firstOpcode + 2, mnemonic, R8_M8)
		single(firstOpcode + 3, mnemonic, R16_M16)
		single(firstOpcode + 3, mnemonic, R32_M32)
		single(firstOpcode + 3, mnemonic, R64_M64)
		single(firstOpcode + 4, mnemonic, AL_IMM8)
		single(firstOpcode + 5, mnemonic, AX_IMM16)
		single(firstOpcode + 5, mnemonic, EAX_IMM32)
		single(firstOpcode + 5, mnemonic, RAX_IMM32)
		single(0x80, mnemonic, R8_IMM8, extension = extension)
		single(0x81, mnemonic, R16_IMM16, extension = extension)
		single(0x81, mnemonic, R32_IMM32, extension = extension)
		single(0x81, mnemonic, R64_IMM32, extension = extension)
		single(0x83, mnemonic, R16_IMM8, extension = extension)
		single(0x83, mnemonic, M16_IMM8, extension = extension)
		single(0x83, mnemonic, R32_IMM8, extension = extension)
		single(0x83, mnemonic, M32_IMM8, extension = extension)
		single(0x83, mnemonic, R64_IMM8, extension = extension)
		single(0x83, mnemonic, M64_IMM8, extension = extension)
	}



	private fun addGroup2(mnemonic: Mnemonic, extension: Int) {
		single(0xD0, mnemonic, R8_ONE, extension = extension)
		single(0xD0, mnemonic, M8_ONE, extension = extension)
		single(0xD2, mnemonic, R8_CL, extension = extension)
		single(0xD2, mnemonic, M8_CL, extension = extension)
		single(0xC0, mnemonic, R8_IMM8, extension = extension)
		single(0xC0, mnemonic, M8_IMM8, extension = extension)
		single(0xD1, mnemonic, R16_ONE, extension = extension)
		single(0xD1, mnemonic, M16_ONE, extension = extension)
		single(0xD1, mnemonic, R32_ONE, extension = extension)
		single(0xD1, mnemonic, M32_ONE, extension = extension)
		single(0xD1, mnemonic, R64_ONE, extension = extension)
		single(0xD1, mnemonic, M64_ONE, extension = extension)
		single(0xD1, mnemonic, R16_CL, extension = extension)
		single(0xD1, mnemonic, M16_CL, extension = extension)
		single(0xD1, mnemonic, R32_CL, extension = extension)
		single(0xD1, mnemonic, M32_CL, extension = extension)
		single(0xD1, mnemonic, R64_CL, extension = extension)
		single(0xD1, mnemonic, M64_CL, extension = extension)
		single(0xD1, mnemonic, R16_IMM8, extension = extension)
		single(0xD1, mnemonic, M16_IMM8, extension = extension)
		single(0xD1, mnemonic, R32_IMM8, extension = extension)
		single(0xD1, mnemonic, M32_IMM8, extension = extension)
		single(0xD1, mnemonic, R64_IMM8, extension = extension)
		single(0xD1, mnemonic, M64_IMM8, extension = extension)
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