package me.vasek.serveressentials

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class ServerEssentials : JavaPlugin(), Listener {
    private lateinit var muteManager: MuteManager
    private lateinit var commandHandler: CommandHandler
    private lateinit var configFile: ConfigFile

    override fun onEnable() {
        logger.info("ServerEssentials has been enabled!")
        muteManager = MuteManager()
        configFile = ConfigFile()
        commandHandler = CommandHandler(muteManager, configFile)

        configFile.setup(dataFolder)

        server.pluginManager.registerEvents(this, this)
        getCommand("ban")?.setExecutor(commandHandler)
        getCommand("unban")?.setExecutor(commandHandler)
        getCommand("kick")?.setExecutor(commandHandler)
        getCommand("playerinfo")?.setExecutor(commandHandler)
        getCommand("mute")?.setExecutor(commandHandler)
        getCommand("unmute")?.setExecutor(commandHandler)
        getCommand("op")?.setExecutor(commandHandler)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerName = player.name
        val message = "${ChatColor.GREEN}Hello, $playerName! Welcome to server!"

        Bukkit.broadcastMessage(message)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val playerName = player.name
        val message = "${ChatColor.AQUA}Goodbye $playerName!"

        Bukkit.broadcastMessage(message)
    }

    override fun onDisable() {
        logger.info("ServerEssentials has been disabled!")
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (muteManager.isMuted(event.player.name)) {
            event.player.sendMessage("${ChatColor.RED}You are muted and cannot send messages.")
            event.isCancelled = true
        }
    }
}

