plugins {
    kotlin("jvm") version "1.8.0"
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

	kotlin {
		jvmToolchain(18)
	}

	dependencies {
		if(name != "module-core")
			implementation(project(":module-core"))
	}

	tasks.withType(Jar::class) {
		val mainClass = mainClasses[project.name] ?: return@withType

		manifest {
			attributes("Main-Class" to mainClass)
		}

		duplicatesStrategy = DuplicatesStrategy.EXCLUDE

		from({ configurations.runtimeClasspath.get().map { zipTree(it) } })
	}
}