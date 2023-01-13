package terraria

import java.awt.Graphics
import java.awt.Rectangle
import java.awt.Robot
import java.awt.event.MouseEvent
import javax.swing.*
import kotlin.math.abs



private var running = false

private var waiting = false

private val robot = Robot()

private val width = 64

private val height = 64

private var x = (1920 - width) / 2
	set(value) { field = value; rect.x = value }

private var y = (1080 - height) / 2 + 60
	set(value) { field = value; rect.y = value }

private val rect = Rectangle(x, y, width, height)

private val imageDataBack = IntArray(rect.width * rect.height)

private val imageDataFront = IntArray(rect.width * rect.height)

private var imageData = imageDataBack

private var image = robot.createScreenCapture(rect)



fun main() {
	val frame = JFrame()
	frame.title = "Auto-fisher"
	frame.setLocation(100, 100)
	frame.setSize(300, 400)
	frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

	val runButton = JButton("Run")
	runButton.addActionListener {
		if(running) {
			println("STOPPING")
			running = false
			runButton.text = "Run"
			return@addActionListener
		}

		println("RUNNING")
		runButton.text = "Stop"

		Thread {
			Thread.sleep(2000)
			running = true
		}.start()
	}

	val imagePanel = object : JPanel() {
		override fun paintComponent(g: Graphics) {
			super.paintComponent(g)
			g.drawImage(image.getScaledInstance(256, 256, 0), 0, 0, this)
		}
	}

	val box = Box(BoxLayout.Y_AXIS)

	box.add(imagePanel)
	box.add(runButton)
	frame.contentPane = box
	frame.isVisible = true

	Timer(50) {
		imageData = if(imageData == imageDataBack) imageDataFront else imageDataBack
		image = robot.createScreenCapture(rect)
		image.getRGB(0, 0, rect.width, rect.height, imageData, 0, rect.width)
		frame.repaint()
		if(running && !waiting)
			checkDifference()
	}.start()
}



private fun checkDifference() {
	var diff = 0

	for(i in 0 until rect.width * rect.height) {
		val a = Colour(imageDataFront[i])
		val b = Colour(imageDataBack[i])
		diff += abs(b.r - a.r) + abs(b.g - a.g) + abs(b.b - a.b)
	}

	if(diff < 40000) return

	Thread {
		waiting = true
		robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK)
		Thread.sleep(50)
		robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK)
		Thread.sleep(50)
		robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK)
		Thread.sleep(50)
		robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK)
		Thread.sleep(2000)
		waiting = false
	}.start()
}



@JvmInline
private value class Colour(val value: Int) {

	val r get() = value and 0xFF
	val g get() = (value shr 8) and 0xFF
	val b get() = (value shr 16) and 0xFF
	val a get() = (value shr 24) and 0xFF

}