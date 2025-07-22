package org.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.lootboxes.LootBoxManager;

public class ExcludeCommands implements CommandExecutor {
    private final LootBoxManager lootBoxManager;
    private final JavaPlugin plugin;

    public ExcludeCommands(LootBoxManager lootBoxManager, JavaPlugin plugin) {
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

        if (command.getName().equalsIgnoreCase("excludepos1")) {
            Location loc = player.getLocation();
            player.setMetadata("exclude_pos1", new FixedMetadataValue(plugin, loc));
            player.sendMessage(ChatColor.RED + "Exclude position 1 set at: " +
                    ChatColor.WHITE + "X:" + (int)loc.getX() + " Y:" + (int)loc.getY() + " Z:" + (int)loc.getZ());
            return true;
        }

        if (command.getName().equalsIgnoreCase("excludepos2")) {
            Location loc = player.getLocation();
            player.setMetadata("exclude_pos2", new FixedMetadataValue(plugin, loc));

            if (player.hasMetadata("exclude_pos1")) {
                Location pos1 = (Location) player.getMetadata("exclude_pos1").get(0).value();
                lootBoxManager.addExcludedRegion(pos1, loc);
                lootBoxManager.saveRegions();

                player.sendMessage(ChatColor.RED + "Exclude position 2 set at: " +
                        ChatColor.WHITE + "X:" + (int)loc.getX() + " Y:" + (int)loc.getY() + " Z:" + (int)loc.getZ());
                player.sendMessage(ChatColor.YELLOW + "Excluded region has been saved! Loot boxes will not spawn in this area.");

                player.removeMetadata("exclude_pos1", plugin);
                player.removeMetadata("exclude_pos2", plugin);
            } else {
                player.sendMessage(ChatColor.RED + "You must set excludepos1 first!");
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("excludeclear")) {
            lootBoxManager.clearExcludedRegions();
            lootBoxManager.saveRegions();
            player.sendMessage(ChatColor.GREEN + "All excluded regions have been cleared!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("excludelist")) {
            player.sendMessage(ChatColor.YELLOW + "Excluded Regions:");
            int count = 0;
            for (LootBoxManager.RegionData region : lootBoxManager.getExcludedRegions()) {
                count++;
                Location pos1 = region.getPos1();
                Location pos2 = region.getPos2();
                player.sendMessage(ChatColor.WHITE + String.valueOf(count) + ". From X:" + (int)pos1.getX() + " Y:" + (int)pos1.getY() + " Z:" + (int)pos1.getZ() +
                        " to X:" + (int)pos2.getX() + " Y:" + (int)pos2.getY() + " Z:" + (int)pos2.getZ());
            }
            if (count == 0) {
                player.sendMessage(ChatColor.GRAY + "No excluded regions set.");
            }
            return true;
        }

        return false;
    }
}