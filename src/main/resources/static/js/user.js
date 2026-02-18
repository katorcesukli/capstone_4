const apiUrl = "/api/tasks";

// Form elements
const tasksTableBody = document.querySelector("#tasksTable tbody");
const taskIdInput = document.querySelector("#taskIdInput"); // display only
const taskNameInput = document.querySelector("#taskNameInput");
const taskDescriptionInput = document.querySelector("#taskDescriptionInput");
const taskStatusInput = document.querySelector("#taskStatusInput");
const taskDateInput = document.querySelector("#taskDateInput");
const saveTaskBtn = document.querySelector("#saveTaskBtn");
const resetFormBtn = document.querySelector("#resetFormBtn");
const formTitle = document.querySelector("#formTitle");

// Get logged-in user from localStorage
const savedUser = localStorage.getItem("loggedUser");
if (!savedUser) {
    alert("Not logged in! Redirecting to login page.");
    window.location.href = "login.html";
}
const user = JSON.parse(savedUser);

// ----------------- Load Tasks -----------------
async function loadTasks() {
    try {
        const res = await fetch(apiUrl);
        if (!res.ok) throw new Error("Failed to load tasks");
        const tasks = await res.json();
        tasksTableBody.innerHTML = "";

        const userTasks = tasks.filter(task => task.accountId === user.accountId);

        userTasks.forEach(task => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${task.taskId ? task.taskId.accountId : ""}</td>
                <td>${task.taskName}</td>
                <td>${task.taskDescription}</td>
                <td>${task.taskStatus}</td>
                <td>${task.taskDate}</td>
                <td>
                    <button onclick="editTask(${task.id})">Edit</button>
                    <button onclick="deleteTask(${task.id})">Delete</button>
                </td>
            `;
            tasksTableBody.appendChild(row);
        });

    } catch (error) {
        console.error(error);
        alert("Error loading tasks.");
    }
}

// ----------------- Save or Update Task -----------------
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

        if (taskIdInput.dataset.pk) {
            // UPDATE existing task
            res = await fetch(`${apiUrl}/${taskIdInput.dataset.pk}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(taskData)
            });

            if (!res.ok) throw new Error("Failed to update task");
            alert("Task updated successfully!");
        } else {
            // CREATE new task, include accountId as query param
            res = await fetch(`${apiUrl}?accountId=${user.accountId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(taskData)
            });

            if (!res.ok) throw new Error("Failed to create task");
            const createdTask = await res.json();
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

// ----------------- Edit Task -----------------
async function editTask(id) {
    try {
        const res = await fetch(`${apiUrl}/${id}`);
        if (!res.ok) throw new Error("Task not found");
        const task = await res.json();

        if (!task.taskId || task.taskId.accountId !== user.accountId) {
            return alert("You can only edit your own tasks!");
        }

        taskIdInput.value = task.taskId.accountId;
        taskIdInput.dataset.pk = task.id; // backend id for PUT
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

// ----------------- Delete Task -----------------
async function deleteTask(id) {
    if (!confirm("Are you sure you want to delete this task?")) return;

    try {
        // Get task first to verify ownership
        const res = await fetch(`${apiUrl}/${id}`);
        if (!res.ok) throw new Error("Failed to get task");
        const task = await res.json();

        if (!task.taskId || task.taskId.accountId !== user.accountId) {
            return alert("You can only delete your own tasks!");
        }

        const delRes = await fetch(`${apiUrl}/${id}`, { method: "DELETE" });
        if (!delRes.ok) throw new Error("Failed to delete task");

        alert("Task deleted successfully!");
        loadTasks();

    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// ----------------- Reset Form -----------------
function resetForm() {
    taskIdInput.value = "";
    delete taskIdInput.dataset.pk;
    taskNameInput.value = "";
    taskDescriptionInput.value = "";
    taskStatusInput.value = "PENDING";
    taskDateInput.value = "";
    formTitle.textContent = "Create New Task";
}

// ----------------- Event Listeners -----------------
saveTaskBtn.addEventListener("click", saveTask);
resetFormBtn.addEventListener("click", resetForm);

// Initial load
loadTasks();
