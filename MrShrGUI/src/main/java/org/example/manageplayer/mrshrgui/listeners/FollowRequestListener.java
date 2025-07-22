package org.example.manageplayer.mrshrgui.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.example.manageplayer.mrshrgui.MrShrGUI;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class FollowRequestListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player receiver, byte[] message) {
        if (!channel.equals("FollowRequest")) return;

        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String requester = in.readUTF();
            String targetName = in.readUTF();

            Player target = Bukkit.getPlayerExact(targetName);
            if (target != null) {
                Location loc = target.getLocation();

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("FollowLocation");
                out.writeUTF(requester);
                out.writeUTF(targetName);
                out.writeUTF(loc.getWorld().getName());
                out.writeDouble(loc.getX());
                out.writeDouble(loc.getY());
                out.writeDouble(loc.getZ());
                out.writeFloat(loc.getYaw());
                out.writeFloat(loc.getPitch());

                target.sendPluginMessage(MrShrGUI.getInstance(), "Follow", out.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}