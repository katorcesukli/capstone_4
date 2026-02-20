import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const BASE_URL = "http://localhost:8080/api";

function Register() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleRegister = async () => {
    setError('');
    setLoading(true);

    if (username.length < 3) {
      setError("Username must be at least 3 characters.");
      setLoading(false);
      return;
    }

    if (password.length < 4) {
      setError("Password must be at least 4 characters.");
      setLoading(false);
      return;
    }

    try {
      await axios.post(`${BASE_URL}/auth/register`, {
        username: username.trim(),
        password: password.trim(),
        role: "USER"
      });

      alert("Registration successful!");
      navigate('/login');
    } catch (error) {
      console.error(error);
      setError(error.response?.data?.message || "Registration failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="bg-white p-10 rounded-xl shadow-lg w-full max-w-md border border-gray-200">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">Task Tracker</h1>
          <h2 className="text-2xl font-semibold text-gray-700">Create Account</h2>
          <p className="text-gray-500 mt-2">Join us to start tracking your tasks</p>
        </div>
        <div className="mb-6">
          <label className="block text-sm font-semibold mb-2 text-gray-700">Username</label>
          <input
            type="text"
            placeholder="Choose a username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
        <div className="mb-6">
          <label className="block text-sm font-semibold mb-2 text-gray-700">Password</label>
          <input
            type="password"
            placeholder="Create a password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
        <button
          onClick={handleRegister}
          disabled={loading}
          className="w-full bg-green-500 hover:bg-green-600 text-white py-3 rounded-lg shadow-md transition duration-200 disabled:opacity-50 disabled:cursor-not-allowed text-lg font-semibold"
        >
          {loading ? 'Creating account...' : 'Create Account'}
        </button>
        {error && (
          <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-red-600 text-center text-sm">{error}</p>
          </div>
        )}
        <div className="mt-6 text-center">
          <p className="text-gray-600">Already have an account? <a href="/login" className="text-blue-500 hover:text-blue-600 font-medium">Sign in</a></p>
        </div>
      </div>
    </div>
  );
}

export default Register;