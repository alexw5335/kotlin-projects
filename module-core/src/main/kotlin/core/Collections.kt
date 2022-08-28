package core

/**
 * Constructs a map of keys to lists of elements that return the mapped key with the given [mapper].
 */
fun<E> List<E>.associateFlatMap(mapper: (E) -> String): Map<String, List<E>> {
	val map = HashMap<String, ArrayList<E>>()
	for(element in this) {
		val key = mapper(element)
		map.getOrPut(key, ::ArrayList).add(element)
	}
	return map
}