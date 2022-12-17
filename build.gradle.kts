import org.jetbrains.kotlin.gradle.tasks.KotlinCompile



plugins {
    kotlin("jvm") version "1.7.20"
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
		tasks.withType<KotlinCompile> {
			kotlinOptions.jvmTarget = "17"
		}

		plugin("org.jetbrains.kotlin.jvm")
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