package org.lootboxes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DesignBoxConfig {
    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration config;

    public DesignBoxConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        setupConfig();
    }

    private void setupConfig() {
        configFile = new File(plugin.getDataFolder(), "design_boxes.yml");
        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();
            config = new YamlConfiguration();

            config.set("hologram-height", 0.0);
            config.set("box-height", -1.0);
            config.set("rotation-enabled", true);

            config.set("hologram-text.YELLOW", "&8➡ &eYellow loot");
            config.set("hologram-text.GREEN", "&8➡ &aGreen loot");
            config.set("hologram-text.BLACK", "&8➡ &0Black loot");

            config.createSection("boxes");

            try {
                config.save(configFile);
                plugin.getLogger().info("Created new design_boxes.yml file");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create design_boxes.yml: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getHologramText(LootType type) {
        if (config == null) {
            plugin.getLogger().warning("Config is null!");
            return "&f✦ LOOT BOX ✦";
        }

        String key = "hologram-text." + type.name();
        String value = config.getString(key);

        if (value == null) {
            plugin.getLogger().warning("Hologram text not found for key: " + key);
            switch (type) {
                case YELLOW: return "&8➡ Yellow loot";
                case GREEN: return "&8➡ Green loot";
                case BLACK: return "&8➡ &0Black loot";
                default: return "&8➡ loot";
            }
        }

        return value;
    }

    public String getBoxName(LootType type) {
        String defaultName;
        switch (type) {
            case YELLOW:
                defaultName = "&8➡ Yellow loot";
                break;
            case GREEN:
                defaultName = "&8➡ Green loot";
                break;
            case BLACK:
                defaultName = "&8➡ &0Black loot";
                break;
            default:
                defaultName = "&8➡ loot";
                break;
        }
        return config.getString("box-names." + type.name(), defaultName);
    }

    public String getHologramColor(LootType type) {
        String defaultColor;
        switch (type) {
            case YELLOW:
                defaultColor = "&e";
                break;
            case GREEN:
                defaultColor = "&a";
                break;
            case BLACK:
                defaultColor = "&8";
                break;
            default:
                defaultColor = "&f";
                break;
        }
        return config.getString("hologram-colors." + type.name(), defaultColor);
    }

    public String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save design_boxes.yml: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public double getHologramHeight() {
        return config.getDouble("hologram-height", 2.5);
    }

    public double getBoxHeight() {
        return config.getDouble("box-height", 0.0);
    }

    public boolean isRotationEnabled() {
        return config.getBoolean("rotation-enabled", true);
    }

    public void saveDesignBox(UUID boxId, Location location, LootType type) {
        String path = "boxes." + boxId.toString();
        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", location.getYaw());
        config.set(path + ".pitch", location.getPitch());
        config.set(path + ".type", type.name());
        saveConfig();
    }

    public void removeDesignBox(UUID boxId) {
        config.set("boxes." + boxId.toString(), null);
        saveConfig();
    }

    public Map<UUID, BoxData> loadDesignBoxes() {
        Map<UUID, BoxData> boxes = new HashMap<>();
        if (config.getConfigurationSection("boxes") == null) {
            return boxes;
        }

        for (String key : config.getConfigurationSection("boxes").getKeys(false)) {
            try {
                UUID boxId = UUID.fromString(key);
                String worldName = config.getString("boxes." + key + ".world");
                World world = Bukkit.getWorld(worldName);

                if (world == null) continue;

                double x = config.getDouble("boxes." + key + ".x");
                double y = config.getDouble("boxes." + key + ".y");
                double z = config.getDouble("boxes." + key + ".z");
                float yaw = (float) config.getDouble("boxes." + key + ".yaw");
                float pitch = (float) config.getDouble("boxes." + key + ".pitch");

                Location location = new Location(world, x, y, z, yaw, pitch);
                LootType type = LootType.valueOf(config.getString("boxes." + key + ".type"));

                boxes.put(boxId, new BoxData(location, type));
            } catch (Exception e) {
                plugin.getLogger().warning("Error loading design box: " + key);
            }
        }
        return boxes;
    }

    public static class BoxData {
        private final Location location;
        private final LootType type;

        public BoxData(Location location, LootType type) {
            this.location = location;
            this.type = type;
        }

        public Location getLocation() {
            return location;
        }

        public LootType getType() {
            return type;
        }
    }
}