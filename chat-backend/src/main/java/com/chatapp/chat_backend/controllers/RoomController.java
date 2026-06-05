package com.chatapp.chat_backend.controllers;

import com.chatapp.chat_backend.models.ChatRoom;
import com.chatapp.chat_backend.repositories.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:3000")
public class RoomController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    // Fetch all available rooms
    @GetMapping
    public ResponseEntity<List<ChatRoom>> getAllRooms() {
        return ResponseEntity.ok(chatRoomRepository.findAll());
    }

    // Create a new room (or return it if it already exists)
    @PostMapping("/{roomName}")
    public ResponseEntity<ChatRoom> createRoom(@PathVariable String roomName) {
        ChatRoom room = chatRoomRepository.findByName(roomName)
                .orElseGet(() -> chatRoomRepository.save(new ChatRoom(roomName)));
        return ResponseEntity.ok(room);
    }
}