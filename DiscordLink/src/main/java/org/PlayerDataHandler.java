package org;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class PlayerDataHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String requestBody = readRequestBody(exchange);

        String uuid = null;
        String name = null;

        try {
            JSONObject json = (JSONObject) new JSONParser().parse(requestBody);
            uuid = (String) json.get("uuid");
            name = (String) json.get("name");
        } catch (Exception e) {
            sendJson(exchange, 400, "{\"error\":\"Invalid JSON format\"}");
            return;
        }

        OfflinePlayer player = null;

        if (uuid != null && !uuid.isEmpty()) {
            try {
                player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            } catch (IllegalArgumentException e) {
                sendJson(exchange, 400, "{\"error\":\"Invalid UUID format\"}");
                return;
            }
        }

        if ((player == null || (!player.hasPlayedBefore() && !player.isOnline())) && name != null) {
            player = Bukkit.getOfflinePlayer(name);
        }

        if (player == null || (!player.hasPlayedBefore() && !player.isOnline())) {
            sendJson(exchange, 404, "{\"error\":\"Player not found\"}");
            return;
        }

        try {
            UUID playerUUID = player.getUniqueId();
            String playerName = player.getName();


            LuckPerms lp = LuckPermsProvider.get();
            User user = lp.getUserManager().loadUser(playerUUID).join();

            Group highestGroup = lp.getGroupManager().getLoadedGroups().stream()
                    .filter(g -> user.getInheritedGroups(QueryOptions.nonContextual()).contains(g))
                    .max((g1, g2) -> {
                        int w1 = g1.getWeight().orElse(0);
                        int w2 = g2.getWeight().orElse(0);
                        return Integer.compare(w1, w2);
                    }).orElse(null);

            String primaryGroup = (highestGroup != null) ? highestGroup.getName() : user.getPrimaryGroup();
            String displayName = getGroupDisplayName(lp, primaryGroup);


            String prefix = user.getCachedData().getMetaData(QueryOptions.nonContextual()).getPrefix();
            if (prefix == null || prefix.length() < 2 || !prefix.startsWith("§")) {
                prefix = "§7";
            }

            String coloredName = prefix + playerName;
            int hours = PlaytimeStorage.getPlaytimeHours(player);

            JSONObject response = new JSONObject();
            response.put("uuid", playerUUID.toString());
            response.put("name", playerName);
            response.put("coloredName", coloredName);
            response.put("rank", primaryGroup);
            response.put("rankColor", prefix);
            response.put("hours", hours);
            response.put("displayName", displayName);
            response.put("isOnline", player.isOnline());
            response.put("lastSeen", player.getLastPlayed());

            sendJson(exchange, 200, response.toJSONString());

        } catch (Exception e) {
            DiscordLink.getInstance().getLogger().severe("Error handling player data request: " + e.getMessage());
            e.printStackTrace();
            sendJson(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
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




    private String readRequestBody(HttpExchange exchange) throws IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }

    private void sendJson(HttpExchange exchange, int code, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(code, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
            os.flush();
        }
    }
}