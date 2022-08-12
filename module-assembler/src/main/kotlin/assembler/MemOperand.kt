package assembler

class MemOperand(
	val width        : Width? = null,
	val base         : Register?,
	val index        : Register?,
	val scale        : Int,
	val displacement : AstNode?
) {


	override fun toString() = "$width [$base, $index * $scale, ${displacement?.printableString}]"
}