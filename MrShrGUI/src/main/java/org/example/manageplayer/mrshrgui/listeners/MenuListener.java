package org.example.manageplayer.mrshrgui.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.manageplayer.mrshrgui.menus.ManageMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuListener implements Listener {

    private final Map<UUID, String> manageTargets = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        Player player = (Player) e.getWhoClicked();
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        if (title.startsWith(ChatColor.RED + "Manage ")) {
            e.setCancelled(true);

            ItemStack head = e.getInventory().getItem(4);
            if (head == null || !head.hasItemMeta()) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Player can't be found.");
                return;
            }

            String targetName = ChatColor.stripColor(head.getItemMeta().getDisplayName());
            Player target = Bukkit.getPlayerExact(targetName);
            if (target == null) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Player is not online.");
                return;
            }

            manageTargets.put(player.getUniqueId(), target.getName());

            if (clicked.getType() == Material.INK_SACK && clicked.getDurability() == 1) {
                target.setHealth(20);
                player.sendMessage(ChatColor.DARK_GREEN + "Player has been healed.");
                player.closeInventory();
            } else if (clicked.getType() == Material.IRON_SWORD) {
                target.setHealth(0);
                player.sendMessage(ChatColor.DARK_RED + "Player has been killed.");
                player.closeInventory();
            } else if (clicked.getType() == Material.NETHER_STAR) {
                player.closeInventory();
                ManageMenu.openGameModeMenu(player, target);
            }
        }

        else if (title.equals(ChatColor.RED + "Mode Manage " + ChatColor.WHITE + manageTargets.get(player.getUniqueId()))) {
            e.setCancelled(true);

            String targetName = manageTargets.get(player.getUniqueId());
            Player target = (targetName != null) ? Bukkit.getPlayerExact(targetName) : null;

            if (clicked.getType() == Material.NETHER_STAR) {
                if (e.isLeftClick()) {
                    GameMode current = target.getGameMode();
                    GameMode next = (current == GameMode.CREATIVE) ? GameMode.SURVIVAL
                            : (current == GameMode.SURVIVAL) ? GameMode.ADVENTURE
                            : GameMode.CREATIVE;
                    target.setGameMode(next);
                    player.sendMessage(ChatColor.DARK_GREEN + "Your mode has been changed to : " + ChatColor.GOLD + next.name());
                } else if (e.isRightClick()) {
                    if (target != null && target.isOnline()) {
                        Location loc = player.getLocation().add(player.getLocation().getDirection().setY(0).normalize().multiply(1.5));
                        target.teleport(loc);
                        player.sendMessage(ChatColor.GOLD + "Player has been teleported to you : " + ChatColor.WHITE + target.getName());
                    } else {
                        player.sendMessage(ChatColor.RED + "Player is not online.");
                    }
                }
            }
        }
    }
}