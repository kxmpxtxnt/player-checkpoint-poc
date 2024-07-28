package fyi.pauli.save.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

val miniMessage: MiniMessage by lazy { MiniMessage.miniMessage() }

fun String.minimessage(): Component = miniMessage.deserialize(this)