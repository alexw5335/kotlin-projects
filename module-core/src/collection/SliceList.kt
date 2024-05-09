package core.collection

class SliceList<T>(
	private  val list   : List<T>,
	private  val offset : Int,
	override val size   : Int
) : List<T> {

	init {
		if(offset < 0 || offset + size > list.size)
			error("Slice $offset..${offset + size} out of range for list with size ${list.size}")
	}

	inner class SliceIterator : Iterator<T> {
		private var pos        = 0
		override fun hasNext() = pos < size
		override fun next()    = list[offset + pos++]
	}

	inner class SliceListIterator(private var pos: Int = 0) : ListIterator<T> {
		override fun hasNext()       = pos < size
		override fun hasPrevious()   = pos > 0
		override fun next()          = list[offset + pos++]
		override fun nextIndex()     = pos
		override fun previous()      = list[offset + --pos]
		override fun previousIndex() = pos - 1
	}

	override fun contains(element: T): Boolean {
		for(i in offset until offset + size)
			if(list[i] == element)
				return true
		return false
	}

	override fun containsAll(elements: Collection<T>): Boolean {
		for(e in elements)
			if(!contains(e))
				return false
		return true
	}

	override fun get(index: Int) = list[offset + index]

	override fun isEmpty() = size == 0

	override fun iterator() = SliceIterator()

	override fun listIterator() = SliceListIterator()

	override fun listIterator(index: Int) = SliceListIterator(index)

	override fun subList(fromIndex: Int, toIndex: Int) = SliceList(this, fromIndex, toIndex - fromIndex)

	override fun lastIndexOf(element: T): Int {
		for(i in offset + size downTo offset)
			if(list[i] == element)
				return i
		return -1
	}

	override fun indexOf(element: T): Int {
		for(i in offset until offset + size)
			if(list[i] == element)
				return i
		return -1
	}

}