package rome2

import kotlin.reflect.KProperty

sealed interface PackField

data class PackFieldAny<T>(var value: T, val setter: (T) -> String) : PackField {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) { this.value = value }
}

data class PackFieldString(var value: String) : PackField {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) { this.value = value }
}

data class PackFieldInt(var value: Int) : PackField {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) { this.value = value }
}

data class PackFieldFloat(var value: Float) : PackField {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) { this.value = value }
}

data class PackFieldBoolean(var value: Boolean) : PackField {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) { this.value = value }
}

class PackFieldBooleanString(private var reference: PackFieldString) {
	private var value = reference.value == "1" || reference.value == "true"
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
		this.value = value
		reference.value = "1"
	}
}



class PackEntry(val fields: List<PackField>)

class PackTable(val entries: List<PackEntry>, val schema: SchemaTable)

fun PackEntry.string(index: Int) = fields[index] as PackFieldString

fun PackEntry.int(index: Int) = fields[index] as PackFieldInt

fun PackEntry.float(index: Int) = fields[index] as PackFieldFloat

fun PackEntry.boolean(index: Int) = fields[index] as PackFieldBoolean

fun<T> PackEntry.any(index: Int, getter: (String) -> T, setter: (T) -> String) = PackFieldAny(getter((fields[index] as PackFieldString).value), setter)

fun <T : NamedType> PackEntry.any(index: Int, getter: (String) -> T) = PackFieldAny(getter((fields[index] as PackFieldString).value)) { it.name }

fun PackEntry.booleanString(index: Int) = PackFieldBooleanString(fields[index] as PackFieldString)