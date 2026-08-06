package web;

import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {}
        }

        String publicDir = resolvePublicDir();

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new ApiHandler(publicDir));
            server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
            server.start();

            System.out.println("=================================================");
            System.out.println("  Library Management System - Web UI Server");
            System.out.println("=================================================");
            System.out.println(" Server active at: http://localhost:" + port);
            System.out.println(" Serving static files from: " + publicDir);
            System.out.println(" Press Ctrl+C to terminate the server.");
            System.out.println("=================================================");

        } catch (IOException e) {
            System.err.println("Failed to start WebServer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String resolvePublicDir() {
        // Look for public directory in root or current path
        String[] candidates = {
            "src/web/public",
            "public",
            "../src/web/public",
            "LibraryManagementSystem/src/web/public"
        };
        for (String c : candidates) {
            File f = new File(c);
            if (f.exists() && f.isDirectory()) {
                return f.getAbsolutePath();
            }
        }
        File current = new File("src/web/public");
        current.mkdirs();
        return current.getAbsolutePath();
    }
}
