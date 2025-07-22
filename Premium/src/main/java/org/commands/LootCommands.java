package org.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.lootboxes.LootBoxManager;
import org.lootboxes.LootType;

import java.util.List;


public class LootCommands implements CommandExecutor {

    private final LootBoxManager lootBoxManager;
    private final JavaPlugin plugin;

    public LootCommands(LootBoxManager lootBoxManager, JavaPlugin plugin) {
        this.lootBoxManager = lootBoxManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("lootadd")) {
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /lootadd <yellow|green|black>");
                return true;
            }
            openAddGUI(player, args[0].toLowerCase());
            return true;
        }

        if (command.getName().equalsIgnoreCase("lootremove")) {
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /lootremove <yellow|green|black>");
                return true;
            }
            openRemoveGUI(player, args[0].toLowerCase());
            return true;
        }

        if (command.getName().equalsIgnoreCase("lootclear")) {
            lootBoxManager.clearAllLootBoxes();
            player.sendMessage(ChatColor.GREEN + "All loot boxes cleared!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("clearitems")) {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof ArmorStand || entity instanceof Item) {
                        entity.remove();
                    }
                }
            }
            sender.sendMessage(ChatColor.GREEN + "All heads, holograms, and items have been removed.");
            return true;
        }

        return false;
    }

    private void openAddGUI(Player player, String type) {
        if (!isValidType(type)) {
            player.sendMessage(ChatColor.RED + "Invalid type! Use: yellow, green, or black");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 54, "Add Items - " + type.toUpperCase());
        player.openInventory(inv);
        player.setMetadata("loot_add_type", new org.bukkit.metadata.FixedMetadataValue(plugin, type));
    }

    private void openRemoveGUI(Player player, String type) {
        if (!isValidType(type)) {
            player.sendMessage(ChatColor.RED + "Invalid type! Use: yellow, green, or black");
            return;
        }

        List<ItemStack> items = lootBoxManager.getItemsForType(LootType.valueOf(type.toUpperCase()));
        Inventory inv = Bukkit.createInventory(null, 54, "Remove Items - " + type.toUpperCase());

        for (int i = 0; i < items.size() && i < 54; i++) {
            inv.setItem(i, items.get(i));
        }

        player.openInventory(inv);
        player.setMetadata("loot_remove_type", new org.bukkit.metadata.FixedMetadataValue(plugin, type));
    }

    private boolean isValidType(String type) {
        return type.equals("yellow") || type.equals("green") || type.equals("black");
    }
}
