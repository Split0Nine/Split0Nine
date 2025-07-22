package org;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Shrbattlepass2 extends PlaceholderExpansion {

    private final Shrbattlepass plugin;

    public Shrbattlepass2(Shrbattlepass plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "Shrbattlepass";
    }
    @Override
    public String getIdentifier() {
        return "shrbattlepass";
    }
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "";
        }

        switch (params.toLowerCase()) {
            case "level":
                return String.valueOf(plugin.getLevel(player));

            case "progress":
                return String.valueOf(plugin.getProgressPercentage(player));

            case "progressbar":
                return plugin.getProgressBar(player);

            default:
                return null;
        }
    }
}
