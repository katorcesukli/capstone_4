const BASE_URL = "http://localhost:8080/api";
const USER_KEY = "loggedUser";
const TOKEN_KEY = "jwtToken";

document.addEventListener("DOMContentLoaded", () => {

    const loginBtn = document.getElementById("loginBtn");
    const errorEl = document.getElementById("error");

    // Check if user is already logged in
    checkSession();

    loginBtn.addEventListener("click", login);


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

    fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    })
        .then(async response => {
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || data.error || "Login failed");
            }

            return data;
        })

        .then(data => {

            console.log("FULL LOGIN RESPONSE:", data);

            if (!data.token) {
                throw new Error("No token received from server");
            }

            // Save JWT token
            localStorage.setItem(TOKEN_KEY, data.token);

            // Save full user session
            const sessionUser = {
                username: data.username,
                role: data.role,
                accountId: data.accountId || data.account_id
            };

            localStorage.setItem(USER_KEY, JSON.stringify(sessionUser));

            console.log("Login successful");
            console.log("Role:", sessionUser.role);
            console.log("Account ID:", sessionUser.accountId);

            redirectByRole(sessionUser.role);
        })
        .catch(error => {
            console.error("Login Error:", error);

            errorEl.innerText =
                error.message ||
                "Invalid username or password.";
        })
        .finally(() => {
            loginBtn.disabled = false;
        });
}


    function redirectByRole(role) {

        if (!role) {
            window.location.href = "login.html";
            return;
        }

        if (role.toUpperCase() === "ADMIN") {
            window.location.href = "admin.html";
        } else {
            window.location.href = "user.html";
        }

    }

    function checkSession() {

        const token = localStorage.getItem(TOKEN_KEY);
        const savedUser = localStorage.getItem(USER_KEY);

        if (token && savedUser) {

            const user = JSON.parse(savedUser);
            redirectByRole(user.role);

        }

    }


});
