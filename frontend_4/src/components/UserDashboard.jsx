import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const BASE_URL = "http://localhost:8080/api";
const USER_KEY = "loggedUser";
const TOKEN_KEY = "jwtToken";

function UserDashboard() {
  const [user, setUser] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [taskId, setTaskId] = useState('');
  const [taskName, setTaskName] = useState('');
  const [taskDescription, setTaskDescription] = useState('');
  const [taskStatus, setTaskStatus] = useState('PENDING');
  const [taskDate, setTaskDate] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const savedUser = localStorage.getItem(USER_KEY);
    const token = localStorage.getItem(TOKEN_KEY);
    if (!savedUser || !token) {
      navigate('/login');
      return;
    }
    const parsedUser = JSON.parse(savedUser);
    setUser(parsedUser);
    loadTasks();
  }, [navigate]);

  const loadTasks = async () => {
    if (!user) return;
    try {
      const token = localStorage.getItem(TOKEN_KEY);
      const response = await axios.get(`${BASE_URL}/tasks/user/${user.accountId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setTasks(response.data);
    } catch (error) {
      console.error(error);
      alert("Error loading tasks: " + (error.response?.data?.message || error.message));
    }
  };

  useEffect(() => {
    if (user) loadTasks();
  }, [user]);

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

  const editTask = async (task) => {
    if (!task.taskId || task.taskId.accountId !== user.accountId) {
      alert("You can only edit your own tasks!");
      return;
    }
    setTaskId(task.taskId.accountId);
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
      // First get task to verify ownership
      const response = await axios.get(`${BASE_URL}/tasks/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      const task = response.data;
      if (!task.taskId || task.taskId.accountId !== user.accountId) {
        alert("You can only delete your own tasks!");
        return;
      }

      await axios.delete(`${BASE_URL}/tasks/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      alert("Task deleted successfully!");
      loadTasks();
    } catch (error) {
      console.error(error);
      alert(error.message);
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

  if (!user) return <div className="min-h-screen flex items-center justify-center">Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <div className="max-w-6xl mx-auto">
        <div className="bg-white p-6 rounded-lg shadow-md mb-6 flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold">User Dashboard</h1>
            <h2 className="text-xl">Welcome, {user.username}</h2>
          </div>
          <button onClick={logout} className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">
            Logout
          </button>
        </div>

        <div className="bg-white p-6 rounded-lg shadow-md mb-6">
          <h2 className="text-2xl font-bold mb-4">{editingId ? 'Edit Task' : 'Create / Edit Task'}</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Task ID</label>
              <input
                type="text"
                value={taskId}
                placeholder="Task ID"
                readOnly
                className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-100"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Title</label>
              <input
                type="text"
                value={taskName}
                onChange={(e) => setTaskName(e.target.value)}
                placeholder="Task Title"
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm font-medium mb-1">Description</label>
              <input
                type="text"
                value={taskDescription}
                onChange={(e) => setTaskDescription(e.target.value)}
                placeholder="Task Description"
                required
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Status</label>
              <select
                value={taskStatus}
                onChange={(e) => setTaskStatus(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="PENDING">PENDING</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="COMPLETED">COMPLETED</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Date</label>
              <input
                type="date"
                value={taskDate}
                onChange={(e) => setTaskDate(e.target.value)}
                required
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
          <h2 className="text-2xl font-bold mb-4">Your Tasks</h2>
          <div className="overflow-x-auto">
            <table className="w-full table-auto">
              <thead>
                <tr className="bg-gray-50">
                  <th className="px-4 py-2 text-left">Account ID</th>
                  <th className="px-4 py-2 text-left">Title</th>
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
      </div>
    </div>
  );
}

export default UserDashboard;