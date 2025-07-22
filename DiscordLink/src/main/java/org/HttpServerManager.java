package org;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpServerManager {

    private HttpServer server;
    private final int port;

    public HttpServerManager(int port) {
        this.port = port;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    server = HttpServer.create(new InetSocketAddress(port), 0);

                    server.createContext("/player-data", new PlayerDataHandler());

                    server.setExecutor(Executors.newFixedThreadPool(2));
                    server.start();

                    DiscordLink.getInstance().getLogger().info("✅ HTTP Server started on port " + port);

                } catch (IOException e) {
                    DiscordLink.getInstance().getLogger().severe("❌ Failed to start HTTP server: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(DiscordLink.getInstance());
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            DiscordLink.getInstance().getLogger().info("HTTP Server stopped");
        }
    }
}