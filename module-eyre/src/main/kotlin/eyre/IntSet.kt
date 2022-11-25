package eyre

/**
 * A simple fixed-size integer hash set.
 */
class IntSet(bucketCount: Int) {


	private class Bucket {
		var size = 0
		var array: IntArray? = null
	}



	private val buckets = Array(bucketCount) { Bucket() }



	fun add(value: Int): Boolean {
		val bucket = buckets[value % buckets.size]
		val array = bucket.array

		if(array == null) {
			bucket.array = IntArray(4)
			bucket.array!![0] = value
			bucket.size++
			return false
		}

		for(existing in 0 until bucket.size)
			if(array[existing] == value)
				return true

		if(bucket.size == array.size)
			bucket.array = array.copyOf(array.size * 2)

		bucket.array!![bucket.size++] = value

		return false
	}



	operator fun contains(value: Int): Boolean {
		val bucket = buckets[value % buckets.size]

		if(bucket.array == null)
			return false

		for(i in 0 until bucket.size)
			if(bucket.array!![i] == value)
				return true

		return false
	}



	fun remove(value: Int): Boolean {
		val bucket = buckets[value % buckets.size]

		if(bucket.array == null) return false

		var index = -1

		for(i in 0 until bucket.size)
			if(bucket.array!![i] == value)
				index = i

		if(index == -1) return false

		System.arraycopy(bucket.array!!, index + 1, bucket.array!!, index, bucket.size - index)
		bucket.array!![bucket.size - 1] = 0

		return true
	}


}