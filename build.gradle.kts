plugins {
    kotlin("jvm") version "2.0.0-RC2"
}

group = "placeholder"
version = "1.0"

val mainClasses = mapOf(
	"module-eyre" to "eyre.EyreMainKt"
)

allprojects {
	group = project.group
	version = project.version

	repositories {
		mavenCentral()
	}

	apply {
		plugin("org.jetbrains.kotlin.jvm")
	}

	dependencies {
		if(name != "module-core")
			implementation(project(":module-core"))
	}

	// Fat jar creation
	tasks.withType(Jar::class) {
		val mainClass = mainClasses[project.name] ?: return@withType
		manifest { attributes("Main-Class" to mainClass) }
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
		from({ configurations.runtimeClasspath.get().map { zipTree(it) } })
	}
}