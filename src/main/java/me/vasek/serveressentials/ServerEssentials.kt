package me.vasek.serveressentials

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.File

class ServerEssentials : JavaPlugin(), Listener {
    private lateinit var configFile: ConfigFile

    override fun onEnable() {
        logger.info("ModerationPlugin has been enabled!")
        configFile = ConfigFile()
        configFile.setup(dataFolder)

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        logger.info("ModerationPlugin has been disabled!")
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerName = player.name
        val message = "${ChatColor.GREEN}Hi, $playerName! Welcome to server!"

        Bukkit.broadcastMessage(message)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val playerName = player.name
        val message = "${ChatColor.GOLD}See you soon, $playerName!"

        Bukkit.broadcastMessage(message)
    }

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
        }
        return false
    }
}

class ConfigFile {
    private lateinit var conf: File

    fun setup(dir: File) {
        if (!dir.exists()) {
            dir.mkdirs()
        }
        conf = File(dir, "banned.yml")
        if (!conf.exists()) {
            val config = YamlConfiguration.loadConfiguration(conf)
            try {
                config.save(conf)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun banPlayer(player: String, reason: String) {
        val config = YamlConfiguration.loadConfiguration(conf)
        config.set("banned.players.$player", reason)
        try {
            config.save(conf)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getBanReason(player: String): String? {
        val config = YamlConfiguration.loadConfiguration(conf)
        return config.getString("banned.players.$player")
    }

    fun isBanned(player: String): Boolean {
        val config = YamlConfiguration.loadConfiguration(conf)
        return config.contains("banned.players.$player")
    }

    fun unbanPlayer(player: String) {
        if (isBanned(player)) {
            val config = YamlConfiguration.loadConfiguration(conf)
            config.set("banned.players.$player", null)
            try {
                config.save(conf)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
