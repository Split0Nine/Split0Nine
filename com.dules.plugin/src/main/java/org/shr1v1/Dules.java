package org.shr1v1;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.*;


public class Dules extends JavaPlugin implements Listener, CommandExecutor {

    private Set<UUID> frozenPlayers = new HashSet<>();
    private Map<UUID, UUID> duelInvites = new HashMap<>();
    private Map<UUID, UUID> activeDuels = new HashMap<>();
    private Map<UUID, Location> playerOriginalLocation = new HashMap<>();
    private Map<UUID, ItemStack[]> playerOriginalInventory = new HashMap<>();
    private Map<UUID, Integer> playerWins = new HashMap<>();
    private Map<UUID, Integer> playerLosses = new HashMap<>();
    private Set<UUID> playersInDuel = new HashSet<>();
    private Set<UUID> recentlyInDuel = new HashSet<>();
    private Map<UUID, BukkitRunnable> playerTimers = new HashMap<>();

    private Location arenaSpawn1;
    private Location arenaSpawn2;
    private Location lobbySpawn;

    @Override
    public void onEnable() {
        getLogger().info("Dules Plugin 1.8 (PvP Duels System) enabled!");

        getServer().getPluginManager().registerEvents(this, this);

        getCommand("duel").setExecutor(this);
        getCommand("duelaccept").setExecutor(this);
        getCommand("dueldeny").setExecutor(this);
        getCommand("duelstats").setExecutor(this);
        getCommand("duelsetup").setExecutor(this);

        saveDefaultConfig();

        arenaSpawn1 = loadLocationFromConfig("arena.spawn1");
        arenaSpawn2 = loadLocationFromConfig("arena.spawn2");
        lobbySpawn = loadLocationFromConfig("arena.lobby");


        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : new HashSet<>(playersInDuel)) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.sendMessage(ChatColor.RED + "Previous duel was canceled due to server restart.");
                        endDuel(player, false);
                    } else {
                        recentlyInDuel.add(uuid);
                    }
                }
            }
        }.runTaskLater(this, 20L);

        File file = new File(getDataFolder(), "duels.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDisable() {
        for (UUID playerUUID : new HashSet<>(playersInDuel)) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Server is shutting down. Duel ended.");
                endDuel(player, false);
            } else {
                recentlyInDuel.add(playerUUID);
            }
        }
        getLogger().info("Dules Plugin 1.8 disabled!");
    }


    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (playersInDuel.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }


    private void saveLocationToConfig(String path, Location loc) {
        getConfig().set(path + ".world", loc.getWorld().getName());
        getConfig().set(path + ".x", loc.getX());
        getConfig().set(path + ".y", loc.getY());
        getConfig().set(path + ".z", loc.getZ());
        getConfig().set(path + ".yaw", loc.getYaw());
        getConfig().set(path + ".pitch", loc.getPitch());
        saveConfig();
    }

    private Location loadLocationFromConfig(String path) {
        if (!getConfig().contains(path + ".world")) return null;
        World world = Bukkit.getWorld(getConfig().getString(path + ".world"));
        double x = getConfig().getDouble(path + ".x");
        double y = getConfig().getDouble(path + ".y");
        double z = getConfig().getDouble(path + ".z");
        float yaw = (float) getConfig().getDouble(path + ".yaw");
        float pitch = (float) getConfig().getDouble(path + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        File file = new File(getDataFolder(), "duels.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String uuidStr = playerUUID.toString();
        if (config.contains("duels." + uuidStr)) {
            String opponentUUID = config.getString("duels." + uuidStr + ".opponent");

            if (config.contains("duels." + uuidStr + ".inventory")) {
                ItemStack[] originalInventory = ((List<ItemStack>) config.get("duels." + uuidStr + ".inventory")).toArray(new ItemStack[0]);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.getInventory().setContents(originalInventory);
            }

            if (config.contains("duels." + uuidStr + ".location")) {
                World world = Bukkit.getWorld(config.getString("duels." + uuidStr + ".location.world"));
                double x = config.getDouble("duels." + uuidStr + ".location.x");
                double y = config.getDouble("duels." + uuidStr + ".location.y");
                double z = config.getDouble("duels." + uuidStr + ".location.z");
                Location originalLocation = new Location(world, x, y, z);

                if (lobbySpawn != null) {
                    player.teleport(lobbySpawn);
                } else {
                    player.teleport(originalLocation);
                }
            }

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

            player.sendMessage(ChatColor.RED + "You were removed from the duel due to server restart. No winner or loser was determined.");

            config.set("duels." + uuidStr, null);
            config.set("duels." + opponentUUID, null);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (!playerWins.containsKey(playerUUID)) {
            playerWins.put(playerUUID, 0);
            playerLosses.put(playerUUID, 0);
        }

        if (recentlyInDuel.contains(playerUUID)) {
            recentlyInDuel.remove(playerUUID);
            player.sendMessage(ChatColor.RED + "Previous duel was canceled due to disconnect. You have been returned to the lobby.");
            endDuel(player, false);
            return;
        }

        player.sendMessage(ChatColor.GOLD + "Welcome to Dules Server! Use " + ChatColor.YELLOW + "/duel" + ChatColor.GOLD + " to start dueling!");
    }



    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        duelInvites.remove(playerUUID);
        duelInvites.values().removeIf(uuid -> uuid.equals(playerUUID));

        if (playersInDuel.contains(playerUUID)) {
            UUID opponentUUID = activeDuels.get(playerUUID);
            Player opponent = Bukkit.getPlayer(opponentUUID);
            if (opponent != null) {
                opponent.sendMessage(ChatColor.GREEN + "Your opponent left the game. You win!");
                playerWins.put(opponentUUID, playerWins.getOrDefault(opponentUUID, 0) + 1);
                playerLosses.put(playerUUID, playerLosses.getOrDefault(playerUUID, 0) + 1);
                endDuel(opponent, true);
            }
            recentlyInDuel.add(playerUUID);
            endDuel(player, false);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();

        if (playersInDuel.contains(playerUUID)) {
            event.setDroppedExp(0);
            event.getDrops().clear();

            Bukkit.getScheduler().runTask(this, () -> player.spigot().respawn());

            UUID opponentUUID = activeDuels.get(playerUUID);
            Player opponent = Bukkit.getPlayer(opponentUUID);

            if (opponent != null) {
                playerWins.put(opponentUUID, playerWins.getOrDefault(opponentUUID, 0) + 1);
                playerLosses.put(playerUUID, playerLosses.getOrDefault(playerUUID, 0) + 1);


                Bukkit.broadcastMessage(ChatColor.GOLD + "=== DUEL RESULT ===");
                Bukkit.broadcastMessage(ChatColor.GREEN + "Winner: " + ChatColor.YELLOW + opponent.getName());
                Bukkit.broadcastMessage(ChatColor.RED + "Loser: " + ChatColor.GRAY + player.getName());
                Bukkit.broadcastMessage(ChatColor.GOLD + "==================");

                endDuel(opponent, true);
            }
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            endDuel(player, false);
        }
    }

    private void showScoreboard(Player player, int seconds, String mapName) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("duel", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "dules");

        String formattedTime = String.format("%02d:%02d", seconds / 60, seconds % 60);

        Team timeTeam = board.registerNewTeam("time");
        timeTeam.addEntry(ChatColor.WHITE.toString() + ChatColor.BOLD);
        timeTeam.setSuffix(formattedTime);

        Team ipTeam = board.registerNewTeam("ip");
        ipTeam.addEntry(ChatColor.DARK_GRAY.toString());
        ipTeam.setSuffix(ChatColor.AQUA.toString() + ChatColor.BOLD + "WixMC.com");

        objective.getScore(ChatColor.GRAY + new SimpleDateFormat("MMM dd | hh:mm a").format(new Date())).setScore(15);
        objective.getScore(ChatColor.WHITE + "").setScore(14);
        objective.getScore(ChatColor.RED + "Time Elapsed").setScore(13);
        objective.getScore(ChatColor.WHITE.toString() + ChatColor.BOLD).setScore(12);
        objective.getScore(ChatColor.RED + "").setScore(11);
        objective.getScore(ChatColor.GREEN + "Current Map").setScore(10);
        objective.getScore(ChatColor.WHITE + mapName).setScore(9);
        objective.getScore(ChatColor.WHITE + "").setScore(8);
        objective.getScore(ChatColor.DARK_GRAY.toString()).setScore(7);

        player.setScoreboard(board);
    }



    @EventHandler
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (frozenPlayers.contains(player.getUniqueId())) {
            if (!event.getFrom().toVector().equals(event.getTo().toVector())) {
                event.setTo(event.getFrom());
            }
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals(ChatColor.DARK_PURPLE + "Select Player to Duel")) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            ItemStack item = event.getCurrentItem();
            if (item.getType() == Material.SKULL_ITEM) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.getDisplayName() != null) {
                    String targetName = ChatColor.stripColor(meta.getDisplayName());
                    Player target = Bukkit.getPlayer(targetName);

                    if (target != null && target.isOnline()) {
                        sendDuelInvite(player, target);
                        player.closeInventory();
                    } else {
                        player.sendMessage(ChatColor.RED + "Player is no longer online!");
                    }
                }
            }
        }
        if (playersInDuel.contains(player.getUniqueId())) {
            Inventory clickedInv = event.getClickedInventory();
            if (clickedInv == null) return;

            if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.OUTSIDE) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot drop items during a duel!");
                return;
            }

            if (!clickedInv.equals(player.getInventory())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot move items during a duel!");
                return;
            }
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is for players only!");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        switch (command.getName().toLowerCase()) {
            case "duel":
                if (playersInDuel.contains(playerUUID)) {
                    player.sendMessage(ChatColor.RED + "You are already in a duel!");
                    return true;
                }

                if (args.length == 0) {
                    openDuelMenu(player);
                } else {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                    sendDuelInvite(player, target);
                }
                break;

            case "duelaccept":
                if (duelInvites.containsValue(playerUUID)) {
                    UUID inviterUUID = null;
                    for (Map.Entry<UUID, UUID> entry : duelInvites.entrySet()) {
                        if (entry.getValue().equals(playerUUID)) {
                            inviterUUID = entry.getKey();
                            break;
                        }
                    }

                    if (inviterUUID != null) {
                        Player inviter = Bukkit.getPlayer(inviterUUID);
                        if (inviter != null) {
                            startDuel(inviter, player);
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have any pending duel invites!");
                }
                break;

            case "dueldeny":
                if (duelInvites.containsValue(playerUUID)) {
                    UUID inviterUUID = null;
                    for (Map.Entry<UUID, UUID> entry : duelInvites.entrySet()) {
                        if (entry.getValue().equals(playerUUID)) {
                            inviterUUID = entry.getKey();
                            break;
                        }
                    }

                    if (inviterUUID != null) {
                        Player inviter = Bukkit.getPlayer(inviterUUID);
                        if (inviter != null) {
                            inviter.sendMessage(ChatColor.RED + player.getName() + " denied your duel request!");
                        }
                        player.sendMessage(ChatColor.YELLOW + "Duel request denied!");
                        duelInvites.remove(inviterUUID);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have any pending duel invites!");
                }
                break;

            case "duelstats":
                if (args.length == 0) {
                    showStats(player, player);
                } else {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) {
                        showStats(player, target);
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found!");
                    }
                }
                break;

            case "duelsetup":
                if (!player.hasPermission("duel.admin")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return true;
                }

                if (args.length == 0) {
                    player.sendMessage(ChatColor.YELLOW + "Usage: /duelsetup <spawn1|spawn2|lobby>");
                    return true;
                }

                Location loc = player.getLocation();
                switch (args[0].toLowerCase()) {
                    case "spawn1":
                        arenaSpawn1 = loc;
                        saveLocationToConfig("arena.spawn1", loc);
                        player.sendMessage(ChatColor.GREEN + "Arena spawn 1 set and saved!");
                        break;
                    case "spawn2":
                        arenaSpawn2 = loc;
                        saveLocationToConfig("arena.spawn2", loc);
                        player.sendMessage(ChatColor.GREEN + "Arena spawn 2 set and saved!");
                        break;
                    case "lobby":
                        lobbySpawn = loc;
                        saveLocationToConfig("arena.lobby", loc);
                        player.sendMessage(ChatColor.GREEN + "Lobby spawn set and saved!");
                        break;
                }
        }

        return true;
    }

    private void openDuelMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "Select Player to Duel");

        File statsFile = new File(getDataFolder(), "stats.yml");
        FileConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);

        int slot = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.equals(player)) continue;
            if (playersInDuel.contains(onlinePlayer.getUniqueId())) continue;
            if (slot >= 54) break;

            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(onlinePlayer.getName());
            skullMeta.setDisplayName(ChatColor.YELLOW + onlinePlayer.getName());

            String playerUUID = onlinePlayer.getUniqueId().toString();
            int wins = statsConfig.getInt("stats." + playerUUID + ".wins", 0);
            int losses = statsConfig.getInt("stats." + playerUUID + ".losses", 0);

            double winRate = 0.0;
            if (wins + losses > 0) {
                winRate = (double) wins / (wins + losses) * 100;
            }

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Wins: " + ChatColor.GREEN + wins);
            lore.add(ChatColor.GRAY + "Losses: " + ChatColor.RED + losses);
            lore.add(ChatColor.GRAY + "Win Rate: " + ChatColor.AQUA + String.format("%.1f%%", winRate));
            lore.add("");
            lore.add(ChatColor.GOLD + "Click to send duel invite!");
            skullMeta.setLore(lore);

            skull.setItemMeta(skullMeta);
            menu.setItem(slot, skull);
            slot++;
        }

        player.openInventory(menu);
    }

    private int[] getPlayerStats(UUID playerUUID) {
        File statsFile = new File(getDataFolder(), "stats.yml");
        FileConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);

        String uuid = playerUUID.toString();
        int wins = statsConfig.getInt("stats." + uuid + ".wins", 0);
        int losses = statsConfig.getInt("stats." + uuid + ".losses", 0);

        return new int[]{wins, losses};
    }

    private void sendDuelInvite(Player inviter, Player target) {
        if (inviter.equals(target)) {
            inviter.sendMessage(ChatColor.RED + "You can't duel yourself!");
            return;
        }

        if (playersInDuel.contains(target.getUniqueId())) {
            inviter.sendMessage(ChatColor.RED + target.getName() + " is already in a duel!");
            return;
        }

        if (duelInvites.containsKey(inviter.getUniqueId())) {
            inviter.sendMessage(ChatColor.RED + "You already have a pending duel invite!");
            return;
        }

        duelInvites.put(inviter.getUniqueId(), target.getUniqueId());

        inviter.sendMessage(ChatColor.GREEN + "Duel invite sent to " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "!");

        target.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        target.sendMessage(ChatColor.YELLOW + "You have been challenged to a duel!");
        target.sendMessage(ChatColor.GRAY + "From: " + ChatColor.WHITE + inviter.getName());
        target.sendMessage("");
        target.sendMessage(ChatColor.GREEN + "/duelaccept" + ChatColor.GRAY + " - Accept the duel");
        target.sendMessage(ChatColor.RED + "/dueldeny" + ChatColor.GRAY + " - Deny the duel");
        target.sendMessage(ChatColor.GOLD + "═══════════════════════════════");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (duelInvites.containsKey(inviter.getUniqueId()) &&
                        duelInvites.get(inviter.getUniqueId()).equals(target.getUniqueId())) {
                    duelInvites.remove(inviter.getUniqueId());
                    inviter.sendMessage(ChatColor.RED + "Duel invite to " + target.getName() + " expired!");
                    target.sendMessage(ChatColor.RED + "Duel invite from " + inviter.getName() + " expired!");
                }
            }
        }.runTaskLater(this, 600L);
    }

    private void startDuel(Player player1, Player player2) {
        if (arenaSpawn1 == null || arenaSpawn2 == null) {
            player1.sendMessage(ChatColor.RED + "Arena not set up! Contact an admin.");
            player2.sendMessage(ChatColor.RED + "Arena not set up! Contact an admin.");
            return;
        }

        duelInvites.remove(player1.getUniqueId());

        playersInDuel.add(player1.getUniqueId());
        playersInDuel.add(player2.getUniqueId());
        activeDuels.put(player1.getUniqueId(), player2.getUniqueId());
        activeDuels.put(player2.getUniqueId(), player1.getUniqueId());

        playerOriginalLocation.put(player1.getUniqueId(), player1.getLocation());
        playerOriginalLocation.put(player2.getUniqueId(), player2.getLocation());
        playerOriginalInventory.put(player1.getUniqueId(), player1.getInventory().getContents().clone());
        playerOriginalInventory.put(player2.getUniqueId(), player2.getInventory().getContents().clone());

        Bukkit.broadcastMessage(ChatColor.GOLD + "=== DUEL STARTED ===");
        Bukkit.broadcastMessage(ChatColor.YELLOW + player1.getName() + ChatColor.WHITE + " vs " + ChatColor.YELLOW + player2.getName());
        Bukkit.broadcastMessage(ChatColor.GOLD + "===================");

        player1.teleport(arenaSpawn1);
        player2.teleport(arenaSpawn2);

        File file = new File(getDataFolder(), "duels.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        String p1 = player1.getUniqueId().toString();
        String p2 = player2.getUniqueId().toString();

        config.set("duels." + p1 + ".opponent", p2);
        config.set("duels." + p1 + ".inventory", playerOriginalInventory.get(player1.getUniqueId()));
        config.set("duels." + p1 + ".location.world", playerOriginalLocation.get(player1.getUniqueId()).getWorld().getName());
        config.set("duels." + p1 + ".location.x", playerOriginalLocation.get(player1.getUniqueId()).getX());
        config.set("duels." + p1 + ".location.y", playerOriginalLocation.get(player1.getUniqueId()).getY());
        config.set("duels." + p1 + ".location.z", playerOriginalLocation.get(player1.getUniqueId()).getZ());

        config.set("duels." + p2 + ".opponent", p1);
        config.set("duels." + p2 + ".inventory", playerOriginalInventory.get(player2.getUniqueId()));
        config.set("duels." + p2 + ".location.world", playerOriginalLocation.get(player2.getUniqueId()).getWorld().getName());
        config.set("duels." + p2 + ".location.x", playerOriginalLocation.get(player2.getUniqueId()).getX());
        config.set("duels." + p2 + ".location.y", playerOriginalLocation.get(player2.getUniqueId()).getY());
        config.set("duels." + p2 + ".location.z", playerOriginalLocation.get(player2.getUniqueId()).getZ());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        showScoreboard(player1, 0, "ser");
        showScoreboard(player2, 0, "ser");

        prepareDuelPlayer(player1);
        prepareDuelPlayer(player2);

        startCountdown(player1, player2);
    }

    private void prepareDuelPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));
        player.getInventory().setItem(2, new ItemStack(Material.BOW));
        player.getInventory().setItem(1, new ItemStack(Material.FISHING_ROD));
        player.getInventory().setItem(8, new ItemStack(Material.FLINT_AND_STEEL));
        player.getInventory().setItem(9, new ItemStack(Material.ARROW, 6));

        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

        player.setHealth(20);
        player.setFoodLevel(20);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    private void startCountdown(Player player1, Player player2) {
        frozenPlayers.add(player1.getUniqueId());
        frozenPlayers.add(player2.getUniqueId());

        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown > 0) {
                    player1.sendMessage(ChatColor.YELLOW + "Duel starts in: " + ChatColor.RED + countdown);
                    player2.sendMessage(ChatColor.YELLOW + "Duel starts in: " + ChatColor.RED + countdown);
                    countdown--;
                } else {
                    frozenPlayers.remove(player1.getUniqueId());
                    frozenPlayers.remove(player2.getUniqueId());

                    player1.sendMessage(ChatColor.GREEN + "Fight!");
                    player2.sendMessage(ChatColor.GREEN + "Fight!");

                    BukkitRunnable timeTask = new BukkitRunnable() {
                        int seconds = 0;

                        @Override
                        public void run() {
                            if (!playersInDuel.contains(player1.getUniqueId()) || !playersInDuel.contains(player2.getUniqueId())) {
                                cancel();
                                return;
                            }

                            updateTime(player1, seconds);
                            updateTime(player2, seconds);
                            seconds++;
                        }
                    };
                    timeTask.runTaskTimer(Dules.this, 20L, 20L);
                    playerTimers.put(player1.getUniqueId(), timeTask);
                    playerTimers.put(player2.getUniqueId(), timeTask);

                    cancel();
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }



    private void endDuel(Player player, boolean winner) {
        UUID playerUUID = player.getUniqueId();
        if (!playersInDuel.contains(playerUUID)) return;

        UUID opponentUUID = activeDuels.get(playerUUID);
        Player opponent = Bukkit.getPlayer(opponentUUID);
        if (opponent == null) return;

        if (playerTimers.containsKey(playerUUID)) {
            playerTimers.get(playerUUID).cancel();
            playerTimers.remove(playerUUID);
        }
        if (playerTimers.containsKey(opponentUUID)) {
            playerTimers.get(opponentUUID).cancel();
            playerTimers.remove(opponentUUID);
        }

        playersInDuel.remove(playerUUID);
        playersInDuel.remove(opponentUUID);
        activeDuels.remove(playerUUID);
        activeDuels.remove(opponentUUID);

        File file = new File(getDataFolder(), "duels.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("duels." + playerUUID, null);
        config.set("duels." + opponentUUID, null);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskLater(this, () -> {
            resetPlayer(player, playerUUID);
        }, 3L);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            resetPlayer(opponent, opponentUUID);
        }, 6L);

        if (winner) {
            player.sendMessage(ChatColor.GOLD + "Congratulations! You won the duel!");
            opponent.sendMessage(ChatColor.RED + "You lost the duel! Better luck next time!");
        } else {
            player.sendMessage(ChatColor.RED + "You lost the duel! Better luck next time!");
            opponent.sendMessage(ChatColor.GOLD + "Congratulations! You won the duel!");
        }

        updateStats(winner ? playerUUID : opponentUUID, winner ? opponentUUID : playerUUID);
    }

    private void resetPlayer(Player player, UUID playerUUID) {
        getLogger().info("Resetting player: " + player.getName() + " (UUID: " + playerUUID + ")");

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        getLogger().info("Cleared inventory for: " + player.getName());

        if (playerOriginalInventory.containsKey(playerUUID)) {
            ItemStack[] originalItems = playerOriginalInventory.get(playerUUID);
            if (originalItems != null) {
                ItemStack[] restoredItems = new ItemStack[originalItems.length];
                for (int i = 0; i < originalItems.length; i++) {
                    restoredItems[i] = originalItems[i] != null ? originalItems[i].clone() : null;
                }
                player.getInventory().setContents(restoredItems);
                getLogger().info("Restored original inventory for: " + player.getName());
            } else {
                getLogger().warning("Original inventory is null for: " + player.getName());
            }
        } else {
            getLogger().warning("No original inventory found for: " + player.getName());
        }

        if (lobbySpawn != null) {
            player.teleport(lobbySpawn);
            getLogger().info("Teleported " + player.getName() + " to lobby spawn");
        } else if (playerOriginalLocation.containsKey(playerUUID)) {
            player.teleport(playerOriginalLocation.get(playerUUID));
            getLogger().info("Teleported " + player.getName() + " to original location");
        } else {
            getLogger().warning("No location found for: " + player.getName());
        }

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        playerOriginalInventory.remove(playerUUID);
        playerOriginalLocation.remove(playerUUID);
        getLogger().info("Cleaned up data for: " + player.getName());
    }

    private void updateStats(UUID winnerUUID, UUID loserUUID) {
        File statsFile = new File(getDataFolder(), "stats.yml");
        FileConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);

        int wins = statsConfig.getInt("stats." + winnerUUID + ".wins", 0);
        statsConfig.set("stats." + winnerUUID + ".wins", wins + 1);

        int losses = statsConfig.getInt("stats." + loserUUID + ".losses", 0);
        statsConfig.set("stats." + loserUUID + ".losses", losses + 1);

        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    private void updateTime(Player player, int secondsElapsed) {
        Scoreboard board = player.getScoreboard();
        Team timeTeam = board.getTeam("time");
        if (timeTeam == null) return;

        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);

        timeTeam.setSuffix("§f" + timeFormatted);
    }


    private void showStats(Player viewer, Player target) {
        int wins = playerWins.getOrDefault(target.getUniqueId(), 0);
        int losses = playerLosses.getOrDefault(target.getUniqueId(), 0);
        int total = wins + losses;
        double winRate = total > 0 ? (double) wins / total * 100 : 0;

        viewer.sendMessage(ChatColor.GOLD + "═══ " + target.getName() + "'s Duel Stats ═══");
        viewer.sendMessage(ChatColor.GREEN + "Wins: " + ChatColor.WHITE + wins);
        viewer.sendMessage(ChatColor.RED + "Losses: " + ChatColor.WHITE + losses);
        viewer.sendMessage(ChatColor.YELLOW + "Total Duels: " + ChatColor.WHITE + total);
        viewer.sendMessage(ChatColor.AQUA + "Win Rate: " + ChatColor.WHITE + String.format("%.1f%%", winRate));
        viewer.sendMessage(ChatColor.GOLD + "═══════════════════════════");
    }

    private void setupDefaultArena() {
        if (Bukkit.getWorlds().size() > 0) {
            org.bukkit.World world = Bukkit.getWorlds().get(0);
            arenaSpawn1 = new Location(world, 0, 100, 0);
            arenaSpawn2 = new Location(world, 10, 100, 0);
            lobbySpawn = new Location(world, 0, 100, -10);
        }
    }
}
