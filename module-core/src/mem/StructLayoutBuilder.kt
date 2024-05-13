package core.mem

class StructLayoutBuilder {


	/*
	Variables
	 */



	/**
	 * The sizes that have been given by calls to [member].
	 */
	private var sizes = IntArray(10)

	/**
	 * The alignments that have been given by calls to [member].
	 */
	private var alignments = IntArray(10)

	/**
	 * The number of sizes that have been added via calls to [member].
	 */
	private var count = 0

	/**
	 * The maximum alignment that has been given by calls to [member]. This will be the final alignment of the struct.
	 */
	private var alignment = 0

	/**
	 * The maximum size that has been given by calls to [member]. This is only used when building a union.
	 */
	private var maxSize = 0



	/*
	Member addition
	 */



	/**
	 * Adds any type of member with a given [alignment] and [size].
	 */
	fun member(alignment: Int, size: Int) {
		if(count == sizes.size) {
			sizes = sizes.copyOf(sizes.size * 2)
			alignments = alignments.copyOf(alignments.size * 2)
		}

		maxSize = maxSize.coerceAtLeast(size)
		this.alignment = this.alignment.coerceAtLeast(alignment)

		sizes[count] = size
		alignments[count] = alignment

		count++
	}



	/**
	 * Adds a primitive member.
	 */
	fun member(size: Int) = member(size, size)

	/**
	 * Adds a struct member.
	 */
	fun member(layout: StructLayout) = member(layout.alignment, layout.size)

	/**
	 * Adds an array member.
	 */
	fun array(alignment: Int, length: Int) = member(alignment, alignment * length)

	/**
	 * Adds a struct array member.
	 */
	fun array(layout: StructLayout, length: Int) = member(layout.alignment, layout.size * length)



	/*
	Building
	 */



	fun reset() {
		alignment = 0
		maxSize = 0
		count = 0
	}



	fun build(isUnion: Boolean = false): StructLayout {
		if(count == 0) return StructLayout(0, 0, IntArray(0))

		// A union has no offsets.
		if(isUnion) return StructLayout(alignment, maxSize.aligned(), IntArray(count))

		val offsets = IntArray(count)
		var offset = 0

		for(i in 0 until count) {
			val alignmentOffset = offset % alignment

			// A member that exceeds the current alignment multiple is placed at the next multiple of alignment.
			if(alignmentOffset != 0 && alignmentOffset + alignments[i] > alignment) {
				offset = offset.aligned()
			}

			offsets[i] = offset
			offset += sizes[i]
		}

		return StructLayout(alignment, offset.aligned(), offsets)
	}



	inline fun build(isUnion: Boolean = false, block: StructLayoutBuilder.() -> Unit): StructLayout {
		reset()
		block()
		return build(isUnion)
	}



	/**
	 * Rounds this [Int] up to the nearest multiple of [alignment] if it was not already a multiple.
	 */
	private fun Int.aligned() = (this + alignment - 1) and -alignment


}