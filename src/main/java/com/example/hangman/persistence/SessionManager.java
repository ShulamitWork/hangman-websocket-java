package com.example.hangman.persistence;

import com.example.hangman.model.GameSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import jakarta.websocket.Session;  // WebSocket session


public class SessionManager {

    // In-memory store for all game sessions (game state) and their connected sessions (players)
    private static final Map<String, GameSession> sessions = new ConcurrentHashMap<>();
    private static final Map<String, Set<Session>> gameSessions = new ConcurrentHashMap<>();

    // Static initializer to load saved sessions (game states) when the class is first loaded
    static {
        sessions.putAll(GamePersistence.load()); // Assuming GamePersistence handles loading saved sessions
    }

    /**
     * Get all game sessions (for internal use).
     */
    public static Map<String, GameSession> getSessions() {
        return sessions;
    }

    /**
     * Retrieve the game session for a specific session ID.
     * If no session exists, return null.
     */
    public static GameSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * Insert or update a game session in the store and persist to disk.
     */
    public static void putSession(String sessionId, GameSession session) {
        sessions.put(sessionId, session);
        GamePersistence.save(sessions);
    }

    /**
     * Add a player session to the game session identified by sessionId.
     */
    public static void addSession(String sessionId, Session session) {
        gameSessions.computeIfAbsent(sessionId, k -> new HashSet<>()).add(session);
    }

    /**
     * Remove a player session from the game session identified by sessionId.
     */
    public static void removeSession(String sessionId, Session session) {
        Set<Session> sessionsForGame = gameSessions.get(sessionId);
        if (sessionsForGame != null) {
            sessionsForGame.remove(session);
            if (sessionsForGame.isEmpty()) {
                gameSessions.remove(sessionId);
            }
        }
    }

    /**
     * Get the set of player sessions for a specific game session.
     */
    public static Set<Session> getSessionsForGame(String sessionId) {
        return gameSessions.getOrDefault(sessionId, new HashSet<>());
    }
}
