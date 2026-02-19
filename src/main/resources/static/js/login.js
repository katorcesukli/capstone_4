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

        const payload = { username, password };

        fetch(`${BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                username,
                password
            })
        })
            .then(response => {

                if (!response.ok) {
                    return response.json().then(err => { throw err; });
                }

                return response.json();
            })

            .then(data => {
                /*
                Expected backend response example:
                {
                    token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                    username: "john",
                    role: "ADMIN"
                }
                */

                if (!data.token) {
                    throw new Error("No token received from server");
            .then(user => {
            console.log("FULL LOGIN RESPONSE:", user);
                const sessionUser = {
                        username: user.username,
                        role: user.role,
                        accountId: user.accountId || user.account_id // handles both camelCase and snake_case
                    };



                localStorage.setItem(sessionKey, JSON.stringify(sessionUser));
                console.log("Login successful. Role:", sessionUser.role);
                console.log("Account ID:", sessionUser.accountId);

                if (user.role && user.role.toUpperCase() === 'ADMIN') {
                    window.location.href = 'admin.html';
                } else {
                    window.location.href = 'user.html';
                }

                // Save JWT token
                localStorage.setItem(TOKEN_KEY, data.token);

                // Save user info
                localStorage.setItem(USER_KEY, JSON.stringify({
                    username: data.username,
                    role: data.role
                }));

                console.log("Login successful");
                console.log("Token:", data.token);

                redirectByRole(data.role);

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
