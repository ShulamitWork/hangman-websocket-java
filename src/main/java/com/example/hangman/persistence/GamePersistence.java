package com.example.hangman.persistence;

import com.example.hangman.model.GameSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GamePersistence {
    private static final String FILE = "game_sessions.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void save(Map<String, GameSession> sessions) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE), sessions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, GameSession> load() {
        try {
            File f = new File(FILE);
            if (f.exists()) {
                return mapper.readValue(f, new TypeReference<>() {});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ConcurrentHashMap<>();
    }
}
