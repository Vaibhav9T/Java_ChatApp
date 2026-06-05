# Java Chat Application Setup Guide

## Quick Start Guide

### Prerequisites
1. **Java Development Kit (JDK 8 or higher)**
2. **MySQL Server (5.7 or higher)**
3. **MySQL Connector/J (included in Backend/lib/)**

### Step 1: Database Setup

1. Start your MySQL server
2. Open MySQL command line or MySQL Workbench
3. Run the following commands to create the database:

```sql
CREATE DATABASE IF NOT EXISTS chat_app;
USE chat_app;

-- Users table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    is_online BOOLEAN DEFAULT FALSE,
    last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Chat rooms table
CREATE TABLE chat_rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    room_description TEXT,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Messages table
CREATE TABLE messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    room_id INT NOT NULL,
    message_text TEXT NOT NULL,
    message_type ENUM('text', 'image', 'file') DEFAULT 'text',
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES chat_rooms(room_id) ON DELETE CASCADE
);

-- Room members table
CREATE TABLE room_members (
    room_id INT,
    user_id INT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role ENUM('admin', 'member') DEFAULT 'member',
    PRIMARY KEY (room_id, user_id),
    FOREIGN KEY (room_id) REFERENCES chat_rooms(room_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Private messages table
CREATE TABLE private_messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message_text TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Insert default chat room
INSERT INTO chat_rooms (room_name, room_description) VALUES 
('General', 'General discussion room for all users');

-- Insert sample users (password is 'password123')
INSERT INTO users (username, password, email, full_name) VALUES 
('admin', 'password123', 'admin@chatapp.com', 'Administrator'),
('john_doe', 'password123', 'john@example.com', 'John Doe'),
('jane_smith', 'password123', 'jane@example.com', 'Jane Smith');

-- Add users to general room
INSERT INTO room_members (room_id, user_id, role) VALUES 
(1, 1, 'admin'),
(1, 2, 'member'),
(1, 3, 'member');
```

### Step 2: Configure Database Connection

1. Open `Backend/src/backend/database/DatabaseManager.java`
2. Update the database connection settings:
   ```java
   private static final String DB_URL = "jdbc:mysql://localhost:3306/chat_app";
   private static final String DB_USER = "root";
   private static final String DB_PASSWORD = "YOUR_MYSQL_PASSWORD"; // Change this!
   ```

### Step 3: Compile the Application

Run the compilation script:
```bash
compile.bat
```

### Step 4: Run the Application

For the simple version:
```bash
run_simple.bat
```

For the full version (if available):
```bash
run.bat
```

## Default Login Credentials

| Username | Password |
|----------|----------|
| admin | password123 |
| john_doe | password123 |
| jane_smith | password123 |

## Features

### Current Features (Simple Version)
- ✅ User authentication (login)
- ✅ Basic chat messaging
- ✅ Message history
- ✅ Database integration
- ✅ Real-time message display

### Planned Features (Full Version)
- 🔄 User registration
- 🔄 Multiple chat rooms
- 🔄 Online user display
- 🔄 Room creation
- 🔄 Enhanced UI

## Troubleshooting

### Common Issues:

**"Database connection failed"**
- Check if MySQL server is running
- Verify database credentials in DatabaseManager.java
- Ensure chat_app database exists

**"MySQL JDBC Driver not found"**
- Verify mysql-connector-j-9.4.0.jar is in Backend/lib/
- Check classpath in run script

**"Compilation errors"**
- Ensure JDK is installed and in PATH
- Check that all source files are present

**"Login fails"**
- Check if database is set up correctly
- Verify sample users were inserted
- Check database connection

## Project Structure

```
Java Mysql project/
├── Backend/
│   ├── lib/
│   │   └── mysql-connector-j-9.4.0.jar
│   └── src/
│       └── backend/
│           ├── database/
│           ├── models/
│           └── dao/
├── Frontend/
│   └── src/
│       ├── SimpleChatApp.java (working version)
│       └── RegisterDialog.java
├── database/
│   └── chat_database.sql
├── build/ (created after compilation)
├── compile.bat
├── run_simple.bat
└── README.md
```

## Next Steps

1. Test the simple chat application
2. Add more users through direct database insertion
3. Enhance the UI
4. Add more features like room selection
5. Implement user registration

## Support

If you encounter any issues:
1. Check that MySQL is running
2. Verify database setup
3. Ensure all files are compiled
4. Check Java classpath configuration

Happy chatting! 🎉