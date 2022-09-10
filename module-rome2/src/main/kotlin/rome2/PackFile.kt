package rome2

import kotlin.reflect.KProperty



sealed interface PackField {
	fun clone(): PackField
}



data class PackFieldString(var value: String) : PackField {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) { this.value = value; }
	override fun clone() = PackFieldString(value)
}

data class PackFieldInt(var value: Int) : PackField {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) { this.value = value }
	override fun clone() = PackFieldInt(value)
}

data class PackFieldFloat(var value: Float) : PackField {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) { this.value = value }
	override fun clone() = PackFieldFloat(value)
}

data class PackFieldBoolean(var value: Boolean) : PackField {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) { this.value = value }
	override fun clone() = PackFieldBoolean(value)
}

data class PackFieldAny<T>(var reference: PackFieldString, var value: T, val setter: (T) -> String) : PackField {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		this.value = value
		reference.value = setter(value)
	}
	override fun clone() = PackFieldAny(reference, value, setter)
}

class PackFieldBooleanString(private var reference: PackFieldString): PackField {
	private var value = reference.value == "1" || reference.value == "true"
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
		this.value = value
		reference.value = "1"
	}
	override fun clone() = PackFieldBooleanString(reference)
}

class PackFieldIntFloat(private var reference: PackFieldFloat): PackField{
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = reference.value.toInt()
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) { reference.value = value.toFloat() }
	override fun clone() = PackFieldIntFloat(reference)
}



class PackEntry(val fields: List<PackField>) {
	fun clone() = PackEntry(fields.map(PackField::clone))
}

class PackTable(val entries: List<PackEntry>, val schema: SchemaTable)

fun PackEntry.string(index: Int) = fields[index] as PackFieldString

fun PackEntry.int(index: Int) = fields[index] as PackFieldInt

fun PackEntry.float(index: Int) = fields[index] as PackFieldFloat

fun PackEntry.boolean(index: Int) = fields[index] as PackFieldBoolean

fun<T> PackEntry.any(index: Int, getter: (String) -> T, setter: (T) -> String) =
	PackFieldAny(fields[index] as PackFieldString, getter((fields[index] as PackFieldString).value), setter)

fun <T : NamedType> PackEntry.any(index: Int, getter: (String) -> T) =
	PackFieldAny(fields[index] as PackFieldString, getter((fields[index] as PackFieldString).value)) { it.name }

fun PackEntry.booleanString(index: Int) = PackFieldBooleanString(fields[index] as PackFieldString)

fun PackEntry.intFloat(index: Int) = PackFieldIntFloat(fields[index] as PackFieldFloat)