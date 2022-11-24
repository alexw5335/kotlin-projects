package eyre



class Intern(val id: Int, val type: InternType, val value: String) {
	override fun hashCode() = id
	override fun equals(other: Any?) = other === this
	override fun toString() = value
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

	fun addOrGet(string: String) = map.getOrPut(string) { Intern(count++, InternType.NONE, string) }



	private val widthOffset: Int

	private val varWidthOffset: Int

	private val registerOffset: Int

	private val keywordOffset: Int

	private val mnemonicOffset: Int

	private val prefixOffset: Int



	fun width(intern: Intern) =  Width.values[intern.id - widthOffset]

	fun varWidth(intern: Intern) =  Width.values[intern.id - varWidthOffset]

	fun register(intern: Intern) = Register.values[intern.id - registerOffset]

	fun keyword(intern: Intern) = Keyword.values[intern.id - keywordOffset]

	fun mnemonic(intern: Intern) = Mnemonic.values[intern.id - mnemonicOffset]

	fun prefix(intern: Intern) = Prefix.values[intern.id - prefixOffset]



	init {
		widthOffset = count
		for(w in Width.values)
			add(InternType.WIDTH, w.string)

		varWidthOffset = count
		for(w in Width.values)
			add(InternType.VAR_WIDTH, w.varString)

		registerOffset = count
		for(r in Register.values)
			add(InternType.REGISTER, r.string)

		keywordOffset = count
		for(k in Keyword.values)
			add(InternType.KEYWORD, k.string)

		mnemonicOffset = count
		for(m in Mnemonic.values)
			add(InternType.MNEMONIC, m.string)

		prefixOffset = count
		for(p in Prefix.values)
			add(InternType.PREFIX, p.string)
	}


}



object Interns {

	private val String.intern get() = Interning.add(InternType.NONE, this)

	val RES   = "res".intern

	val MAIN  = "main".intern

	val SHORT = "short".intern

	val DLL   = "dll".intern

	val NULL  = "null".intern

	val EMPTY = "".intern

}