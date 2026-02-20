import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const BASE_URL = "http://localhost:8080/api";
const USER_KEY = "loggedUser";
const TOKEN_KEY = "jwtToken";

function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // Check if user is already logged in
    const token = localStorage.getItem(TOKEN_KEY);
    const savedUser = localStorage.getItem(USER_KEY);
    if (token && savedUser) {
      const user = JSON.parse(savedUser);
      redirectByRole(user.role);
    }
  }, []);

  const redirectByRole = (role) => {
    if (!role) {
      navigate('/login');
      return;
    }
    if (role.toUpperCase() === "ADMIN") {
      navigate('/admin');
    } else {
      navigate('/user');
    }
  };

  const handleLogin = async () => {
    setError('');
    setLoading(true);

    if (!username.trim() || !password.trim()) {
      setError("Please enter both username and password.");
      setLoading(false);
      return;
    }

    try {
      const response = await axios.post(`${BASE_URL}/auth/login`, {
        username: username.trim(),
        password: password.trim()
      });

      const data = response.data;

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

      redirectByRole(sessionUser.role);
    } catch (error) {
      console.error("Login Error:", error);
      setError(error.response?.data?.message || error.message || "Invalid username or password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="bg-white p-10 rounded-xl shadow-lg w-full max-w-md border border-gray-200">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">Task Tracker</h1>
          <h2 className="text-2xl font-semibold text-gray-700">Welcome Back</h2>
          <p className="text-gray-500 mt-2">Sign in to manage your tasks</p>
        </div>
        <div className="mb-6">
          <label className="block text-sm font-semibold mb-2 text-gray-700">Username</label>
          <input
            type="text"
            placeholder="Enter your username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
        <div className="mb-6">
          <label className="block text-sm font-semibold mb-2 text-gray-700">Password</label>
          <input
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
        <button
          onClick={handleLogin}
          disabled={loading}
          className="w-full bg-blue-500 hover:bg-blue-600 text-white py-3 rounded-lg shadow-md transition duration-200 disabled:opacity-50 disabled:cursor-not-allowed text-lg font-semibold"
        >
          {loading ? 'Signing in...' : 'Sign In'}
        </button>
        {error && (
          <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-red-600 text-center text-sm">{error}</p>
          </div>
        )}
        <div className="mt-6 text-center">
          <p className="text-gray-600">Don't have an account? <a href="/register" className="text-blue-500 hover:text-blue-600 font-medium">Sign up</a></p>
        </div>
      </div>
    </div>
  );
}

export default Login;