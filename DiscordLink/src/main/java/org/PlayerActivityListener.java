package org;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

public class PlayerActivityListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlaytimeStorage.startSession(player);

        DiscordLink.getInstance().getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") joined the server");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlaytimeStorage.endSession(player);

        DiscordLink.getInstance().getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") left the server");
    }
}