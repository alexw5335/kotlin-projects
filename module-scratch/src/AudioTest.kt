package scratch

import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.nio.file.Files
import java.nio.file.Paths
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.io.path.name
import kotlin.math.absoluteValue



fun main() {
	testing()
}



private fun convert() {
	val bytes = Files.readAllBytes(Paths.get("silent.xnb"))
	for(path in Files.walk(Paths.get("Custom")))
		if(path.name != "Custom")
			Files.write(path, bytes)
}



private fun testing() {
	val mixers = AudioSystem.getMixerInfo().map(AudioSystem::getMixer)
	val mixer = mixers.first { it.mixerInfo.name.startsWith("CABLE Output") }
	val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 1000F, 16, 2, 4, 1000F, false)
	val lineInfo = DataLine.Info(TargetDataLine::class.java, format)
	val line = mixer.getLine(lineInfo) as TargetDataLine

	val bytes = ByteArray(100)

	line.open()
	line.start()

	val thread = Thread {
		line.open()
		line.start()

		var segment = false
		val robot = Robot()
		var last = 0L

		while(true) {
			line.read(bytes, 0, bytes.size)
			val average = bytes.sumOf { it.toInt().absoluteValue } / bytes.size
			val current = System.currentTimeMillis()

			if(average < 3) {
				if(segment) {
					println("Segment end: ${current - last}")

					if(current - last < 600) {
						println("Fishing")
						robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
						Thread.sleep(100)
						robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
						Thread.sleep(100)

						robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
						Thread.sleep(100)
						robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
						Thread.sleep(100)

						robot.keyPress(KeyEvent.VK_B)
						Thread.sleep(100)
						robot.keyRelease(KeyEvent.VK_B)
						Thread.sleep(1000)
					}

					last = current
					segment = false
				}
				continue
			}

			segment = true
		}
	}

	thread.start()
}




/*fun main() {
	val mixers = AudioSystem.getMixerInfo().map(AudioSystem::getMixer)

	for(m in mixers) {
		println(m.mixerInfo)
		for(t in m.targetLineInfo)
			println("\t$t")
	}

	val mixer = mixers.first { it.mixerInfo.name.startsWith("CABLE Output") }

	val format = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100F, 16, 2, 4, 44100F, false)

	val lineInfo = DataLine.Info(TargetDataLine::class.java, format)

	if(!mixer.isLineSupported(lineInfo)) error("format not supported")

	val line = mixer.getLine(lineInfo) as TargetDataLine

	line.open()
	line.start()

	val thread = Thread {
		val stream = AudioInputStream(line)
		val file = File("output.wav")
		println("started")
		AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file)
		println("stopped")
	}

	thread.start()
	JOptionPane.showMessageDialog(null, "Stop recording")
	line.stop()
	line.close()
}*/



/*fun main() {
	val mixers = AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }
	val mixer = mixers[0]
	val line = mixer.getLine(mixer.targetLineInfo[0])
	line.addLineListener { println(it) }
	line.open()
	line.controls.forEach(::println)
	for(i in 0 until 1000) {
		Thread.sleep(10)
	}
	line.close()
}*/


/*fun main() {
	val mixers = AudioSystem.getMixerInfo().map(AudioSystem::getMixer)
	//val mixer = mixers[0]
	//for(m in mixers) for(l in m.targetLineInfo) println("${m.mixerInfo} ${l.lineClass}")
	//val line = mixer.getLine(mixer.targetLineInfo[0])
	val filteredMixers = mixers.filter { m -> m.targetLineInfo.any { it.lineClass == TargetDataLine::class.java } }
	val mixer = filteredMixers.last()
	val line = mixer.getLine(mixer.targetLineInfo[0]) as TargetDataLine
	val bytes = ByteArray(line.bufferSize)
	val output = ByteArrayOutputStream()
	line.start()
	for(i in 0 until 100) {
		println(line.available())
		val bytesRead = line.read(bytes, 0, bytes.size)
		output.write(bytes, 0, bytesRead)
		Thread.sleep(10)
	}
	line.stop()
	println(output.size())
}*/

/*
fun main() {
	val format = AudioFormat(8000F, 16, 1, true, false)
	val info = DataLine.Info(TargetDataLine::class.java, format)
	val line = AudioSystem.getLine(info) as TargetDataLine

	line.addLineListener { println(it) }

	val bytes = ByteArray(line.bufferSize)
	val output = ByteArrayOutputStream()

	line.start()

	for(i in 0 until 100) {
		val bytesRead = line.read(bytes, 0, bytes.size)
		output.write(bytes, 0, bytesRead)
		Thread.sleep(10)
	}

	line.stop()

	println(output.size())
}*/
