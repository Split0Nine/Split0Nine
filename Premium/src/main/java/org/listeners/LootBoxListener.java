package org.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.lootboxes.LootBoxManager;
import org.lootboxes.LootType;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.Material;


import java.util.List;

public class LootBoxListener implements Listener {
    private final LootBoxManager lootBoxManager;

    public LootBoxListener(LootBoxManager lootBoxManager) {
        this.lootBoxManager = lootBoxManager;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) return;

        ArmorStand box = (ArmorStand) event.getRightClicked();

        if (!lootBoxManager.getLootBoxes().containsKey(box.getUniqueId())) {
            return;
        }

        if (lootBoxManager.getDesignBoxes().containsValue(box)) {
            return;
        }

        event.setCancelled(true);
        lootBoxManager.openLootBox(event.getPlayer(), box);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        for (ArmorStand box : lootBoxManager.getLootBoxes().values()) {
            if (box != null && !box.isDead()) {
                if (lootBoxManager.isPlayerOnLootBox(player, box)) {
                    lootBoxManager.openLootBox(player, box);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().startsWith("Add Items - ")) {
            handleAddInventoryClick(event, player);
        } else if (event.getView().getTitle().startsWith("Remove Items - ")) {
            handleRemoveInventoryClick(event, player);
        }
    }

    private void handleAddInventoryClick(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null) return;

        String type = (String) player.getMetadata("loot_add_type").get(0).value();
        ItemStack item = event.getCurrentItem().clone();
        List<ItemStack> items = lootBoxManager.getItemsForType(LootType.valueOf(type.toUpperCase()));

        for (ItemStack existingItem : items) {
            if (areItemsIdentical(existingItem, item)) {
                player.sendMessage(ChatColor.RED + "This item is already added to " + type + " loot!");
                event.setCancelled(true);
                return;
            }
        }

        items.add(item);
        lootBoxManager.saveItems();

        player.sendMessage(ChatColor.GREEN + "Item added to " + type + " loot!");
        event.setCancelled(true);
    }

    private boolean areItemsIdentical(ItemStack item1, ItemStack item2) {
        if (item1.getType() != item2.getType()) return false;
        if (item1.getAmount() != item2.getAmount()) return false;

        if (item1.getType() == Material.ENCHANTED_BOOK && item2.getType() == Material.ENCHANTED_BOOK) {
            if (item1.hasItemMeta() && item2.hasItemMeta()) {
                EnchantmentStorageMeta meta1 = (EnchantmentStorageMeta) item1.getItemMeta();
                EnchantmentStorageMeta meta2 = (EnchantmentStorageMeta) item2.getItemMeta();
                return meta1.getStoredEnchants().equals(meta2.getStoredEnchants());
            }
            return !item1.hasItemMeta() && !item2.hasItemMeta();
        }

        if (item1.hasItemMeta() && item2.hasItemMeta()) {
            return item1.getItemMeta().equals(item2.getItemMeta());
        }

        return !item1.hasItemMeta() && !item2.hasItemMeta();
    }

    private void handleRemoveInventoryClick(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null) return;

        String type = (String) player.getMetadata("loot_remove_type").get(0).value();
        List<ItemStack> items = lootBoxManager.getItemsForType(LootType.valueOf(type.toUpperCase()));

        if (event.getSlot() >= 0 && event.getSlot() < items.size()) {
            ItemStack removedItem = items.remove(event.getSlot());
            lootBoxManager.saveItems();

            player.sendMessage(ChatColor.RED + "Item removed from " + type + " loot: " +
                    ChatColor.WHITE + removedItem.getAmount() + "x " + removedItem.getType().name());
        } else {
            player.sendMessage(ChatColor.RED + "Invalid item selection!");
        }

        event.setCancelled(true);
        player.closeInventory();
    }

}