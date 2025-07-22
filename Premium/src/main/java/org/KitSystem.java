package org;


import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.permission.Permission;
import org.commands.*;
import org.listeners.DesignBoxListener;
import org.listeners.KitListener;
import org.listeners.LootBoxListener;
import org.lootboxes.LootBoxManager;

public class KitSystem extends JavaPlugin {

    private LootBoxManager lootBoxManager;
    private Permission perms = null;
    private KitManager kitManager;


    @Override
    public void onEnable() {
        setupPermissions();

        lootBoxManager = new LootBoxManager(this);
        kitManager = new KitManager(this);

        lootBoxManager.loadDesignBoxes();

        getCommand("kit").setExecutor(new KitCommand(kitManager));
        getCommand("lootadd").setExecutor(new LootCommands(lootBoxManager, this));
        getCommand("lootremove").setExecutor(new LootCommands(lootBoxManager, this));
        getCommand("lootclear").setExecutor(new LootCommands(lootBoxManager, this));
        getCommand("clearitems").setExecutor(new LootCommands(lootBoxManager, this));
        getCommand("lootdesign").setExecutor(new LootDesignCommand(lootBoxManager));

        RegionCommands regionCommands = new RegionCommands(lootBoxManager);
        getCommand("pos1").setExecutor(regionCommands);
        getCommand("pos2").setExecutor(regionCommands);

        ExcludeCommands excludeCommands = new ExcludeCommands(lootBoxManager, this);
        getCommand("excludepos1").setExecutor(excludeCommands);
        getCommand("excludepos2").setExecutor(excludeCommands);
        getCommand("excludeclear").setExecutor(excludeCommands);
        getCommand("excludelist").setExecutor(excludeCommands);
        getCommand("designreload").setExecutor(new DesignBoxReloadCommand(lootBoxManager));

        getServer().getPluginManager().registerEvents(new LootBoxListener(lootBoxManager), this);
        getServer().getPluginManager().registerEvents(new KitListener(kitManager, this), this);
        getServer().getPluginManager().registerEvents(new DesignBoxListener(lootBoxManager), this);

        getLogger().info("KitSystem has been enabled!");
    }


    @Override
    public void onDisable() {
        getLogger().info("KitSystem has been disabled!");
        kitManager.savePlayerKits();
        if (lootBoxManager != null) {
            lootBoxManager.saveAllDesignBoxes();
        }
    }

    public LootBoxManager getLootBoxManager() {
        return lootBoxManager;
    }

    private boolean setupPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault not found! Using basic permissions.");
            return false;
        }

        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        perms = rsp.getProvider();
        return perms != null;
    }

    public Permission getPermissions() {
        return perms;
    }

    public KitManager getKitManager() {
        return kitManager;
    }
}