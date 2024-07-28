plugins {
	kotlin("jvm") version "2.0.0"
	kotlin("plugin.serialization") version "2.0.0"
	id("xyz.jpenilla.run-paper") version "2.3.0"
	id("io.papermc.paperweight.userdev") version "1.7.1"
	id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

repositories {
	mavenCentral()
	maven("https://repo.pauli.fyi/releases")
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	paperLibrary(kotlin("stdlib"))
	paperLibrary("net.axay", "kspigot", "1.20.5")
	paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}

kotlin {
	jvmToolchain(21)
}

tasks {
	runServer {
		minecraftVersion("1.21")
	}

	paper {
		main = "fyi.pauli.save.SavePlugin"
		name = "Player-Checkpoint"
		description = "Player Checkpoint PoC Plugin"

		version = "1.0"

		author = "kxmpxtxnt"

		loader = "fyi.pauli.save.SavePluginLoader"

		foliaSupported = true

		apiVersion = "1.21"

		generateLibrariesJson = true
	}

	assemble {
		dependsOn(reobfJar)
	}
}