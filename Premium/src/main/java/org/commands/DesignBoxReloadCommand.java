package org.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.lootboxes.LootBoxManager;

public class DesignBoxReloadCommand implements CommandExecutor {

    private final LootBoxManager lootBoxManager;

    public DesignBoxReloadCommand(LootBoxManager lootBoxManager) {
        this.lootBoxManager = lootBoxManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lootbox.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }

        try {
            lootBoxManager.reloadDesignBoxConfig();
            sender.sendMessage(ChatColor.GREEN + "Design boxes configuration reloaded successfully!");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error reloading configuration: " + e.getMessage());
        }

        return true;
    }
}