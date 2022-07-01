package assembler

/**
 * Maps longs to strings
 */
class TestTree {


	private var size = 0

	private var root: Entry? = null



	operator fun get(key: Long): Entry? {
		var current = root

		while(current != null) {
			current = when {
				key < current.key -> current.left
				key > current.key -> current.right
				else              -> return current
			}
		}

		return null
	}



	operator fun set(key: Long, value: String) {
		if(root == null) {
			root = Entry(key, value, null)
			size = 1
			return
		}

		var current = root!!

/*		do {
			when {
				key < current.key -> current = current.left

			}
		}*/
	}


}



private const val BLACK = true
private const val RED = false



class Entry(val key: Long, val value: String, val parent: Entry?) {
	var left: Entry? = null
	var right: Entry? = null
	var colour = BLACK
}