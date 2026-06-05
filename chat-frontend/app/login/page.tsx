'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useChatStore } from '@/src/store/chatStore';

export default function LoginPage() {
  const [isLogin, setIsLogin] = useState(true); // Toggles between Login and Register
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const router = useRouter();
const setSession = useChatStore((state) => state.setSession);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const endpoint = isLogin ? '/api/auth/login' : '/api/auth/register';

    try {
      const response = await fetch(`http://localhost:8080${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        if (isLogin) {
          // Read the stream ONCE as JSON to get the token
          const data = await response.json();
          setSession(data.username, data.token);
          router.push('/'); 
        } else {
          // Registration successful
          setIsLogin(true);
          setError("Registration successful! Please log in.");
        }
      } else {
        // If there's a 401 or 400 error, read the stream ONCE as text
        const errorText = await response.text();
        setError(errorText);
      }
    } catch (err: any) {
      console.error(err);
      setError("System Error: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#111B21] flex items-center justify-center p-4 text-[#E9EDEF]">
      <div className="w-full max-w-md bg-[#202C33] rounded-lg shadow-xl p-8 border border-[#222D34]">
        
        <div className="flex justify-center mb-6">
          <div className="w-16 h-16 bg-[#00A884] rounded-full flex items-center justify-center shadow-lg">
            <span className="text-white text-2xl font-bold">#</span>
          </div>
        </div>

        <h2 className="text-2xl font-semibold text-center mb-2">
          {isLogin ? 'Welcome Back' : 'Create Account'}
        </h2>
        <p className="text-[#8696A0] text-center text-sm mb-8">
          {isLogin ? 'Enter your details to connect to the server' : 'Sign up to start messaging securely'}
        </p>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-[#8696A0] mb-1">Username</label>
            <input
              type="text"
              required
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full bg-[#2A3942] border border-transparent focus:border-[#00A884] rounded-lg px-4 py-2.5 outline-none transition-colors"
              placeholder="e.g. testuser"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-[#8696A0] mb-1">Password</label>
            <input
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full bg-[#2A3942] border border-transparent focus:border-[#00A884] rounded-lg px-4 py-2.5 outline-none transition-colors"
              placeholder="••••••••"
            />
          </div>

          {error && (
            <div className={`p-3 rounded text-sm text-center ${error.includes('successful') ? 'bg-[#005C4B]/20 text-[#00A884]' : 'bg-red-500/10 text-red-400'}`}>
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading || !username || !password}
            className="w-full bg-[#00A884] hover:bg-[#008F6F] text-white font-medium py-2.5 rounded-lg transition-colors disabled:opacity-50"
          >
            {loading ? 'Connecting...' : (isLogin ? 'Log In' : 'Register')}
          </button>
        </form>

        <div className="mt-6 text-center text-sm text-[#8696A0]">
          {isLogin ? "Don't have an account? " : "Already have an account? "}
          <button 
            onClick={() => { setIsLogin(!isLogin); setError(''); }}
            className="text-[#00A884] hover:underline focus:outline-none"
          >
            {isLogin ? 'Register here' : 'Log in here'}
          </button>
        </div>

      </div>
    </div>
  );
}