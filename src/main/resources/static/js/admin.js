const apiUrl = "/api/tasks";
const usersApiUrl = "/api/auth";

const tasksTableBody = document.querySelector("#tasksTable tbody");
const taskIdInput = document.querySelector("#taskIdInput"); // display only
const taskNameInput = document.querySelector("#taskNameInput");
const taskDescriptionInput = document.querySelector("#taskDescriptionInput");
const taskStatusInput = document.querySelector("#taskStatusInput");
const taskDateInput = document.querySelector("#taskDateInput");
const saveTaskBtn = document.querySelector("#saveTaskBtn");
const resetFormBtn = document.querySelector("#resetFormBtn");
const formTitle = document.querySelector("#formTitle");

// ================= USER ELEMENTS =================
const usersTableBody = document.querySelector("#usersTable tbody");
const userIdInput = document.querySelector("#userIdInput");
const usernameInput = document.querySelector("#usernameInput");
const passwordInput = document.querySelector("#passwordInput");
const roleInput = document.querySelector("#roleInput");
const saveUserBtn = document.querySelector("#saveUserBtn");
const resetUserFormBtn = document.querySelector("#resetUserFormBtn");
const userFormTitle = document.querySelector("#userFormTitle");

// ----------------- Load Tasks -----------------
async function loadTasks() {
    try {
        const res = await fetch(apiUrl);
        if (!res.ok) throw new Error("Failed to load tasks");
        const tasks = await res.json();

        // Clear existing table rows
        tasksTableBody.innerHTML = "";

        // Render each task safely
        tasks.forEach(t => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${t.taskId ? t.taskId.accountId : ""}</td>
                <td>${t.taskName}</td>
                <td>${t.taskDescription}</td>
                <td>${t.taskStatus}</td>
                <td>${t.taskDate}</td>
                <td>
                    <button class="edit-btn">Edit</button>
                    <button class="delete-btn">Delete</button>
                </td>
            `;

            // Attach event listeners using closure to avoid "task is not defined"
            const editBtn = row.querySelector(".edit-btn");
            const deleteBtn = row.querySelector(".delete-btn");

            editBtn.addEventListener("click", () => editTask(t.id));
            deleteBtn.addEventListener("click", () => deleteTask(t.id));

            tasksTableBody.appendChild(row);
        });

    } catch (error) {
        console.error(error);
        alert("Error loading tasks.");
    }
}


/// Save or Update task
 async function saveTask() {
     const taskData = {
         taskName: taskNameInput.value.trim(),
         taskDescription: taskDescriptionInput.value.trim(),
         taskStatus: taskStatusInput.value,
         taskDate: taskDateInput.value
     };

     if (!taskData.taskName || !taskData.taskDescription || !taskData.taskDate) {
         return alert("Please fill in all required fields.");
     }

     try {
         let res;

         const savedUser = localStorage.getItem("loggedUser");
         if (!savedUser) return alert("No logged-in user found.");
         const user = JSON.parse(savedUser);

         if (!user.accountId) return alert("User accountId missing.");

         if (taskIdInput.dataset.pk) {
             // UPDATE existing task
             res = await fetch(`${apiUrl}/${taskIdInput.dataset.pk}`, {
                 method: "PUT",
                 headers: { "Content-Type": "application/json" },
                 body: JSON.stringify(taskData)
             });

             if (!res.ok) throw new Error("Failed to update task");
             alert("Task updated successfully!");
             resetForm();
             await loadTasks(); // ensures table reloads

         } else {
             // CREATE new task
             // Include accountId in request as query param
             res = await fetch(`${apiUrl}?accountId=${user.accountId}`, {
                 method: "POST",
                 headers: { "Content-Type": "application/json" },
                 body: JSON.stringify(taskData)
             });

             if (!res.ok) throw new Error("Failed to create task");
             const createdTask = await res.json();

             // Display generated taskId immediately
             taskIdInput.value = createdTask.taskId.accountId || "";
             alert("Task created successfully!");
         }

         resetForm();
         loadTasks();

     } catch (error) {
         console.error(error);
         alert(error.message);
     }
 }


// Edit task
async function editTask(id) {
    try {
        const res = await fetch(`${apiUrl}/${id}`);
        if (!res.ok) throw new Error("Task not found");
        const task = await res.json();

        taskIdInput.value = task.taskId ? task.taskId.accountId : "";

        taskIdInput.dataset.pk = task.id;       // backend id for PUT
        taskNameInput.value = task.taskName;
        taskDescriptionInput.value = task.taskDescription;
        taskStatusInput.value = task.taskStatus;
        taskDateInput.value = task.taskDate;
        formTitle.textContent = "Edit Task";
    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// Delete task
async function deleteTask(id) {
    if (!confirm("Are you sure you want to delete this task?")) return;
    try {
        const res = await fetch(`${apiUrl}/${id}`, { method: "DELETE" });
        if (!res.ok) throw new Error("Failed to delete task");
        alert("Task deleted successfully!");
        resetForm();
        await loadTasks(); // ensures table reloads

    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// Reset form
// Reset form
function resetForm() {
    taskIdInput.value = "";                  // clear Task ID
    delete taskIdInput.dataset.pk;           // remove backend id
    taskNameInput.value = "";
    taskDescriptionInput.value = "";
    taskStatusInput.value = "PENDING";
    taskDateInput.value = "";
    formTitle.textContent = "Create New Task";
}

//USER STUFF TO TEST HERE
// Load all users
async function loadUsers() {
    try {
        const res = await fetch(usersApiUrl);
        if (!res.ok) throw new Error("Failed to load users");
        const users = await res.json();

        usersTableBody.innerHTML = "";
        users.forEach(u => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${u.accountId}</td>
                <td>${u.username}</td>
                <td>${u.role}</td>
                <td>
                    <button class="edit-btn">Edit</button>
                    <button class="delete-btn">Delete</button>
                </td>
            `;

            row.querySelector(".edit-btn").addEventListener("click", () => editUser(u.accountId));
            row.querySelector(".delete-btn").addEventListener("click", () => deleteUser(u.accountId));
            usersTableBody.appendChild(row);
        });
    } catch (error) {
        console.error(error);
        alert("Error loading users.");
    }
}

// Save or update user
async function saveUser() {
    const userData = {
        username: usernameInput.value.trim(),
        password: passwordInput.value,
        role: roleInput.value.toUpperCase()
    };

    if (!userData.username || (!userIdInput.dataset.pk && !userData.password) || !userData.role) {
        return alert("Please fill in all required fields.");
    }

    try {
        let res;
        if (userIdInput.dataset.pk) {
            // Update user
            res = await fetch(`${usersApiUrl}/edit/${userIdInput.dataset.pk}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(userData)
            });
        } else {
            // Create user
            res = await fetch(usersApiUrl, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(userData)
            });
        }

        if (!res.ok) throw new Error("Failed to save user");
        alert("User saved successfully!");
        resetUserForm();
        await loadUsers();
    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// Edit user
async function editUser(accountId) {
    try {
        const res = await fetch(`${usersApiUrl}/${accountId}`);
        if (!res.ok) throw new Error("User not found");
        const user = await res.json();

        userIdInput.dataset.pk = user.accountId;
        usernameInput.value = user.username;
        passwordInput.value = "";
        roleInput.value = user.role;
        userFormTitle.textContent = "Edit User";
    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// Delete user
async function deleteUser(accountId) {
    if (!confirm("Are you sure you want to delete this user?")) return;
    try {
        const res = await fetch(`${usersApiUrl}/${accountId}`, { method: "DELETE" });
        if (!res.ok) throw new Error("Failed to delete user");
        alert("User deleted successfully!");
        resetUserForm();
        await loadUsers();
    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// Reset user form
function resetUserForm() {
    userIdInput.value = "";
    delete userIdInput.dataset.pk;
    usernameInput.value = "";
    passwordInput.value = "";
    roleInput.value = "USER";
    userFormTitle.textContent = "Create New User";
}


// Event listeners
saveTaskBtn.addEventListener("click", saveTask);
resetFormBtn.addEventListener("click", resetForm);

saveUserBtn.addEventListener("click", saveUser);
resetUserFormBtn.addEventListener("click", resetUserForm);

// Initial load
loadTasks();
loadUsers();