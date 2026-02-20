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
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
        <h2 className="text-2xl font-bold mb-6 text-center">Register</h2>
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
          onClick={handleRegister}
          disabled={loading}
          className="w-full bg-green-500 text-white py-2 rounded-md hover:bg-green-600 disabled:opacity-50"
        >
          {loading ? 'Registering...' : 'Register'}
        </button>
        <p className="text-red-500 text-center mt-4">{error}</p>
      </div>
    </div>
  );
}

export default Register;