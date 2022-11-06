package scratch

import kotlin.math.max

class AvlTree {

	private var root: Node? = null

	private var list = ArrayList<Node>()

	fun add(value: Int): Boolean {
		list.clear()

		if(root == null) {
			root = Node(value)
			return false
		}

		var current = root!!

		while(true) {
			list.add(current)

			if(value < current.value) {
				if(current.left == null) {
					current.left = Node(value)
					break
				} else {
					current = current.left!!
				}
			} else if(value > current.value) {
				if(current.right == null) {
					current.right = Node(value)
					break
				} else {
					current = current.right!!
				}
			} else {
				return true
			}
		}

		for(i in list.size - 1 downTo 0) {
			val node = list[i]

			node.recalculateHeight()

			val balanceFactor = (node.left?.height ?: 0) - (node.right?.height ?: 0)
		}

		return false
	}
	
	private fun Node.recalculateHeight() {
		height = if(left == null)
			if(right == null)
				0
			else
				right!!.height + 1
		else
			if(right == null)
				left!!.height + 1
			else
				max(left!!.height, right!!.height) + 1
	}

	private fun rotateRight(node: Node): Node {
		val b = node.left!!
		val t3 = b.right
		
		b.right = node
		node.left = t3
		node.recalculateHeight()
		b.recalculateHeight()
		return b
	}

	private fun rotateLeft(node: Node): Node {
		val b = node.right!!
		val t3 = b.left

		b.left = node
		node.right = t3
		node.recalculateHeight()
		b.recalculateHeight()
		return b
	}

	fun print() = root?.print(0)

}

private fun Node.print(depth: Int) {
	repeat(depth) { print('\t') }
	println("$value $height")
	left?.print(depth + 1)
	right?.print(depth + 1)
}

private class Node(val value: Int) {
	var height : Int   = 0
	var left   : Node? = null
	var right  : Node? = null
}