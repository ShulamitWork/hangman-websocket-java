# Hangman WebSocket Java (No Spring)

This is a simple multiplayer **Hangman** game implemented as a **Java WebSocket server** with a minimal HTML+JavaScript frontend. The server uses **Tyrus** for WebSocket support and **Jackson** for JSON handling. No Spring or heavy frameworks are used.

---

## Project Structure

```
hangman-websocket-java/
├── pom.xml                                 # Maven project dependencies
├── src/
│   └── main/
│       ├── java/com/example/hangman/
│       │   ├── model/
│       │   │   └── GameSession.java        # Game logic/state
│       │
│       │   ├── service/
│       │   │   └── RandomWordService.java  # External word API
│       │
│       │   ├── persistence/
│       │   │   ├── GamePersistence.java    # File save/load
│       │   │   └── SessionManager.java     # Session tracking
│       │
│       │   ├── websocket/
│       │   │   └── HangmanEndpoint.java    # WebSocket handler
│       │
│       │   └── server/
│       │       └── HangmanServer.java      # Starts server
│
│       └── resources/static/
│           ├── index.html                  # Game frontend
│           └── app.js                      # Frontend logic
```
## How to Run
Build the project and run the application:

### Requirements
- Java 17 (Tested with **Corretto 17**)
- Maven 3.x

### Run the Server
```bash
mvn clean package
java -jar target/*.jar
```
After the server starts successfully, open your browser and navigate to:
```text
http://localhost:8080/index.html
```
### Game Rules

- The game picks a random word from an external API.
- You must guess one letter at a time.
- The number of allowed attempts is limited.
- Once the game ends, you can restart with the same session.
- In multi-player mode (optional), each player provides a word, and they take turns.

## Features

### Functional Features

| Feature                     | Implemented | Notes                                    |
|-----------------------------|:-----------:|------------------------------------------|
| Random word from external service | ✔️          | Uses [random-word-api]                   |
| Single-player mode           | ✔️          | Against a random word                     |
| Multi-session support        | ✔️          | Each browser session gets a unique ID    |
| Persistent session (browser) | ✔️          | Stored in localStorage                    |
| Persistent session (backend) | ✔️          | Saved to disk with GamePersistence       |
| WebSocket communication      | ✔️          | Bi-directional real-time updates          |
| Guess validation on client side | ✔️          | With visible warnings to users            |
| Restart game button          | ✔️          | Sends `__RESTART__` message               |
| Invite URL for 2-player mode |  Partial  | Session ID can be reused but no turn-based logic yet |

## Technical Features

- **SessionManager** tracks all game sessions in-memory.
- **GamePersistence** handles saving/loading sessions to disk (JSON file).
- **RandomWordService** fetches words from an online API.
- Game logic is centralized in **GameSession** class.
- Frontend is written in vanilla JavaScript and uses WebSocket for all interactions.

---

## Scalability Considerations

The design should consider scale and a high volume of requests...

### Current Scalability Design

- Multi-session capable: Each session is isolated via a unique UUID (stored in browser localStorage).
- Persistence layer: Session state is saved to disk periodically to survive server restarts.
- Stateless client: All game logic is managed on the server.
- WebSocket-based: Efficient bi-directional communication avoids polling and reduces load.

### Future Improvements for Scale

To move to production-level scale:

- **Session Store Upgrade**  
  Abstract session storage to support:
    - Redis
    - MongoDB
    - SQL databases

- **Horizontal Scaling (Multiple Instances)**
    - Move WebSocket connections behind a load balancer with sticky sessions
    - Or use a shared pub/sub system (e.g. Redis Pub/Sub, Kafka) to synchronize state

- **Microservices**  
  Separate services: word provider, game logic, session persistence, etc.

- **Security & Rate Limiting**
    - Add authentication or session validation
    - Throttle abusive clients

---

## Testing

| Scenario                        | Expected Result                     |
|---------------------------------|-----------------------------------|
| Refreshing browser              | Game continues from same state    |
| Restarting server              | Game continues from saved state   |
| Multiple tabs with same session ID | Synced game state               |
| Two players with different session IDs | Isolated games              |
| Invalid input (e.g. more than 1 letter) | Visible warning to user        |
| Win/lose scenario              | Final message shown, input disabled|
| Restart game                  | Game resets cleanly                |

### SSL Certificate Note

During development, the project uses an external API (https://random-word-api.herokuapp.com/word) to fetch random words.

 Due to SSL certificate issues, the current code disables certificate verification for this request.

 This is strictly for educational/testing purposes only. In a production system, you must handle certificates securely.

---

### Design Decisions

- WebSocket chosen over HTTP for real-time updates
- UUID-based session tracking for easy sharing and restart
- JSON-based persistence for simplicity (can be swapped later)
- Minimal frontend – focus is on backend logic and communication

---

### Known Limitations

- 2-player turn-based mode is not fully implemented.
- No authentication or access control.
- No UI for switching between single/multi-player mode.
- Game word is visible in browser DevTools (should be hidden or obfuscated).

---

### Future Enhancements

- Full implementation of 2-player mode:
    - Turn logic
    - Word submission by each player
    - Invite links with UI flow
- REST API for game history and analytics
- UI improvements: animations, keyboard input, virtual keyboard
- Mobile responsiveness
- Docker support

---

### License

MIT License (or company-specific if required)

---

### About

This project was implemented as part of a take-home assignment for a backend Java position.  
Please feel free to reach out for questions or code discussions.
