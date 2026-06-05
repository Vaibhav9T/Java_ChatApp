import { create } from 'zustand';
import { persist } from 'zustand/middleware';

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
  
  // --- NEW ROOM STATE ---
  activeRoom: string;
  setActiveRoom: (room: string) => void;
  rooms: string[];
  setRooms: (rooms: string[]) => void;
  
  messages: Message[];
  addMessage: (msg: Message) => void;
  setMessages: (msgs: Message[]) => void;
}

export const useChatStore = create<ChatState>()(
  persist(
    (set) => ({
      currentUser: null,
      token: null,
      setSession: (user, token) => set({ currentUser: user, token: token }),
      logout: () => set({ currentUser: null, token: null, messages: [], activeRoom: 'general' }),

      // Default to general, empty room list initially
      activeRoom: 'general',
      setActiveRoom: (room) => set({ activeRoom: room, messages: [] }), // Clear messages when switching rooms
      rooms: [],
      setRooms: (rooms) => set({ rooms: rooms }),

      messages: [],
      addMessage: (msg) => set((state) => ({ messages: [...state.messages, msg] })),
      setMessages: (msgs) => set({ messages: msgs }),
    }),
    {
      name: 'chat-storage',
      partialize: (state) => ({ currentUser: state.currentUser, token: state.token }), 
    }
  )
);