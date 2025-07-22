package org;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class LinkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        if (args.length != 2 || !args[0].equalsIgnoreCase("link")) {
            sender.sendMessage("§cUsage: /discord link <discord_id>");
            return true;
        }

        Player player = (Player) sender;
        String discordId = args[1];

        if (!discordId.matches("\\d+")) {
            player.sendMessage("§cInvalid Discord ID format!");
            return true;
        }

        player.sendMessage("§eAttempting to link your account...");

        try {
            String uuid = player.getUniqueId().toString();
            String name = player.getName();

            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().loadUser(player.getUniqueId()).join();

            Group highestGroup = api.getGroupManager().getLoadedGroups().stream()
                    .filter(g -> user.getInheritedGroups(QueryOptions.nonContextual()).contains(g))
                    .max((g1, g2) -> {
                        int w1 = g1.getWeight().orElse(0);
                        int w2 = g2.getWeight().orElse(0);
                        return Integer.compare(w1, w2);
                    }).orElse(null);

            String primaryGroup = (highestGroup != null) ? highestGroup.getName() : user.getPrimaryGroup();
            String displayName = getGroupDisplayName(api, primaryGroup);
            DiscordLink.getInstance().getLogger().info("DisplayName: " + displayName);


            String prefix = user.getCachedData().getMetaData(QueryOptions.nonContextual()).getPrefix();
            if (prefix == null || prefix.length() < 2 || !prefix.startsWith("§")) {
                prefix = "§7";
            }

            String coloredName = prefix + name;
            int hours = PlaytimeStorage.getPlaytimeHours(player);


            JSONObject json = new JSONObject();
            json.put("discordId", discordId);
            json.put("uuid", uuid);
            json.put("name", name);
            json.put("coloredName", coloredName);
            json.put("rank", primaryGroup);
            json.put("rankColor", prefix);
            json.put("hours", hours);
            json.put("displayName", displayName);

            String response = sendLinkRequest(json);

            if (response != null && response.contains("\"success\":true")) {
                player.sendMessage("§a✅ Your account has been successfully linked to Discord!");
                player.sendMessage("§eYou can now use Discord commands to check your stats.");
            } else {
                player.sendMessage("§c❌ Failed to link your account. Please try again.");
                if (response != null && response.contains("already linked")) {
                    player.sendMessage("§cThis account is already linked to another Discord account.");
                    player.sendMessage("§cContact an admin if you need help.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage("§c❌ An unexpected error occurred. Please contact an admin.");
            DiscordLink.getInstance().getLogger().severe("Error linking account: " + e.getMessage());
        }

        return true;
    }

    private String getGroupDisplayName(LuckPerms api, String groupName) {
        try {
            Optional<Group> optionalGroup = api.getGroupManager().loadGroup(groupName).join();
            Group group = optionalGroup.orElse(null);
            if (group != null) {
                String displayName = group.getCachedData().getMetaData(QueryOptions.nonContextual()).getMetaValue("displayname");
                if (displayName != null && !displayName.isEmpty()) {
                    return displayName;
                }
            }
        } catch (Exception e) {
            DiscordLink.getInstance().getLogger().warning("Failed to get display name for group " + groupName + ": " + e.getMessage());
        }
        return groupName;
    }




    private String sendLinkRequest(JSONObject json) {
        try {
            String webhookURL = DiscordLink.getInstance().getConfig().getString("webhook", "http://ip:port/player-data");
            URL url = new URL(webhookURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            con.setConnectTimeout(5000);
            con.setReadTimeout(10000);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.toJSONString().getBytes(StandardCharsets.UTF_8);
                os.write(input);
                os.flush();
            }

            int responseCode = con.getResponseCode();

            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? con.getInputStream()
                    : con.getErrorStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            DiscordLink.getInstance().getLogger().info("Link response code: " + responseCode);
            DiscordLink.getInstance().getLogger().info("Link response: " + response.toString());

            return response.toString();

        } catch (Exception e) {
            DiscordLink.getInstance().getLogger().severe("Failed to send link request: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}