package rol

import core.xml.XmlElement
import kotlin.reflect.KProperty

abstract class RolObject(var element: XmlElement) {


	inner class StringProperty(private val propertyName: String) {
		operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
			return element.child(propertyName).text!!
		}

		operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
			element.child(propertyName).text = value
		}
	}



	inner class IntProperty(private val propertyName: String) {
		operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
			return element.child(propertyName).text!!.toInt()
		}

		operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
			element.child(propertyName).text = value.toString()
		}
	}


}