package org.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lootboxes.LootBoxManager;

public class RegionCommands implements CommandExecutor {

    private final LootBoxManager lootBoxManager;

    public RegionCommands(LootBoxManager lootBoxManager) {
        this.lootBoxManager = lootBoxManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("pos1")) {
            Location loc = player.getLocation();
            lootBoxManager.setPos1(loc);
            player.sendMessage(ChatColor.GREEN + "Position 1 set at: " +
                    ChatColor.WHITE + "X:" + (int)loc.getX() + " Y:" + (int)loc.getY() + " Z:" + (int)loc.getZ());
            return true;
        }

        if (command.getName().equalsIgnoreCase("pos2")) {
            Location loc = player.getLocation();
            lootBoxManager.setPos2(loc);
            player.sendMessage(ChatColor.GREEN + "Position 2 set at: " +
                    ChatColor.WHITE + "X:" + (int)loc.getX() + " Y:" + (int)loc.getY() + " Z:" + (int)loc.getZ());

            if (lootBoxManager.getPos1() != null && lootBoxManager.getPos2() != null) {
                lootBoxManager.saveRegions();
                player.sendMessage(ChatColor.YELLOW + "Loot box spawn region has been saved!");
            }
            return true;
        }

        return false;
    }
}