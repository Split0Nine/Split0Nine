package org;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Shrbattlepass extends JavaPlugin implements Listener, CommandExecutor {

    private File dataFile;
    private FileConfiguration dataConfig;
    private Map<UUID, PlayerData> playerData;

    @Override
    public void onEnable() {
        playerData = new HashMap<>();

        createDataFile();

        loadPlayerData();

        getServer().getPluginManager().registerEvents(this, this);

        getCommand("battlepass").setExecutor(this);



        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Shrbattlepass2(this).register();
            getLogger().info("PlaceholderAPI hooked successfully!");
        }



        getLogger().info("Shrbattlepass has been enabled!");
    }

    @Override
    public void onDisable() {
        savePlayerData();
        getLogger().info("Shrbattlepass has been disabled!");
    }

    private void createDataFile() {
        dataFile = new File(getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        getLogger().info("Created playerdata.yml: " + dataFile.exists());
    }

    private void loadPlayerData() {
        if (dataConfig.getConfigurationSection("players") == null) return;

        for (String playerName : dataConfig.getConfigurationSection("players").getKeys(false)) {
            String uuidStr = dataConfig.getString("players." + playerName + ".UUID");
            if (uuidStr == null) continue;

            UUID uuid = UUID.fromString(uuidStr);
            int level = dataConfig.getInt("players." + playerName + ".level", 1);
            int xp = dataConfig.getInt("players." + playerName + ".xp", 0);
            int maxXp = dataConfig.getInt("players." + playerName + ".maxXp", 100);

            playerData.put(uuid, new PlayerData(level, xp, maxXp));
        }
    }


    public void savePlayerData() {
        for (Map.Entry<UUID, PlayerData> entry : playerData.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerData data = entry.getValue();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            String name = player.getName();

            dataConfig.set("players." + name + ".UUID", uuid.toString());
            dataConfig.set("players." + name + ".level", data.getLevel());
            dataConfig.set("players." + name + ".xp", data.getXp());
            dataConfig.set("players." + name + ".maxXp", data.getMaxXp());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String currentName = player.getName();

        String matchedName = null;

        if (dataConfig.getConfigurationSection("players") != null) {
            for (String storedName : dataConfig.getConfigurationSection("players").getKeys(false)) {
                String storedUUID = dataConfig.getString("players." + storedName + ".UUID");
                if (storedUUID != null && storedUUID.equals(uuid.toString())) {
                    matchedName = storedName;
                    break;
                }
            }
        }

        if (matchedName != null && !matchedName.equals(currentName)) {
            Object oldData = dataConfig.get("players." + matchedName);

            dataConfig.set("players." + currentName, oldData);

            int index = 1;
            while (dataConfig.contains("players." + currentName + ".oldname" + (index == 1 ? "" : index))) {
                index++;
            }
            dataConfig.set("players." + currentName + ".oldname" + (index == 1 ? "" : index), matchedName);

            dataConfig.set("players." + matchedName, null);

            saveDataFile();
        }

        if (!playerData.containsKey(uuid)) {
            int level = dataConfig.getInt("players." + currentName + ".level", 1);
            int xp = dataConfig.getInt("players." + currentName + ".xp", 0);
            int maxXp = dataConfig.getInt("players." + currentName + ".maxXp", 100);

            playerData.put(uuid, new PlayerData(level, xp, maxXp));

            if (!dataConfig.contains("players." + currentName + ".UUID")) {
                dataConfig.set("players." + currentName + ".UUID", uuid.toString());
                saveDataFile();
            }
        }
    }

        private void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("battlepass")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GOLD + "=== Shrbattlepass Commands ===");
                sender.sendMessage(ChatColor.YELLOW + "/battlepass addxp <player> <amount> - Add XP to player");
                sender.sendMessage(ChatColor.YELLOW + "/battlepass setlevel <player> <level> - Set player level");
                sender.sendMessage(ChatColor.YELLOW + "/battlepass info <player> - Show player info");
                return true;
            }

            if (args[0].equalsIgnoreCase("addxp") && args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }

                try {
                    int amount = Integer.parseInt(args[2]);
                    addXP(target, amount);
                    sender.sendMessage(ChatColor.GREEN + "Added " + amount + " XP to " + target.getName());
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number!");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("setlevel") && args.length == 3) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }

                try {
                    int level = Integer.parseInt(args[2]);
                    setLevel(target, level);
                    sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s level to " + level);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number!");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("info") && args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }

                PlayerData data = getPlayerData(target);
                sender.sendMessage(ChatColor.GOLD + "=== " + target.getName() + " BattlePass Info ===");
                sender.sendMessage(ChatColor.YELLOW + "Level: " + data.getLevel());
                sender.sendMessage(ChatColor.YELLOW + "XP: " + data.getXp() + "/" + data.getMaxXp());
                sender.sendMessage(ChatColor.YELLOW + "Progress: " + getProgressPercentage(target) + "%");
                return true;
            }
        }
        return false;
    }

    public PlayerData getPlayerData(Player player) {
        return playerData.getOrDefault(player.getUniqueId(), new PlayerData(1, 0, 100));
    }

    public void addXP(Player player, int amount) {
        PlayerData data = getPlayerData(player);
        data.addXp(amount);

        while (data.getXp() >= data.getMaxXp()) {
            data.setXp(data.getXp() - data.getMaxXp());
            data.setLevel(data.getLevel() + 1);
            data.setMaxXp(data.getMaxXp() + 50);

            player.sendMessage(ChatColor.GOLD + "★ " + ChatColor.AQUA + ChatColor.BOLD + "BattlePass" +
                    ChatColor.RESET + ChatColor.GOLD + " ★");
            player.sendMessage(ChatColor.GREEN + "Level Up! You are now level " + data.getLevel());
        }

        playerData.put(player.getUniqueId(), data);
        savePlayerData();
    }

    public void setLevel(Player player, int level) {
        PlayerData data = getPlayerData(player);
        data.setLevel(level);
        data.setXp(0);
        data.setMaxXp(100 + (level - 1) * 50);
        playerData.put(player.getUniqueId(), data);
        savePlayerData();
    }

    public int getLevel(Player player) {
        return getPlayerData(player).getLevel();
    }

    public int getProgressPercentage(Player player) {
        PlayerData data = getPlayerData(player);
        return (int) ((double) data.getXp() / data.getMaxXp() * 100);
    }

    public String getProgressBar(Player player) {
        int percentage = getProgressPercentage(player);
        int filled = percentage / 10;

        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < filled; i++) {
            bar.append(ChatColor.GREEN + "■");
        }

        for (int i = filled; i < 10; i++) {
            bar.append(ChatColor.WHITE + "■");
        }

        return bar.toString();
    }

    public static class PlayerData {
        private int level;
        private int xp;
        private int maxXp;

        public PlayerData(int level, int xp, int maxXp) {
            this.level = level;
            this.xp = xp;
            this.maxXp = maxXp;
        }

        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }

        public int getXp() { return xp; }
        public void setXp(int xp) { this.xp = xp; }

        public int getMaxXp() { return maxXp; }
        public void setMaxXp(int maxXp) { this.maxXp = maxXp; }

        public void addXp(int amount) { this.xp += amount; }
    }
}
