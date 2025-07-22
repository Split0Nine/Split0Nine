package org.lootboxes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramManager {

    private Map<UUID, ArmorStand> holograms = new HashMap<>();

    public ArmorStand createHologram(Location location, String coloredText) {
        Location hologramLoc = location.clone().add(0, 2.3, 0);

        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(hologramLoc, EntityType.ARMOR_STAND);
        hologram.setGravity(false);
        hologram.setCanPickupItems(false);
        hologram.setVisible(false);
        hologram.setSmall(false);
        hologram.setBasePlate(false);
        hologram.setArms(false);

        String finalText = ChatColor.translateAlternateColorCodes('&', coloredText);
        hologram.setCustomName(finalText);
        hologram.setCustomNameVisible(true);
        hologram.setMarker(true);

        return hologram;
    }

    public ArmorStand createHologram(Location location, String text, ChatColor color) {
        return createHologram(location, color + text);
    }

    public void addHologram(UUID boxId, ArmorStand hologram) {
        holograms.put(boxId, hologram);
    }

    public void removeHologram(UUID boxId) {
        ArmorStand hologram = holograms.get(boxId);
        if (hologram != null && !hologram.isDead()) {
            hologram.remove();
        }
        holograms.remove(boxId);
    }

    public ArmorStand getHologram(UUID boxId) {
        return holograms.get(boxId);
    }

    public void clearAllHolograms() {
        for (ArmorStand hologram : holograms.values()) {
            if (hologram != null && !hologram.isDead()) {
                hologram.remove();
            }
        }
        holograms.clear();
    }

    public Map<UUID, ArmorStand> getAllHolograms() {
        return holograms;
    }
}