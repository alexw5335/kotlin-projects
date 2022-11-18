package assembler



class Intern(val id: Int, val type: InternType, val name: String) {
	override fun hashCode() = id
	override fun equals(other: Any?) = other === this
	override fun toString() = name
}



enum class InternType {
	NONE,
	KEYWORD,
	WIDTH,
	VAR_WIDTH,
	MNEMONIC,
	REGISTER,
	PREFIX;
}



object Interning {


	private var count = 0

	private val map = HashMap<String, Intern>()

	fun add(type: InternType, string: String) = Intern(count++, type, string).also { map[string] = it }

	fun addAndGet(string: String) = map.getOrPut(string) { Intern(count++, InternType.NONE, string) }



	private val widths = Width.values()

	private val registers = Register.values()

	private val keywords = Keyword.values()

	private val mnemonics = Mnemonic.values()

	private val prefixes = Prefix.values()



	private val widthOffset: Int

	private val varWidthOffset: Int

	private val registerOffset: Int

	private val keywordOffset: Int

	private val mnemonicOffset: Int

	private val prefixOffset: Int



	fun width(intern: Intern) = widths[intern.id - widthOffset]

	fun varWidth(intern: Intern) = widths[intern.id - varWidthOffset]

	fun register(intern: Intern) = registers[intern.id - registerOffset]

	fun keyword(intern: Intern) = keywords[intern.id - keywordOffset]

	fun mnemonic(intern: Intern) = mnemonics[intern.id - mnemonicOffset]

	fun prefix(intern: Intern) = prefixes[intern.id - prefixOffset]



	init {
		widthOffset = count
		for(w in widths)
			add(InternType.WIDTH, w.string)

		varWidthOffset = count
		for(w in widths)
			add(InternType.VAR_WIDTH, w.varString)

		registerOffset = count
		for(r in registers)
			add(InternType.REGISTER, r.string)

		keywordOffset = count
		for(k in keywords)
			add(InternType.KEYWORD, k.string)

		mnemonicOffset = count
		for(m in mnemonics)
			add(InternType.MNEMONIC, m.string)

		prefixOffset = count
		for(m in prefixes)
			add(InternType.PREFIX, m.string)
	}


}



object Interns {

	val RES = Interning.add(InternType.NONE, "res")

	val MAIN = Interning.add(InternType.NONE, "main")

	val SHORT = Interning.add(InternType.NONE, "short")

	val DLL = Interning.add(InternType.NONE, "dll")

}