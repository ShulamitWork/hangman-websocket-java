// Get existing session ID from localStorage, or generate a new one if none exists
let sessionId = localStorage.getItem("hangmanSessionId");
if (!sessionId) {
    sessionId = crypto.randomUUID();
    localStorage.setItem("hangmanSessionId", sessionId);
}

// Log session ID for debugging
console.log("[INFO] Using session ID:", sessionId);

// Create WebSocket connection using the session ID
const socket = new WebSocket(`ws://localhost:8081/game/${sessionId}`);

socket.onopen = () => {
    console.log("[INFO] WebSocket connection opened");
};

socket.onmessage = (event) => {
    console.log("[DEBUG] Message received:", event.data);
    try {
        const data = JSON.parse(event.data);
        document.getElementById("maskedWord").textContent = data.word;
        document.getElementById("remaining").textContent = `Remaining attempts: ${data.remaining}`;
        document.getElementById("status").textContent = data.gameOver ? "Game Over" : "";
    } catch (error) {
        console.error("[ERROR] Failed to parse message:", error);
    }
};

socket.onerror = (error) => {
    console.error("[ERROR] WebSocket error:", error);
};

socket.onclose = (event) => {
    console.warn("[INFO] WebSocket connection closed:", event.reason || event.code);
};

// Function to send a guess to the server
function sendGuess() {
    const inputElem = document.getElementById("letter");
    const letter = inputElem.value;
    if (letter.length === 1) {
        console.log("[INFO] Sending guess:", letter);
        socket.send(letter);
        inputElem.value = "";
    } else {
        console.warn("[WARN] Please enter exactly one letter before guessing");
    }
}
