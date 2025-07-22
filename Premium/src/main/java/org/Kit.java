package org;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class Kit {

    private final String name;
    private final String permission;
    private final Material icon;
    private final String displayName;
    private final Map<Integer, ItemStack> items;
    private ItemStack helmet, chestplate, leggings, boots;

    public Kit(String name, String permission, Material icon, String displayName) {
        this.name = name;
        this.permission = permission;
        this.icon = icon;
        this.displayName = displayName;
        this.items = new HashMap<>();
    }

    public void addItem(ItemStack item, int slot) {
        items.put(slot, item);
    }

    public void addArmor(ItemStack armor, ArmorType type) {
        switch (type) {
            case HELMET:
                this.helmet = armor;
                break;
            case CHESTPLATE:
                this.chestplate = armor;
                break;
            case LEGGINGS:
                this.leggings = armor;
                break;
            case BOOTS:
                this.boots = armor;
                break;
        }
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Map<Integer, ItemStack> getItems() {
        return items;
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public boolean isKitItem(ItemStack item) {
        if (item == null) return false;
        if (item.equals(helmet) || item.equals(chestplate) || item.equals(leggings) || item.equals(boots)) {
            return true;
        }
        return items.containsValue(item);
    }

}
