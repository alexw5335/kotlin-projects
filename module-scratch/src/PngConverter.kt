package scratch

import java.io.File
import javax.imageio.ImageIO

fun main() {
	val image = ImageIO.read(File("star2.png"))

	var index = 0
	for(y in 0 until image.height) {
		for(x in 0 until image.width) {
			val value = image.getRGB(x, y)

			val r = (value shr 0) and 0xFF
			val g = (value shr 8) and 0xFF
			val b = (value shr 16) and 0xFF
			val a = if(r == 0 && g == 0 && b == 0) 0 else 255

			//val printValue = r or (g shl 8) or (b shl 16) or (a shl 24)

			val printValue = if(value == -16711919) 0 else value or 255

			print("$printValue, ")
			if(index++ % 32 == 31) println()
		}
	}
}



