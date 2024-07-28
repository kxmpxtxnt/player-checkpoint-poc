package fyi.pauli.save.commands

import fyi.pauli.save.saving.Save
import fyi.pauli.save.saving.fromContainer
import fyi.pauli.save.saving.toContainer
import fyi.pauli.save.util.minimessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestListSuspending
import org.bukkit.WeatherType
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import kotlin.io.path.*

val saveCommand = command("save") {
  runs {
    val save = Save(
      Save.WorldState(
        player.world.time,
        if (player.world.isClearWeather) WeatherType.CLEAR else WeatherType.DOWNFALL
      ),
      player.location,
      player.activePotionEffects.toList(),
      player.inventory.toContainer(),
    )

    val now = Instant.now().toEpochMilli()


    Paths.get("./saves", "$now-${player.name}.json").let {
      if (!it.parent.exists()) it.parent.createDirectory()
      if (!it.exists()) it.createFile()

      it.writeText(Json.encodeToString(save))

      player.sendMessage("Save saved in <red>'${it.fileName}'".minimessage())
    }
  }
}

val loadCommand = command("load") {
  argument<String>("save") {
    suggestListSuspending { Paths.get("./saves").listDirectoryEntries().map(Path::nameWithoutExtension) }

    runs {
      val file = Paths.get("./saves", "${getArgument<String>("save")}.json")

      if (file.notExists()) {
        player.sendMessage("<red>Gibts nicht :(")
        return@runs
      }

      val save: Save = Json.decodeFromString(file.readText())

      player.inventory.fromContainer(save.inventory)
    }
  }
}