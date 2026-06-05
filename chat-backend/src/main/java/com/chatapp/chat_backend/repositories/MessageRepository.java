package com.chatapp.chat_backend.repositories;

import com.chatapp.chat_backend.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Automatically fetches all messages for a specific room history
    List<Message> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);
    // Add this line inside the interface:
    List<Message> findByChatRoom_NameOrderByTimestampAsc(String roomName);
}