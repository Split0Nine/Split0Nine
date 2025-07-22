package org.menus;

import org.Kit;
import org.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class KitGUI {

    public static void openKitGUI(Player player, KitManager kitManager) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8Kits");


        List<Kit> availableKits = kitManager.getAvailableKits(player);
        String activeKit = kitManager.getActiveKit(player);

        for (Kit kit : availableKits) {
            ItemStack item = new ItemStack(kit.getIcon());
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(kit.getDisplayName());

                if (kit.getName().equals(activeKit)) {

                } else {
                    meta.setLore(Arrays.asList("§7Click to select"));
                }

                item.setItemMeta(meta);
            }

            if (kit.getName().equals(activeKit)) {
                item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
                ItemMeta glowMeta = item.getItemMeta();
                if (glowMeta != null) {
                    glowMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                    item.setItemMeta(glowMeta);
                }
            }

            int slot = 0;
            switch (kit.getName()) {
                case "Player":
                    slot = 0;
                    break;
                case "Gold":
                    slot = 1;
                    break;
                case "Diamond":
                    slot = 2;
                    break;
                case "Emerald":
                    slot = 3;
                    break;
            }

            inv.setItem(slot, item);
        }

        ItemStack defaultKit = new ItemStack(Material.BARRIER);
        ItemMeta defaultMeta = defaultKit.getItemMeta();
        if (defaultMeta != null) {
            defaultMeta.setDisplayName("§cDefault Kit");
            defaultMeta.setLore(Arrays.asList("§7Click to get Player Kit"));
            defaultKit.setItemMeta(defaultMeta);
        }
        inv.setItem(8, defaultKit);

        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName("§7");
            glass.setItemMeta(glassMeta);
        }

        for (int i = 18; i < 27; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }

        player.openInventory(inv);
    }
}
