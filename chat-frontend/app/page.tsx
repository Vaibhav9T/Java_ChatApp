'use client';

import { useState, useRef, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useChatStore } from '@/src/store/chatStore';
import { useWebSocket } from '@/src/hooks/useWebSocket';
import { Send, UserCircle2, Plus, MoreVertical, Phone, Video, Paperclip, Smile, LogOut, Hash, Search } from 'lucide-react';

export default function ChatInterface() {
  const router = useRouter(); 
  
  // Zustand State
  const currentUser = useChatStore((state) => state.currentUser); 
  const token = useChatStore((state) => state.token);
  const messages = useChatStore((state) => state.messages);
  const setMessages = useChatStore((state) => state.setMessages);
  const logout = useChatStore((state) => state.logout);
  
  // New Room State
  const activeRoom = useChatStore((state) => state.activeRoom);
  const setActiveRoom = useChatStore((state) => state.setActiveRoom);
  const rooms = useChatStore((state) => state.rooms);
  const setRooms = useChatStore((state) => state.setRooms);

  const [inputValue, setInputValue] = useState('');
  const [newRoomName, setNewRoomName] = useState(''); // State for creating rooms
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const [isMounted, setIsMounted] = useState(false);
  
  useEffect(() => {
    setIsMounted(true);
  }, []);

  // --- DYNAMIC WEBSOCKET CONNECTION ---
  // When activeRoom changes, this hook automatically reconnects!
  const { sendMessage } = useWebSocket(activeRoom, isMounted ? (currentUser || 'guest') : 'guest');

  useEffect(() => {
    if (isMounted && !currentUser) {
      router.push('/login');
    }
  }, [isMounted, currentUser, router]);

  // --- FETCH ROOMS LIST ---
  const fetchRooms = async () => {
    if (!token) return;
    try {
      const res = await fetch('http://localhost:8080/api/rooms', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      const data = await res.json();
      
      // FIX: Ensure data is an array before mapping
      if (Array.isArray(data)) {
        setRooms(data.map((r: any) => r.name));
      } else if (data && Array.isArray(data.content)) {
        // Fallback: Sometimes Spring Data wraps lists in a 'content' object
        setRooms(data.content.map((r: any) => r.name));
      } else {
        console.error("API did not return an array:", data);
        setRooms([]); // Safe fallback to prevent crashes
      }
      
    } catch (err) {
      console.error("Failed to load rooms", err);
    }
  };

  // Fetch rooms on load
  useEffect(() => {
    if (isMounted && currentUser && token) {
      fetchRooms();
    }
  }, [isMounted, currentUser, token]);

  // --- FETCH CHAT HISTORY (Triggered on load AND when activeRoom changes) ---
  useEffect(() => {
    if (isMounted && currentUser && token) {
      fetch(`http://localhost:8080/api/messages/${activeRoom}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      })
        .then(res => {
          if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
          return res.json();
        })
        .then(data => setMessages(data))
        .catch(err => console.error("Failed to load history:", err));
    }
  }, [isMounted, currentUser, token, activeRoom, setMessages]); // <-- activeRoom added as dependency

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputValue.trim()) {
      sendMessage(inputValue);
      setInputValue('');
      
      // Auto-refresh room list in case this was the first message in a new room
      if (!rooms.includes(activeRoom)) fetchRooms();
    }
  };

  const handleCreateRoom = async (e: React.FormEvent) => {
    e.preventDefault();
    const formattedName = newRoomName.trim().toLowerCase().replace(/\s+/g, '-');
    if (!formattedName) return;

    try {
      await fetch(`http://localhost:8080/api/rooms/${formattedName}`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setNewRoomName('');
      fetchRooms(); // Refresh the sidebar
      setActiveRoom(formattedName); // Automatically jump to the new room
    } catch (err) {
      console.error("Failed to create room", err);
    }
  };

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  if (!isMounted) {
    return <div className="h-screen bg-[#111B21] flex items-center justify-center text-[#00A884]">Loading...</div>;
  }

  return (
    <div className="flex h-screen bg-[#111B21] overflow-hidden text-[#E9EDEF] selection:bg-[#005C4B] selection:text-white">
      
      {/* --- SIDEBAR --- */}
      <aside className="w-[30%] min-w-[300px] max-w-[420px] border-r border-[#222D34] flex flex-col bg-[#111B21] z-20">
        
        {/* Sidebar Header */}
        <div className="bg-[#202C33] px-4 py-3 flex justify-between items-center h-[60px]">
          <div className="flex items-center gap-3 cursor-pointer">
            <UserCircle2 className="w-10 h-10 text-[#AEBAC1] hover:text-[#D1D7DB] transition-colors" />
            <span className="font-semibold text-[15px]">{currentUser}</span>
          </div>
          <div className="flex gap-4 text-[#AEBAC1]">
            <button onClick={handleLogout} className="p-1.5 rounded-full hover:bg-[#2A3942] transition-colors group text-[#AEBAC1] hover:text-red-400" title="Log Out">
              <LogOut className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Create Room Bar */}
        <div className="p-2 border-b border-[#222D34]">
          <form onSubmit={handleCreateRoom} className="bg-[#202C33] rounded-lg flex items-center px-4 py-1.5 h-[35px] group focus-within:bg-[#111B21] border border-transparent focus-within:border-[#00A884] transition-colors">
            <Plus className="w-5 h-5 text-[#8696A0] mr-3 shrink-0 cursor-pointer hover:text-[#00A884]" onClick={handleCreateRoom} />
            <input 
              type="text" 
              value={newRoomName}
              onChange={(e) => setNewRoomName(e.target.value)}
              placeholder="Create a new room" 
              className="bg-transparent border-none focus:outline-none w-full text-[14px] placeholder-[#8696A0]"
            />
          </form>
        </div>

        {/* Dynamic Chat List */}
        <div className="flex-1 overflow-y-auto custom-scrollbar">
          {rooms.map((room) => (
            <div 
              key={room}
              onClick={() => setActiveRoom(room)}
              className={`flex items-center px-3 py-3 cursor-pointer transition-colors border-b border-[#222D34] ${
                activeRoom === room ? 'bg-[#2A3942]' : 'hover:bg-[#202C33]'
              }`}
            >
              <div className="w-[48px] h-[48px] rounded-full bg-emerald-600 flex items-center justify-center mr-4 shrink-0 shadow-sm">
                <Hash className="w-6 h-6 text-white" />
              </div>
              <div className="flex-1 min-w-0 pr-2">
                <div className="flex justify-between items-center mb-0.5">
                  <h2 className="text-[16px] font-normal truncate">#{room}</h2>
                  {activeRoom === room && <span className="text-[12px] text-[#00A884] font-medium">Live</span>}
                </div>
                <p className="text-[13px] text-[#8696A0] truncate leading-5">
                  Click to join chat
                </p>
              </div>
            </div>
          ))}
          {/* Default view if no rooms exist yet */}
          {rooms.length === 0 && !rooms.includes('general') && (
             <div className="text-center text-[#8696A0] text-sm mt-10">No rooms yet. Create one above!</div>
          )}
        </div>
      </aside>

      {/* --- MAIN CHAT AREA --- */}
      <main className="flex-1 flex flex-col relative bg-[#0B141A]">
        
        <div className="absolute inset-0 opacity-[0.04] pointer-events-none z-0" style={{ backgroundImage: `url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M20 20.5V18H0v-2h20v-2H0v-2h20v-2H0V8h20V6H0V4h20V2H0V0h22v20h2v-2h2v2h2v-2h2v2h2v-2h2v2h2v-2h2v2h2v20H22v-2h-2v2h-2v-2h-2v2h-2v-2h-2v2h-2v-2h-2v2h-2v-2h-2v2h-2v-2h-2v2h-2v-2h-2v2H0v-2h20v-2H0v-2h20v-2H0v-2h20v-2H0v-2h20v-2H0v-2h20v-2H0v-2h20v-2z' fill='%23ffffff' fill-opacity='1' fill-rule='evenodd'/%3E%3C/svg%3E")` }} />

        {/* Chat Header */}
        <header className="bg-[#202C33] flex items-center justify-between px-4 h-[60px] z-10 border-b border-[#222D34]">
          <div className="flex items-center cursor-pointer">
            <div className="w-[40px] h-[40px] bg-emerald-600 rounded-full flex items-center justify-center mr-4">
               <Hash className="w-5 h-5 text-white" />
            </div>
            <div>
              <h1 className="text-[#E9EDEF] font-normal text-[16px] leading-tight">#{activeRoom}</h1>
              <p className="text-[#8696A0] text-[13px] leading-tight">Tap here for group info</p>
            </div>
          </div>
          <div className="flex gap-6 text-[#AEBAC1]">
            <Video className="w-[22px] h-[22px] cursor-pointer hover:text-[#E9EDEF] transition-colors" />
            <Search className="w-[20px] h-[20px] cursor-pointer hover:text-[#E9EDEF] transition-colors" />
            <MoreVertical className="w-[22px] h-[22px] cursor-pointer hover:text-[#E9EDEF] transition-colors" />
          </div>
        </header>

        {/* Scrollable Message Area */}
        <div className="flex-1 overflow-y-auto px-[5%] py-4 space-y-1 z-10 custom-scrollbar">
          
          <div className="flex justify-center mb-6 mt-2">
            <p className="bg-[#182229] text-[#FFEECD] text-[12.5px] py-2 px-3 rounded-lg text-center max-w-[90%] shadow-sm">
              <span className="mr-1">🔒</span> Welcome to #{activeRoom}. Messages are end-to-end encrypted.
            </p>
          </div>

          {messages.map((msg, index) => {
            const isMe = msg.sender.username === currentUser;
            const isAi = msg.sender.username === 'AI_Assistant'; 
            const isFirstInGroup = index === 0 || messages[index - 1].sender.username !== msg.sender.username;
            
            return (
              <div key={index} className={`flex ${isMe ? 'justify-end' : 'justify-start'} ${isFirstInGroup ? 'mt-3' : 'mt-[2px]'}`}>
                <div className={`max-w-[70%] px-3 py-2 rounded-lg relative shadow-[0_1px_0.5px_rgba(11,20,26,0.13)] text-[14.5px] leading-[20px] ${
                    isMe 
                      ? 'bg-[#005C4B] rounded-tr-none' 
                      : isAi 
                        ? 'bg-[#1E2A30] border border-[#00A884] shadow-[0_0_10px_rgba(0,168,132,0.15)] rounded-tl-none' // Custom AI styling
                        : 'bg-[#202C33] rounded-tl-none'
                  }`}
                >
                  {!isMe && isFirstInGroup && (
                    <span className={`block text-[12.5px] font-medium mb-1 capitalize cursor-pointer hover:underline ${
                      isAi ? 'text-[#00A884]' : 'text-[#53bdeb]' // Custom AI name color
                    }`}>
                      {isAi ? '✨ ' + msg.sender.username : msg.sender.username}
                    </span>
                  )}
                  <div className="flex flex-wrap items-end gap-2">
                    <span className="break-words min-w-0" style={{ wordBreak: 'break-word', whiteSpace: 'pre-wrap' }}>
                      {msg.content}
                    </span>
                    <span className="text-white/60 text-[11px] min-w-fit float-right mt-1 self-end leading-none">
                      {new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                      {isMe && <span className="ml-1 text-[#53bdeb] text-[13px] inline-block -translate-y-[1px]">✓✓</span>}
                    </span>
                  </div>
                </div>
              </div>
            );
          })}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Footer */}
        <footer className="bg-[#202C33] px-4 py-3 flex items-center gap-4 z-10 min-h-[62px]">
          <div className="flex gap-4 text-[#8696A0] items-center">
            <Smile className="w-[26px] h-[26px] cursor-pointer hover:text-[#E9EDEF] transition-colors" />
            <Paperclip className="w-[24px] h-[24px] cursor-pointer hover:text-[#E9EDEF] transition-colors" />
          </div>
          <form onSubmit={handleSend} className="flex-1 flex items-center bg-[#2A3942] rounded-lg overflow-hidden">
            <input
              type="text"
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              placeholder={`Message #${activeRoom}`}
              className="w-full bg-transparent text-[#E9EDEF] placeholder-[#8696A0] px-4 py-[10px] focus:outline-none text-[15px]"
            />
          </form>
          <button 
            onClick={handleSend}
            disabled={!inputValue.trim()}
            className={`p-2 transition-colors flex items-center justify-center rounded-full ${
              inputValue.trim() ? 'bg-[#00A884] text-white hover:bg-[#008F6F]' : 'text-[#8696A0] cursor-not-allowed'
            }`}
          >
            <Send className="w-5 h-5 ml-0.5" />
          </button>
        </footer>
      </main>
    </div>
  );
}