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

    @Value("${gemini.api-key}")
    private String API_KEY;

    private final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";
    private final RestTemplate restTemplate = new RestTemplate();

    public void processAiCommand(String roomId, String prompt) {
        new Thread(() -> {
            try {
                String fullUrl = BASE_URL + API_KEY;

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String safePrompt = prompt.replace("\"", "\\\"");
                
                String requestBody = """
                {
                  "contents": [{"parts":[{"text": "%s"}]}],
                  "tools": [
                    {
                      "functionDeclarations": [
                        {
                          "name": "clear_chat_history",
                          "description": "Deletes all messages in the current chat room. Call this when a user asks to clear, wipe, or delete the chat."
                        }
                      ]
                    }
                  ]
                }
                """.formatted(safePrompt);

                HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
                ResponseEntity<Map> response = restTemplate.postForEntity(fullUrl, request, Map.class);

                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                
                Map<String, Object> firstPart = parts.get(0);
                String aiResponseText = "";

                // FETCH USER AND ROOM EARLY: We need these for the system wipe command
                User aiUser = userRepository.findByUsername("AI_Assistant").orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername("AI_Assistant");
                    newUser.setPassword("hidden_bot_password");
                    return userRepository.save(newUser);
                });

                ChatRoom room = chatRoomRepository.findByName(roomId).orElseThrow();

                // --- AGENTIC LOGIC: Check if Gemini wants to call a function ---
                if (firstPart.containsKey("functionCall")) {
                    Map<String, Object> functionCall = (Map<String, Object>) firstPart.get("functionCall");
                    String functionName = (String) functionCall.get("name");
                    
                    if ("clear_chat_history".equals(functionName)) {
                        System.out.println("🤖 AI Agent triggered function: clear_chat_history");
                        
                        // 1. Wipe the PostgreSQL Database
                        messageRepository.deleteByChatRoom(room);
                        
                        // 2. Blast a hidden command to all connected WebSockets instantly
                        Message wipeCommand = new Message();
                        wipeCommand.setContent("SYSTEM_WIPE_COMMAND");
                        wipeCommand.setSender(aiUser);
                        wipeCommand.setChatRoom(room);
                        messagingTemplate.convertAndSend("/topic/" + roomId, wipeCommand);
                        
                        // 3. Prepare the AI's verbal confirmation
                        aiResponseText = "I have successfully cleared the chat history for this room.";
                    }
                } else if (firstPart.containsKey("text")) {
                    // Standard text response
                    aiResponseText = (String) firstPart.get("text");
                }

                // 4. Save and Broadcast the AI's Final Message
                Message aiMessage = new Message();
                aiMessage.setContent(aiResponseText);
                aiMessage.setSender(aiUser);
                aiMessage.setChatRoom(room);

                messageRepository.save(aiMessage);

                // Manually fire the text message into the specific room's channel
                messagingTemplate.convertAndSend("/topic/" + roomId, aiMessage);

            } catch (Exception e) {
                System.err.println("AI Request Failed: " + e.getMessage());
            }
        }).start();
    }
}