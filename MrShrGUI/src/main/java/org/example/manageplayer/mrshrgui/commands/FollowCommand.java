package org.example.manageplayer.mrshrgui.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.manageplayer.mrshrgui.MrShrGUI;

public class FollowCommand implements CommandExecutor {


    private void sendFollowRequestToProxy(Player requester, String targetName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(requester.getName());
        out.writeUTF(targetName);

        requester.sendPluginMessage(MrShrGUI.getInstance(), "FollowRequest", out.toByteArray());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is for players only.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /follow <player>");
            return true;
        }

        String targetName = args[0];

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("FollowRequest");
        out.writeUTF(player.getName());
        out.writeUTF(targetName);

        player.sendPluginMessage(MrShrGUI.getInstance(), "BungeeCord", out.toByteArray());

        player.sendMessage(ChatColor.YELLOW + "Trying to follow " + targetName + " across servers...");
        return true;
    }
}