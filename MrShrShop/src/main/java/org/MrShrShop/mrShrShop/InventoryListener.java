package org.MrShrShop.mrShrShop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inv = event.getInventory();
        if (!inv.getTitle().equalsIgnoreCase("§4MrShrShop")) return;

        event.setCancelled(true);


        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(inv)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        Material clicked = clickedItem.getType();

        switch (clicked) {
            case SANDSTONE -> {
                if (hasGold(player, 15)) {
                    removeGold(player, 15);
                    player.getInventory().addItem(new ItemStack(Material.SANDSTONE, 16));
                    player.sendMessage( "§7┃ §eWi§6x§cMC §7» §aYou bought 16 Sandstones!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 15 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }

            case IRON_SWORD -> {
                if (hasGold(player, 4)) {
                    removeGold(player, 4);
                    player.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought an Iron Sword!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 4 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }

            case WOOL -> {
                if (hasGold(player, 10)) {
                    removeGold(player, 10);
                    player.getInventory().addItem(new ItemStack(Material.WOOL, 16));
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought 16 Wool!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 10 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }


            case DIAMOND_SWORD -> {
                if (hasGold(player, 3)) {
                    removeGold(player, 3);
                    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                    ItemMeta meta = sword.getItemMeta();
                    meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
                    meta.addEnchant(Enchantment.DURABILITY, 3, true);
                    meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    sword.setItemMeta(meta);
                    player.getInventory().addItem(sword);
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought a Diamond Sword!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 3 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }

            case BOW -> {
                if (hasGold(player, 3)) {
                    removeGold(player, 3);
                    ItemStack bow = new ItemStack(Material.BOW);
                    ItemMeta meta = bow.getItemMeta();
                    meta.addEnchant(Enchantment.ARROW_DAMAGE, 4, true);
                    meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
                    meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
                    meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    bow.setItemMeta(meta);
                    player.getInventory().addItem(bow);
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought a Diamond Bow!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 3 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }

            case DIAMOND_CHESTPLATE -> {
                if (hasGold(player, 3)) {
                    removeGold(player, 3);
                    ItemStack armor = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    ItemMeta meta = armor.getItemMeta();
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                    meta.addEnchant(Enchantment.DURABILITY, 3, true);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    armor.setItemMeta(meta);
                    player.getInventory().addItem(armor);
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought a Diamond Chestplate!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 3 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }

            case DIAMOND_HELMET -> {
                if (hasGold(player, 3)) {
                    removeGold(player, 3);
                    ItemStack armor = new ItemStack(Material.DIAMOND_HELMET);
                    ItemMeta meta = armor.getItemMeta();
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                    meta.addEnchant(Enchantment.DURABILITY, 3, true);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    armor.setItemMeta(meta);
                    player.getInventory().addItem(armor);
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought a Diamond Helmet!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 3 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }

            case DIAMOND_LEGGINGS -> {
                if (hasGold(player, 3)) {
                    removeGold(player, 3);
                    ItemStack armor = new ItemStack(Material.DIAMOND_LEGGINGS);
                    ItemMeta meta = armor.getItemMeta();
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                    meta.addEnchant(Enchantment.DURABILITY, 3, true);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    armor.setItemMeta(meta);
                    player.getInventory().addItem(armor);
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought a Diamond Leggings!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 3 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }

            case DIAMOND_BOOTS -> {
                if (hasGold(player, 3)) {
                    removeGold(player, 3);
                    ItemStack armor = new ItemStack(Material.DIAMOND_BOOTS);
                    ItemMeta meta = armor.getItemMeta();
                    meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                    meta.addEnchant(Enchantment.DURABILITY, 3, true);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    armor.setItemMeta(meta);
                    player.getInventory().addItem(armor);
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought a Diamond Boots!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 3 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }

            case POTION -> {
                if (hasGold(player, 3)) {
                    removeGold(player, 3);

                    ItemStack potionItem = new ItemStack(Material.POTION, 1, (short) 8194);
                    ItemMeta meta = potionItem.getItemMeta();
                    meta.setDisplayName("§fPotion of Swiftness");
                    meta.setLore(Arrays.asList(
                            "§7Speed (3:00)",
                            "",
                            "§5When Consumed:",
                            "§9+20% Speed"
                    ));
                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                    potionItem.setItemMeta(meta);

                    player.getInventory().addItem(potionItem);
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought a Potion of Swiftness!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 3 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }

            case DIAMOND_AXE -> {
                if (hasGold(player, 3)) {
                    removeGold(player, 3);

                    ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
                    ItemMeta meta = axe.getItemMeta();

                    meta.setDisplayName("§dStrength Axe");
                    meta.setLore(Arrays.asList(
                            "§7Sharpness III",
                            "§7Strength Effect II",
                            "",
                            "§9+9.75 Attack Damage"
                    ));
                    meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
                    meta.addItemFlags(
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_ENCHANTS
                    );
                    axe.setItemMeta(meta);

                    player.getInventory().addItem(axe);
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought the §dStrength Axe§a!");
                } else {
                    player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 3 Gold to buy this.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                }
            }

            case GOLDEN_APPLE -> {
                ItemMeta clickedMeta = clickedItem.getItemMeta();
                int slot = event.getSlot();

                if (clickedMeta != null && clickedMeta.hasDisplayName()) {
                    String displayName = clickedMeta.getDisplayName();

                    if (displayName.equals("§dGolden Apple")) {
                        if (slot == 30) {
                            if (hasGold(player, 3)) {
                                removeGold(player, 3);
                                ItemStack godApple = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
                                ItemMeta meta = godApple.getItemMeta();
                                meta.setDisplayName("§dGolden Apple");
                                godApple.setItemMeta(meta);
                                player.getInventory().addItem(godApple);
                                player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought the §dGolden Apple!");
                            } else {
                                player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 3 Gold to buy this.");
                                player.closeInventory();
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                            }
                        } else if (slot == 31) {
                            if (hasGold(player, 3)) {
                                removeGold(player, 3);
                                ItemStack godApple2 = new ItemStack(Material.GOLDEN_APPLE, 10, (short) 1);
                                ItemMeta Meta2 = godApple2.getItemMeta();
                                Meta2.setDisplayName("§dGolden Apple");
                                godApple2.setItemMeta(Meta2);
                                player.getInventory().addItem(godApple2);
                                player.sendMessage("§7┃ §eWi§6x§cMC §7» §aYou bought the §dGolden Apple!");
                            } else {
                                player.sendMessage("§7┃ §eWi§6x§cMC §7» §4You need 3 Gold to buy this.");
                                player.closeInventory();
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                            }
                        }
                    }
                }
            }


            default -> player.sendMessage(ChatColor.RED + "This item cannot be purchased.");
        }
    }

    private boolean hasGold(Player player, int amount) {
        return player.getInventory().containsAtLeast(new ItemStack(Material.GOLD_INGOT), amount);
    }

    private void removeGold(Player player, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() != Material.GOLD_INGOT) continue;

            int stackAmount = item.getAmount();
            if (stackAmount <= remaining) {
                remaining -= stackAmount;
                contents[i] = null;
            } else {
                item.setAmount(stackAmount - remaining);
                remaining = 0;
                break;
            }
        }

        player.getInventory().setContents(contents);
    }

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player attacker = (Player) event.getDamager();
        ItemStack item = attacker.getItemInHand();
        if (item == null || item.getType() != Material.DIAMOND_AXE) return;

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        String name = item.getItemMeta().getDisplayName();

        if (!name.equals("§dStrength Axe")) return;

        long now = System.currentTimeMillis();
        long last = cooldowns.getOrDefault(attacker.getUniqueId(), 0L);
        if ((now - last) < 500) return;

        cooldowns.put(attacker.getUniqueId(), now);

        attacker.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 15, 1));
    }
}