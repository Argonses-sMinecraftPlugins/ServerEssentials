package me.vasek.serveressentials

class MuteManager {
    private val mutedPlayers = mutableSetOf<String>()

    fun mutePlayer(playerName: String) {
        mutedPlayers.add(playerName)
    }

    fun unmutePlayer(playerName: String) {
        mutedPlayers.remove(playerName)
    }

    fun isMuted(playerName: String): Boolean {
        return mutedPlayers.contains(playerName)
    }
}
