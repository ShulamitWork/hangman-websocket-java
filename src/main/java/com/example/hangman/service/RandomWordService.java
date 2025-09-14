package com.example.hangman.service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RandomWordService {
    public static String fetchRandomWord() {
        try {
            // Disable SSL verification (Not recommended for production!)
            TrustManager[] trustAllCertificates = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Initialize the SSLContext with the TrustManager to ignore SSL cert validation
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCertificates, new java.security.SecureRandom());
            SSLContext.setDefault(sc);

            // The URL for the random word API
            URL url = new URL("https://random-word-api.herokuapp.com/word?number=1");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // Read the response from the API
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();

            // Clean the response and return the word (e.g., "apple")
            return response.replaceAll("[\\[\\]\"]", "").split(",")[0];
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging purposes
            return "default";  // Return a default word if there's an error
        }
    }
}
