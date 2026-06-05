package com.chatapp.chat_backend.controllers;

import com.chatapp.chat_backend.models.ChatRoom;
import com.chatapp.chat_backend.models.Message;
import com.chatapp.chat_backend.models.User;
import com.chatapp.chat_backend.repositories.ChatRoomRepository;
import com.chatapp.chat_backend.repositories.MessageRepository;
import com.chatapp.chat_backend.repositories.UserRepository;
import com.chatapp.chat_backend.services.AiService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private AiService aiService; 

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/{roomId}")
    public Message sendMessage(@DestinationVariable String roomId, @Payload Message chatMessage) {
        
        User sender = userRepository.findByUsername(chatMessage.getSender().getUsername()).orElse(null);
        
        ChatRoom room = chatRoomRepository.findByName(roomId).orElseGet(() -> {
            ChatRoom newRoom = new ChatRoom(roomId);
            return chatRoomRepository.save(newRoom);
        });

        if (sender != null) {
            chatMessage.setSender(sender);
            chatMessage.setChatRoom(room);
            messageRepository.save(chatMessage);
        }
        
       
        if (chatMessage.getContent().trim().startsWith("@AI")) {
            // Strip out the "@AI" part and send the rest to Gemini
            String prompt = chatMessage.getContent().substring(3).trim();
            aiService.processAiCommand(roomId, prompt);
        }
        
        return chatMessage;
    }
}