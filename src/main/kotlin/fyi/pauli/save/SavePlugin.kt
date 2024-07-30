package fyi.pauli.save

import fyi.pauli.save.commands.loadCommand
import fyi.pauli.save.commands.saveCommand
import fyi.pauli.save.listener.onJoin
import fyi.pauli.save.listener.onLeave
import net.axay.kspigot.main.KSpigot

class SavePlugin : KSpigot() {

  override fun startup() {
    saveCommand
    loadCommand

    onJoin
    onLeave
  }
}