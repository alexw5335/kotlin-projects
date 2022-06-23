package core



val Int.hexChar get() = if(this < 10)
	'0' + this
else
	'A' + this - 10



val Int.hex8 get() = String(charArrayOf(
	(this shr 4).hexChar,
	(this and 15).hexChar
))