package core.collection

/**
 * Constructs a map of keys to lists of elements that return the mapped key with the given [mapper].
 */
fun<E, K> List<E>.associateFlatMap(mapper: (E) -> K): Map<K, List<E>> {
	val map = HashMap<K, ArrayList<E>>()
	for(element in this) {
		val key = mapper(element)
		map.getOrPut(key, ::ArrayList).add(element)
	}
	return map
}



fun<E, K> List<E>.associateByList(mapper: (E) -> Iterable<K>): Map<K, E> {
	val map = HashMap<K, E>()
	for(element in this)
		for(key in mapper(element))
			map[key] = element
	return map
}