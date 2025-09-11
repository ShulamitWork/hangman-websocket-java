const sessionId = localStorage.getItem("sessionId") || crypto.randomUUID();
localStorage.setItem("sessionId", sessionId);

const socket = new WebSocket(`ws://localhost:8080/game/${sessionId}`);

socket.onmessage = (event) => {
    const data = JSON.parse(event.data);
    document.getElementById("maskedWord").textContent = data.word;
    document.getElementById("remaining").textContent = `Remaining attempts: ${data.remaining}`;
    document.getElementById("status").textContent = data.gameOver ? "Game Over" : "";
};

function sendGuess() {
    const letter = document.getElementById("letter").value;
    if (letter.length === 1) {
        socket.send(letter);
        document.getElementById("letter").value = "";
    }
}
