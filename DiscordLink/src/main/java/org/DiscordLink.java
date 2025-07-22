package org;

import org.bukkit.plugin.java.JavaPlugin;

public class DiscordLink extends JavaPlugin {

    private static DiscordLink instance;
    private HttpServerManager httpServer;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        getCommand("discord").setExecutor(new LinkCommand());
        getServer().getPluginManager().registerEvents(new PlayerActivityListener(), this);

        int serverPort = getConfig().getInt("http-server.port", 8080);
        httpServer = new HttpServerManager(serverPort);
        httpServer.start();

        getLogger().info("DiscordLink enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (httpServer != null) {
            httpServer.stop();
        }

        PlaytimeStorage.saveAllSessions();
        getLogger().info("DiscordLink disabled");
    }

    public static DiscordLink getInstance() {
        return instance;
    }
}