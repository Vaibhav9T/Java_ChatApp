package backend.dao;

import backend.database.DatabaseManager;
import backend.models.ChatRoom;
import backend.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomDAO {
    private DatabaseManager dbManager;
    
    public ChatRoomDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    // Create a new chat room
    public boolean createRoom(ChatRoom room) {
        String sql = "INSERT INTO chat_rooms (room_name, room_description, created_by) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, room.getRoomName());
            stmt.setString(2, room.getRoomDescription());
            stmt.setInt(3, room.getCreatedBy());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int roomId = keys.getInt(1);
                    // Add creator as admin of the room
                    addUserToRoom(roomId, room.getCreatedBy(), "admin");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating room: " + e.getMessage());
        }
        return false;
    }
    
    // Get all chat rooms
    public List<ChatRoom> getAllRooms() {
        List<ChatRoom> rooms = new ArrayList<>();
        String sql = "SELECT * FROM chat_rooms ORDER BY room_name";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                rooms.add(mapResultSetToChatRoom(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all rooms: " + e.getMessage());
        }
        return rooms;
    }
    
    // Get rooms for a specific user
    public List<ChatRoom> getRoomsForUser(int userId) {
        List<ChatRoom> rooms = new ArrayList<>();
        String sql = "SELECT cr.* FROM chat_rooms cr " +
                    "JOIN room_members rm ON cr.room_id = rm.room_id " +
                    "WHERE rm.user_id = ? " +
                    "ORDER BY cr.room_name";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rooms.add(mapResultSetToChatRoom(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting rooms for user: " + e.getMessage());
        }
        return rooms;
    }
    
    // Add user to room
    public boolean addUserToRoom(int roomId, int userId, String role) {
        String sql = "INSERT INTO room_members (room_id, user_id, role) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE role = VALUES(role)";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, userId);
            stmt.setString(3, role);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user to room: " + e.getMessage());
            return false;
        }
    }
    
    // Remove user from room
    public boolean removeUserFromRoom(int roomId, int userId) {
        String sql = "DELETE FROM room_members WHERE room_id = ? AND user_id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error removing user from room: " + e.getMessage());
            return false;
        }
    }
    
    // Get members of a room
    public List<User> getRoomMembers(int roomId) {
        List<User> members = new ArrayList<>();
        String sql = "SELECT u.*, rm.role FROM users u " +
                    "JOIN room_members rm ON u.user_id = rm.user_id " +
                    "WHERE rm.room_id = ? " +
                    "ORDER BY u.full_name";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setOnline(rs.getBoolean("is_online"));
                user.setLastSeen(rs.getTimestamp("last_seen"));
                members.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting room members: " + e.getMessage());
        }
        return members;
    }
    
    // Check if user is member of room
    public boolean isUserInRoom(int roomId, int userId) {
        String sql = "SELECT COUNT(*) FROM room_members WHERE room_id = ? AND user_id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user is in room: " + e.getMessage());
        }
        return false;
    }
    
    // Get room by ID
    public ChatRoom getRoomById(int roomId) {
        String sql = "SELECT * FROM chat_rooms WHERE room_id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToChatRoom(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting room by ID: " + e.getMessage());
        }
        return null;
    }
    
    // Helper method to map ResultSet to ChatRoom object
    private ChatRoom mapResultSetToChatRoom(ResultSet rs) throws SQLException {
        ChatRoom room = new ChatRoom();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomName(rs.getString("room_name"));
        room.setRoomDescription(rs.getString("room_description"));
        room.setCreatedBy(rs.getInt("created_by"));
        room.setCreatedAt(rs.getTimestamp("created_at"));
        return room;
    }
}