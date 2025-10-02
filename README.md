# 💬 WhatsApp-Style Java Chat Application

A modern, real-time chat application built with Java Swing and MySQL, featuring a WhatsApp-inspired dark theme design with responsive UI components.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-orange?style=for-the-badge)

## 🌟 Features

### 🎨 Modern UI Design
- **WhatsApp-Inspired Dark Theme**: Authentic green color scheme (#25D366)
- **Responsive Design**: Adapts to different screen sizes
- **High Contrast Interface**: Enhanced button visibility and readability
- **Smooth Animations**: Hover effects and interactive feedback
- **Professional Layout**: Modern typography and spacing

### 💬 Chat Functionality
- **Real-time Messaging**: Instant message delivery and updates
- **Multi-user Support**: Multiple users can join and chat simultaneously
- **Online Status Tracking**: See who's currently online
- **Auto-refresh**: Messages update every 3 seconds automatically
- **Chat Rooms**: Support for different chat rooms

### 🔐 User Management
- **User Authentication**: Secure login with username/password
- **Demo Accounts**: Pre-configured test accounts for easy testing
- **Session Management**: Proper login/logout functionality
- **User Status**: Online/offline status tracking

## 🚀 Getting Started

### Prerequisites
- **Java 8 or higher**
- **MySQL Server**
- **MySQL Connector/J** (included in the project)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/whatsapp-java-chat.git
   cd whatsapp-java-chat
   ```

2. **Database Setup**
   - Ensure MySQL server is running
   - The application will automatically create the database and tables on first run
   - Default database credentials: `root` with password `VT#Root2026`

3. **Compile the Application**
   ```bash
   javac -cp "Backend/lib/mysql-connector-j-9.4.0.jar;Backend/src;Frontend/src" Frontend/src/*.java Backend/src/backend/models/*.java Backend/src/backend/dao/*.java
   ```

4. **Run the Application**
   ```bash
   java -cp "Backend/lib/mysql-connector-j-9.4.0.jar;Backend/src;Frontend/src" SimpleChatApp
   ```

### 🧪 Demo Accounts
Use these pre-configured accounts to test the application:
- **Username**: `admin` | **Password**: `password123`
- **Username**: `john_doe` | **Password**: `password123`
- **Username**: `jane_smith` | **Password**: `password123`

## 📁 Project Structure

```
├── Backend/
│   ├── lib/
│   │   └── mysql-connector-j-9.4.0.jar
│   └── src/
│       └── backend/
│           ├── dao/           # Data Access Objects
│           │   ├── UserDAO.java
│           │   ├── MessageDAO.java
│           │   └── ChatRoomDAO.java
│           └── models/        # Data Models
│               ├── User.java
│               ├── Message.java
│               └── ChatRoom.java
├── Frontend/
│   └── src/
│       └── SimpleChatApp.java # Main GUI Application
├── database/
│   └── DatabaseManager.java  # Database connection manager
└── README.md
```

## 🛠️ Technical Implementation

### Architecture
- **MVC Pattern**: Model-View-Controller architecture
- **DAO Pattern**: Data Access Object for database operations
- **Swing GUI**: Java Swing for user interface
- **JDBC**: Database connectivity with MySQL

### Key Components
1. **SimpleChatApp.java**: Main GUI application with WhatsApp-style interface
2. **DatabaseManager.java**: Handles MySQL connections and database creation
3. **DAO Classes**: Handle all database operations (UserDAO, MessageDAO, ChatRoomDAO)
4. **Model Classes**: Data structures for User, Message, and ChatRoom entities

### Database Schema
- **users**: User information and authentication
- **chat_rooms**: Chat room management
- **messages**: Message storage and retrieval
- **room_members**: User-room relationship mapping
- **private_messages**: Direct messaging support

## 🎨 UI Features

### Login Screen
- Clean, modern login interface
- WhatsApp green branding
- Demo account information
- Responsive design

### Chat Interface
- Dark theme chat area
- Online users sidebar
- Message input with focus indicators
- Real-time message updates
- Professional message formatting

## 🔧 Customization

### Color Scheme
The application uses a WhatsApp-inspired color palette:
- **Primary Green**: `#25D366`
- **Dark Background**: `#111B21`
- **Chat Background**: `#0B141A`
- **Input Background**: `#2A3942`

### Database Configuration
Modify database settings in `DatabaseManager.java`:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/";
private static final String DB_NAME = "chat_app_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🐛 Troubleshooting

### Common Issues:

1. **"Database connection failed"**
   - Check if MySQL server is running
   - Verify database credentials in DatabaseManager.java
   - Ensure MySQL service is started

2. **Blank UI Screen**
   - Application may need window resize
   - Check terminal for error messages
   - Verify all dependencies are loaded

3. **Compilation errors**
   - Ensure JDK is properly installed
   - Check classpath configuration
   - Verify all source files are present

## 🔮 Future Enhancements

- [ ] File sharing capabilities
- [ ] Emoji support
- [ ] Private messaging
- [ ] Message notifications
- [ ] User profiles with avatars
- [ ] Message encryption
- [ ] Voice/video call integration
- [ ] Mobile app version

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- WhatsApp for design inspiration
- MySQL Community for excellent database support
- Java Swing community for UI guidance

---

⭐ **Star this repository if you found it helpful!**