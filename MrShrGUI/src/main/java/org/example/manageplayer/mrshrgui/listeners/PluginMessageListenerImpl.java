package org.example.manageplayer.mrshrgui.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PluginMessageListenerImpl implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player receiver, byte[] message) {
        if (!channel.equals("Follow")) return;

        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subChannel = in.readUTF();

            if (subChannel.equals("FollowLocation")) {
                String requester = in.readUTF();
                String targetName = in.readUTF();
                String world = in.readUTF();
                double x = in.readDouble();
                double y = in.readDouble();
                double z = in.readDouble();
                float yaw = in.readFloat();
                float pitch = in.readFloat();

                Player requesterPlayer = Bukkit.getPlayerExact(requester);
                if (requesterPlayer != null) {
                    Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                    requesterPlayer.teleport(location);
                    requesterPlayer.sendMessage("§aYou are now following §e" + targetName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}