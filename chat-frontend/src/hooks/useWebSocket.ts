import { useEffect, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useChatStore } from '../store/chatStore';

export const useWebSocket = (roomId: string, username: string) => {
  const stompClient = useRef<Client | null>(null);
  const addMessage = useChatStore((state) => state.addMessage);

  useEffect(() => {
    // 1. Initialize the STOMP Client
    const client = new Client({
      // Map this to the endpoint defined in your Spring Boot WebSocketConfig.java
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      
      onConnect: () => {
        console.log('🌐 Connected to WebSocket Server!');
        
        // 2. Subscribe to the room's broadcast channel
        client.subscribe(`/topic/${roomId}`, (message) => {
          const receivedMessage = JSON.parse(message.body);
          addMessage(receivedMessage); // Push to Zustand store
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
  }, [roomId, addMessage]);

  // 5. Provide a function for the UI to send messages back to the server
  const sendMessage = useCallback((content: string) => {
    if (stompClient.current && stompClient.current.connected) {
      const chatMessage = {
        content,
        sender: { username }, // Matches the expected Spring Boot payload
      };
      
      stompClient.current.publish({
        destination: `/app/chat/${roomId}`, // Maps to @MessageMapping in ChatController.java
        body: JSON.stringify(chatMessage),
      });
    } else {
      console.error('Cannot send message: WebSocket is not connected.');
    }
  }, [roomId, username]);

  return { sendMessage };
};