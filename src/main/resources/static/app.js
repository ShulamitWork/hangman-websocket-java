let sessionId = localStorage.getItem("hangmanSessionId");
if (!sessionId) {
    sessionId = crypto.randomUUID();
    localStorage.setItem("hangmanSessionId", sessionId);
}

console.log("[INFO] Using session ID:", sessionId);

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

        const input = document.getElementById("letter");
        const restartBtn = document.getElementById("restartBtn");

        if (data.gameOver) {
            input.disabled = true;
            restartBtn.style.display = "inline";

            if (data.win) {
                document.getElementById("status").textContent = "You won!!!";
            } else {
                document.getElementById("status").textContent = "You lost";
            }
        } else {
            input.disabled = false;
            document.getElementById("status").textContent = "";
            restartBtn.style.display = "none";
        }

        // Clear any previous user message
        document.getElementById("userMessage").textContent = "";

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

// Called when the user clicks the "Guess" button
function sendGuess() {
    const inputElem = document.getElementById("letter");
    const letter = inputElem.value;

    if (letter.length === 1 && /^[a-zA-Z]$/.test(letter)) {
        console.log("[INFO] Sending guess:", letter);
        socket.send(letter);
        inputElem.value = "";
        document.getElementById("userMessage").textContent = ""; // Clear any warning message
    } else {
        // Show warning to the user instead of just logging
        document.getElementById("userMessage").textContent = "Please enter exactly one valid letter (A-Z)";
    }
}

// Called when the user clicks the "Restart Game" button
function restartGame() {
    console.log("[INFO] Sending restart request to server...");
    socket.send("__RESTART__"); // Special command to restart the game
}
