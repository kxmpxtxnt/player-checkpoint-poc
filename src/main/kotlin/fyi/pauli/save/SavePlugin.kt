package fyi.pauli.save

import fyi.pauli.save.commands.saveCommand
import net.axay.kspigot.main.KSpigot

class SavePlugin : KSpigot() {

	override fun startup() {
		saveCommand
	}
}