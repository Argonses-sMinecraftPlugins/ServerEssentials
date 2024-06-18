package me.vasek.serveressentials

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

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
