package org.example.manageplayer.mrshrgui.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.example.manageplayer.mrshrgui.FollowDataManager;
import org.example.manageplayer.mrshrgui.MrShrGUI;

public class MovementListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
                e.getFrom().getBlockY() == e.getTo().getBlockY() &&
                e.getFrom().getBlockZ() == e.getTo().getBlockZ()) {
            return;
        }

        String serverName = MrShrGUI.getInstance().getServer().getName();
        FollowDataManager.savePlayerLocation(player.getName(), player.getLocation(), serverName);
    }
}