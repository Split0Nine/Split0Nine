package org.commands;

import org.KitManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.menus.KitGUI;

public class KitCommand implements CommandExecutor {

    private final KitManager kitManager;

    public KitCommand(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            KitGUI.openKitGUI(player, kitManager);
            return true;
        }

        if (args.length == 1) {
            String kitName = args[0];

            if (kitManager.getKits().containsKey(kitName)) {
                kitManager.giveKit(player, kitName);
            } else {
                player.sendMessage("§cKit not found! Available kits: Player, Gold, Diamond, Emerald");
            }
            return true;
        }

        player.sendMessage("§cUsage: /kit [kitname]");
        return true;
    }
}
