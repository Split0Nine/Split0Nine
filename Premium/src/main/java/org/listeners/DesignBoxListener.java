package org.listeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.lootboxes.LootBoxManager;
import org.lootboxes.LootType;

public class DesignBoxListener implements Listener {

    private final LootBoxManager lootBoxManager;

    public DesignBoxListener(LootBoxManager lootBoxManager) {
        this.lootBoxManager = lootBoxManager;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) return;

        ArmorStand clickedBox = (ArmorStand) event.getRightClicked();
        Player player = event.getPlayer();

        if (!lootBoxManager.getDesignBoxes().containsValue(clickedBox)) {
        }

        event.setCancelled(true);

        LootType type = lootBoxManager.getLootTypeFromBox(clickedBox);
        lootBoxManager.openLootPreview(player, type);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        if (title.contains("Yellow Loot (Preview)") ||
                title.contains("Green Loot (Preview)") ||
                title.contains("Black Loot (Preview)")) {

            event.setCancelled(true);
        }
    }
}