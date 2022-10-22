package macro

import core.memory.Unsafe
import kotlin.reflect.KProperty

interface Struct {


	val address: Long



	fun byte(offset: Int) = NativeByte(address + offset)

	fun short(offset: Int) = NativeShort(address + offset)

	fun int(offset: Int) = NativeInt(address + offset)

	fun long(offset: Int) = NativeLong(address + offset)



	class NativeByte(private val address: Long) {
		operator fun getValue(ref: Any?, prop: KProperty<*>) = Unsafe.getByte(address).toInt()
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Int) = Unsafe.setByte(address, value.toByte())
	}

	class NativeShort(private val address: Long) {
		operator fun getValue(ref: Any?, prop: KProperty<*>) = Unsafe.getShort(address).toInt()
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Int) = Unsafe.setShort(address, value.toShort())
	}

	class NativeInt(private val address: Long) {
		operator fun getValue(ref: Any?, prop: KProperty<*>) = Unsafe.getInt(address)
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Int) = Unsafe.setInt(address, value)
	}

	class NativeLong(private val address: Long) {
		operator fun getValue(ref: Any?, prop: KProperty<*>) = Unsafe.getLong(address)
		operator fun setValue(ref: Any?, prop: KProperty<*>, value: Long) = Unsafe.setLong(address, value)
	}


}