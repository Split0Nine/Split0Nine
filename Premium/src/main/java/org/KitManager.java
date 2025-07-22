package org;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class KitManager {

    private final KitSystem plugin;
    private final Map<String, Kit> kits;
    private final Map<UUID, String> playerActiveKits;

    public KitManager(KitSystem plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
        this.playerActiveKits = new HashMap<>();
        initializeKits();
        loadPlayerKits();
    }

    private void initializeKits() {

        Kit playerKit = new Kit("Player", "kit.player", Material.CHAINMAIL_CHESTPLATE, "§7Player Kit");
        playerKit.addItem(createEnchantedItem(Material.WOOD_SWORD, "§7Wooden Sword",
                new EnchantmentData(Enchantment.DAMAGE_ALL, 2)), 0);
        playerKit.addItem(createEnchantedItem(Material.BOW, "§7Bow",
                new EnchantmentData(Enchantment.ARROW_DAMAGE, 1)), 1);
        playerKit.addArmor(createEnchantedItem(Material.CHAINMAIL_HELMET, "§7Chain Helmet",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 1)), ArmorType.HELMET);
        playerKit.addArmor(createEnchantedItem(Material.IRON_CHESTPLATE, "§7Iron Chestplate",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 1)), ArmorType.CHESTPLATE);
        playerKit.addArmor(createEnchantedItem(Material.CHAINMAIL_LEGGINGS, "§7Chain Leggings",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 1)), ArmorType.LEGGINGS);
        playerKit.addArmor(createEnchantedItem(Material.CHAINMAIL_BOOTS, "§7Chain Boots",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 1)), ArmorType.BOOTS);


        Kit goldKit = new Kit("Gold", "kit.gold", Material.GOLD_CHESTPLATE, "§6Gold Kit");
        goldKit.addItem(createEnchantedItem(Material.STONE_SWORD, "§6Stone Sword",
                new EnchantmentData(Enchantment.DAMAGE_ALL, 5),
                new EnchantmentData(Enchantment.KNOCKBACK, 1),
                new EnchantmentData(Enchantment.FIRE_ASPECT, 1)), 0);
        goldKit.addItem(createEnchantedItem(Material.BOW, "§6Bow",
                new EnchantmentData(Enchantment.ARROW_DAMAGE, 1),
                new EnchantmentData(Enchantment.ARROW_INFINITE, 1)), 1);
        goldKit.addArmor(createEnchantedItem(Material.DIAMOND_HELMET, "§6Diamond Helmet",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 3),
                new EnchantmentData(Enchantment.DURABILITY, 1)), ArmorType.HELMET);
        goldKit.addArmor(createEnchantedItem(Material.IRON_CHESTPLATE, "§6Iron Chestplate",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 5),
                new EnchantmentData(Enchantment.DURABILITY, 1)), ArmorType.CHESTPLATE);
        goldKit.addArmor(createEnchantedItem(Material.IRON_LEGGINGS, "§6Iron Leggings",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 5),
                new EnchantmentData(Enchantment.DURABILITY, 1)), ArmorType.LEGGINGS);
        goldKit.addArmor(createEnchantedItem(Material.DIAMOND_BOOTS, "§6Diamond Boots",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 3),
                new EnchantmentData(Enchantment.DURABILITY, 1)), ArmorType.BOOTS);


        Kit diamondKit = new Kit("Diamond", "kit.diamond", Material.DIAMOND_CHESTPLATE, "§bDiamond Kit");
        diamondKit.addItem(createEnchantedItem(Material.IRON_SWORD, "§bIron Sword",
                new EnchantmentData(Enchantment.DAMAGE_ALL, 3),
                new EnchantmentData(Enchantment.KNOCKBACK, 1),
                new EnchantmentData(Enchantment.FIRE_ASPECT, 1)), 0);
        diamondKit.addItem(createEnchantedItem(Material.BOW, "§bBow",
                new EnchantmentData(Enchantment.ARROW_DAMAGE, 2),
                new EnchantmentData(Enchantment.ARROW_INFINITE, 1)), 1);
        diamondKit.addArmor(createEnchantedItem(Material.DIAMOND_HELMET, "§bDiamond Helmet",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 3),
                new EnchantmentData(Enchantment.DURABILITY, 2)), ArmorType.HELMET);
        diamondKit.addArmor(createEnchantedItem(Material.DIAMOND_CHESTPLATE, "§bDiamond Chestplate",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 3),
                new EnchantmentData(Enchantment.DURABILITY, 2)), ArmorType.CHESTPLATE);
        diamondKit.addArmor(createEnchantedItem(Material.IRON_LEGGINGS, "§bIron Leggings",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 3),
                new EnchantmentData(Enchantment.DURABILITY, 2)), ArmorType.LEGGINGS);
        diamondKit.addArmor(createEnchantedItem(Material.DIAMOND_BOOTS, "§bDiamond Boots",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 3),
                new EnchantmentData(Enchantment.DURABILITY, 2)), ArmorType.BOOTS);


        Kit emeraldKit = new Kit("Emerald", "kit.emerald", Material.IRON_CHESTPLATE, "§aEmerald Kit");
        emeraldKit.addItem(createEnchantedItem(Material.DIAMOND_SWORD, "§aDiamond Sword",
                new EnchantmentData(Enchantment.DAMAGE_ALL, 4),
                new EnchantmentData(Enchantment.KNOCKBACK, 2),
                new EnchantmentData(Enchantment.FIRE_ASPECT, 2)), 0);
        emeraldKit.addItem(createEnchantedItem(Material.BOW, "§aBow",
                new EnchantmentData(Enchantment.ARROW_DAMAGE, 3),
                new EnchantmentData(Enchantment.ARROW_INFINITE, 1)), 1);
        emeraldKit.addArmor(createEnchantedItem(Material.DIAMOND_HELMET, "§aDiamond Helmet",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 4),
                new EnchantmentData(Enchantment.DURABILITY, 3)), ArmorType.HELMET);
        emeraldKit.addArmor(createEnchantedItem(Material.DIAMOND_CHESTPLATE, "§aDiamond Chestplate",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 4),
                new EnchantmentData(Enchantment.DURABILITY, 3)), ArmorType.CHESTPLATE);
        emeraldKit.addArmor(createEnchantedItem(Material.DIAMOND_LEGGINGS, "§aDiamond Leggings",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 4),
                new EnchantmentData(Enchantment.DURABILITY, 3)), ArmorType.LEGGINGS);
        emeraldKit.addArmor(createEnchantedItem(Material.DIAMOND_BOOTS, "§aDiamond Boots",
                new EnchantmentData(Enchantment.PROTECTION_ENVIRONMENTAL, 4),
                new EnchantmentData(Enchantment.DURABILITY, 3)), ArmorType.BOOTS);

        kits.put("Player", playerKit);
        kits.put("Gold", goldKit);
        kits.put("Diamond", diamondKit);
        kits.put("Emerald", emeraldKit);
    }

    private ItemStack createEnchantedItem(Material material, String name, EnchantmentData... enchantments) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);

            meta.spigot().setUnbreakable(true);

            meta.addItemFlags(
                    org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS,
                    org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES,
                    org.bukkit.inventory.ItemFlag.HIDE_DESTROYS,
                    org.bukkit.inventory.ItemFlag.HIDE_PLACED_ON,
                    org.bukkit.inventory.ItemFlag.HIDE_POTION_EFFECTS,
                    org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE
            );
            item.setItemMeta(meta);
        }

        for (EnchantmentData enchData : enchantments) {
            item.addUnsafeEnchantment(enchData.getEnchantment(), enchData.getLevel());
        }

        return item;
    }


    public void giveKit(Player player, String kitName) {
        giveKit(player, kitName, false);
    }

    public void giveKit(Player player, String kitName, boolean forceGive) {
        Kit kit = kits.get(kitName);
        if (kit == null) return;

        if (!hasPermission(player, kit.getPermission())) {
            player.sendMessage("§cYou don't have permission to use this kit!");
            return;
        }

        if (!forceGive) {
            String currentKit = playerActiveKits.get(player.getUniqueId());
            if (currentKit != null && currentKit.equals(kitName)) {
                return;
            }
        }

        for (Map.Entry<Integer, ItemStack> entry : kit.getItems().entrySet()) {
            player.getInventory().setItem(entry.getKey(), entry.getValue());
        }

        if (kit.getHelmet() != null) player.getInventory().setHelmet(kit.getHelmet());
        if (kit.getChestplate() != null) player.getInventory().setChestplate(kit.getChestplate());
        if (kit.getLeggings() != null) player.getInventory().setLeggings(kit.getLeggings());
        if (kit.getBoots() != null) player.getInventory().setBoots(kit.getBoots());

        boolean hasArrows = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.ARROW) {
                hasArrows = true;
                break;
            }
        }

        if (!hasArrows) {
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        }

        player.updateInventory();

        playerActiveKits.put(player.getUniqueId(), kitName);
        savePlayerKits();
    }

    public void removeActiveKit(Player player) {
        playerActiveKits.remove(player.getUniqueId());
        savePlayerKits();
    }

    public void giveDefaultKit(Player player) {
        String[] kitOrder = {"Emerald", "Diamond", "Gold", "Player"};

        for (String kitName : kitOrder) {
            Kit kit = kits.get(kitName);
            if (kit != null && hasPermission(player, kit.getPermission())) {
                giveKit(player, kitName);
                return;
            }
        }

        giveKit(player, "Player");
    }

    public List<Kit> getAvailableKits(Player player) {
        List<Kit> availableKits = new ArrayList<>();

        for (Kit kit : kits.values()) {
            if (hasPermission(player, kit.getPermission())) {
                availableKits.add(kit);
            }
        }

        return availableKits;
    }

    public String getActiveKit(Player player) {
        return playerActiveKits.get(player.getUniqueId());
    }

    public boolean hasPermission(Player player, String permission) {
        if (permission.equals("kit.player")) return true;

        if (permission.equals("kit.gold") && player.hasPermission("kit.diamond")) return true;
        if (permission.equals("kit.gold") && player.hasPermission("kit.emerald")) return true;
        if (permission.equals("kit.diamond") && player.hasPermission("kit.emerald")) return true;

        return player.hasPermission(permission);
    }

    public Map<String, Kit> getKits() {
        return kits;
    }

    public static class EnchantmentData {
        private final Enchantment enchantment;
        private final int level;

        public EnchantmentData(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }

        public Enchantment getEnchantment() {
            return enchantment;
        }

        public int getLevel() {
            return level;
        }
    }

    public void savePlayerKits() {
        try {
            File file = new File(plugin.getDataFolder(), "player-kits.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            for (Map.Entry<UUID, String> entry : playerActiveKits.entrySet()) {
                config.set(entry.getKey().toString(), entry.getValue());
            }

            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save player kits: " + e.getMessage());
        }
    }

    public void loadPlayerKits() {
        try {
            File file = new File(plugin.getDataFolder(), "player-kits.yml");
            if (!file.exists()) return;

            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            for (String key : config.getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                String kitName = config.getString(key);
                playerActiveKits.put(uuid, kitName);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not load player kits: " + e.getMessage());
        }
    }


    public void removeKitItems(Player player, String kitName) {
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        player.getInventory().setItem(0, null);
        player.getInventory().setItem(1, null);

        player.updateInventory();
    }


    public void giveDefaultKitWithoutClear(Player player) {
        String currentKit = playerActiveKits.get(player.getUniqueId());
        if (currentKit != null) {
            removeKitItems(player, currentKit);
        }

        String[] kitOrder = {"Emerald", "Diamond", "Gold", "Player"};

        for (String kitName : kitOrder) {
            Kit kit = kits.get(kitName);
            if (kit != null && hasPermission(player, kit.getPermission())) {
                giveKitWithoutClear(player, kitName);
                return;
            }
        }

        giveKitWithoutClear(player, "Player");
    }


    public void giveKitWithoutClear(Player player, String kitName) {
        Kit kit = kits.get(kitName);
        if (kit == null) return;

        if (!hasPermission(player, kit.getPermission())) {
            player.sendMessage("§cYou don't have permission to use this kit!");
            return;
        }

        String currentKit = playerActiveKits.get(player.getUniqueId());

        if (currentKit != null) {
            removeKitItems(player, currentKit);
        }

        for (Map.Entry<Integer, ItemStack> entry : kit.getItems().entrySet()) {
            player.getInventory().setItem(entry.getKey(), entry.getValue());
        }

        if (kit.getHelmet() != null) player.getInventory().setHelmet(kit.getHelmet());
        if (kit.getChestplate() != null) player.getInventory().setChestplate(kit.getChestplate());
        if (kit.getLeggings() != null) player.getInventory().setLeggings(kit.getLeggings());
        if (kit.getBoots() != null) player.getInventory().setBoots(kit.getBoots());

        boolean hasArrows = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.ARROW) {
                hasArrows = true;
                break;
            }
        }

        if (!hasArrows) {
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        }

        player.updateInventory();

        playerActiveKits.put(player.getUniqueId(), kitName);
        savePlayerKits();
    }




    public void clearActiveKit(Player player) {
        playerActiveKits.remove(player.getUniqueId());
        savePlayerKits();
    }


    public void restoreMissingKitItems(Player player, String kitName) {
        Kit kit = kits.get(kitName);
        if (kit == null) return;

        if (player.getInventory().getHelmet() == null && kit.getHelmet() != null) {
            player.getInventory().setHelmet(kit.getHelmet());
        }
        if (player.getInventory().getChestplate() == null && kit.getChestplate() != null) {
            player.getInventory().setChestplate(kit.getChestplate());
        }
        if (player.getInventory().getLeggings() == null && kit.getLeggings() != null) {
            player.getInventory().setLeggings(kit.getLeggings());
        }
        if (player.getInventory().getBoots() == null && kit.getBoots() != null) {
            player.getInventory().setBoots(kit.getBoots());
        }

        ItemStack swordSlot = player.getInventory().getItem(0);
        if (swordSlot == null || !isSword(swordSlot.getType())) {
            ItemStack kitSword = kit.getItems().get(0);
            if (kitSword != null) {
                player.getInventory().setItem(0, kitSword);
            }
        }

        ItemStack bowSlot = player.getInventory().getItem(1);
        if (bowSlot == null || bowSlot.getType() != Material.BOW) {
            ItemStack kitBow = kit.getItems().get(1);
            if (kitBow != null) {
                player.getInventory().setItem(1, kitBow);
            }
        }

        player.updateInventory();
    }


    private boolean isSword(Material material) {
        return material == Material.WOOD_SWORD ||
                material == Material.STONE_SWORD ||
                material == Material.IRON_SWORD ||
                material == Material.DIAMOND_SWORD ||
                material == Material.GOLD_SWORD;
    }
}
