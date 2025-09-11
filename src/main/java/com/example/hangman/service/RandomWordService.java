package com.example.hangman.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RandomWordService {
    public static String fetchRandomWord() {
        try {
            URL url = new URL("https://random-word-api.herokuapp.com/word");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();

            // Response is like ["apple"]
            return response.replaceAll("[\\[\\]\"]", "").split(",")[0];
        } catch (Exception e) {
            return "default";
        }
    }
}
