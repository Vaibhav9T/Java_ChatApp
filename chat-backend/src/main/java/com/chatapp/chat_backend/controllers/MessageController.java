package com.chatapp.chat_backend.controllers;

import com.chatapp.chat_backend.models.Message;
import com.chatapp.chat_backend.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000") 
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/{roomName}")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable String roomName) {
        return ResponseEntity.ok(messageRepository.findByChatRoom_NameOrderByTimestampAsc(roomName));
    }
}