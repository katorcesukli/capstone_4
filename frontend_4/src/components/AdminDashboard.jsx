import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const BASE_URL = "http://localhost:8080/api";
const USER_KEY = "loggedUser";
const TOKEN_KEY = "jwtToken";

function AdminDashboard() {
  const [user, setUser] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [taskId, setTaskId] = useState('');
  const [taskName, setTaskName] = useState('');
  const [taskDescription, setTaskDescription] = useState('');
  const [taskStatus, setTaskStatus] = useState('PENDING');
  const [taskDate, setTaskDate] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(false);

  // User management state
  const [users, setUsers] = useState([]);
  const [userId, setUserId] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('USER');
  const [editingUserId, setEditingUserId] = useState(null);

  const navigate = useNavigate();

  useEffect(() => {
    const savedUser = localStorage.getItem(USER_KEY);
    const token = localStorage.getItem(TOKEN_KEY);
    if (!savedUser || !token) {
      navigate('/login');
      return;
    }
    const parsedUser = JSON.parse(savedUser);
    if (parsedUser.role.toUpperCase() !== 'ADMIN') {
      navigate('/user');
      return;
    }
    setUser(parsedUser);
    loadTasks();
    loadUsers();
  }, [navigate]);

  const loadTasks = async () => {
    try {
      const token = localStorage.getItem(TOKEN_KEY);
      const response = await axios.get(`${BASE_URL}/tasks`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setTasks(response.data);
    } catch (error) {
      console.error(error);
      alert("Error loading tasks.");
    }
  };

  const loadUsers = async () => {
    try {
      const token = localStorage.getItem(TOKEN_KEY);
      const response = await axios.get(`${BASE_URL}/auth`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setUsers(response.data);
    } catch (error) {
      console.error(error);
      alert("Error loading users.");
    }
  };

  const saveTask = async () => {
    if (!taskName.trim() || !taskDescription.trim() || !taskDate) {
      alert("Please fill in all required fields.");
      return;
    }

    setLoading(true);
    try {
      const token = localStorage.getItem(TOKEN_KEY);
      const taskData = {
        taskName: taskName.trim(),
        taskDescription: taskDescription.trim(),
        taskStatus,
        taskDate
      };

      if (editingId) {
        await axios.put(`${BASE_URL}/tasks/${editingId}`, taskData, {
          headers: { Authorization: `Bearer ${token}` }
        });
        alert("Task updated successfully!");
      } else {
        const response = await axios.post(`${BASE_URL}/tasks?accountId=${user.accountId}`, taskData, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setTaskId(response.data.taskId?.accountId || '');
        alert("Task created successfully!");
      }
      resetForm();
      loadTasks();
    } catch (error) {
      console.error(error);
      alert(error.response?.data?.Error || error.message);
    } finally {
      setLoading(false);
    }
  };

  const editTask = (task) => {
    setTaskId(task.taskId?.accountId || '');
    setTaskName(task.taskName);
    setTaskDescription(task.taskDescription);
    setTaskStatus(task.taskStatus);
    setTaskDate(task.taskDate);
    setEditingId(task.id);
  };

  const deleteTask = async (id) => {
    if (!confirm("Are you sure you want to delete this task?")) return;
    try {
      const token = localStorage.getItem(TOKEN_KEY);
      await axios.delete(`${BASE_URL}/tasks/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      alert("Task deleted successfully!");
      loadTasks();
    } catch (error) {
      console.error(error);
      alert(error.response?.data?.Error || error.message);
    }
  };

  const resetForm = () => {
    setTaskId('');
    setTaskName('');
    setTaskDescription('');
    setTaskStatus('PENDING');
    setTaskDate('');
    setEditingId(null);
  };

  const logout = async () => {
    const token = localStorage.getItem(TOKEN_KEY);
    try {
      await axios.post(`${BASE_URL}/auth/logout`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      });
    } catch (error) {
      console.error(error);
    }
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(TOKEN_KEY);
    navigate('/login');
  };

  // User management functions
  const saveUser = async () => {
    if (!username.trim() || (!editingUserId && !password.trim()) || !role) {
      alert("Please fill in all required fields.");
      return;
    }

    setLoading(true);
    try {
      const token = localStorage.getItem(TOKEN_KEY);
      const userData = {
        username: username.trim(),
        password: password.trim(),
        role: role.toUpperCase()
      };

      if (editingUserId) {
        await axios.put(`${BASE_URL}/auth/edit/${editingUserId}`, userData, {
          headers: { Authorization: `Bearer ${token}` }
        });
        alert("User updated successfully!");
      } else {
        await axios.post(`${BASE_URL}/auth`, userData, {
          headers: { Authorization: `Bearer ${token}` }
        });
        alert("User created successfully!");
      }
      resetUserForm();
      loadUsers();
    } catch (error) {
      console.error(error);
      alert(error.response?.data?.Error || error.message);
    } finally {
      setLoading(false);
    }
  };

  const editUser = async (accountId) => {
    try {
      const token = localStorage.getItem(TOKEN_KEY);
      const response = await axios.get(`${BASE_URL}/auth/${accountId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      const userData = response.data;
      setUserId(userData.accountId);
      setUsername(userData.username);
      setPassword('');
      setRole(userData.role);
      setEditingUserId(userData.accountId);
    } catch (error) {
      console.error(error);
      alert(error.message);
    }
  };

  const deleteUser = async (accountId) => {
    if (!confirm("Are you sure you want to delete this user?")) return;
    try {
      const token = localStorage.getItem(TOKEN_KEY);
      await axios.delete(`${BASE_URL}/auth/${accountId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      alert("User deleted successfully!");
      alert("User deleted successfully!");
      loadUsers();
    } catch (error) {
      console.error(error);
      alert(error.message);
    }
  };

  const resetUserForm = () => {
    setUserId('');
    setUsername('');
    setPassword('');
    setRole('USER');
    setEditingUserId(null);
  };

  if (!user) return <div>Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-3xl font-bold">Admin Dashboard</h1>
            <h2 className="text-xl">Welcome, {user.username}</h2>
          </div>
          <button onClick={logout} className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">
            Logout
          </button>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md mb-6">
          <h2 className="text-2xl font-bold mb-4">{editingId ? 'Edit Task' : 'Create New Task'}</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Task ID:</label>
              <input
                type="text"
                value={taskId}
                placeholder="Auto-generated"
                readOnly
                className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-100"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Task Name:</label>
              <input
                type="text"
                value={taskName}
                onChange={(e) => setTaskName(e.target.value)}
                placeholder="Enter task name"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm font-medium mb-1">Task Description:</label>
              <input
                type="text"
                value={taskDescription}
                onChange={(e) => setTaskDescription(e.target.value)}
                placeholder="Enter description"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Task Status:</label>
              <select
                value={taskStatus}
                onChange={(e) => setTaskStatus(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="PENDING">PENDING</option>
                <option value="COMPLETED">COMPLETED</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Task Date:</label>
              <input
                type="date"
                value={taskDate}
                onChange={(e) => setTaskDate(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
          <div className="mt-4 flex gap-2">
            <button
              onClick={saveTask}
              disabled={loading}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 disabled:opacity-50"
            >
              {loading ? 'Saving...' : 'Save Task'}
            </button>
            <button
              onClick={resetForm}
              className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
            >
              Reset Form
            </button>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md">
          <h2 className="text-2xl font-bold mb-4">Tasks</h2>
          <div className="overflow-x-auto">
            <table className="w-full table-auto">
              <thead>
                <tr className="bg-gray-50">
                  <th className="px-4 py-2 text-left">Account ID</th>
                  <th className="px-4 py-2 text-left">Name</th>
                  <th className="px-4 py-2 text-left">Description</th>
                  <th className="px-4 py-2 text-left">Status</th>
                  <th className="px-4 py-2 text-left">Date</th>
                  <th className="px-4 py-2 text-left">Actions</th>
                </tr>
              </thead>
              <tbody>
                {tasks.map(task => (
                  <tr key={task.id} className="border-t">
                    <td className="px-4 py-2">{task.taskId?.accountId || ''}</td>
                    <td className="px-4 py-2">{task.taskName}</td>
                    <td className="px-4 py-2">{task.taskDescription}</td>
                    <td className="px-4 py-2">{task.taskStatus}</td>
                    <td className="px-4 py-2">{task.taskDate}</td>
                    <td className="px-4 py-2">
                      <button
                        onClick={() => editTask(task)}
                        className="bg-yellow-500 text-white px-2 py-1 rounded mr-2 hover:bg-yellow-600"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => deleteTask(task.id)}
                        className="bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Users Management Section */}
        <div className="bg-white p-6 rounded-lg shadow-md mb-6">
          <h2 className="text-2xl font-bold mb-4">Users Management</h2>
          <div className="overflow-x-auto">
            <table className="w-full table-auto">
              <thead>
                <tr className="bg-gray-50">
                  <th className="px-4 py-2 text-left">Account ID</th>
                  <th className="px-4 py-2 text-left">Username</th>
                  <th className="px-4 py-2 text-left">Role</th>
                  <th className="px-4 py-2 text-left">Actions</th>
                </tr>
              </thead>
              <tbody>
                {users.map(user => (
                  <tr key={user.accountId} className="border-t">
                    <td className="px-4 py-2">{user.accountId}</td>
                    <td className="px-4 py-2">{user.username}</td>
                    <td className="px-4 py-2">{user.role}</td>
                    <td className="px-4 py-2">
                      <button
                        onClick={() => editUser(user.accountId)}
                        className="bg-yellow-500 text-white px-2 py-1 rounded mr-2 hover:bg-yellow-600"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => deleteUser(user.accountId)}
                        className="bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md mb-6">
          <h2 className="text-2xl font-bold mb-4">{editingUserId ? 'Edit User' : 'Create New User'}</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Account ID:</label>
              <input
                type="text"
                value={userId}
                placeholder="Auto-generated"
                readOnly
                className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-100"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Username:</label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter username"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Password:</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter password"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Role:</label>
              <select
                value={role}
                onChange={(e) => setRole(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </div>
          </div>
          <div className="mt-4 flex gap-2">
            <button
              onClick={saveUser}
              disabled={loading}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 disabled:opacity-50"
            >
              {loading ? 'Saving...' : 'Save User'}
            </button>
            <button
              onClick={resetUserForm}
              className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
            >
              Reset Form
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;