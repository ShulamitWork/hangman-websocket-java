package com.example.hangman.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameSession implements Serializable {
    private String wordToGuess;
    private Set<Character> guessedLetters = new HashSet<>();
    private int remainingAttempts = 6;
    private boolean isGameOver = false;
    private boolean isWin = false; // New field

    // Required no-args constructor for JSON deserialization
    public GameSession() {}

    public GameSession(String wordToGuess) {
        this.wordToGuess = wordToGuess.toLowerCase();
    }

    // Handle user guess
    public boolean guess(char letter) {
        if (isGameOver || guessedLetters.contains(letter)) {
            return false;
        }

        guessedLetters.add(letter);

        if (!wordToGuess.contains(String.valueOf(letter))) {
            remainingAttempts--;
        }

        if (isWordGuessed()) {
            isGameOver = true;
            isWin = true;
        } else if (remainingAttempts == 0) {
            isGameOver = true;
            isWin = false;
        }

        return true;
    }

    // Check if the full word is guessed
    public boolean isWordGuessed() {
        for (char c : wordToGuess.toCharArray()) {
            if (!guessedLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    // Build a masked word string (e.g., d__a___)
    public String getMaskedWord() {
        StringBuilder sb = new StringBuilder();
        for (char c : wordToGuess.toCharArray()) {
            if (guessedLetters.contains(c)) {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    // Reset the game with a new word
    public void reset(String newWord) {
        this.wordToGuess = newWord.toLowerCase();
        this.guessedLetters.clear();
        this.remainingAttempts = 6;
        this.isGameOver = false;
        this.isWin = false;
    }

    // Getters and setters
    public String getWordToGuess() {
        return wordToGuess;
    }

    public void setWordToGuess(String wordToGuess) {
        this.wordToGuess = wordToGuess;
    }

    public Set<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public void setGuessedLetters(Set<Character> guessedLetters) {
        this.guessedLetters = guessedLetters;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public void setRemainingAttempts(int remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }
}
