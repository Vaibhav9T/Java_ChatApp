package backend.models;

import java.sql.Timestamp;

public class Message {
    private int messageId;
    private int senderId;
    private int roomId;
    private String messageText;
    private String messageType;
    private Timestamp sentAt;
    private String senderName; // For display purposes
    
    public Message() {}
    
    public Message(int senderId, int roomId, String messageText) {
        this.senderId = senderId;
        this.roomId = roomId;
        this.messageText = messageText;
        this.messageType = "text";
    }
    
    // Getters and Setters
    public int getMessageId() { return messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }
    
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public Timestamp getSentAt() { return sentAt; }
    public void setSentAt(Timestamp sentAt) { this.sentAt = sentAt; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", 
            sentAt != null ? sentAt.toString().substring(11, 19) : "now", 
            senderName != null ? senderName : "User" + senderId, 
            messageText);
    }
}