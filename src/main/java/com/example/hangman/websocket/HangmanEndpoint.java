package com.example.hangman.websocket;

import com.example.hangman.model.GameSession;
import com.example.hangman.persistence.SessionManager;
import com.example.hangman.service.RandomWordService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.Session;  // WebSocket session


import java.io.IOException;
import java.util.Set;

// WebSocket server endpoint for the Hangman game
@ServerEndpoint("/game/{sessionId}")
public class HangmanEndpoint {

    // Called when a new WebSocket connection is opened
    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) throws IOException {
        GameSession gameSession = SessionManager.getSession(sessionId);

        // If there is no existing game session, create a new one with a random word
        if (gameSession == null) {
            gameSession = new GameSession(RandomWordService.fetchRandomWord());
            SessionManager.putSession(sessionId, gameSession);
        }

        // Add this session (player) to the set of sessions for the current game
        SessionManager.addSession(sessionId, session);

        // Send the initial game state to the player
        sendUpdateToPlayers(sessionId, gameSession);
    }

    // Called when a message is received (such as a player's guess or restart command)
    @OnMessage
    public void onMessage(Session session, String message, @PathParam("sessionId") String sessionId) throws IOException {
        GameSession gameSession = SessionManager.getSession(sessionId);

        // If the game session is not found, return
        if (gameSession == null) return;

        // Handle the special restart command
        if ("__RESTART__".equals(message)) {
            gameSession.reset(RandomWordService.fetchRandomWord());  // Reset the game with a new word
            SessionManager.putSession(sessionId, gameSession);
            sendUpdateToPlayers(sessionId, gameSession);
            return;
        }

        // Process a letter guess if the message is a valid single character
        if (message.length() == 1 && Character.isLetter(message.charAt(0))) {
            char guess = Character.toLowerCase(message.charAt(0));
            gameSession.guess(guess);
            SessionManager.putSession(sessionId, gameSession);
            sendUpdateToPlayers(sessionId, gameSession);
        }
    }

    // Called when the WebSocket connection is closed
    @OnClose
    public void onClose(Session session, CloseReason reason, @PathParam("sessionId") String sessionId) {
        // Remove the session from the set when the connection is closed
        SessionManager.removeSession(sessionId, session);
        System.out.println("[WebSocket] Connection closed: " + reason.getReasonPhrase());
    }

    // Called when an error occurs during the WebSocket connection
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("[WebSocket] Error: " + throwable.getMessage());
    }

    // Sends the updated game state to all connected players for the given game session
    private void sendUpdateToPlayers(String sessionId, GameSession gameSession) throws IOException {
        // Prepare the game state in JSON format
        String json = String.format(
                "{ \"word\": \"%s\", \"remaining\": %d, \"gameOver\": %b, \"win\": %b }",
                gameSession.getMaskedWord(),   // The masked word with guessed letters
                gameSession.getRemainingAttempts(), // The remaining attempts the player has
                gameSession.isGameOver(),      // If the game is over
                gameSession.isWin()            // If the player has won
        );

        // Broadcast the update only to the players connected to this game (sessionId)
        Set<Session> gameSessions = SessionManager.getSessionsForGame(sessionId);
        for (Session s : gameSessions) {
            if (s.isOpen()) {
                s.getBasicRemote().sendText(json);
            }
        }
    }
}
