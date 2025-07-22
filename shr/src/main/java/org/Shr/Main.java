package org.Shr;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerRespawnEvent;



import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    private String getPlayTime(Player player) {
        int ticks = player.getStatistic(org.bukkit.Statistic.PLAY_ONE_TICK);
        int totalSeconds = ticks / 20;

        int days = totalSeconds / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder time = new StringBuilder();

        if (days > 0) time.append(days).append("D ");
        if (hours > 0) time.append(hours).append("H ");
        if (minutes > 0) time.append(minutes).append("M ");
        if (seconds > 0 || time.length() == 0) time.append(seconds).append("S");

        return time.toString().trim();
    }

    private final String PROFILE_ITEM_NAME = "§aProfile Menu";


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("clearchat").setExecutor(new chatcommand());
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Shr enabled!");
        getServer().getScheduler().runTaskLater(this, () -> {
            try {
                LuckPerms test = LuckPermsProvider.get();
                getLogger().info("LuckPerms successfully loaded! Primary groups available.");
            } catch (Exception e) {
                getLogger().warning("Failed to load LuckPerms!");
            }
        }, 20L);

    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            giveProfileHead(event.getPlayer());
        }, 10L);
    }



    private void giveProfileHead(Player player) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName(PROFILE_ITEM_NAME);
        skull.setItemMeta(meta);

        player.getInventory().setItem(6, skull);
        player.updateInventory();
    }

    @EventHandler
    public void onProfileHeadClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.SKULL_ITEM) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName() && meta.getDisplayName().equals(PROFILE_ITEM_NAME)) {
            event.setCancelled(true);
            openMenu(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equalsIgnoreCase("§ayour Profile")) {
            event.setCancelled(true);

            if (event.getRawSlot() == 13) {
                ItemStack item = event.getCurrentItem();
                if (isProtectedProfileItem(item)) {
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }

            ItemStack cursor = event.getCursor();
            if (isProtectedProfileItem(cursor)) {
                event.setCancelled(true);
                player.setItemOnCursor(null);
                player.updateInventory();
            }
        } else {
            if (event.getSlot() == 6 && isProtectedProfileItem(event.getCurrentItem())) {
                event.setCancelled(true);
                player.updateInventory();
            }

            ItemStack cursor = event.getCursor();
            if (isProtectedProfileItem(cursor)) {
                event.setCancelled(true);
                player.setItemOnCursor(null);
                player.updateInventory();
            }
        }
    }



    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equalsIgnoreCase("§ayour Profile")) {
            if (event.getRawSlots().contains(13)) {
                event.setCancelled(true);
            }
        } else {
            if (event.getRawSlots().contains(6) && isProtectedProfileItem(event.getOldCursor())) {
                event.setCancelled(true);
            }
        }
    }




    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isProtectedProfileItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreativeInventory(InventoryCreativeEvent event) {
        if (isProtectedProfileItem(event.getCursor())) {
            event.setCancelled(true);
        }
    }


    private boolean isProtectedProfileItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals(PROFILE_ITEM_NAME);
    }



    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            giveProfileHead(event.getPlayer());
        }, 5L);
    }

    private void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, "§ayour Profile");

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(player.getName());

        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        String colorCode = "§7";
        String groupName = "Unknown";
        String coloredRank = "§7Unknown";

        if (user != null) {
            groupName = user.getPrimaryGroup();
            coloredRank = getColoredRank(groupName);

            if (coloredRank != null && coloredRank.matches("^§[0-9a-fk-or].*")) {
                colorCode = coloredRank.substring(0, 2);
            }
        }

        meta.setDisplayName(colorCode + player.getName());

        int gifts = 0;
        int tokens = 1171;
        String playTime = getPlayTime(player);

        List<String> lore = new ArrayList<>();
        lore.add("§7Rank: " + coloredRank);
        lore.add("§7Gifts: §b" + gifts);
        lore.add("§7Tokens: §b" + tokens);
        lore.add("§7Online: §b" + playTime);
        meta.setLore(lore);

        skull.setItemMeta(meta);
        menu.setItem(13, skull);

        player.openInventory(menu);

    }


    private String getColoredRank(String rank) {
        switch (rank.toLowerCase()) {
            case "serverowner": return "§fOwner";
            case "founder": return "§aFounder";
            case "adminmanamger": return "§aManager";
            case "headadmin": return "§aHead Admin";
            case "admin": return "§4Admin";
            case "srmod": return "§cSR.Watcher";
            case "mod": return "§cWatcher";
            case "serverdeveloper": return "§1Developer";
            case "builder": return "&2Builder";
            case "designer": return "&3Designer";
            case "crystal": return "&5Crystal";
            case "pearls": return "&dPearls";
            case "emerald": return "§aEmerald";
            case "diamond": return "&bDiamond";
            case "gold": return "&6Gold";
            case "leather": return "&3Leather";
            case "default": return "§9Citizen";

            default: return "§f" + rank;
        }
    }
}