package org.example.manageplayer.mrshrgui;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.manageplayer.mrshrgui.commands.FlyCommand;
import org.example.manageplayer.mrshrgui.commands.FollowCommand;
import org.example.manageplayer.mrshrgui.commands.ManageCommand;
import org.example.manageplayer.mrshrgui.listeners.MenuListener;
import org.example.manageplayer.mrshrgui.listeners.PluginMessageListenerImpl;
import org.example.manageplayer.mrshrgui.listeners.FollowRequestListener;

public final class MrShrGUI extends JavaPlugin {

    private static MrShrGUI instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("manage").setExecutor(new ManageCommand());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("follow").setExecutor(new FollowCommand());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "FollowRequest");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "Follow");

        getServer().getMessenger().registerIncomingPluginChannel(this, "FollowRequest", new FollowRequestListener());
        getServer().getMessenger().registerIncomingPluginChannel(this, "Follow", new PluginMessageListenerImpl());

        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        getLogger().info("MrShrGUI Enabled Successfully");
    }

    @Override
    public void onDisable() {
        getLogger().info("MrShrGUI Disabled");
    }

    public static MrShrGUI getInstance() {
        return instance;
    }

    public static void sendFollowRequestToProxy(String adminName, String targetName) {
        Player player = Bukkit.getPlayerExact(adminName);
        if (player != null) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(adminName);
            out.writeUTF(targetName);
            player.sendPluginMessage(instance, "FollowRequest", out.toByteArray());
        }
    }
}