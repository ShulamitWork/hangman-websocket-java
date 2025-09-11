package com.example.hangman.websocket;

import com.example.hangman.model.GameSession;
import com.example.hangman.persistence.GamePersistence;
import com.example.hangman.service.RandomWordService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/game/{sessionId}")
public class HangmanEndpoint {

    private static final Map<String, GameSession> sessions = GamePersistence.load();

    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) throws IOException {
        sessions.computeIfAbsent(sessionId, id -> new GameSession(RandomWordService.fetchRandomWord()));
        GamePersistence.save(sessions);
        sendUpdate(session, sessions.get(sessionId));
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("sessionId") String sessionId) throws IOException {
        GameSession game = sessions.get(sessionId);
        if (message.length() == 1) {
            game.guess(message.toLowerCase().charAt(0));
            GamePersistence.save(sessions);
            sendUpdate(session, game);
        }
    }

    private void sendUpdate(Session session, GameSession game) throws IOException {
        String json = String.format(
                "{ \"word\": \"%s\", \"remaining\": %d, \"gameOver\": %b }",
                game.getMaskedWord(), game.getRemainingAttempts(), game.isGameOver()
        );
        session.getBasicRemote().sendText(json);
    }
}
