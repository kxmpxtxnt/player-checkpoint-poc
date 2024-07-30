package fyi.pauli.save.listener

import fyi.pauli.save.saving.Save
import fyi.pauli.save.saving.load
import fyi.pauli.save.saving.save
import fyi.pauli.save.util.minimessage
import kotlinx.serialization.json.Json
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.items.itemStack
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText

val onJoin = listen<PlayerJoinEvent> {
	val saves = Paths.get("./saves/${it.player.uniqueId}").listDirectoryEntries()

	if (saves.isEmpty()) return@listen

	it.player.teleport(it.player.location.add(0.0, 1000.0, 0.0))
	it.player.gameMode = GameMode.SPECTATOR

	val inventory = kSpigotGUI(GUIType.THREE_BY_NINE) {
		title = "<rainbow>S A V E S".minimessage()
		page(1) {
			val compound = createRectCompound<Save>(
				Slots.RowTwoSlotTwo, Slots.RowThreeSlotEight,
				iconGenerator = { save ->
					itemStack(Material.entries.random()) {
						editMeta { meta ->
							meta.displayName("<rainbow><bold>${save.name}".minimessage())

							meta.lore(buildList {
								add("Gamemode: ${save.gameMode}".minimessage())
								add("Flying: ${save.flying}".minimessage())
								add("Sneaking: ${save.sneaking}".minimessage())
								add("Exp: ${save.exp}".minimessage())
								add("Location: [X: ${save.location.x}, Y: ${save.location.y}, Z: ${save.location.z}]".minimessage())
								add("ActiveEffects: ${save.activeEffects.map { "${it.type} : ${it.duration}s" }.joinToString { 
									" - "
								}}".minimessage())
								add("Inventory: ${save.inventory.items.size} Items".minimessage())
							})
						}
					}
				},
				onClick = { event, save ->
					event.player.load(save)
				}
			)

			compound.addContent(saves.map { Json.decodeFromString(it.readText()) })
		}
	}

	it.player.openGUI(inventory)
}

val onLeave = listen<PlayerQuitEvent> {
	it.player.save("Leave-${SimpleDateFormat("h:mm:ss").format(Date.from(Instant.now()))}")
}