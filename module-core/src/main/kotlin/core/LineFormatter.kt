package core

class LineFormatter(
	private val lines         : List<List<String>>,
	private val singleSpacing : Int = 1,
	private val spacings      : Map<Int, Int> = emptyMap()
) {


	fun format(): List<String> {
		val lengths = ArrayList<Int>()
		var index = 0
		var length = 0
		val remaining = lines.toMutableList()

		if(lines.isEmpty() || lines.none { it.isNotEmpty() })
			return emptyList()

		while(remaining.isNotEmpty()) {
			val partLength = remaining.maxOf { it[index].length } + (spacings[index] ?: singleSpacing)
			lengths.add(partLength)
			length += partLength
			index++
			remaining.removeAll { it.size <= index }
		}

		return lines.map { line ->
			buildString {
				for((i, s) in line.withIndex()) {
					append(s)
					for(j in 0 until lengths[i] - s.length) append(' ')
				}
			}
		}
	}


}