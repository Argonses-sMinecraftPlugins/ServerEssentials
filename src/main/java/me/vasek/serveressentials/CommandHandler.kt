package me.vasek.serveressentials

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CommandHandler(private val muteManager: MuteManager, private val configFile: ConfigFile) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        when (command.name.toLowerCase()) {
            "ban" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /ban <player> [reason]")
                    return true
                }
                val playerName = args[0]
                val reason = if (args.size > 1) args.drop(1).joinToString(" ") else "You have been banned!"
                configFile.banPlayer(playerName, reason)
                val player = Bukkit.getPlayer(playerName)
                player?.kickPlayer(reason)
                sender.sendMessage("Player $playerName has been banned for: $reason")
                return true
            }
            "unban" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /unban <player>")
                    return true
                }
                val playerName = args[0]
                configFile.unbanPlayer(playerName)
                sender.sendMessage("Player $playerName has been unbanned.")
                return true
            }
            "kick" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /kick <player> [reason]")
                    return true
                }
                val playerName = args[0]
                val reason = if (args.size > 1) args.drop(1).joinToString(" ") else "You have been kicked!"
                val player = Bukkit.getPlayer(playerName)
                player?.kickPlayer(reason)
                sender.sendMessage("Player $playerName has been kicked for: $reason")
                return true
            }
            "playerinfo" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /playerinfo <player>")
                    return true
                }
                val playerName = args[0]
                val player = Bukkit.getPlayer(playerName)
                if (player == null) {
                    sender.sendMessage("Player not found!")
                    return true
                }
                sender.sendMessage("Player: ${player.name}")
                sender.sendMessage("UUID: ${player.uniqueId}")
                sender.sendMessage("IP: ${player.address?.address?.hostAddress}")
                sender.sendMessage("Location: ${player.location}")
                sender.sendMessage("Health: ${player.health}")
                sender.sendMessage("Food Level: ${player.foodLevel}")
                return true
            }
            "op" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /op <player>")
                    return true
                }
                val playerName = args[0]
                val player = Bukkit.getPlayer(playerName)
                if (player != null) {
                    if (player.isOp) {
                        sender.sendMessage("${ChatColor.RED}${player.name} is already an operator.")
                    } else {
                        player.isOp = true
                        sender.sendMessage("${ChatColor.GREEN}${player.name} has been made an operator.")
                        player.sendMessage("${ChatColor.GREEN}You are now an operator.")
                    }
                } else {
                    sender.sendMessage("${ChatColor.RED}Player not found!")
                }
                return true
            }
            "mute" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /mute <player>")
                    return true
                }
                val playerName = args[0]
                if (muteManager.isMuted(playerName)) {
                    sender.sendMessage("${ChatColor.RED}$playerName is already muted.")
                } else {
                    muteManager.mutePlayer(playerName)
                    sender.sendMessage("${ChatColor.DARK_RED}$playerName has been muted.")
                }
                return true
            }
            "unmute" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /unmute <player>")
                    return true
                }
                val playerName = args[0]
                if (!muteManager.isMuted(playerName)) {
                    sender.sendMessage("${ChatColor.RED}$playerName is not muted.")
                } else {
                    muteManager.unmutePlayer(playerName)
                    sender.sendMessage("${ChatColor.GREEN}$playerName has been unmuted.")
                }
                return true
            }

        }
        return false
    }
}
