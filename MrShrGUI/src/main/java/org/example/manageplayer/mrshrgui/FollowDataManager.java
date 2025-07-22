package org.example.manageplayer.mrshrgui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FollowDataManager {

    private static final File folder = new File(Bukkit.getServer().getPluginManager()
            .getPlugin("MrShrGUI").getDataFolder(), "followdata");

    public static File getPlayerDataFile(String playerName) {
        if (!folder.exists()) folder.mkdirs();
        return new File(folder, playerName + ".yml");
    }

    public static Location getLastLocation(String playerName) {
        File file = getPlayerDataFile(playerName);
        if (!file.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.contains("world")) return null;

        return new Location(
                Bukkit.getWorld(config.getString("world")),
                config.getDouble("x"),
                config.getDouble("y"),
                config.getDouble("z"),
                (float) config.getDouble("yaw"),
                (float) config.getDouble("pitch")
        );
    }

    public static String getLastServer(String playerName) {
        File file = getPlayerDataFile(playerName);
        if (!file.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getString("server");
    }

    public static void savePlayerLocation(String playerName, Location loc, String serverName) {
        if (!folder.exists()) folder.mkdirs();
        File file = getPlayerDataFile(playerName);
        YamlConfiguration config = new YamlConfiguration();

        config.set("world", loc.getWorld().getName());
        config.set("x", loc.getX());
        config.set("y", loc.getY());
        config.set("z", loc.getZ());
        config.set("yaw", loc.getYaw());
        config.set("pitch", loc.getPitch());
        config.set("server", serverName);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}