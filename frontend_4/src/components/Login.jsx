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
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
        <h2 className="text-2xl font-bold mb-6 text-center">Login</h2>
        <div className="mb-4">
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div className="mb-4">
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <button
          onClick={handleLogin}
          disabled={loading}
          className="w-full bg-blue-500 text-white py-2 rounded-md hover:bg-blue-600 disabled:opacity-50"
        >
          {loading ? 'Logging in...' : 'Login'}
        </button>
        <p className="text-red-500 text-center mt-4">{error}</p>
      </div>
    </div>
  );
}

export default Login;