package scratch

import core.dec8
import core.hex8

fun printAscii(start: Int, end: Int) {
	for(i in start until end) {
		print(i.hex8)
		print("  ")
		print(i.dec8)
		print("  ")
		print(i.toChar())
		print("  ")
		if(i in 65 until 65+26)
			println("UPPER_${i.toChar()}")
		else if(i in 97 until 97+26)
			println("LOWER_${i.toChar().uppercase()}")
		else {
			println()
		}
	}
}