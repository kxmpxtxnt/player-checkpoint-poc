package fyi.pauli.save.saving

import kotlinx.serialization.Serializable
import net.axay.kspigot.serialization.ItemStackSerializer
import net.axay.kspigot.serialization.KSerializerForBukkit
import net.axay.kspigot.serialization.LocationSerializer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.WeatherType
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.potion.PotionEffect

object PotionEffectSerializer : KSerializerForBukkit<PotionEffect>(PotionEffect::class)

@Serializable
data class Save(
	val world: WorldState,
	@Serializable(with = LocationSerializer::class) val location: Location,
	val activeEffects: List<@Serializable(with = PotionEffectSerializer::class) PotionEffect>,
	val inventory: InventoryContainer,
) {

	@Serializable
	data class WorldState(
		val time: Long,
		val weatherType: WeatherType,
	)

	@Serializable
	data class InventoryContainer(
		@Serializable(with = ItemStackSerializer::class) val mainHand: ItemStack,
		@Serializable(with = ItemStackSerializer::class) val offHand: ItemStack,
		val items: List<@Serializable(with = ItemStackSerializer::class) ItemStack>,
		val armor: List<@Serializable(with = ItemStackSerializer::class) ItemStack>
	) {
		companion object {

			fun fromInventory(inventory: PlayerInventory): InventoryContainer {
				val items = buildList {
					repeat(inventory.size) {
						add(inventory.getItem(it) ?: ItemStack(Material.AIR))
					}
				}

				val armor = buildList {
					add(inventory.helmet ?: ItemStack(Material.AIR))
					add(inventory.chestplate ?: ItemStack(Material.AIR))
					add(inventory.leggings ?: ItemStack(Material.AIR))
					add(inventory.boots ?: ItemStack(Material.AIR))
				}

				val main = inventory.itemInMainHand
				val off = inventory.itemInOffHand

				inventory.clear()

				return InventoryContainer(main, off, items, armor)
			}
		}
	}
}

fun PlayerInventory.toContainer(): Save.InventoryContainer = Save.InventoryContainer.fromInventory(this)

fun PlayerInventory.fromContainer(container: Save.InventoryContainer) {
	clear()

	this.contents = container.items.toTypedArray()
	this.armorContents = container.armor.toTypedArray().reversedArray()
	this.setItemInMainHand(container.mainHand)
	this.setItemInOffHand(container.offHand)
}