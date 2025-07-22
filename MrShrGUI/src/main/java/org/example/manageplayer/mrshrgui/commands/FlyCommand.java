package org.example.manageplayer.mrshrgui.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only Admins can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("fly.use")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(ChatColor.RED + "You can not use flight in this mode (SPECTATOR).");
            return true;
        }

        boolean canFly = player.getAllowFlight();
        player.setAllowFlight(!canFly);
        player.setFlying(!canFly);

        if (!canFly) {
            player.sendMessage(ChatColor.GOLD + "Set fly mode" + ChatColor.GREEN + " " + "enabled" + ChatColor.GOLD + " " + "for" + " " + ChatColor.GREEN + player.getDisplayName());
        } else {
            player.sendMessage(ChatColor.GOLD + "Set fly mode" + ChatColor.RED + " " + "disabled" + ChatColor.GOLD + " " + "for" + " " + ChatColor.GREEN + player.getDisplayName());
        }

        return true;
    }
}

