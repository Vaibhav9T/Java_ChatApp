import { create } from 'zustand';
// 1. Add createJSONStorage to your imports
import { persist, createJSONStorage } from 'zustand/middleware';

export interface Message {
  id?: number;
  content: string;
  sender: { username: string };
  timestamp?: string;
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
    }),
    {
      name: 'chat-storage',
      // 2. THIS IS THE MAGIC LINE: Switch to sessionStorage!
      storage: createJSONStorage(() => sessionStorage), 
      partialize: (state) => ({ currentUser: state.currentUser, token: state.token }), 
    }
  )
);