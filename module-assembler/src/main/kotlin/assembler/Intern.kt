package assembler



class Interned(val id: Int, val type: InternType, val name: String) {
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
	REGISTER;
}



object Intern {


	private var count = 0

	private val map = HashMap<String, Interned>()

	fun add(type: InternType, string: String) = Interned(count++, type, string).also { map[string] = it }

	fun addAndGet(string: String) = map.getOrPut(string) { Interned(count++, InternType.NONE, string) }



	private val widths = Width.values()

	private val registers = Register.values()

	private val keywords = Keyword.values()

	private val mnemonics = Mnemonic.values()



	private val widthOffset: Int

	private val varWidthOffset: Int

	private val registerOffset: Int

	private val keywordOffset: Int

	private val mnemonicOffset: Int



	fun width(interned: Interned) = widths[interned.id - widthOffset]

	fun varWidth(interned: Interned) = widths[interned.id - varWidthOffset]

	fun register(interned: Interned) = registers[interned.id - registerOffset]

	fun keyword(interned: Interned) = keywords[interned.id - keywordOffset]

	fun mnemonic(interned: Interned) = mnemonics[interned.id - mnemonicOffset]



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
	}



	val short = add(InternType.NONE, "short")

	val res = add(InternType.NONE, "res")

	val main = add(InternType.NONE, "main")


}