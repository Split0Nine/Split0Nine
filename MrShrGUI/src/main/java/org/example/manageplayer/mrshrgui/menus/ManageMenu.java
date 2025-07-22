package org.example.manageplayer.mrshrgui.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class ManageMenu {


    public static void open(Player player, Player target) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.RED + "Manage " + ChatColor.WHITE + target.getDisplayName());

        ItemStack targetHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) targetHead.getItemMeta();
        skullMeta.setOwner(target.getName());
        skullMeta.setDisplayName(ChatColor.WHITE + target.getName());
        targetHead.setItemMeta(skullMeta);
        inv.setItem(4, targetHead);

        ItemStack heal = new ItemStack(Material.INK_SACK, 1, (short) 1);
        ItemMeta healMeta = heal.getItemMeta();
        healMeta.setDisplayName(ChatColor.RED + "Heal");
        healMeta.setLore(Arrays.asList(ChatColor.WHITE + "Heal Player"));
        heal.setItemMeta(healMeta);
        inv.setItem(11, heal);

        ItemStack kill = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta killMeta = kill.getItemMeta();
        killMeta.setDisplayName(ChatColor.BLUE + "Kill");
        killMeta.setLore(Arrays.asList(ChatColor.WHITE + "Kill Player"));
        killMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        kill.setItemMeta(killMeta);
        inv.setItem(15, kill);

        ItemStack gmStar = new ItemStack(Material.NETHER_STAR);
        ItemMeta gmMeta = gmStar.getItemMeta();
        gmMeta.setDisplayName(ChatColor.RED + "Mode Manage");
        gmMeta.setLore(Arrays.asList(ChatColor.GRAY + "Player options"));
        gmStar.setItemMeta(gmMeta);
        inv.setItem(22, gmStar);

        player.openInventory(inv);
    }

    public static void openGameModeMenu(Player player, Player target) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.RED + "Mode Manage " + ChatColor.WHITE + target.getDisplayName());

        ItemStack modeItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = modeItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Mode Options Manage");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "♦ Creative",
                ChatColor.GRAY + "♦ Survival",
                ChatColor.GRAY + "♦ Adventure",
                "",
                ChatColor.GOLD + "Left-Click: " + ChatColor.YELLOW + "To set mode",
                ChatColor.GOLD + "Right-Click: " + ChatColor.YELLOW + "Teleport player to you"
        ));
        modeItem.setItemMeta(meta);
        inv.setItem(4, modeItem);

        player.openInventory(inv);
    }
}
