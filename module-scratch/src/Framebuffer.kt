@file:Suppress("ReplaceArrayEqualityOpWithArraysEquals")

package scratch

import java.awt.Graphics
import java.awt.Rectangle
import java.awt.Robot
import java.awt.event.MouseEvent
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.math.abs



private class ImagePanel : JPanel() {
	override fun paintComponent(g: Graphics?) {
		super.paintComponent(g)
		val buffer = robot.createScreenCapture(rect)
		g!!.drawImage(robot.createScreenCapture(rect).getScaledInstance(256, 256, 0), 0, 0, this)
	}
}



private fun handleDiff(count: Int) {
	println(count)
	if(count < 30000) return

	Thread {
		running = false
		robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK)
		Thread.sleep(50)
		robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK)
		Thread.sleep(50)
		robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK)
		Thread.sleep(50)
		robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK)
		Thread.sleep(2000)
		running = true
	}.start()
}



@JvmInline
private value class Colour(val value: Int) {

	val r get() = value and 0xFF
	val g get() = (value shr 8) and 0xFF
	val b get() = (value shr 16) and 0xFF
	val a get() = (value shr 24) and 0xFF

}



var running = false



fun main() {
	val frame = JFrame()
	frame.title = "Autofisher"
	frame.setLocation(100, 100)
	frame.setSize(256, 400)
	frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

	val button = JButton("Run")
	button.addActionListener {
		if(running) {
			println("STOPPING")
			running = false
			return@addActionListener
		}

		Thread {
			println("RUNNING")
			Thread.sleep(3000)
			running = !running
		}.start()
	}

	val box = Box(BoxLayout.Y_AXIS)
	box.add(ImagePanel())
	box.add(button)
	frame.contentPane = box
	frame.isVisible = true

	var current = data1
	fillImage(data1)
	Timer(50) {
		frame.repaint()
		current = if(current == data1) data2 else data1
		fillImage(current)

		var diff = 0

		for(i in data1.indices) {
			val a = Colour(data1[i])
			val b = Colour(data2[i])
			diff += abs(b.r - a.r) + abs(b.g - a.g) + abs(b.b - a.b)
		}

		if(running)
			handleDiff(diff)
	}.start()
}



private val robot = Robot()
private val x = (1920 - 32) / 2
private val y = (1080 - 32) / 2 + 40
private val width = 64
private val height = 64
private val rect = Rectangle(x, y, width, height)
val data1 = IntArray(width * height)
val data2 = IntArray(width * height)



private fun fillImage(data: IntArray) {
	val image = robot.createScreenCapture(rect)
	image.getRGB(0, 0, width, height, data, 0, width)
}