package backend.models;

import java.sql.Timestamp;

public class ChatRoom {
    private int roomId;
    private String roomName;
    private String roomDescription;
    private int createdBy;
    private Timestamp createdAt;
    
    public ChatRoom() {}
    
    public ChatRoom(String roomName, String roomDescription, int createdBy) {
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    public String getRoomDescription() { return roomDescription; }
    public void setRoomDescription(String roomDescription) { this.roomDescription = roomDescription; }
    
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return roomName;
    }
}