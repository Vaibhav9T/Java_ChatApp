package backend.database;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chat_app";
    private static final String DB_BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "VT#Root2026"; // Change this to your MySQL password
    
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        connect();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void connect() {
        try {
            Properties props = new Properties();
            props.setProperty("user", DB_USER);
            props.setProperty("password", DB_PASSWORD);
            props.setProperty("useSSL", "false");
            props.setProperty("serverTimezone", "UTC");
            props.setProperty("allowPublicKeyRetrieval", "true");
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // First try to connect to the specific database
            try {
                connection = DriverManager.getConnection(DB_URL, props);
                System.out.println("Connected to MySQL database successfully!");
            } catch (SQLException e) {
                // If chat_app database doesn't exist, connect to MySQL server and create it
                System.out.println("Database 'chat_app' not found. Creating it...");
                connection = DriverManager.getConnection(DB_BASE_URL, props);
                
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS chat_app");
                stmt.executeUpdate("USE chat_app");
                stmt.close();
                
                System.out.println("Database 'chat_app' created and connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.err.println("Please check:");
            System.err.println("1. MySQL server is running");
            System.err.println("2. Username and password are correct");
            System.err.println("3. MySQL is accessible on localhost:3306");
        }
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            connect();
        }
        return connection;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    // Test database connection
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}