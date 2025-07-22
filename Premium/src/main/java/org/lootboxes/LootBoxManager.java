package org.lootboxes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.inventory.meta.SkullMeta;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LootBoxManager {

    private final JavaPlugin plugin;
    private Map<UUID, ArmorStand> lootBoxes = new HashMap<>();
    private List<ItemStack> yellowItems = new ArrayList<>();
    private List<ItemStack> greenItems = new ArrayList<>();
    private List<ItemStack> blackItems = new ArrayList<>();
    private File itemsFile;
    private File regionsFile;
    private FileConfiguration itemsConfig;
    private FileConfiguration regionsConfig;
    private Random random = new Random();
    private org.lootboxes.HologramManager hologramManager = new org.lootboxes.HologramManager();
    private Map<UUID, ArmorStand> designBoxes = new HashMap<>();
    private DesignBoxConfig designBoxConfig;
    private Map<UUID, BukkitRunnable> rotationTasks = new HashMap<>();

    private Location pos1;
    private Location pos2;
    private List<RegionData> excludedRegions = new ArrayList<>();

    public LootBoxManager(JavaPlugin plugin) {
        this.plugin = plugin;
        setupItemsFile();
        setupRegionsFile();
        loadItems();
        loadRegions();
        startLootBoxSpawner();
        this.designBoxConfig = new DesignBoxConfig(plugin);
        loadDesignBoxes();
    }


    private void setupItemsFile() {
        itemsFile = new File(plugin.getDataFolder(), "lootbox_items.yml");
        if (!itemsFile.exists()) {
            itemsFile.getParentFile().mkdirs();
            try {
                itemsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }

    private void setupRegionsFile() {
        regionsFile = new File(plugin.getDataFolder(), "lootbox_regions.yml");
        if (!regionsFile.exists()) {
            regionsFile.getParentFile().mkdirs();
            try {
                regionsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);
    }

    public void loadItems() {
        yellowItems.clear();
        greenItems.clear();
        blackItems.clear();

        List<String> yellowList = itemsConfig.getStringList("yellow");
        List<String> greenList = itemsConfig.getStringList("green");
        List<String> blackList = itemsConfig.getStringList("black");

        for (String item : yellowList) {
            if (item.startsWith("ENCHANTED_BOOK:")) {
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();

                String enchantData = item.substring(15);
                String[] enchants = enchantData.split(",");

                for (String enchant : enchants) {
                    int lastUnderscore = enchant.lastIndexOf('_');
                    if (lastUnderscore != -1) {
                        String enchantmentName = enchant.substring(0, lastUnderscore);
                        int level = Integer.parseInt(enchant.substring(lastUnderscore + 1));
                        Enchantment ench = Enchantment.getByName(enchantmentName);
                        if (ench != null) {
                            meta.addStoredEnchant(ench, level, true);
                        }
                    }
                }
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                book.setItemMeta(meta);
                yellowItems.add(book);
            } else {
                String[] parts = item.split(":");
                Material mat = Material.valueOf(parts[0]);
                int amount = Integer.parseInt(parts[1]);
                yellowItems.add(new ItemStack(mat, amount));
            }
        }

        for (String item : greenList) {
            if (item.startsWith("ENCHANTED_BOOK:")) {
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();

                String enchantData = item.substring(15);
                String[] enchants = enchantData.split(",");

                for (String enchant : enchants) {
                    int lastUnderscore = enchant.lastIndexOf('_');
                    if (lastUnderscore != -1) {
                        String enchantmentName = enchant.substring(0, lastUnderscore);
                        int level = Integer.parseInt(enchant.substring(lastUnderscore + 1));
                        Enchantment ench = Enchantment.getByName(enchantmentName);
                        if (ench != null) {
                            meta.addStoredEnchant(ench, level, true);
                        }
                    }
                }
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                book.setItemMeta(meta);
                greenItems.add(book);
            } else {
                String[] parts = item.split(":");
                Material mat = Material.valueOf(parts[0]);
                int amount = Integer.parseInt(parts[1]);
                greenItems.add(new ItemStack(mat, amount));
            }
        }

        for (String item : blackList) {
            if (item.startsWith("ENCHANTED_BOOK:")) {
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();

                String enchantData = item.substring(15);
                String[] enchants = enchantData.split(",");

                for (String enchant : enchants) {
                    int lastUnderscore = enchant.lastIndexOf('_');
                    if (lastUnderscore != -1) {
                        String enchantmentName = enchant.substring(0, lastUnderscore);
                        int level = Integer.parseInt(enchant.substring(lastUnderscore + 1));
                        Enchantment ench = Enchantment.getByName(enchantmentName);
                        if (ench != null) {
                            meta.addStoredEnchant(ench, level, true);
                        }
                    }
                }
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                book.setItemMeta(meta);
                blackItems.add(book);
            } else {
                String[] parts = item.split(":");
                Material mat = Material.valueOf(parts[0]);
                int amount = Integer.parseInt(parts[1]);
                blackItems.add(new ItemStack(mat, amount));
            }
        }
    }

    private void loadRegions() {
        excludedRegions.clear();

        if (regionsConfig.contains("spawn-region.pos1") && regionsConfig.contains("spawn-region.pos2")) {
            String worldName = regionsConfig.getString("spawn-region.world", "world");
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                double x1 = regionsConfig.getDouble("spawn-region.pos1.x");
                double y1 = regionsConfig.getDouble("spawn-region.pos1.y");
                double z1 = regionsConfig.getDouble("spawn-region.pos1.z");
                double x2 = regionsConfig.getDouble("spawn-region.pos2.x");
                double y2 = regionsConfig.getDouble("spawn-region.pos2.y");
                double z2 = regionsConfig.getDouble("spawn-region.pos2.z");

                pos1 = new Location(world, x1, y1, z1);
                pos2 = new Location(world, x2, y2, z2);
            }
        }

        List<String> excludedList = regionsConfig.getStringList("excluded-regions");
        for (String regionData : excludedList) {
            String[] parts = regionData.split(",");
            if (parts.length == 7) {
                String worldName = parts[0];
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    double x1 = Double.parseDouble(parts[1]);
                    double y1 = Double.parseDouble(parts[2]);
                    double z1 = Double.parseDouble(parts[3]);
                    double x2 = Double.parseDouble(parts[4]);
                    double y2 = Double.parseDouble(parts[5]);
                    double z2 = Double.parseDouble(parts[6]);

                    Location pos1 = new Location(world, x1, y1, z1);
                    Location pos2 = new Location(world, x2, y2, z2);
                    excludedRegions.add(new RegionData(pos1, pos2));
                }
            }
        }
    }

    public void saveRegions() {
        if (pos1 != null && pos2 != null) {
            regionsConfig.set("spawn-region.world", pos1.getWorld().getName());
            regionsConfig.set("spawn-region.pos1.x", pos1.getX());
            regionsConfig.set("spawn-region.pos1.y", pos1.getY());
            regionsConfig.set("spawn-region.pos1.z", pos1.getZ());
            regionsConfig.set("spawn-region.pos2.x", pos2.getX());
            regionsConfig.set("spawn-region.pos2.y", pos2.getY());
            regionsConfig.set("spawn-region.pos2.z", pos2.getZ());
        }

        List<String> excludedList = new ArrayList<>();
        for (RegionData region : excludedRegions) {
            String data = region.getPos1().getWorld().getName() + "," +
                    region.getPos1().getX() + "," + region.getPos1().getY() + "," + region.getPos1().getZ() + "," +
                    region.getPos2().getX() + "," + region.getPos2().getY() + "," + region.getPos2().getZ();
            excludedList.add(data);
        }
        regionsConfig.set("excluded-regions", excludedList);

        try {
            regionsConfig.save(regionsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveItems() {
        List<String> yellowList = new ArrayList<>();
        List<String> greenList = new ArrayList<>();
        List<String> blackList = new ArrayList<>();

        for (ItemStack item : yellowItems) {
            if (item.getType() == Material.ENCHANTED_BOOK && item.hasItemMeta()) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                StringBuilder enchantString = new StringBuilder("ENCHANTED_BOOK:");
                for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
                    enchantString.append(entry.getKey().getName()).append("_").append(entry.getValue()).append(",");
                }
                if (enchantString.toString().endsWith(",")) {
                    enchantString.setLength(enchantString.length() - 1);
                }
                yellowList.add(enchantString.toString());
            } else {
                yellowList.add(item.getType().name() + ":" + item.getAmount());
            }
        }

        for (ItemStack item : greenItems) {
            if (item.getType() == Material.ENCHANTED_BOOK && item.hasItemMeta()) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                StringBuilder enchantString = new StringBuilder("ENCHANTED_BOOK:");
                for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
                    enchantString.append(entry.getKey().getName()).append("_").append(entry.getValue()).append(",");
                }
                if (enchantString.toString().endsWith(",")) {
                    enchantString.setLength(enchantString.length() - 1);
                }
                greenList.add(enchantString.toString());
            } else {
                greenList.add(item.getType().name() + ":" + item.getAmount());
            }
        }

        for (ItemStack item : blackItems) {
            if (item.getType() == Material.ENCHANTED_BOOK && item.hasItemMeta()) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                StringBuilder enchantString = new StringBuilder("ENCHANTED_BOOK:");
                for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
                    enchantString.append(entry.getKey().getName()).append("_").append(entry.getValue()).append(",");
                }
                if (enchantString.toString().endsWith(",")) {
                    enchantString.setLength(enchantString.length() - 1);
                }
                blackList.add(enchantString.toString());
            } else {
                blackList.add(item.getType().name() + ":" + item.getAmount());
            }
        }

        itemsConfig.set("yellow", yellowList);
        itemsConfig.set("green", greenList);
        itemsConfig.set("black", blackList);

        try {
            itemsConfig.save(itemsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startLootBoxSpawner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanupDeadLootBoxes();

                int totalBoxes = lootBoxes.size();
                int maxBoxes = 20;

                if (totalBoxes < maxBoxes) {
                    spawnRandomLootBox();
                }
            }
        }.runTaskTimer(plugin, 0L, 300L);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (ArmorStand box : lootBoxes.values()) {
                    if (box != null && !box.isDead()) {
                        EulerAngle currentAngle = box.getHeadPose();
                        box.setHeadPose(currentAngle.add(0, 0.10, 0));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void cleanupDeadLootBoxes() {
        Iterator<Map.Entry<UUID, ArmorStand>> iterator = lootBoxes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, ArmorStand> entry = iterator.next();
            ArmorStand box = entry.getValue();

            if (box == null || box.isDead() || !box.isValid()) {
                iterator.remove();
            }
        }
    }


    private void spawnRandomLootBox() {
        Location loc = getRandomSafeLocation();
        if (loc == null) return;

        if (isNearDesignBox(loc)) {
            return;
        }

        List<LootType> availableTypes = getAvailableLootTypes();
        if (availableTypes.isEmpty()) {
            return;
        }

        LootType type = availableTypes.get(random.nextInt(availableTypes.size()));
        ArmorStand box = createLootBox(loc, type);
        lootBoxes.put(box.getUniqueId(), box);
    }

    private boolean isNearDesignBox(Location loc) {
        for (ArmorStand designBox : designBoxes.values()) {
            if (designBox != null && !designBox.isDead()) {
                if (designBox.getLocation().distance(loc) < 10.0) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<LootType> getAvailableLootTypes() {
        List<LootType> available = new ArrayList<>();

        if (!yellowItems.isEmpty()) {
            available.add(LootType.YELLOW);
        }
        if (!greenItems.isEmpty()) {
            available.add(LootType.GREEN);
        }
        if (!blackItems.isEmpty()) {
            available.add(LootType.BLACK);
        }

        return available;
    }

    private Location getRandomSafeLocation() {
        if (pos1 == null || pos2 == null) {
            World world = Bukkit.getWorlds().get(0);
            return getRandomSafeLocationInWorld(world);
        }

        int attempts = 0;
        while (attempts < 100) {
            double minX = Math.min(pos1.getX(), pos2.getX());
            double maxX = Math.max(pos1.getX(), pos2.getX());
            double minZ = Math.min(pos1.getZ(), pos2.getZ());
            double maxZ = Math.max(pos1.getZ(), pos2.getZ());

            double x = minX + random.nextDouble() * (maxX - minX);
            double z = minZ + random.nextDouble() * (maxZ - minZ);

            World world = pos1.getWorld();
            int y = world.getHighestBlockYAt((int) x, (int) z);

            Location loc = new Location(world, x, y, z);

            if (isSafeLocation(loc) && !isInExcludedRegion(loc)) {
                return loc;
            }

            attempts++;
        }
        return null;
    }

    private Location getRandomSafeLocationInWorld(World world) {
        int attempts = 0;
        while (attempts < 50) {
            int x = random.nextInt(200) - 100;
            int z = random.nextInt(200) - 100;
            int y = world.getHighestBlockYAt(x, z);

            Location loc = new Location(world, x, y, z);

            if (isSafeLocation(loc) && !isInExcludedRegion(loc)) {
                return loc;
            }
            attempts++;
        }
        return null;
    }

    private boolean isSafeLocation(Location loc) {
        Block block = loc.getBlock();
        Block below = loc.clone().subtract(0, 1, 0).getBlock();

        return block.getType() == Material.AIR &&
                below.getType() != Material.AIR &&
                below.getType() != Material.WATER &&
                below.getType() != Material.LAVA &&
                loc.getY() > 5;
    }

    private boolean isInExcludedRegion(Location loc) {
        for (RegionData region : excludedRegions) {
            if (region.contains(loc)) {
                return true;
            }
        }
        return false;
    }

    public ItemStack createCustomSkull(String base64) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }

    public ArmorStand createLootBox(Location loc, LootType type) {
        loc = loc.clone().add(0, -1.0, 0);
        ArmorStand box = loc.getWorld().spawn(loc, ArmorStand.class);
        box.setGravity(false);
        box.setCanPickupItems(false);
        box.setVisible(false);
        box.setSmall(false);
        box.setBasePlate(false);
        box.setArms(false);

        String texture;
        switch (type) {
            case YELLOW:
                texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzlkMWVmNGMxNTNiZGU4YTdiMDhmOTJjMGM0Yjc5ZmRjMmZjYjU3ZjgzYTMxYTgyMjljNjJhYzc1ZjFmMGEzMSJ9fX0=";
                break;
            case GREEN:
                texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjYxOWY5NDdlZjIzNTM5MDNhZTliZTA2ZWUzNzViMzBmYTE2ZjI2NzlmZGMzYWIwMTg4ZmJiZTQ1MDBmZWY0In19fQ==";
                break;
            case BLACK:
                texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmNkOWY4MWEyNzcyYmRjNDg2NDQ3MmU4MzMzNjIzMTA0ODVjOGE2YmMwYjc2YjgxNzAzMzkwYTliMDMyZSJ9fX0=";
                break;
            default:
                texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFhZDlkZjhkNDE4NjQ2YzU2MDEzN2E4ZTc0NTVmOTlkYmI1MzBiMTI3YzQwMjc1ZGZkYzY5MWE0MzA0M2JkMCJ9fX0=";
                break;
        }

        ItemStack helmet = createCustomSkull(texture);

        ItemMeta meta = helmet.getItemMeta();
        meta.setDisplayName(type.getDisplayName());
        helmet.setItemMeta(meta);

        box.setHelmet(helmet);
        box.setCustomName(type.getDisplayName());
        box.setCustomNameVisible(false);

        return box;
    }

    public void openLootBox(Player player, ArmorStand box) {
        LootType type = getLootTypeFromBox(box);
        List<ItemStack> items = getItemsForType(type);

        ItemStack reward = items.get(random.nextInt(items.size()));

        if (reward.getType() == Material.ENCHANTED_BOOK) {
            ItemMeta meta = reward.getItemMeta();
            if (meta != null) {
                reward.setItemMeta(meta);
            }
        }

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);

        Location dropLocation = box.getLocation();
        dropLocation.getWorld().dropItem(dropLocation, reward.clone());

        player.sendMessage(ChatColor.GREEN + "You received: " +
                ChatColor.WHITE + reward.getAmount() + "x " + reward.getType().name());

        lootBoxes.remove(box.getUniqueId());
        box.remove();
    }

    public boolean isPlayerOnLootBox(Player player, ArmorStand box) {
        Location playerLoc = player.getLocation();
        Location boxLoc = box.getLocation();

        double xDiff = Math.abs(playerLoc.getX() - boxLoc.getX());
        double zDiff = Math.abs(playerLoc.getZ() - boxLoc.getZ());
        double yDiff = playerLoc.getY() - boxLoc.getY();

        return xDiff <= 0.8 && zDiff <= 0.8 && yDiff >= 0.5 && yDiff <= 2.0;
    }

    public LootType getLootTypeFromBox(ArmorStand box) {
        ItemStack helmet = box.getHelmet();
        if (helmet == null || helmet.getType() != Material.SKULL_ITEM) return LootType.YELLOW;

        SkullMeta meta = (SkullMeta) helmet.getItemMeta();
        if (meta == null) return LootType.YELLOW;

        String customName = box.getCustomName();
        if (customName != null) {
            if (customName.contains("Green")) return LootType.GREEN;
            if (customName.contains("Black")) return LootType.BLACK;
            return LootType.YELLOW;
        }

        return LootType.YELLOW;
    }

    public List<ItemStack> getItemsForType(LootType type) {
        switch (type) {
            case YELLOW: return yellowItems;
            case GREEN: return greenItems;
            case BLACK: return blackItems;
            default: return yellowItems;
        }
    }

    public Map<UUID, ArmorStand> getLootBoxes() {
        return lootBoxes;
    }

    public void clearAllLootBoxes() {
        for (ArmorStand box : lootBoxes.values()) {
            if (box != null && !box.isDead()) {
                box.remove();
            }
        }
        lootBoxes.clear();
    }

    public void setPos1(Location loc) {
        this.pos1 = loc;
    }

    public void setPos2(Location loc) {
        this.pos2 = loc;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void addExcludedRegion(Location pos1, Location pos2) {
        excludedRegions.add(new RegionData(pos1, pos2));
    }

    public List<RegionData> getExcludedRegions() {
        return excludedRegions;
    }

    public void clearExcludedRegions() {
        excludedRegions.clear();
    }


    public static class RegionData {
        private final Location pos1;
        private final Location pos2;

        public RegionData(Location pos1, Location pos2) {
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        public boolean contains(Location loc) {
            if (!loc.getWorld().equals(pos1.getWorld())) {
                return false;
            }

            double minX = Math.min(pos1.getX(), pos2.getX());
            double maxX = Math.max(pos1.getX(), pos2.getX());
            double minY = Math.min(pos1.getY(), pos2.getY());
            double maxY = Math.max(pos1.getY(), pos2.getY());
            double minZ = Math.min(pos1.getZ(), pos2.getZ());
            double maxZ = Math.max(pos1.getZ(), pos2.getZ());

            return loc.getX() >= minX && loc.getX() <= maxX &&
                    loc.getY() >= minY && loc.getY() <= maxY &&
                    loc.getZ() >= minZ && loc.getZ() <= maxZ;
        }

        public Location getPos1() {
            return pos1;
        }

        public Location getPos2() {
            return pos2;
        }
    }

    public ArmorStand createDesignLootBox(Location loc, LootType type) {
        loc = loc.clone().add(0, designBoxConfig.getBoxHeight(), 0);
        ArmorStand box = createLootBox(loc, type);

        if (designBoxConfig.isRotationEnabled()) {
            BukkitRunnable rotationTask = new BukkitRunnable() {
                float yaw = 0;

                @Override
                public void run() {
                    if (box == null || box.isDead()) {
                        rotationTasks.remove(box.getUniqueId());
                        this.cancel();
                        return;
                    }

                    yaw += 5.76f;
                    if (yaw >= 360) yaw = 0;

                    Location currentLoc = box.getLocation();
                    currentLoc.setYaw(yaw);
                    box.teleport(currentLoc);
                }
            };
            rotationTask.runTaskTimer(plugin, 0L, 1L);
            rotationTasks.put(box.getUniqueId(), rotationTask);
        }

        String hologramText = designBoxConfig.getHologramText(type);
        Location hologramLoc = box.getLocation().clone().add(0, designBoxConfig.getHologramHeight(), 0);
        ArmorStand hologram = hologramManager.createHologram(hologramLoc, hologramText);
        hologramManager.addHologram(box.getUniqueId(), hologram);


        designBoxes.put(box.getUniqueId(), box);
        designBoxConfig.saveDesignBox(box.getUniqueId(), box.getLocation(), type);

        return box;
    }

    public boolean isDesignBox(UUID boxId) {
        return designBoxes.containsKey(boxId);
    }

    public org.lootboxes.HologramManager getHologramManager() {
        return hologramManager;
    }

    public Map<UUID, ArmorStand> getDesignBoxes() {
        return designBoxes;
    }


    public void openLootPreview(Player player, LootType type) {
        String colorName = type.name().toLowerCase();
        String displayName = "";
        ChatColor titleColor;

        switch (colorName) {
            case "yellow":
                displayName = "Yellow Loot (Preview)";
                titleColor = ChatColor.YELLOW;
                break;
            case "green":
                displayName = "Green Loot (Preview)";
                titleColor = ChatColor.GREEN;
                break;
            case "black":
                displayName = "Black Loot (Preview)";
                titleColor = ChatColor.DARK_GRAY;
                break;
            default:
                displayName = "Loot Preview";
                titleColor = ChatColor.WHITE;
        }

        Inventory gui = Bukkit.createInventory(null, 36, titleColor + displayName);

        List<ItemStack> items = getItemsForType(type);
        if (items != null) {
            for (ItemStack item : items) {
                gui.addItem(item);
            }
        }

        player.openInventory(gui);
    }

    public void loadDesignBoxes() {
        Map<UUID, DesignBoxConfig.BoxData> savedBoxes = designBoxConfig.loadDesignBoxes();

        for (Map.Entry<UUID, DesignBoxConfig.BoxData> entry : savedBoxes.entrySet()) {
            UUID savedBoxId = entry.getKey();
            DesignBoxConfig.BoxData boxData = entry.getValue();

            Location loc = boxData.getLocation().clone().add(0, designBoxConfig.getBoxHeight(), 0);
            ArmorStand box = createLootBox(loc, boxData.getType());

            designBoxes.put(savedBoxId, box);

            if (designBoxConfig.isRotationEnabled()) {
                BukkitRunnable rotationTask = new BukkitRunnable() {
                    float yaw = 0;

                    @Override
                    public void run() {
                        if (box == null || box.isDead()) {
                            rotationTasks.remove(savedBoxId);
                            this.cancel();
                            return;
                        }

                        yaw += 2.0f;
                        if (yaw >= 360) yaw = 0;

                        Location currentLoc = box.getLocation();
                        currentLoc.setYaw(yaw);
                        box.teleport(currentLoc);
                    }
                };
                rotationTask.runTaskTimer(plugin, 0L, 1L);
                rotationTasks.put(savedBoxId, rotationTask);
            }


            String hologramText = designBoxConfig.getHologramText(boxData.getType());
            Location hologramLoc = box.getLocation().clone().add(0, designBoxConfig.getHologramHeight(), 0);
            ArmorStand hologram = hologramManager.createHologram(hologramLoc, hologramText);
            hologramManager.addHologram(savedBoxId, hologram);
        }
    }


    public void updateAllHolograms() {
        for (Map.Entry<UUID, ArmorStand> entry : designBoxes.entrySet()) {
            UUID boxId = entry.getKey();
            ArmorStand box = entry.getValue();
            LootType type = getLootTypeFromBox(box);

            if (type != null) {
                hologramManager.removeHologram(boxId);

                String hologramText = designBoxConfig.getHologramText(type);
                Location hologramLoc = box.getLocation().clone().add(0, designBoxConfig.getHologramHeight(), 0);
                ArmorStand hologram = hologramManager.createHologram(hologramLoc, hologramText);
                hologramManager.addHologram(boxId, hologram);
            }
        }
    }

    public void reloadDesignBoxConfig() {
        designBoxConfig.reloadConfig();
        updateAllHolograms();
    }

    public void removeDesignBox(UUID boxId) {
        ArmorStand box = designBoxes.get(boxId);
        if (box != null) {
            BukkitRunnable rotationTask = rotationTasks.get(boxId);
            if (rotationTask != null) {
                rotationTask.cancel();
                rotationTasks.remove(boxId);
            }

            hologramManager.removeHologram(boxId);

            box.remove();
            designBoxes.remove(boxId);

            designBoxConfig.removeDesignBox(boxId);
        }
    }

    public void saveAllDesignBoxes() {
        for (Map.Entry<UUID, ArmorStand> entry : designBoxes.entrySet()) {
            UUID boxId = entry.getKey();
            ArmorStand box = entry.getValue();
            LootType type = getLootTypeFromBox(box);
            if (type != null) {
                designBoxConfig.saveDesignBox(boxId, box.getLocation(), type);
            }
        }
    }

    public DesignBoxConfig getDesignBoxConfig() {
        return designBoxConfig;
    }

}