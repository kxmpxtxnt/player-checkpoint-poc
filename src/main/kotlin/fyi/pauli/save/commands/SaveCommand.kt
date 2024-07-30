package fyi.pauli.save.commands

import fyi.pauli.save.saving.Save
import fyi.pauli.save.saving.load
import fyi.pauli.save.saving.save
import kotlinx.serialization.json.Json
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestListSuspending
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

val saveCommand = command("save") {
	argument<String>("name") {
		runs {
			player.save(getArgument("name"))
		}
	}

	runs {
		player.sendRichMessage("<red>Usage: /save <name>")
	}
}

val loadCommand = command("load") {
	argument<String>("name") {
		runs {
			val save: Save = Json.decodeFromString(Paths.get("./saves/${player.uniqueId}", "${getArgument<String>("name")}.json").readText())
			player.load(save)
		}

		suggestListSuspending {
			Paths.get("./saves/${it.source.playerOrException.stringUUID}").listDirectoryEntries().map(Path::nameWithoutExtension)
		}
	}
}
