
const USER_KEY = "loggedUser";
const TOKEN_KEY = "jwtToken";

document.addEventListener("DOMContentLoaded", () => {

    const usernameDisplay = document.getElementById("username-display");
    const logoutBtn = document.getElementById("logoutBtn");

    const savedUser = localStorage.getItem(USER_KEY);

    if (savedUser) {
        try {
            const user = JSON.parse(savedUser);
            usernameDisplay.textContent = user.username;
        } catch {
            logout();
        }
    } else {
        // No user → redirect to login
        window.location.href = "login.html";
    }


    logoutBtn.addEventListener("click", logout);


    function logout() {

        const token = localStorage.getItem(TOKEN_KEY);

        // Optional backend logout
        fetch("http://localhost:8080/api/auth/logout", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }).catch(() => {});

        // IMPORTANT: remove BOTH
        localStorage.removeItem(USER_KEY);
        localStorage.removeItem(TOKEN_KEY);

        window.location.href = "login.html";

    }

});