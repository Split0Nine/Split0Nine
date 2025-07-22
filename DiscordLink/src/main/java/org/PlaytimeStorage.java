package org;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeStorage {

    private static final HashMap<UUID, Long> sessions = new HashMap<>();
    private static final HashMap<UUID, Integer> tempPlaytime = new HashMap<>();
    private static YamlConfiguration config;
    private static File file;

    static {
        file = new File(DiscordLink.getInstance().getDataFolder(), "playtime.yml");
        config = YamlConfiguration.loadConfiguration(file);

        new BukkitRunnable() {
            @Override
            public void run() {
                savePlaytimeData();
            }
        }.runTaskTimer(DiscordLink.getInstance(), 6000L, 6000L);
    }

    public static void startSession(Player player) {
        UUID uuid = player.getUniqueId();
        sessions.put(uuid, System.currentTimeMillis());

        if (tempPlaytime.containsKey(uuid)) {
            int currentPlaytime = config.getInt(uuid.toString(), 0);
            config.set(uuid.toString(), currentPlaytime + tempPlaytime.get(uuid));
            tempPlaytime.remove(uuid);
        }

        DiscordLink.getInstance().getLogger().info("Started session for player: " + player.getName());
    }

    public static void endSession(Player player) {
        UUID uuid = player.getUniqueId();

        if (!sessions.containsKey(uuid)) {
            DiscordLink.getInstance().getLogger().warning("No session found for player: " + player.getName());
            return;
        }

        long sessionStart = sessions.get(uuid);
        long sessionDuration = System.currentTimeMillis() - sessionStart;
        int minutesPlayed = (int) (sessionDuration / 1000 / 60);

        tempPlaytime.put(uuid, tempPlaytime.getOrDefault(uuid, 0) + minutesPlayed);

        sessions.remove(uuid);

        DiscordLink.getInstance().getLogger().info("Ended session for player: " + player.getName() + " (+" + minutesPlayed + " minutes)");
    }

    public static int getPlaytimeHours(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        int savedMinutes = config.getInt(uuid.toString(), 0);
        int tempMinutes = tempPlaytime.getOrDefault(uuid, 0);

        int currentSessionMinutes = 0;
        if (sessions.containsKey(uuid)) {
            long sessionStart = sessions.get(uuid);
            long sessionDuration = System.currentTimeMillis() - sessionStart;
            currentSessionMinutes = (int) (sessionDuration / 1000 / 60);
        }

        int totalMinutes = savedMinutes + tempMinutes + currentSessionMinutes;
        return totalMinutes / 60;
    }

    public static int getPlaytimeMinutes(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        int savedMinutes = config.getInt(uuid.toString(), 0);
        int tempMinutes = tempPlaytime.getOrDefault(uuid, 0);

        int currentSessionMinutes = 0;
        if (sessions.containsKey(uuid)) {
            long sessionStart = sessions.get(uuid);
            long sessionDuration = System.currentTimeMillis() - sessionStart;
            currentSessionMinutes = (int) (sessionDuration / 1000 / 60);
        }

        return savedMinutes + tempMinutes + currentSessionMinutes;
    }

    public static void savePlaytimeData() {
        try {
            for (Map.Entry<UUID, Integer> entry : tempPlaytime.entrySet()) {
                UUID uuid = entry.getKey();
                int minutes = entry.getValue();
                int currentPlaytime = config.getInt(uuid.toString(), 0);
                config.set(uuid.toString(), currentPlaytime + minutes);
            }

            tempPlaytime.clear();
            config.save(file);

            DiscordLink.getInstance().getLogger().info("Playtime data saved successfully");
        } catch (IOException e) {
            DiscordLink.getInstance().getLogger().severe("Failed to save playtime data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveAllSessions() {
        DiscordLink.getInstance().getLogger().info("Saving all active sessions...");

        for (Map.Entry<UUID, Long> entry : sessions.entrySet()) {
            UUID uuid = entry.getKey();
            long sessionStart = entry.getValue();
            long sessionDuration = System.currentTimeMillis() - sessionStart;
            int minutesPlayed = (int) (sessionDuration / 1000 / 60);

            tempPlaytime.put(uuid, tempPlaytime.getOrDefault(uuid, 0) + minutesPlayed);
        }

        sessions.clear();
        savePlaytimeData();
    }

    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file);
        DiscordLink.getInstance().getLogger().info("Playtime configuration reloaded");
    }
}