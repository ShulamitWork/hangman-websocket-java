# Hangman WebSocket Java (No Spring)

This is a simple multiplayer **Hangman** game implemented as a **Java WebSocket server** with a minimal HTML+JavaScript frontend. The server uses **Tyrus** for WebSocket support and **Jackson** for JSON handling. No Spring or heavy frameworks are used.

---

## Project Structure

```
hangman-websocket-java/
├─ pom.xml # Maven project with dependencies
├─ src/
│ └─ main/
│ ├─ java/com/example/hangman/
│ │ ├─ model/GameSession.java # Game logic and state
│ │ ├─ service/RandomWordService.java # External word API
│ │ ├─ persistence/
│ │   ├─GamePersistence.java # Save/load game state to file
│ │   ├─SessionManager.java
│ │ ├─ websocket/HangmanEndpoint.java # WebSocket endpoint
│ │ └─ server/HangmanServer.java # Starts the WebSocket server
│ └─ resources/static/
│ ├─ index.html
│ └─ app.js
```