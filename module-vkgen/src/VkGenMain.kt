@file:Suppress("unused")

package vkgen

import core.xml.XmlParser
import java.nio.file.Paths



fun main() {
	val parser = VkParser(XmlParser.parse(Paths.get("module-vkgen/res/vk.xml")))
	parser.parse()
	val generator = VkGenerator(parser, Paths.get("gen/vulkan"), Paths.get("gen/c"), "vkgen")
	generator.gen()
}