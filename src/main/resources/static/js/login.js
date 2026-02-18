const BASE_URL = "http://localhost:8080/api";
const sessionKey = "loggedUser";

document.addEventListener("DOMContentLoaded", () => {

    const loginBtn = document.getElementById("loginBtn");
    const errorEl = document.getElementById("error");

    // Check if user is already logged in
    checkSession();

    loginBtn.addEventListener("click", login);

    function checkSession() {
        const savedUser = localStorage.getItem(sessionKey);
        if (savedUser) {
            const user = JSON.parse(savedUser);
            if (user.role && user.role.toUpperCase() === 'ADMIN') {
                window.location.href = 'admin.html';
            } else {
                window.location.href = 'user.html';
            }
        }
    }

    function login() {
        const username = document.getElementById("username").value.trim();
        const password = document.getElementById("password").value.trim();

        errorEl.innerText = "";
        loginBtn.disabled = true;

        if (!username || !password) {
            errorEl.innerText = "Please enter both username and password.";
            loginBtn.disabled = false;
            return;
        }

        const payload = { username, password };

        fetch(`${BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw err; });
                }
                return response.json();
            })
            .then(user => {
                localStorage.setItem(sessionKey, JSON.stringify(user));
                console.log("Login successful. Role:", user.role);

                if (user.role && user.role.toUpperCase() === 'ADMIN') {
                    window.location.href = 'admin.html';
                } else {
                    window.location.href = 'user.html';
                }
            })
            .catch(error => {
                console.error("Login Error:", error);
                errorEl.innerText = error.message || "Login failed. Please try again.";
            })
            .finally(() => loginBtn.disabled = false);
    }


});
