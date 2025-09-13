package com.example.hangman.persistence;

import com.example.hangman.model.GameSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    // Singleton in-memory store for all game sessions
    private static final Map<String, GameSession> sessions;

    static {
        // Load saved sessions from disk when the class is first loaded
        sessions = GamePersistence.load();
    }

    /**
     * Get all sessions map (for internal use).
     */
    public static Map<String, GameSession> getSessions() {
        return sessions;
    }

    /**
     * Retrieve a session by its ID.
     * If no session exists with that ID, return null.
     */
    public static GameSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * Insert or update a session in the store, then persist to disk.
     */
    public static void putSession(String sessionId, GameSession session) {
        sessions.put(sessionId, session);
        GamePersistence.save(sessions);
    }
}
