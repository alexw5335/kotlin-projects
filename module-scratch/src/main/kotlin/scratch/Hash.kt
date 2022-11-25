package scratch

import kotlin.random.Random
import kotlin.system.measureTimeMillis


var test = 0

fun main() {
	val map = HashMap(8192 * 8)
	val list = ArrayList<Intern>()
	val map2 = java.util.HashMap<String, Intern>()

	for(i in 0 until 100_000) {
		val string = randomString()
		map.insert(string)
		list.add(Intern(0, hash(string), string))
		map2[string] = Intern(0, 0, string)
	}

	//map.collisionCount()


	measureTimeMillis {
		map.insert("TESTING")
		list.add(Intern(test++, 0, "TESTING"))
		for(i in 0 until 100_000) {
			map["TESTING"] // Takes 5 milliseconds
			//map2["TESTING"]
			//list.firstOrNull { it.string == "TESTING" } // Takes 75 seconds
		}
	}.also(::println)


}



private fun randomString() = String(CharArray(Random.nextInt(1, 10)) {
	Char(Random.nextInt(21, 126))
})



private fun hash(string: String): Int {
	var h = 0
	for (c in string)
		h = 31 * h + (c.code and 0xFF)
	return h
}



class Intern(val index: Int, val hash: Int, val string: String)



class Entry(val key: String, val value: Intern, var next: Entry?)



class Bucket {
	var entry: Entry? = null
}



private var internIndex = 0



class HashMap(capacity: Int = 256) {


	init {
		if(capacity.countOneBits() != 1)
			error("Capacity must be a power of two: $capacity")
	}



	private val buckets = Array(capacity) { Bucket() }

	private val alignment = capacity.takeHighestOneBit()



	fun collisionCount() {
		for(bucket in buckets) {
			if(bucket.entry == null) {
				println("Empty bucket")
				continue
			}

			var entry = bucket.entry!!

			if(entry.next == null) {
				println("Single bucket")
				continue
			}

			var count = 0
			while(entry.next != null) {
				count++
				entry = entry.next!!
			}
			println("Collisions: $count")
		}
	}



	fun insert(string: String) = insert(string, hash(string))



	fun insert(string: String, hash: Int): Boolean {
		val bucket = buckets[hash and (alignment - 1)]

		if(bucket.entry != null) {
			var entry = bucket.entry!!

			while(true) {
				if(entry.key == string)
					return false
				entry = entry.next ?: break
			}

			entry.next = Entry(string, Intern(internIndex++, hash, string), null)
		} else {
			bucket.entry = Entry(string, Intern(internIndex++, hash, string), null)
		}

		return true
	}



	fun get(string: String, hash: Int): Intern? {
		val bucket = buckets[hash and (alignment - 1)]
		var entry = bucket.entry ?: return null

		while(true) {
			if(entry.value.hash == hash && entry.value.string == string)
				return entry.value
			entry = entry.next ?: return null
		}
	}



	operator fun get(string: String) = get(string, hash(string))


}



