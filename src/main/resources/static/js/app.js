const sessionKey = "loggedUser";

document.addEventListener("DOMContentLoaded", () => {
    const usernameDisplay = document.getElementById("username-display");
    const logoutBtn = document.getElementById("logoutBtn");

    // Load user from session
    const savedUser = localStorage.getItem(sessionKey);
    if (savedUser) {
        const user = JSON.parse(savedUser);
        usernameDisplay.textContent = user.username;
    }

    // Logout handler
    logoutBtn.addEventListener("click", () => {
        localStorage.removeItem(sessionKey); // clear session
        // Optional: call backend logout
        fetch("http://localhost:8080/api/auth/logout", {
            method: "POST"
        }).finally(() => {
            window.location.href = "login.html"; // redirect to login
        });
    });
});
