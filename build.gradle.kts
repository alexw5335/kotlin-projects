import org.jetbrains.kotlin.gradle.tasks.KotlinCompile



plugins {
    kotlin("jvm") version "1.7.10"
}



group = "placeholder"
version = "1.0"



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
}