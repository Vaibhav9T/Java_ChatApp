import { useEffect, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useChatStore } from '../store/chatStore';

// Safely pull the API URL from the .env file, defaulting to localhost for safety
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export const useWebSocket = (roomId: string, username: string) => {
  const stompClient = useRef<Client | null>(null);
  const addMessage = useChatStore((state) => state.addMessage);
  const setIsAiTyping = useChatStore((state) => state.setIsAiTyping);

  useEffect(() => {
    // 1. Initialize the STOMP Client dynamically
    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_URL}/ws`),
      reconnectDelay: 5000,
      
      onConnect: () => {
        console.log('🌐 Connected to WebSocket Server!');
        
        // 2. Subscribe to the room's broadcast channel
        client.subscribe(`/topic/${roomId}`, (message) => {
          const receivedMessage = JSON.parse(message.body);
          
          // --- AI UX LOGIC ---
          // If the AI responds, instantly turn off the "Thinking" indicator
          if (receivedMessage.sender.username === 'AI_Assistant') {
            setIsAiTyping(false);
          }
          
          // --- SYSTEM COMMAND INTERCEPTOR ---
          // If this is a hidden server command, execute it and STOP. Do not render it.
          if (receivedMessage.content === 'SYSTEM_WIPE_COMMAND') {
            useChatStore.getState().setMessages([]); // Instantly clear the screen!
            return; 
          }
          
          // Otherwise, it's a normal message. Add it to the screen.
          addMessage(receivedMessage); 
        });
      },
      
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      },
    });

    // 3. Activate the connection
    client.activate();
    stompClient.current = client;

    // 4. Cleanup connection when the user leaves the chat page
    return () => {
      if (stompClient.current) {
        stompClient.current.deactivate();
        console.log('🛑 Disconnected from WebSocket');
      }
    };
  }, [roomId, addMessage, setIsAiTyping]);

  // 5. Provide a function for the UI to send messages back to the server
  const sendMessage = useCallback((content: string) => {
    if (stompClient.current && stompClient.current.connected) {
      const chatMessage = {
        content,
        sender: { username }, 
      };
      
      stompClient.current.publish({
        destination: `/app/chat/${roomId}`, 
        body: JSON.stringify(chatMessage),
      });
    } else {
      console.error('Cannot send message: WebSocket is not connected.');
    }
  }, [roomId, username]);

  return { sendMessage };
};