package org.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.lootboxes.LootBoxManager;
import org.lootboxes.LootType;

public class LootDesignCommand implements CommandExecutor {

    private final LootBoxManager lootBoxManager;

    public LootDesignCommand(LootBoxManager lootBoxManager) {
        this.lootBoxManager = lootBoxManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /lootdesign <yellow/green/black>");
            return true;
        }

        String typeString = args[0].toLowerCase();
        LootType lootType;

        switch (typeString) {
            case "yellow":
                lootType = LootType.YELLOW;
                break;
            case "green":
                lootType = LootType.GREEN;
                break;
            case "black":
                lootType = LootType.BLACK;
                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid type! Use: yellow, green, or black");
                return true;
        }

        Location spawnLocation = player.getLocation().clone().add(0, 1, 0);
        ArmorStand designBox = lootBoxManager.createDesignLootBox(spawnLocation, lootType);

        player.sendMessage(ChatColor.GREEN + "Design " + lootType.name().toLowerCase() + " loot box spawned!");
        return true;
    }
}