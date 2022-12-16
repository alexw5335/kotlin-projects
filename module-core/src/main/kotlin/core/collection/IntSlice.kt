package core.collection

class IntSlice(
	val array: IntArray,
	val offset: Int,
	val size: Int
) {

	init {
		if(offset < 0 || offset + size > array.size)
			error("Slice $offset..${offset + size} out of range for list with size ${array.size}")
	}

}