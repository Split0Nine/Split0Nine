package org.MrShrShop.mrShrShop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class MrShrShop extends JavaPlugin implements Listener {

    private List<Byte> clayColors;
    private Random random;

    @Override
    public void onEnable() {
        getCommand("ShrShop").setExecutor(new ShrCommand());
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(this, this);


        clayColors = Arrays.asList(
                (byte) 0,
                (byte) 1,
                (byte) 2,
                (byte) 3,
                (byte) 4,
                (byte) 5,
                (byte) 6,
                (byte) 7,
                (byte) 8,
                (byte) 9,
                (byte) 10,
                (byte) 11,
                (byte) 12,
                (byte) 13,
                (byte) 14,
                (byte) 15
        );

        random = new Random();
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        ItemStack horseArmor = new ItemStack(Material.IRON_BARDING);
        ItemMeta meta = horseArmor.getItemMeta();
        meta.setDisplayName("§6Rainbow Block Launcher");
        meta.setLore(Arrays.asList("§7Right click to place colorful clay blocks!"));
        horseArmor.setItemMeta(meta);

        player.getInventory().setItem(4, horseArmor);
        player.getInventory().setHeldItemSlot(4);
        player.sendMessage("§aWelcome! You received the Rainbow Clay Launcher!");
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (item.getType() == Material.IRON_BARDING &&
                item.hasItemMeta() &&
                item.getItemMeta().getDisplayName().equals("§6Rainbow Block Launcher")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot drop the Rainbow Block Launcher!");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        if (item.getType() == Material.IRON_BARDING &&
                item.hasItemMeta() &&
                item.getItemMeta().getDisplayName().equals("§6Rainbow Block Launcher")) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("§cYou cannot move the Rainbow Block Launcher!");
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                ItemStack slot5Item = player.getInventory().getItem(4);
                if (slot5Item == null || slot5Item.getType() != Material.IRON_BARDING) {
                    ItemStack horseArmor = new ItemStack(Material.IRON_BARDING);
                    ItemMeta meta = horseArmor.getItemMeta();
                    meta.setDisplayName("§6Fun Gun");
                    meta.setLore(Arrays.asList("§7Right click to place colorful clay blocks!"));
                    horseArmor.setItemMeta(meta);
                    player.getInventory().setItem(4, horseArmor);
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item == null || item.getType() != Material.IRON_BARDING) {
            return;
        }

        if (!event.getAction().toString().contains("RIGHT_CLICK")) {
            return;
        }

        event.setCancelled(true);

        launchColorfulBlocks(player);

    }

    private void launchColorfulBlocks(Player player) {
        Location playerLoc = player.getLocation().add(0, 1, 0);

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 60) {
                    return;
                }

                if (ticks % 2 == 0) {
                    int blockCount = random.nextInt(3) + 3;

                    for (int i = 0; i < blockCount; i++) {
                        byte clayColor = clayColors.get(random.nextInt(clayColors.size()));

                        Location blockLoc = playerLoc.clone().add(
                                random.nextInt(11) - 5,
                                random.nextInt(8) + 1,
                                random.nextInt(11) - 5
                        );

                        blockLoc.getBlock().setType(Material.STAINED_CLAY);
                        blockLoc.getBlock().setData(clayColor);

                        final Location finalLoc = blockLoc.clone();
                        Bukkit.getScheduler().runTaskLater(MrShrShop.this, new Runnable() {
                            @Override
                            public void run() {
                                if (finalLoc.getBlock().getType() == Material.STAINED_CLAY) {
                                    finalLoc.getBlock().setType(Material.AIR);
                                }
                            }
                        }, 60L);
                    }
                }

                ticks++;
            }
        }, 0L, 1L);
    }
}