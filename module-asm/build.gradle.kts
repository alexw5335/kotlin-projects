dependencies {
	implementation(project(":module-core"))
}

sourceSets {
	kotlin.sourceSets.create("testing")
}



sourceSets {
	main {
		java {
			srcDir("src")
		}
	}
}



task("printSourceSets") {
	doLast {
		sourceSets.forEach {
			println(it.name)
			println(it.allJava.srcDirs.joinToString())
		}
	}
}