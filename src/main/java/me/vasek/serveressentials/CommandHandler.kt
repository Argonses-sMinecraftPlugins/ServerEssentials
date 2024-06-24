package me.vasek.serveressentials

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHandler(private val muteManager: MuteManager, private val configFile: ConfigFile) : CommandExecutor {

    private val tpRequests = mutableMapOf<Player, Player>()

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
            "heal" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /heal <player>")
                    return true
                }

                val playerName = args[0]
                val player = Bukkit.getPlayer(playerName)

                if (player == null) {
                    sender.sendMessage("Player not found.")
                    return true
                }

                player.health = player.maxHealth
                player.sendMessage("You have been healed to full health!")
                sender.sendMessage("${player.name} has been healed to full health.")

                return true
            }
            "feed" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /feed <player>")
                    return true
                }

                val playerName = args[0]
                val player = Bukkit.getPlayer(playerName)

                if (player == null) {
                    sender.sendMessage("Player not found.")
                    return true
                }

                player.foodLevel = 20
                player.sendMessage("You have been fed to full feed!")
                sender.sendMessage("${player.name} has been fed to full level.")

                return true
            }
            "tp" -> {
                if (args.isEmpty()) {
                    sender.sendMessage("Usage: /tp <player>")
                    return true
                }

                if (sender !is Player) {
                    sender.sendMessage("Only players can use this command.")
                    return true
                }

                val player: Player = sender
                val target = Bukkit.getPlayer(args[0])

                if (target == null) {
                    player.sendMessage("Player not found.")
                    return true
                }

                if (target == player) {
                    player.sendMessage("You cannot teleport to yourself.")
                    return true
                }

                tpRequests[target] = player
                target.sendMessage("${player.name} wants to teleport to you. Type /tpaccept to accept or /tpdeny to deny.")
                player.sendMessage("Teleport request sent to ${target.name}.")
                return true
            }
            "tpaccept" -> {
                if (sender !is Player) {
                    sender.sendMessage("Only players can use this command.")
                    return true
                }

                val player: Player = sender
                val requester = tpRequests[player]

                if (requester == null) {
                    player.sendMessage("No teleport requests found.")
                    return true
                }

                requester.teleport(player.location)
                player.sendMessage("You have accepted the teleport request.")
                requester.sendMessage("${player.name} has accepted your teleport request.")
                tpRequests.remove(player)
                return true
            }
            "tpdeny" -> {
                if (sender !is Player) {
                    sender.sendMessage("Only players can use this command.")
                    return true
                }

                val player: Player = sender
                val requester = tpRequests[player]

                if (requester == null) {
                    player.sendMessage("No teleport requests found.")
                    return true
                }

                player.sendMessage("You have denied the teleport request.")
                requester.sendMessage("${player.name} has denied your teleport request.")
                tpRequests.remove(player)
                return true
            }
            "msg" -> {
                if (args.size < 2) {
                    sender.sendMessage("Usage: /msg <player> <message>")
                    return false
                }

                val targetPlayerName = args[0]
                val message = args.drop(1).joinToString(" ")

                val targetPlayer = Bukkit.getPlayerExact(targetPlayerName)
                if (targetPlayer == null) {
                    sender.sendMessage("Player not found: $targetPlayerName")
                    return true
                }

                if (sender is Player) {
                    targetPlayer.sendMessage("${ChatColor.GOLD}Private message from ${ChatColor.BLUE}${sender.name}${ChatColor.RESET}: ${ChatColor.BOLD}$message${ChatColor.RESET}")
                } else {
                    targetPlayer.sendMessage("${ChatColor.GRAY}Private message from server: ${ChatColor.RESET}$message")
                }

                return true
            }
            "allcommands" -> {
                val commands = listOf(
                    "${ChatColor.GREEN}/ban <player> [reason] - ${ChatColor.LIGHT_PURPLE}Bans a player. | OP",
                    "${ChatColor.GREEN}/unban <player> - ${ChatColor.LIGHT_PURPLE}Unbans a player. | OP",
                    "${ChatColor.GREEN}/kick <player> [reason] - ${ChatColor.LIGHT_PURPLE}Kicks a player. | OP",
                    "${ChatColor.GREEN}/playerinfo <player> - ${ChatColor.LIGHT_PURPLE}Shows info about a player. | OP",
                    "${ChatColor.GREEN}/op <player> - ${ChatColor.LIGHT_PURPLE}Makes from regular person an OP. | OP",
                    "${ChatColor.GREEN}/heal <player> - ${ChatColor.LIGHT_PURPLE}Heals other player. | OP",
                    "${ChatColor.GREEN}/mute <player> - ${ChatColor.LIGHT_PURPLE}Mutes other player. | OP",
                    "${ChatColor.GREEN}/unmute <player> - ${ChatColor.LIGHT_PURPLE}Unmutes other player. | OP",
                    "${ChatColor.GREEN}/tp <player> - ${ChatColor.LIGHT_PURPLE}Sends a teleport request to another player.",
                    "${ChatColor.GREEN}/tpaccept - ${ChatColor.LIGHT_PURPLE}Accepts a teleport request.",
                    "${ChatColor.GREEN}/tpdeny - ${ChatColor.LIGHT_PURPLE}Denies a teleport request.",
                    "${ChatColor.GREEN}/msg - ${ChatColor.LIGHT_PURPLE}Sends private message to other player.",
                    "${ChatColor.GREEN}/allcommands - ${ChatColor.LIGHT_PURPLE}Shows all commands."
                )

                sender.sendMessage("${ChatColor.GOLD}Available ServerEssentials Commands:")
                commands.forEach { command ->
                    sender.sendMessage(command)
                }
            }
        }
        return false
    }
}
