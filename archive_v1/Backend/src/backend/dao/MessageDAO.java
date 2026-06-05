package backend.dao;

import backend.database.DatabaseManager;
import backend.models.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private DatabaseManager dbManager;
    
    public MessageDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    // Send a message to a room
    public boolean sendMessage(Message message) {
        String sql = "INSERT INTO messages (sender_id, room_id, message_text, message_type) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getRoomId());
            stmt.setString(3, message.getMessageText());
            stmt.setString(4, message.getMessageType());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error sending message: " + e.getMessage());
            return false;
        }
    }
    
    // Get messages for a specific room
    public List<Message> getMessagesForRoom(int roomId, int limit) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.username, u.full_name " +
                    "FROM messages m " +
                    "JOIN users u ON m.sender_id = u.user_id " +
                    "WHERE m.room_id = ? " +
                    "ORDER BY m.sent_at DESC " +
                    "LIMIT ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, limit);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message message = mapResultSetToMessage(rs);
                message.setSenderName(rs.getString("full_name"));
                messages.add(0, message); // Add to beginning to maintain chronological order
            }
        } catch (SQLException e) {
            System.err.println("Error getting messages for room: " + e.getMessage());
        }
        return messages;
    }
    
    // Get recent messages for a room (default 50 messages)
    public List<Message> getRecentMessagesForRoom(int roomId) {
        return getMessagesForRoom(roomId, 50);
    }
    
    // Send private message
    public boolean sendPrivateMessage(int senderId, int receiverId, String messageText) {
        String sql = "INSERT INTO private_messages (sender_id, receiver_id, message_text) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, messageText);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error sending private message: " + e.getMessage());
            return false;
        }
    }
    
    // Get private messages between two users
    public List<Message> getPrivateMessages(int userId1, int userId2, int limit) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT pm.*, u.username, u.full_name " +
                    "FROM private_messages pm " +
                    "JOIN users u ON pm.sender_id = u.user_id " +
                    "WHERE (pm.sender_id = ? AND pm.receiver_id = ?) " +
                    "   OR (pm.sender_id = ? AND pm.receiver_id = ?) " +
                    "ORDER BY pm.sent_at DESC " +
                    "LIMIT ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            stmt.setInt(5, limit);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message message = new Message();
                message.setMessageId(rs.getInt("message_id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setMessageText(rs.getString("message_text"));
                message.setSentAt(rs.getTimestamp("sent_at"));
                message.setSenderName(rs.getString("full_name"));
                message.setMessageType("private");
                messages.add(0, message); // Add to beginning to maintain chronological order
            }
        } catch (SQLException e) {
            System.err.println("Error getting private messages: " + e.getMessage());
        }
        return messages;
    }
    
    // Mark private messages as read
    public void markPrivateMessagesAsRead(int senderId, int receiverId) {
        String sql = "UPDATE private_messages SET is_read = true WHERE sender_id = ? AND receiver_id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error marking messages as read: " + e.getMessage());
        }
    }
    
    // Get unread message count for user
    public int getUnreadMessageCount(int userId) {
        String sql = "SELECT COUNT(*) FROM private_messages WHERE receiver_id = ? AND is_read = false";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting unread message count: " + e.getMessage());
        }
        return 0;
    }
    
    // Helper method to map ResultSet to Message object
    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setMessageId(rs.getInt("message_id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setRoomId(rs.getInt("room_id"));
        message.setMessageText(rs.getString("message_text"));
        message.setMessageType(rs.getString("message_type"));
        message.setSentAt(rs.getTimestamp("sent_at"));
        return message;
    }
}