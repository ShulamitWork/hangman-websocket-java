package com.example.hangman.server;

import org.glassfish.tyrus.server.Server;

import java.util.Scanner;

public class HangmanServer {
    public static void main(String[] args) {
        Server server = new Server("localhost", 8080, "/", "com.example.hangman.websocket");

        try {
            server.start();
            System.out.println("Server started at ws://localhost:8080/game/{sessionId}");
            System.out.println("Press Enter to stop...");
            new Scanner(System.in).nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
