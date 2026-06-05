package com.chatapp.chat_backend.services;

import com.chatapp.chat_backend.models.ChatRoom;
import com.chatapp.chat_backend.models.Message;
import com.chatapp.chat_backend.models.User;
import com.chatapp.chat_backend.repositories.ChatRoomRepository;
import com.chatapp.chat_backend.repositories.MessageRepository;
import com.chatapp.chat_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private MessageRepository messageRepository;

    // Spring injects the key here from your .env file
    @Value("${gemini.api-key}")
    private String API_KEY;

    // Base URL WITHOUT the key attached
    private final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();

    public void processAiCommand(String roomId, String prompt) {
        // Run in a background thread to keep the WebSocket stream fast
        new Thread(() -> {
            try {
                // Combine the base URL and the injected key right before making the request!
                String fullUrl = BASE_URL + API_KEY;

                // 1. Build the Request to Gemini
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Escape quotes to prevent JSON errors
                String safePrompt = prompt.replace("\"", "\\\"");
                String requestBody = "{\"contents\": [{\"parts\":[{\"text\": \"" + safePrompt + "\"}]}]}";

                HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
                ResponseEntity<Map> response = restTemplate.postForEntity(fullUrl, request, Map.class);

                // 2. Parse the JSON Response
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                String aiResponseText = (String) parts.get(0).get("text");

                // 3. Ensure the AI User exists in PostgreSQL
                User aiUser = userRepository.findByUsername("AI_Assistant").orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername("AI_Assistant");
                    newUser.setPassword("hidden_bot_password"); // Bypass BCrypt auth
                    return userRepository.save(newUser);
                });

                ChatRoom room = chatRoomRepository.findByName(roomId).orElseThrow();

                // 4. Save and Broadcast the AI's Message
                Message aiMessage = new Message();
                aiMessage.setContent(aiResponseText);
                aiMessage.setSender(aiUser);
                aiMessage.setChatRoom(room);

                messageRepository.save(aiMessage);

                // Manually fire the message into the specific room's channel
                messagingTemplate.convertAndSend("/topic/" + roomId, aiMessage);

            } catch (Exception e) {
                System.err.println("AI Request Failed: " + e.getMessage());
            }
        }).start();
    }
}