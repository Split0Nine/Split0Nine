package org.Shr;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.cacheddata.CachedMetaData;



public class chatcommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("clearchat.use")) {
            sender.sendMessage(ChatColor.RED + "you don't have permission! (✖)");
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 100; i++) {
                player.sendMessage(" ");
            }
            String coloredName = getColoredName((Player) sender);
        }

        return true;
    }

    private String getColoredName(Player player) {
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) return player.getName();

            CachedMetaData meta = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions());
            String prefix = meta.getPrefix();
            return (prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) : "") + player.getName();

        } catch (Exception e) {
            return player.getName();
        }
    }
}