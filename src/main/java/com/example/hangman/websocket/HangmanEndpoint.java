package com.example.hangman.websocket;

import com.example.hangman.model.GameSession;
import com.example.hangman.persistence.SessionManager;
import com.example.hangman.service.RandomWordService;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint("/game/{sessionId}")
public class HangmanEndpoint {

    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) throws IOException {
        // Attempt to retrieve existing session by ID
        GameSession gameSession = SessionManager.getSession(sessionId);
        if (gameSession == null) {
            // No existing session found → create new one
            gameSession = new GameSession(RandomWordService.fetchRandomWord());
            SessionManager.putSession(sessionId, gameSession);
        }

        // Send current game state to the client
        sendUpdate(session, gameSession);
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("sessionId") String sessionId) throws IOException {
        // Retrieve active game session
        GameSession game = SessionManager.getSession(sessionId);

        // Validate and process the guess
        if (game != null && message.length() == 1 && Character.isLetter(message.charAt(0))) {
            char guess = Character.toLowerCase(message.charAt(0));
            game.guess(guess);

            // Persist updated game state
            SessionManager.putSession(sessionId, game);

            // Send updated game state to the client
            sendUpdate(session, game);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("[WebSocket] Connection closed: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("[WebSocket] Error: " + throwable.getMessage());
    }

    private void sendUpdate(Session session, GameSession game) throws IOException {
        // Format game state into JSON
        String json = String.format(
                "{ \"word\": \"%s\", \"remaining\": %d, \"gameOver\": %b }",
                game.getMaskedWord(),
                game.getRemainingAttempts(),
                game.isGameOver()
        );
        session.getBasicRemote().sendText(json);
    }
}
