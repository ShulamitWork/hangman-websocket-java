package com.example.hangman.server;

import com.example.hangman.websocket.HangmanEndpoint;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.tyrus.server.Server;

import java.util.Collections;
import java.util.Scanner;

public class HangmanServer {
    public static void main(String[] args) {
        System.out.println("[INFO] Starting HTTP + WebSocket servers...");

        // HTTP server on port 8080 (for static files)
        HttpServer httpServer = HttpServer.createSimpleServer("/", 8080);
        httpServer.getServerConfiguration().addHttpHandler(
                new StaticHttpHandler("src/main/resources/static"), "/");

        // WebSocket server on port 8081
        Server wsServer = new Server("localhost", 8081, "/", null,
                Collections.singleton(HangmanEndpoint.class));

        try {
            httpServer.start();
            wsServer.start();

            System.out.println("[INFO] HTTP server at http://localhost:8080");
            System.out.println("[INFO] WebSocket server at ws://localhost:8081/game/{sessionId}");
            System.out.println("Press Enter to stop...");
            new Scanner(System.in).nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wsServer.stop();
            httpServer.shutdownNow();
        }
    }
}
