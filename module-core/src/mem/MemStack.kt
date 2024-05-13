package core.mem

/**
 * Represents a contiguous array of off-heap memory that can be sub-allocated in a stack-like fashion. The [with] and
 * [get] methods free any memory that is allocated within their blocks. These functions will not implicitly free memory
 * if an exception is thrown within their blocks. If an exception that was thrown within a block is caught outside of a
 * block, then a memory leak of one or more stack frames will occur. If the same exception is caught within a block,
 * then no memory is leaked. Exceptions caught outside any block should result in the [resetting][reset] of the
 * MemStack. Using try-with-resources statements (or the equivalent 'use' function in Kotlin) is avoided in order to
 * improve performance as the block functions are used very frequently. None of these precautions are necessary if the
 * exceptions are never caught. This class must be loaded from the main thread.
 *
 * ### Examples:
 *
 *     val stack: MemStack
 *
 *     // Memory leak, exception caught outside of a stack frame.
 *     try {
 *         stack.with {
 *             mallocInt()
 *             throw RuntimeException()
 *         }
 *     } catch(e: Exception) { }
 *
 *
 *     // No memory leak, exception caught within a stack frame.
 *     stack.with {
 *         try {
 *             stack.with {
 *                 mallocInt()
 *                 throw RuntimeException()
 *             }
 *         } catch(e: Exception) { }
 *     }
 *
 *
 *     // No memory leak, exception caught outside of a stack frame,
 *     // but the stack is reset in response.
 *     int pointer = stack.pointer
 *     try {
 *         stack.with {
 *             mallocInt()
 *             throw RuntimeException()
 *         }
 *     } catch(e: Exception) {
 *         stack.pointer = pointer
 *     }
 *
 *     // TODO: Add memory-safe block functions
 *     // TODO: Document other memory leaks such as returning early from block functions.
 */
open class MemStack(address: Long, size: Long) : LinearAllocator(address, size) {


	/*
	Stack implementation
	 */



	/**
	 * Only use when block functions ([with] and [get]) cannot be used. Returns the current [pointer] address to be used
	 * in a future call to [pop]. Each [push] must be paired with a [pop].
	 */
	fun push() = pointer

	/**
	 * Only use when block functions ([with] and [get]) cannot be used. Must only be used with a [pointer] address
	 * obtained by a call to [push]. Each [push] must be paired with a [pop].
	 */
	fun pop(pointer: Long) { this.pointer = pointer }



	/*
	Blocks
	 */



	/**
	 * Calls [push], then the [block], then [pop]. Any memory allocations made within the [block] are freed when exiting
	 * the function. If an exception is thrown within the [block], then memory may not be freed. If the exception is
	 * caught within another [with] or [get] block, then the memory will be freed. If the exception is caught at the
	 * top level (outside any [with] or [get] blocks), then the memory will not be freed, and [reset] should be
	 * called.
	 */
	inline fun with(block: MemStack.() -> Unit) {
		val pointer = this.pointer
		block(this)
		this.pointer = pointer
	}



	/**
	 * Calls [push], then the [block], then [pop], before returning the result computed within the block. See [with] for
	 * more details.
	 */
	inline fun<T> get(block: MemStack.() -> T): T {
		val pointer = this.pointer
		val result = block(this)
		this.pointer = pointer
		return result
	}



	/*
	Companion
	 */



	companion object {

		/**
		 * A [ThreadLocal] that stores a single 1 MB [MemStack] per thread.
		 */
		private val locals = ThreadLocal.withInitial { MemStack(Unsafe.calloc(1 shl 20), 1 shl 20) }

		/**
		 * The thread-local [MemStack] associated with the current thread.
		 */
		private fun local() = locals.get()

		/**
		 * The thread-local [MemStack] of the main thread.
		 */
		private val default = locals.get()

		/**
		 * The [MemStack] that is used for global stack functions.
		 */
		var current: () -> MemStack = { default }

		/**
		 * If global [MemStack] functions use the main thread's stack or thread-local stacks. Must be set to true if
		 * using the global [MemStack] functions from multiple threads.
		 */
		var isThreadSafe
			get()      = current() == default
			set(value) { current = if(value) Companion::local else Companion::default }

	}


}