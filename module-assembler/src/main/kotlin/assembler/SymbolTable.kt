package assembler



/*class SymbolStack(private val stack: Deque<SymbolTable> = LinkedList()) {

	fun pop() = stack.pop()
	fun push() = stack.push(SymbolTable())
	fun push(table: SymbolTable) = stack.push(table)

	fun get(components: List<String>): Symbol? {
		for(table in stack)
			table.get(components)?.let { return it }
		return null
	}

}*/



class SymbolTable(private val root: HashMap<String, Node> = HashMap()) {


	class Node(val symbol: Symbol?, val map: HashMap<String, Node>?)



	fun get(components: List<String>): Symbol? {
		var map = root

		for(i in 0 until components.size - 1)
			map = map[components[i]]?.map ?: error("Invalid namespace")

		return map[components[components.size - 1]]?.symbol
	}



/*	fun add(namespace: Namespace, symbol: Symbol) {
		var map = root

		for(component in namespace.components) {
			map = map.getOrPut()
		}//
	}*/


}