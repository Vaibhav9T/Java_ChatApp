import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

export interface Message {
  id?: number;
  content: string;
  sender: { username: string };
  timestamp?: string;

  fileData?: string;
  fileName?: string;
}

interface ChatState {
  currentUser: string | null;
  token: string | null;
  setSession: (user: string, token: string) => void;
  logout: () => void;
  
  activeRoom: string;
  setActiveRoom: (room: string) => void;
  rooms: string[];
  setRooms: (rooms: string[]) => void;
  
  messages: Message[];
  addMessage: (msg: Message) => void;
  setMessages: (msgs: Message[]) => void;

  isAiTyping: boolean;
  setIsAiTyping: (isTyping: boolean) => void;

  // --- NEW THEME STATE ---
  theme: 'dark' | 'light';
  toggleTheme: () => void;
}

export const useChatStore = create<ChatState>()(
  persist(
    (set) => ({
      currentUser: null,
      token: null,
      setSession: (user, token) => set({ currentUser: user, token: token }),
      
      logout: () => set({ currentUser: null, token: null, messages: [], activeRoom: 'general', isAiTyping: false }),

      activeRoom: 'general',
      setActiveRoom: (room) => set({ activeRoom: room, messages: [], isAiTyping: false }), 
      rooms: [],
      setRooms: (rooms) => set({ rooms: rooms }),

      messages: [],
      addMessage: (msg) => set((state) => ({ messages: [...state.messages, msg] })),
      setMessages: (msgs) => set({ messages: msgs }),

      isAiTyping: false,
      setIsAiTyping: (isTyping) => set({ isAiTyping: isTyping }),

      // Theme Implementation defaults to dark
      theme: 'dark',
      toggleTheme: () => set((state) => ({ theme: state.theme === 'dark' ? 'light' : 'dark' })),
    }),
    {
      name: 'chat-storage',
      storage: createJSONStorage(() => sessionStorage), 
      partialize: (state) => ({ currentUser: state.currentUser, token: state.token, theme: state.theme }), 
    }
  )
);