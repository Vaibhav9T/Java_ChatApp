import java.sql.*;
import backend.database.DatabaseManager;

public class SimpleDBSetup {
    public static void main(String[] args) {
        System.out.println("Testing MySQL Connection...");
        
        DatabaseManager dbManager = DatabaseManager.getInstance();
        Connection conn = dbManager.getConnection();
        
        if (conn != null) {
            System.out.println("✅ Database connection successful!");
            
            try {
                createTablesAndData(conn);
                System.out.println("✅ Database setup completed successfully!");
                
                // Test the setup
                testSetup(conn);
                
            } catch (SQLException e) {
                System.err.println("❌ Error setting up database: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("❌ Failed to connect to database!");
            System.out.println("\n🔧 Troubleshooting steps:");
            System.out.println("1. Make sure MySQL server is running");
            System.out.println("2. Check if username/password are correct in DatabaseManager.java");
            System.out.println("3. Verify MySQL is listening on port 3306");
        }
    }
    
    private static void createTablesAndData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Create database
        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS chat_app");
        stmt.executeUpdate("USE chat_app");
        System.out.println("✅ Database 'chat_app' ready");
        
        // Users table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
            "user_id INT AUTO_INCREMENT PRIMARY KEY," +
            "username VARCHAR(50) UNIQUE NOT NULL," +
            "password VARCHAR(255) NOT NULL," +
            "email VARCHAR(100) UNIQUE NOT NULL," +
            "full_name VARCHAR(100) NOT NULL," +
            "is_online BOOLEAN DEFAULT FALSE," +
            "last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")");
        System.out.println("✅ Users table ready");
        
        // Chat rooms table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS chat_rooms (" +
            "room_id INT AUTO_INCREMENT PRIMARY KEY," +
            "room_name VARCHAR(100) NOT NULL," +
            "room_description TEXT," +
            "created_by INT," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")");
        System.out.println("✅ Chat rooms table ready");
        
        // Messages table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS messages (" +
            "message_id INT AUTO_INCREMENT PRIMARY KEY," +
            "sender_id INT NOT NULL," +
            "room_id INT NOT NULL," +
            "message_text TEXT NOT NULL," +
            "message_type ENUM('text', 'image', 'file') DEFAULT 'text'," +
            "sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")");
        System.out.println("✅ Messages table ready");
        
        // Room members table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS room_members (" +
            "room_id INT," +
            "user_id INT," +
            "joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "role ENUM('admin', 'member') DEFAULT 'member'," +
            "PRIMARY KEY (room_id, user_id)" +
            ")");
        System.out.println("✅ Room members table ready");
        
        // Check if sample data exists
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
        rs.next();
        int userCount = rs.getInt(1);
        rs.close();
        
        if (userCount == 0) {
            // Insert sample users
            stmt.executeUpdate("INSERT INTO users (username, password, email, full_name) VALUES " +
                "('admin', 'password123', 'admin@chatapp.com', 'Administrator')," +
                "('john_doe', 'password123', 'john@example.com', 'John Doe')," +
                "('jane_smith', 'password123', 'jane@example.com', 'Jane Smith')");
            System.out.println("✅ Sample users created");
            
            // Insert default chat room
            stmt.executeUpdate("INSERT INTO chat_rooms (room_name, room_description) VALUES " +
                "('General', 'General discussion room for all users')");
            System.out.println("✅ Default chat room created");
            
            // Add users to general room
            stmt.executeUpdate("INSERT INTO room_members (room_id, user_id, role) VALUES " +
                "(1, 1, 'admin'), (1, 2, 'member'), (1, 3, 'member')");
            System.out.println("✅ Users added to general room");
        } else {
            System.out.println("✅ Sample data already exists");
        }
        
        stmt.close();
    }
    
    private static void testSetup(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Test users
        ResultSet rs = stmt.executeQuery("SELECT username, full_name FROM users");
        System.out.println("\n📋 Available users:");
        while (rs.next()) {
            System.out.println("  - " + rs.getString("username") + " (" + rs.getString("full_name") + ")");
        }
        rs.close();
        
        // Test rooms
        rs = stmt.executeQuery("SELECT room_name, room_description FROM chat_rooms");
        System.out.println("\n💬 Available chat rooms:");
        while (rs.next()) {
            System.out.println("  - " + rs.getString("room_name") + ": " + rs.getString("room_description"));
        }
        rs.close();
        
        stmt.close();
        
        System.out.println("\n🎉 Your database is ready! You can now:");
        System.out.println("   • Login with username: admin, password: password123");
        System.out.println("   • Or use: john_doe / jane_smith with password: password123");
        System.out.println("   • Run SimpleChatApp to start chatting!");
    }
}