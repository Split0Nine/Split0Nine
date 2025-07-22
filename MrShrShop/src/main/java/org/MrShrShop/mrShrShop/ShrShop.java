package org.MrShrShop.mrShrShop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;

public class ShrShop {

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, "§4MrShrShop");


        ItemStack sandstone = new ItemStack(Material.SANDSTONE, 16);
        ItemMeta sandstoneMeta = sandstone.getItemMeta();
        sandstoneMeta.setDisplayName("§eSandstone");
        sandstoneMeta.setLore(Arrays.asList(
                "§7Cost: §b15 §6Gold",
                "",
                "§b→ Click to purchase"

        ));
        sandstone.setItemMeta(sandstoneMeta);
        inv.setItem(4, sandstone);


        ItemStack ironSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = ironSword.getItemMeta();
        swordMeta.setDisplayName("§bIron Sword");
        swordMeta.setLore(Arrays.asList(
                "§7Cost: §b4 §6Gold",
                "",
                "§b→ Click to purchase"

        ));
        swordMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        ironSword.setItemMeta(swordMeta);
        inv.setItem(13, ironSword);


        ItemStack wool = new ItemStack(Material.WOOL, 16);
        ItemMeta woolMeta = wool.getItemMeta();
        woolMeta.setDisplayName("§fWool");
        woolMeta.setLore(Arrays.asList(
                "§7Cost: §b10 §6Gold",
                "",
                "§b→ Click to purchase"


        ));
        wool.setItemMeta(woolMeta);
        inv.setItem(22, wool);


        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta chestplateMeta = chestplate.getItemMeta();
        chestplateMeta.setDisplayName("§bDiamond Chestplate");
        chestplateMeta.setLore(Arrays.asList(
                "§7Protection IV",
                "§7Unbreaking III",
                "",
                "§7Cost: §b3 §6Gold",
                "",
                "§b→ Click to purchase"
        ));
        chestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        chestplateMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        chestplateMeta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS
        );
        chestplate.setItemMeta(chestplateMeta);
        inv.setItem(9, chestplate);


        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.setDisplayName("§bDiamond Helmet");
        helmetMeta.setLore(Arrays.asList(
                "§7Protection IV",
                "§7Unbreaking III",
                "",
                "§7Cost: §b3 §6Gold",
                "",
                "§b→ Click to purchase"
        ));
        helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        helmetMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        helmetMeta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS
        );
        helmet.setItemMeta(helmetMeta);
        inv.setItem(0, helmet);

        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemMeta leggingsMeta = leggings.getItemMeta();
        leggingsMeta.setDisplayName("§bDiamond Helmet");
        leggingsMeta.setLore(Arrays.asList(
                "§7Protection IV",
                "§7Unbreaking III",
                "",
                "§7Cost: §b3 §6Gold",
                "",
                "§b→ Click to purchase"
        ));
        leggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        leggingsMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        leggingsMeta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS
        );
        leggings.setItemMeta(leggingsMeta);
        inv.setItem(18, leggings);

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta bootsMeta = boots.getItemMeta();
        bootsMeta.setDisplayName("§bDiamond Helmet");
        bootsMeta.setLore(Arrays.asList(
                "§7Protection IV",
                "§7Unbreaking III",
                "",
                "§7Cost: §b3 §6Gold",
                "",
                "§b→ Click to purchase"
        ));
        bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        bootsMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        bootsMeta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS
        );
        boots.setItemMeta(bootsMeta);
        inv.setItem(27, boots);

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordItemMeta = sword.getItemMeta();
        swordItemMeta.setDisplayName("§bDiamond Sword");
        swordItemMeta.setLore(Arrays.asList(
                "§7Sharpness V",
                "§7Fire Aspect II",
                "§7Unbreaking III",
                "",
                "§7Cost: §b3 §6Gold",
                "",
                "§b→ Click to purchase"
        ));
        swordItemMeta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        swordItemMeta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        swordItemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        swordItemMeta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS
        );
        sword.setItemMeta(swordItemMeta);
        inv.setItem(7, sword);

        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowItemMeta = bow.getItemMeta();
        bowItemMeta.setDisplayName("§bBow");
        bowItemMeta.setLore(Arrays.asList(
                "§7Power IV",
                "§7Punch II",
                "§7Flame I",
                "§7Infinity I",
                "",
                "§7Cost: §b3 §6Gold",
                "",
                "§b→ Click to purchase"
        ));
        bowItemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 4, true);
        bowItemMeta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
        bowItemMeta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
        bowItemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        bowItemMeta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS
        );
        bow.setItemMeta(bowItemMeta);
        inv.setItem(8, bow);

        ItemStack speedPotion = new ItemStack(Material.POTION, 1, (short) 8194);
        ItemMeta speedMeta = speedPotion.getItemMeta();

        speedMeta.setDisplayName("§fPotion of Swiftness");
        speedMeta.setLore(Arrays.asList(
                "§7Speed (3:00)",
                "",
                "§5When Consumed:",
                "§9+20% Speed",
                "",
                "§7Cost: §b3 §6Gold",
                "",
                "§b→ Click to purchase"
        ));
        speedMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        speedPotion.setItemMeta(speedMeta);
        inv.setItem(28, speedPotion);


        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = axe.getItemMeta();

        meta.setDisplayName("§dStrength Axe");
        meta.setLore(Arrays.asList(
                "§7Sharpness III",
                "§7Strength Effect II",
                "",
                "§7Cost: §b3 §6Gold",
                "",
                "§b→ Click to purchase"
        ));
        meta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS
        );
        axe.setItemMeta(meta);
        inv.setItem(29, axe);




        ItemStack godApple = new ItemStack(Material.GOLDEN_APPLE, 1, (short)1);
        ItemMeta Meta = godApple.getItemMeta();

        Meta.setDisplayName("§dGolden Apple");
        Meta.setLore(Arrays.asList(
                "",
                "§7Cost: §b3 §6Gold",
                "",
                "§b→ Click to purchase"
        ));
        godApple.setItemMeta(Meta);
        inv.setItem(30, godApple);


        ItemStack godApple2 = new ItemStack(Material.GOLDEN_APPLE, 10, (short)1);
        ItemMeta Meta2 = godApple2.getItemMeta();

        Meta2.setDisplayName("§dGolden Apple");
        Meta2.setLore(Arrays.asList(
                "",
                "§7Cost: §b3 §6Gold",
                "",
                "§b→ Click to purchase"
        ));
        godApple2.setItemMeta(Meta2);
        inv.setItem(31, godApple2);
        player.openInventory(inv);
    }
}