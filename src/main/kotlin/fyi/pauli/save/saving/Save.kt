package fyi.pauli.save.saving

import fyi.pauli.save.util.minimessage
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.axay.kspigot.serialization.ItemStackSerializer
import net.axay.kspigot.serialization.KSerializerForBukkit
import net.axay.kspigot.serialization.LocationSerializer
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.WeatherType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.potion.PotionEffect
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import kotlin.io.path.*

object PotionEffectSerializer : KSerializerForBukkit<PotionEffect>(PotionEffect::class)

@Serializable
data class Save(
  val name: String,
  val gameMode: GameMode,
  val flying: Boolean,
  val sneaking: Boolean,
  val exp: Float,
  @Serializable(with = LocationSerializer::class) val location: Location,
  val activeEffects: List<@Serializable(with = PotionEffectSerializer::class) PotionEffect>,
  val inventory: InventoryContainer,
) {

  @Serializable
  data class InventoryContainer(
    @Serializable(with = ItemStackSerializer::class) val mainHand: ItemStack,
    @Serializable(with = ItemStackSerializer::class) val offHand: ItemStack,
    val items: List<@Serializable(with = ItemStackSerializer::class) ItemStack>,
    val armor: List<@Serializable(with = ItemStackSerializer::class) ItemStack>,
  )
}

fun Player.save(saveName: String): Save {
  val path = Paths.get("./saves/$uniqueId", "$saveName.json")

  val save = Save(
    name = saveName,
    gameMode = this.gameMode,
    flying = this.isFlying,
    sneaking = this.isSneaking,
    location = this.location,
    exp = this.exp,
    activeEffects = this.activePotionEffects.toList(),
    inventory = Save.InventoryContainer(
      mainHand = inventory.itemInMainHand,
      offHand = inventory.itemInOffHand,
      items = buildList {
        repeat(inventory.size) {
          add(inventory.getItem(it) ?: ItemStack(Material.AIR))
        }
      },
      armor = buildList {
        add(inventory.helmet ?: ItemStack(Material.AIR))
        add(inventory.chestplate ?: ItemStack(Material.AIR))
        add(inventory.leggings ?: ItemStack(Material.AIR))
        add(inventory.boots ?: ItemStack(Material.AIR))
      }.reversed()
    )
  )

  if (path.parent.notExists()) path.parent.createDirectories()
  if (path.notExists()) path.createFile()

  path.writeText(Json.encodeToString(save))

  inventory.clear()
  clearActivePotionEffects()
  gameMode = GameMode.SPECTATOR
  exp = 0F
  kick("<green>Save <italic><red>[${path.nameWithoutExtension}] <reset><green>successfully...".minimessage())

  return save
}

fun Player.load(save: Save) {
  teleport(save.location)
  inventory.clear()
  addPotionEffects(save.activeEffects)
  inventory.contents = save.inventory.items.toTypedArray()
  inventory.armorContents = save.inventory.armor.toTypedArray()
  inventory.setItemInMainHand(save.inventory.mainHand)
  inventory.setItemInOffHand(save.inventory.offHand)
  gameMode = save.gameMode
  isFlying = save.flying
  isSneaking = save.sneaking
  exp = save.exp
}