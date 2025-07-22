package org.listeners;

import org.Kit;
import org.KitManager;
import org.KitSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;


public class KitListener implements Listener {

    private final KitManager kitManager;
    private final KitSystem plugin;

    public KitListener(KitManager kitManager, KitSystem plugin) {
        this.kitManager = kitManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String currentKit = kitManager.getActiveKit(player);
        if (currentKit == null) {
            kitManager.giveDefaultKit(player);
        }
    }


    private boolean hasEssentialKitItems(Player player, String kitName) {
        Kit kit = kitManager.getKits().get(kitName);
        if (kit == null) return false;

        boolean hasHelmet = hasArmorPiece(player, kit.getHelmet());
        boolean hasChestplate = hasArmorPiece(player, kit.getChestplate());
        boolean hasLeggings = hasArmorPiece(player, kit.getLeggings());
        boolean hasBoots = hasArmorPiece(player, kit.getBoots());

        boolean hasSword = hasSwordInInventory(player);

        boolean hasBow = hasBowInInventory(player);

        return hasHelmet && hasChestplate && hasLeggings && hasBoots && hasSword && hasBow;
    }


    private boolean hasArmorPiece(Player player, ItemStack expectedArmor) {
        if (expectedArmor == null) return true;

        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack armorPiece : armor) {
            if (armorPiece != null && armorPiece.getType() == expectedArmor.getType()) {
                return true;
            }
        }

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == expectedArmor.getType()) {
                return true;
            }
        }

        return false;
    }


    private boolean hasSwordInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isSword(item.getType())) {
                return true;
            }
        }
        return false;
    }


    private boolean hasBowInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.BOW) {
                return true;
            }
        }
        return false;
    }


    private void restoreMissingKitItemsAdvanced(Player player, String kitName) {
        Kit kit = kitManager.getKits().get(kitName);
        if (kit == null) return;

        if (!hasArmorPiece(player, kit.getHelmet()) && kit.getHelmet() != null) {
            player.getInventory().setHelmet(kit.getHelmet());
        }
        if (!hasArmorPiece(player, kit.getChestplate()) && kit.getChestplate() != null) {
            player.getInventory().setChestplate(kit.getChestplate());
        }
        if (!hasArmorPiece(player, kit.getLeggings()) && kit.getLeggings() != null) {
            player.getInventory().setLeggings(kit.getLeggings());
        }
        if (!hasArmorPiece(player, kit.getBoots()) && kit.getBoots() != null) {
            player.getInventory().setBoots(kit.getBoots());
        }

        if (!hasSwordInInventory(player)) {
            ItemStack kitSword = kit.getItems().get(0);
            if (kitSword != null) {
                player.getInventory().setItem(0, kitSword);
            }
        }

        if (!hasBowInInventory(player)) {
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals("§8Kits")) {
            event.setCancelled(true);

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) {
                return;
            }

            String displayName = clicked.getItemMeta().getDisplayName();

            if (displayName.equals("§cDefault Kit")) {
                kitManager.clearActiveKit(player);
                kitManager.giveKit(player, "Player");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            for (Kit kit : kitManager.getKits().values()) {
                if (kit.getDisplayName().equals(displayName)) {
                    kitManager.giveKit(player, kit.getName());
                    player.closeInventory();
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String activeKit = kitManager.getActiveKit(player);

        if (activeKit != null) {
            Kit kit = kitManager.getKits().get(activeKit);

            if (kit != null) {
                if (!kitManager.hasPermission(player, kit.getPermission())) {
                    event.getDrops().removeIf(item -> {
                        if (item == null) return false;
                        return kit.isKitItem(item);
                    });
                    kitManager.clearActiveKit(player);
                } else {
                    event.getDrops().removeIf(item -> {
                        if (item == null) return false;
                        return kit.isKitItem(item);
                    });
                }
            }
        }
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        String activeKit = kitManager.getActiveKit(player);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (activeKit != null) {
                Kit kit = kitManager.getKits().get(activeKit);

                if (kit != null && kitManager.hasPermission(player, kit.getPermission())) {
                    kitManager.giveKitWithoutClear(player, activeKit);
                } else {
                    kitManager.giveDefaultKitWithoutClear(player);
                }
            } else {
                kitManager.giveDefaultKitWithoutClear(player);
            }
        }, 10L);
    }
}
