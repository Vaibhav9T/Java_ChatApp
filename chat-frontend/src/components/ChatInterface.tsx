'use client';

import { useState, useRef, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useChatStore } from '@/src/store/chatStore';
import { useWebSocket } from '@/src/hooks/useWebSocket';
import { Send, UserCircle2, Plus, MoreVertical, Video, Paperclip, Smile, LogOut, Hash, Search, Sun, Moon, X, FileText } from 'lucide-react';
import EmojiPicker, { Theme } from 'emoji-picker-react';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export default function ChatInterface() {
  const router = useRouter(); 
  
  const { 
    currentUser, token, messages, setMessages, logout,
    activeRoom, setActiveRoom, rooms, setRooms,
    isAiTyping, setIsAiTyping,
    theme, toggleTheme 
  } = useChatStore();

  const [inputValue, setInputValue] = useState('');
  const [newRoomName, setNewRoomName] = useState(''); 
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const [isMounted, setIsMounted] = useState(false);

  const [showEmojiPicker, setShowEmojiPicker] = useState(false);
  const [isSearching, setIsSearching] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  
  // --- NEW ATTACHMENT STATE ---
  const [attachment, setAttachment] = useState<{name: string, data: string} | null>(null);

  const fileInputRef = useRef<HTMLInputElement>(null);
  const emojiPickerRef = useRef<HTMLDivElement>(null);
  
  useEffect(() => setIsMounted(true), []);

  const { sendMessage } = useWebSocket(activeRoom, isMounted ? (currentUser || 'guest') : 'guest');

  useEffect(() => {
    if (isMounted && !currentUser) router.push('/login');
  }, [isMounted, currentUser, router]);

  const fetchRooms = async () => {
    if (!token) return;
    try {
      const res = await fetch(`${API_URL}/api/rooms`, { headers: { 'Authorization': `Bearer ${token}` } });
      const data = await res.json();
      if (Array.isArray(data)) setRooms(data.map((r: any) => r.name));
      else if (data && Array.isArray(data.content)) setRooms(data.content.map((r: any) => r.name));
      else setRooms([]); 
    } catch (err) { console.error("Failed to load rooms", err); }
  };

  useEffect(() => {
    if (isMounted && currentUser && token) fetchRooms();
  }, [isMounted, currentUser, token]);

  useEffect(() => {
    if (isMounted && currentUser && token) {
      fetch(`${API_URL}/api/messages/${activeRoom}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }
      })
      .then(res => res.ok ? res.json() : Promise.reject(res))
      .then(data => setMessages(data))
      .catch(err => console.error("Failed to load history:", err));
    }
  }, [isMounted, currentUser, token, activeRoom, setMessages]); 

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isAiTyping, attachment]);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (emojiPickerRef.current && !emojiPickerRef.current.contains(event.target as Node)) {
        setShowEmojiPicker(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputValue.trim() || attachment) {
      if (inputValue.trim().startsWith('@AI')) setIsAiTyping(true);
      
      // Send message with optional attachment data
      sendMessage(inputValue, attachment?.data, attachment?.name);
      
      setInputValue('');
      setAttachment(null); // Clear attachment preview
      setShowEmojiPicker(false);
      if (!rooms.includes(activeRoom)) fetchRooms();
    }
  };

  const handleCreateRoom = async (e: React.FormEvent) => {
    e.preventDefault();
    const formattedName = newRoomName.trim().toLowerCase().replace(/\s+/g, '-');
    if (!formattedName) return;
    try {
      await fetch(`${API_URL}/api/rooms/${formattedName}`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setNewRoomName('');
      fetchRooms(); 
      setActiveRoom(formattedName); 
    } catch (err) { console.error("Failed to create room", err); }
  };

  const handleLogout = () => { logout(); router.push('/login'); };

  const handleEmojiClick = (emojiObj: any) => {
    setInputValue(prev => prev + emojiObj.emoji);
  };

  // --- BASE64 FILE READER LOGIC ---
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // Prevent massive files from crashing the WebSocket (limit to ~2MB)
      if (file.size > 2 * 1024 * 1024) {
        alert("File is too large. Please select an image under 2MB.");
        return;
      }
      
      const reader = new FileReader();
      reader.onloadend = () => {
        setAttachment({
          name: file.name,
          data: reader.result as string // This is the Base64 string
        });
      };
      reader.readAsDataURL(file);
    }
    // Reset input so the same file can be selected again if needed
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  const isDark = theme === 'dark';
  const c = {
    bgApp: isDark ? 'bg-[#111B21]' : 'bg-[#EFEAE2]', 
    bgPanel: isDark ? 'bg-[#202C33]' : 'bg-[#FFFFFF]',
    bgSidebar: isDark ? 'bg-[#111B21]' : 'bg-[#FFFFFF]',
    border: isDark ? 'border-[#222D34]' : 'border-[#E1E5E8]',
    textMain: isDark ? 'text-[#E9EDEF]' : 'text-[#111B21]',
    textMuted: isDark ? 'text-[#8696A0]' : 'text-[#54656F]',
    inputBg: isDark ? 'bg-[#2A3942]' : 'bg-[#F0F2F5]',
    hover: isDark ? 'hover:bg-[#202C33]' : 'hover:bg-[#F5F6F6]',
    activeRoom: isDark ? 'bg-[#2A3942]' : 'bg-[#F0F2F5]',
    myMsg: isDark ? 'bg-[#005C4B] text-[#E9EDEF]' : 'bg-[#D9FDD3] text-[#111B21]',
    otherMsg: isDark ? 'bg-[#202C33] text-[#E9EDEF]' : 'bg-[#FFFFFF] text-[#111B21]',
    aiMsg: isDark ? 'bg-[#1E2A30] border-[#00A884]' : 'bg-[#F0FDF4] border-[#00A884]',
    aiText: isDark ? 'text-[#E9EDEF]' : 'text-[#111B21]',
    systemMsg: isDark ? 'bg-[#182229] text-[#FFEECD]' : 'bg-[#FFFFFF] text-[#54656F]',
    svgPattern: isDark ? '%23ffffff' : '%23000000',
    svgOpacity: isDark ? '0.04' : '0.03'
  };

  const filteredMessages = messages.filter(msg => 
    msg.content.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (!isMounted) return <div className={`h-screen ${c.bgApp} flex items-center justify-center text-[#00A884]`}>Loading...</div>;

  return (
    <div className={`flex h-screen overflow-hidden ${c.bgApp} ${c.textMain} transition-colors duration-300 selection:bg-[#005C4B] selection:text-white`}>
      
      {/* SIDEBAR */}
      <aside className={`w-[30%] min-w-[320px] max-w-[420px] border-r ${c.border} flex flex-col ${c.bgSidebar} z-20`}>
        <div className={`${c.bgPanel} px-4 py-3 flex justify-between items-center h-[65px] shadow-sm z-10`}>
          <div className="flex items-center gap-3 cursor-pointer">
            <UserCircle2 className={`w-10 h-10 ${c.textMuted} hover:text-[#00A884] transition-colors`} />
            <span className="font-semibold text-[15px] tracking-wide">{currentUser}</span>
          </div>
          <div className={`flex gap-3 ${c.textMuted}`}>
            <button onClick={toggleTheme} className={`p-2 rounded-full ${c.hover} transition-colors group`} title="Toggle Theme">
              {isDark ? <Sun className="w-5 h-5 group-hover:text-amber-400 transition-colors" /> : <Moon className="w-5 h-5 group-hover:text-indigo-500 transition-colors" />}
            </button>
            <button onClick={handleLogout} className={`p-2 rounded-full ${c.hover} transition-colors group hover:text-red-500`} title="Log Out">
              <LogOut className="w-5 h-5" />
            </button>
          </div>
        </div>

        <div className={`p-3 border-b ${c.border}`}>
          <form onSubmit={handleCreateRoom} className={`${c.inputBg} rounded-2xl flex items-center px-4 py-2 group border border-transparent focus-within:border-[#00A884] transition-all shadow-sm`}>
            <Plus className={`w-5 h-5 ${c.textMuted} mr-3 shrink-0 cursor-pointer group-focus-within:text-[#00A884] transition-colors`} onClick={handleCreateRoom} />
            <input 
              type="text" 
              value={newRoomName}
              onChange={(e) => setNewRoomName(e.target.value)}
              placeholder="Create a new room" 
              className={`bg-transparent border-none focus:outline-none w-full text-[14px] ${c.textMain} placeholder-${c.textMuted}`}
            />
          </form>
        </div>

        <div className="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
          {rooms.map((room) => (
            <div 
              key={room}
              onClick={() => setActiveRoom(room)}
              className={`flex items-center px-3 py-3 cursor-pointer transition-all rounded-2xl ${
                activeRoom === room ? `${c.activeRoom} shadow-sm` : c.hover
              }`}
            >
              <div className="w-[48px] h-[48px] rounded-[20px] bg-emerald-600 flex items-center justify-center mr-4 shrink-0 shadow-sm text-white">
                <Hash className="w-6 h-6" />
              </div>
              <div className="flex-1 min-w-0 pr-2">
                <div className="flex justify-between items-center mb-0.5">
                  <h2 className="text-[16px] font-medium truncate">{room}</h2>
                  {activeRoom === room && <span className="text-[12px] text-[#00A884] font-bold">Live</span>}
                </div>
                <p className={`text-[13px] ${c.textMuted} truncate leading-5`}>Click to join chat</p>
              </div>
            </div>
          ))}
        </div>
      </aside>

      {/* MAIN CHAT AREA */}
      <main className={`flex-1 flex flex-col relative ${c.bgApp}`}>
        <div className="absolute inset-0 pointer-events-none z-0 transition-opacity duration-300" style={{ opacity: c.svgOpacity, backgroundImage: `url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M20 20.5V18H0v-2h20v-2H0v-2h20v-2H0V8h20V6H0V4h20V2H0V0h22v20h2v-2h2v2h2v-2h2v2h2v-2h2v2h2v-2h2v2h2v20H22v-2h-2v2h-2v-2h-2v2h-2v-2h-2v2h-2v-2h-2v2h-2v-2h-2v2h-2v-2h-2v2h-2v-2h-2v2H0v-2h20v-2H0v-2h20v-2H0v-2h20v-2H0v-2h20v-2H0v-2h20v-2H0v-2h20v-2z' fill='${c.svgPattern}' fill-opacity='1' fill-rule='evenodd'/%3E%3C/svg%3E")` }} />

        <header className={`${c.bgPanel} flex items-center justify-between px-6 h-[65px] z-10 border-b ${c.border} shadow-sm`}>
          <div className="flex items-center cursor-pointer group">
            <div className="w-[42px] h-[42px] bg-emerald-600 rounded-[18px] flex items-center justify-center mr-4 text-white shadow-sm group-hover:bg-emerald-500 transition-colors">
               <Hash className="w-5 h-5" />
            </div>
            <div>
              <h1 className="font-semibold text-[16px] leading-tight tracking-wide">#{activeRoom}</h1>
              <p className={`${c.textMuted} text-[13px] leading-tight`}>End-to-end encrypted</p>
            </div>
          </div>
          <div className={`flex gap-5 ${c.textMuted} items-center`}>
            {isSearching ? (
              <div className={`flex items-center ${c.inputBg} rounded-full px-3 py-1.5`}>
                <Search className="w-4 h-4 mr-2" />
                <input 
                  type="text" 
                  autoFocus
                  placeholder="Search messages..." 
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className={`bg-transparent outline-none text-[13px] ${c.textMain} w-[150px]`}
                />
                <X className="w-4 h-4 ml-2 cursor-pointer hover:text-red-400" onClick={() => {setIsSearching(false); setSearchTerm('');}} />
              </div>
            ) : (
              <Search className={`w-[20px] h-[20px] cursor-pointer hover:text-[#00A884] transition-colors`} onClick={() => setIsSearching(true)} />
            )}
            <Video className={`w-[22px] h-[22px] cursor-pointer hover:text-[#00A884] transition-colors`} aria-label="Coming Soon" role="img" />
            <MoreVertical className={`w-[22px] h-[22px] cursor-pointer hover:text-[#00A884] transition-colors`} />
          </div>
        </header>

        <div className="flex-1 overflow-y-auto px-[5%] py-6 space-y-2 z-10 custom-scrollbar relative">
          
          <div className="flex justify-center mb-8 mt-2">
            <p className={`${c.systemMsg} text-[12.5px] py-2 px-4 rounded-2xl text-center max-w-[90%] shadow-sm font-medium border ${c.border}`}>
              <span className="mr-2">🔒</span> Welcome to #{activeRoom}. Messages are end-to-end encrypted.
            </p>
          </div>

          {filteredMessages.map((msg, index) => {
            const isMe = msg.sender.username === currentUser;
            const isAi = msg.sender.username === 'AI_Assistant'; 
            const isFirstInGroup = index === 0 || filteredMessages[index - 1].sender.username !== msg.sender.username;
            
            return (
              <div key={index} className={`flex ${isMe ? 'justify-end' : 'justify-start'} ${isFirstInGroup ? 'mt-4' : 'mt-[3px]'}`}>
                <div className={`max-w-[75%] px-4 py-2.5 relative shadow-sm text-[15px] leading-[22px] flex flex-col ${
                    isMe 
                      ? `${c.myMsg} rounded-2xl ${isFirstInGroup ? 'rounded-tr-sm' : ''}`
                      : isAi 
                        ? `${c.aiMsg} ${c.aiText} border shadow-[0_0_15px_rgba(0,168,132,0.1)] rounded-2xl ${isFirstInGroup ? 'rounded-tl-sm' : ''}` 
                        : `${c.otherMsg} rounded-2xl border ${c.border} ${isFirstInGroup ? 'rounded-tl-sm' : ''}`
                  }`}
                >
                  {!isMe && isFirstInGroup && (
                    <span className={`block text-[13px] font-bold mb-1 capitalize tracking-wide ${
                      isAi ? 'text-[#00A884]' : isDark ? 'text-[#53bdeb]' : 'text-[#027EB5]' 
                    }`}>
                      {isAi ? '✨ ' + msg.sender.username : msg.sender.username}
                    </span>
                  )}
                  
                  {/* --- DYNAMIC FILE RENDERING --- */}
                  {msg.fileData && (
                    <div className="mt-1 mb-2">
                      {msg.fileData.startsWith('data:image') ? (
                        <img src={msg.fileData} alt="attachment" className="max-w-full sm:max-w-[280px] rounded-xl object-cover shadow-sm cursor-pointer hover:opacity-90 transition-opacity" />
                      ) : (
                        <a href={msg.fileData} download={msg.fileName} className={`flex items-center gap-2 ${isDark ? 'bg-black/20 hover:bg-black/30' : 'bg-black/5 hover:bg-black/10'} p-3 rounded-xl text-[13px] transition-colors`}>
                          <FileText className="w-5 h-5" /> 
                          <span className="truncate max-w-[200px] font-medium">{msg.fileName || 'Download Document'}</span>
                        </a>
                      )}
                    </div>
                  )}

                  <div className="flex flex-wrap items-end gap-3 justify-between">
                    <span className="break-words min-w-0" style={{ wordBreak: 'break-word', whiteSpace: 'pre-wrap' }}>
                      {msg.content}
                    </span>
                    <span className={`text-[11px] min-w-fit float-right mt-1 self-end leading-none font-medium ${isMe ? (isDark ? 'text-white/70' : 'text-black/50') : c.textMuted}`}>
                      {new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                      {isMe && <span className={`ml-1.5 text-[13px] inline-block -translate-y-[1px] ${isDark ? 'text-[#53bdeb]' : 'text-[#027EB5]'}`}>✓✓</span>}
                    </span>
                  </div>
                </div>
              </div>
            );
          })}

          {isAiTyping && (
            <div className="flex justify-start mt-4 mb-2 animate-pulse transition-all duration-300">
              <div className={`${c.aiMsg} ${c.aiText} border px-5 py-3 rounded-2xl rounded-tl-sm shadow-[0_0_15px_rgba(0,168,132,0.1)] flex items-center gap-3`}>
                <span className="text-[#00A884] text-[14px] font-bold tracking-wide">✨ AI is thinking</span>
                <div className="flex gap-1.5 pt-1">
                  <div className="w-1.5 h-1.5 bg-[#00A884] rounded-full animate-bounce"></div>
                  <div className="w-1.5 h-1.5 bg-[#00A884] rounded-full animate-bounce" style={{ animationDelay: '0.15s' }}></div>
                  <div className="w-1.5 h-1.5 bg-[#00A884] rounded-full animate-bounce" style={{ animationDelay: '0.3s' }}></div>
                </div>
              </div>
            </div>
          )}

          <div ref={messagesEndRef} className="h-4" />
        </div>

        {/* Input Footer */}
        <footer className={`${c.bgPanel} px-6 py-4 flex items-center gap-5 z-20 border-t ${c.border} relative`}>
          
          {/* File Preview Bubble */}
          {attachment && (
            <div className={`absolute -top-[75px] left-6 ${c.bgPanel} p-2 rounded-2xl shadow-xl border ${c.border} flex items-center gap-3 animate-in slide-in-from-bottom-2 z-50`}>
              {attachment.data.startsWith('data:image') ? (
                <img src={attachment.data} alt="preview" className="w-14 h-14 object-cover rounded-xl" />
              ) : (
                <div className="w-14 h-14 bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600 rounded-xl flex items-center justify-center">
                  <FileText className="w-6 h-6" />
                </div>
              )}
              <div className="flex flex-col pr-4">
                <span className={`text-[13px] font-medium ${c.textMain} truncate max-w-[150px]`}>{attachment.name}</span>
                <span className={`text-[11px] ${c.textMuted}`}>Ready to send</span>
              </div>
              <div className={`p-1.5 rounded-full ${c.hover} cursor-pointer mr-1`} onClick={() => setAttachment(null)}>
                <X className="w-4 h-4 text-red-500" />
              </div>
            </div>
          )}

          {showEmojiPicker && (
            <div ref={emojiPickerRef} className="absolute bottom-[80px] left-6 shadow-2xl z-50 animate-in slide-in-from-bottom-4 duration-200">
              <EmojiPicker 
                theme={isDark ? Theme.DARK : Theme.LIGHT} 
                onEmojiClick={handleEmojiClick}
                lazyLoadEmojis={true}
              />
            </div>
          )}

          <input 
            type="file" 
            ref={fileInputRef} 
            onChange={handleFileChange} 
            className="hidden" 
            accept="image/*,.pdf,.doc,.docx,.txt"
          />

          <div className={`flex gap-5 ${c.textMuted} items-center`}>
            <Smile 
              onClick={() => setShowEmojiPicker(!showEmojiPicker)}
              className={`w-[26px] h-[26px] cursor-pointer transition-colors ${showEmojiPicker ? 'text-[#00A884]' : 'hover:text-[#00A884]'}`} 
            />
            <Paperclip 
              onClick={() => fileInputRef.current?.click()}
              className={`w-[24px] h-[24px] cursor-pointer hover:text-[#00A884] transition-colors`} 
            />
          </div>
          <form onSubmit={handleSend} className={`flex-1 flex items-center ${c.inputBg} rounded-full overflow-hidden shadow-sm border border-transparent focus-within:border-[#00A884] transition-colors`}>
            <input
              type="text"
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              placeholder={`Message #${activeRoom}`}
              className={`w-full bg-transparent ${c.textMain} placeholder-${c.textMuted} px-6 py-[12px] focus:outline-none text-[15px]`}
            />
          </form>
          <button 
            onClick={handleSend}
            disabled={!inputValue.trim() && !attachment}
            className={`p-3.5 transition-all flex items-center justify-center rounded-full shadow-sm ${
              inputValue.trim() || attachment ? 'bg-[#00A884] text-white hover:bg-[#008F6F] hover:shadow-md hover:-translate-y-0.5' : `${c.inputBg} ${c.textMuted} cursor-not-allowed`
            }`}
          >
            <Send className="w-[20px] h-[20px] ml-0.5" />
          </button>
        </footer>
      </main>
    </div>
  );
}