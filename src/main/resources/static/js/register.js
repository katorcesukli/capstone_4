const BASE_URL = "http://localhost:8080/api";

function register() {

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const errorEl = document.getElementById("error");

    errorEl.innerText = "";

    // Validation
    if (username.length < 3) {
        errorEl.innerText = "Username must be at least 3 characters.";
        return;
    }

    if (password.length < 4) {
        errorEl.innerText = "Password must be at least 4 characters.";
        return;
    }

    const payload = {
        username: username,
        password: password,
        role: "USER"
    };

    fetch(`${BASE_URL}/auth/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
        .then(response => {

            if (!response.ok) {
                return response.json().then(err => {
                    throw err;
                });
            }

            return response.json();
        })
        .then(data => {
            alert("Registration successful!");
            window.location.href = "login.html";
        })
        .catch(error => {

            if (error.message) {
                errorEl.innerText = error.message;
            } else {
                errorEl.innerText = "Registration failed.";
            }

            console.error(error);
        });

}
