import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const BASE_URL = "http://localhost:8080/api";
const USER_KEY = "loggedUser";
const TOKEN_KEY = "jwtToken";

function UserDashboard() {
  const [user, setUser] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [allTasks, setAllTasks] = useState([]);
  const [taskId, setTaskId] = useState('');
  const [accountId, seAccountId] = useState('');
  const [taskName, setTaskName] = useState('');
  const [taskDescription, setTaskDescription] = useState('');
  const [taskStatus, setTaskStatus] = useState('PENDING');
  const [taskDate, setTaskDate] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [stringTaskId, setStringTaskId] = useState("");
  const [error, setError] = useState("");
  const [isSearching, setIsSearching] = useState(false);
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
      setAllTasks(response.data);
      setTasks(response.data);
      setIsSearching(false);
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
      setShowEditModal(false);
    } catch (error) {
      console.error(error);
      alert(error.response?.data?.Error || error.message);
    } finally {
      setLoading(false);
    }
  };

  const closeEditModal = () => {
    setShowEditModal(false);
    resetForm();
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
    setShowEditModal(true);
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

  const searchTask = async () => {
  try {
    if (!stringTaskId.trim())
      return setError("Please enter a Task ID");

    const loggedUser = JSON.parse(localStorage.getItem("loggedUser"));

    if (!loggedUser?.accountId)
      return setError("User session not found");

    setError("");

    const { data } = await axios.get(
      `${BASE_URL}/tasks/id/${stringTaskId}`
    );

   const userTasks = data.filter(task => {
  console.log("Checking task", task.taskId, "account_id:", task.account_id);
  return Number(task.taskId?.accountId) === Number(loggedUser.accountId);
});

    setTasks(userTasks);
    setIsSearching(true);

    if (userTasks.length === 0)
      setError("No task found or unauthorized");

  } catch (err) {
    console.error(err);
    setError("Task not found");
    setTasks([]);
    setIsSearching(true);
  }
};

  const clearSearch = () => {
    setStringTaskId("");
    setError("");
    setTasks(allTasks);
    setIsSearching(false);
  };

  if (!user) return <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100">Loading...</div>;

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-4xl font-bold text-gray-800">My Task Tracker</h1>
            <p className="text-lg text-gray-600">Welcome, {user.username}</p>
          </div>
          <button onClick={logout} className="bg-red-500 hover:bg-red-600 text-white px-6 py-3 rounded-lg shadow-md transition duration-200 flex items-center gap-2">
            <span>Logout</span>
          </button>
        </div>

        {/* Tasks Display */}
        <div className="bg-white p-8 rounded-xl shadow-lg border border-gray-200">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold text-gray-800">My Tasks</h2>
            <div className="flex gap-4 items-center">
              <div>
                <label className="text-sm font-medium text-gray-700 mr-2">Search Task by ID:</label>
                <input
                  type="text"
                  placeholder="Enter String Task ID"
                  value={stringTaskId}
                  onChange={(e) => setStringTaskId(e.target.value)}
                  className="px-3 py-2 border border-gray-300 rounded-lg"
                />
                <button onClick={searchTask} className="ml-2 bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg text-sm transition duration-200">
                  Search
                </button>
                {isSearching && (
                  <button onClick={clearSearch} className="ml-2 bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded-lg text-sm transition duration-200">
                    Clear Search
                  </button>
                )}
              </div>
              <button
                onClick={() => setShowEditModal(true)}
                className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-3 rounded-lg shadow-md transition duration-200 flex items-center gap-2"
              >
                <span>+ New Task</span>
              </button>
            </div>
          </div>

          {error && <p style={{ color: "red" }} className="mb-4">{error}</p>}

          {isSearching && tasks.length > 0 && (
            <div className="mb-4">
              <p className="text-gray-700 font-medium">Search Results:</p>
            </div>
          )}

          {!isSearching && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {allTasks.map(task => (
              <div key={task.id} className="bg-white border border-gray-200 rounded-lg p-6 shadow-sm hover:shadow-md transition duration-200">
                <div className="flex justify-between items-start mb-4">
                  <h3 className="text-lg font-semibold text-gray-800">{task.taskName}<span className="text-gray-600 mb-4">#{task.stringTaskId}</span></h3>
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                    task.taskStatus === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                    task.taskStatus === 'IN_PROGRESS' ? 'bg-yellow-100 text-yellow-800' :
                    'bg-gray-100 text-gray-800'
                  }`}>
                    {task.taskStatus.replace('_', '-')}
                  </span>
                </div>
                <p className="text-gray-600 mb-4">{task.taskDescription}</p>
                <div className="text-sm text-gray-500 mb-4">
                  <p>Account ID: {task.taskId?.accountId || ''}</p>
                  <p>Due: {task.taskDate}</p>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => editTask(task)}
                    className="bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded-lg text-sm transition duration-200"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => deleteTask(task.id)}
                    className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg text-sm transition duration-200"
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
            </div>
          )}

          {isSearching && tasks.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {tasks.map(task => (
              <div key={task.id} className="bg-white border border-gray-200 rounded-lg p-6 shadow-sm hover:shadow-md transition duration-200">
                <div className="flex justify-between items-start mb-4">
                  <h3 className="text-lg font-semibold text-gray-800">{task.taskName} <span className="text-gray-600 mb-4">#{task.stringTaskId}</span></h3>
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                    task.taskStatus === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                    task.taskStatus === 'IN_PROGRESS' ? 'bg-yellow-100 text-yellow-800' :
                    'bg-gray-100 text-gray-800'
                  }`}>
                    {task.taskStatus.replace('_', '-')}
                  </span>
                </div>
                <p className="text-gray-600 mb-4">{task.taskDescription}</p>
                <div className="text-sm text-gray-500 mb-4">
                  <p>Account ID: {task.taskId?.accountId || ''}</p>
                  <p>Due: {task.taskDate}</p>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => editTask(task)}
                    className="bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded-lg text-sm transition duration-200"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => deleteTask(task.id)}
                    className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg text-sm transition duration-200"
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
            </div>
          )}
        </div>
      </div>

      {/* Task Edit Modal Overlay */}
      {showEditModal && (
        <>
          <div className="fixed inset-0 z-40"></div>
          <div className="fixed inset-0 flex items-center justify-center p-4 z-50" style={{ backdropFilter: 'blur(8px)', backgroundColor: 'rgba(0, 0, 0, 0.3)' }}>
            {/* Modal Container */}
            <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-gray-100">
              {/* Modal Header */}
              <div className="sticky top-0 flex justify-between items-center p-8 border-b border-gray-200 bg-white rounded-t-2xl">
                <h2 className="text-3xl font-bold text-gray-800">{editingId ? 'Edit Task' : 'Create New Task'}</h2>
                <button
                  onClick={closeEditModal}
                  className="text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg p-2 transition duration-200"
                  title="Close modal"
                >
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              {/* Modal Body */}
              <div className="p-8">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-semibold mb-2 text-gray-700">Task ID:</label>
                    <input
                      type="text"
                      value={taskId}
                      placeholder="Auto-generated"
                      readOnly
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-500 font-medium"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold mb-2 text-gray-700">Task Title:</label>
                    <input
                      type="text"
                      value={taskName}
                      onChange={(e) => setTaskName(e.target.value)}
                      placeholder="Enter task title"
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold mb-2 text-gray-700">Status:</label>
                    <select
                      value={taskStatus}
                      onChange={(e) => setTaskStatus(e.target.value)}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                    >
                      <option value="PENDING">PENDING</option>
                      <option value="IN_PROGRESS">IN_PROGRESS</option>
                      <option value="COMPLETED">COMPLETED</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-semibold mb-2 text-gray-700">Due Date:</label>
                    <input
                      type="date"
                      value={taskDate}
                      onChange={(e) => setTaskDate(e.target.value)}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                    />
                  </div>
                  <div className="md:col-span-2">
                    <label className="block text-sm font-semibold mb-2 text-gray-700">Description:</label>
                    <textarea
                      value={taskDescription}
                      onChange={(e) => setTaskDescription(e.target.value)}
                      placeholder="Enter task description"
                      rows="4"
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                    />
                  </div>
                </div>
              </div>

              {/* Modal Footer */}
              <div className="sticky bottom-0 flex gap-4 justify-end p-8 border-t border-gray-200 bg-gray-50 rounded-b-2xl">
                <button
                  onClick={closeEditModal}
                  className="px-6 py-3 rounded-lg text-gray-700 bg-gray-200 hover:bg-gray-300 font-semibold transition duration-200"
                >
                  Cancel
                </button>
                <button
                  onClick={saveTask}
                  disabled={loading}
                  className="px-6 py-3 rounded-lg text-white bg-blue-500 hover:bg-blue-600 font-semibold transition duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? 'Saving...' : (editingId ? 'Update Task' : 'Create Task')}
                </button>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

export default UserDashboard;