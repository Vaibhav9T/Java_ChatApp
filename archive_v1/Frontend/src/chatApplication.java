import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// Import backend classes
import backend.models.*;
import backend.dao.*;

public class chatApplication extends JFrame implements ActionListener {
    // UI Components
    private JPanel loginPanel;
    private JPanel chatPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    // Chat components
    private JList<ChatRoom> roomList;
    private JList<User> userList;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton logoutButton;
    private JButton createRoomButton;
    private JLabel statusLabel;
    
    // Backend components
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private ChatRoomDAO chatRoomDAO;
    private User currentUser;
    private ChatRoom currentRoom;
    private Timer refreshTimer;
    
    public chatApplication() {
        initializeBackend();
        initializeGUI();
        showLoginPanel();
    }
    
    private void initializeBackend() {
        userDAO = new UserDAO();
        messageDAO = new MessageDAO();
        chatRoomDAO = new ChatRoomDAO();
    }
    
    private void initializeGUI() {
        setTitle("Modern Chat Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default
        }
        
        createLoginPanel();
        createChatPanel();
        
        // Start with login panel
        add(loginPanel);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        JLabel titleLabel = new JLabel("Chat Application");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 30, 0);
        loginPanel.add(titleLabel, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("Username:"), gbc);
        
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);
        
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        buttonPanel.add(loginButton);
        
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        loginPanel.add(buttonPanel, gbc);
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        gbc.gridy = 4;
        loginPanel.add(statusLabel, gbc);
        
        // Enter key listeners
        usernameField.addActionListener(this);
        passwordField.addActionListener(this);
    }
    
    private void createChatPanel() {
        chatPanel = new JPanel(new BorderLayout());
        
        // Left panel for rooms and users
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Rooms & Users"));
        
        // Room list
        roomList = new JList<>();
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ChatRoom selected = roomList.getSelectedValue();
                if (selected != null) {
                    joinRoom(selected);
                }
            }
        });
        
        JScrollPane roomScrollPane = new JScrollPane(roomList);
        roomScrollPane.setPreferredSize(new Dimension(180, 200));
        roomScrollPane.setBorder(BorderFactory.createTitledBorder("Chat Rooms"));
        
        // User list
        userList = new JList<>();
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(180, 200));
        userScrollPane.setBorder(BorderFactory.createTitledBorder("Online Users"));
        
        // Create room button
        createRoomButton = new JButton("Create Room");
        createRoomButton.addActionListener(this);
        createRoomButton.setBackground(new Color(70, 130, 180));
        createRoomButton.setForeground(Color.WHITE);
        createRoomButton.setFont(new Font("Arial", Font.BOLD, 12));
        createRoomButton.setOpaque(true);
        createRoomButton.setBorderPainted(false);
        
        leftPanel.add(roomScrollPane, BorderLayout.NORTH);
        leftPanel.add(userScrollPane, BorderLayout.CENTER);
        leftPanel.add(createRoomButton, BorderLayout.SOUTH);
        
        // Center panel for chat
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        chatArea.setBackground(Color.WHITE);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createTitledBorder("Chat Messages"));
        
        // Message input panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.addActionListener(this);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setBackground(new Color(34, 139, 34));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 12));
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false);
        
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        
        centerPanel.add(chatScrollPane, BorderLayout.CENTER);
        centerPanel.add(messagePanel, BorderLayout.SOUTH);
        
        // Top panel with logout button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        topPanel.add(logoutButton);
        
        chatPanel.add(topPanel, BorderLayout.NORTH);
        chatPanel.add(leftPanel, BorderLayout.WEST);
        chatPanel.add(centerPanel, BorderLayout.CENTER);
    }
    
    private void showLoginPanel() {
        getContentPane().removeAll();
        add(loginPanel);
        revalidate();
        repaint();
    }
    
    private void showChatPanel() {
        getContentPane().removeAll();
        add(chatPanel);
        revalidate();
        repaint();
        
        // Load initial data
        loadRooms();
        loadOnlineUsers();
        
        // Start refresh timer
        startRefreshTimer();
    }
    
    private void startRefreshTimer() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
        
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    loadOnlineUsers();
                    if (currentRoom != null) {
                        loadMessages();
                    }
                });
            }
        }, 5000, 5000); // Refresh every 5 seconds
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == loginButton || source == usernameField || source == passwordField) {
            handleLogin();
        } else if (source == registerButton) {
            handleRegister();
        } else if (source == sendButton || source == messageField) {
            handleSendMessage();
        } else if (source == logoutButton) {
            handleLogout();
        } else if (source == createRoomButton) {
            handleCreateRoom();
        }
    }
    
    private void handleLogin() {
        String rawUsername = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        // Clear placeholder text if present
        String username = rawUsername.equals("Jiara Martins") ? "" : rawUsername;
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("❌ Please enter both username and password");
            return;
        }
        
        // Show loading state
        statusLabel.setText("🔄 Signing in...");
        loginButton.setText("Signing in...");
        loginButton.setEnabled(false);
        
        // Use SwingWorker for better UX (though not necessary for local DB)
        final String finalUsername = username;
        final String finalPassword = password;
        
        SwingUtilities.invokeLater(() -> {
            User user = userDAO.loginUser(finalUsername, finalPassword);
            if (user != null) {
                currentUser = user;
                statusLabel.setText("✅ Login successful!");
                showChatPanel();
            } else {
                statusLabel.setText("❌ Invalid username or password");
                loginButton.setText("Log in");
                loginButton.setEnabled(true);
            }
        });
    }
    
    private void handleRegister() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.setVisible(true);
    }
    
    private void handleSendMessage() {
        if (currentUser == null || currentRoom == null) {
            return;
        }
        
        String messageText = messageField.getText().trim();
        if (messageText.isEmpty()) {
            return;
        }
        
        Message message = new Message(currentUser.getUserId(), currentRoom.getRoomId(), messageText);
        if (messageDAO.sendMessage(message)) {
            messageField.setText("");
            loadMessages();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to send message", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleLogout() {
        if (currentUser != null) {
            userDAO.updateUserOnlineStatus(currentUser.getUserId(), false);
        }
        
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
        
        currentUser = null;
        currentRoom = null;
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        
        showLoginPanel();
    }
    
    private void handleCreateRoom() {
        String roomName = JOptionPane.showInputDialog(this, "Enter room name:");
        if (roomName != null && !roomName.trim().isEmpty()) {
            String roomDescription = JOptionPane.showInputDialog(this, "Enter room description (optional):");
            if (roomDescription == null) roomDescription = "";
            
            ChatRoom room = new ChatRoom(roomName.trim(), roomDescription.trim(), currentUser.getUserId());
            if (chatRoomDAO.createRoom(room)) {
                loadRooms();
                JOptionPane.showMessageDialog(this, "Room created successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create room", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void joinRoom(ChatRoom room) {
        currentRoom = room;
        
        // Add user to room if not already a member
        if (!chatRoomDAO.isUserInRoom(room.getRoomId(), currentUser.getUserId())) {
            chatRoomDAO.addUserToRoom(room.getRoomId(), currentUser.getUserId(), "member");
        }
        
        loadMessages();
        setTitle("Java Chat Application - " + room.getRoomName());
    }
    
    private void loadRooms() {
        if (currentUser == null) return;
        
        List<ChatRoom> rooms = chatRoomDAO.getAllRooms();
        DefaultListModel<ChatRoom> model = new DefaultListModel<>();
        for (ChatRoom room : rooms) {
            model.addElement(room);
        }
        roomList.setModel(model);
        
        // Select first room by default
        if (!rooms.isEmpty() && currentRoom == null) {
            roomList.setSelectedIndex(0);
            joinRoom(rooms.get(0));
        }
    }
    
    private void loadOnlineUsers() {
        List<User> users = userDAO.getOnlineUsers();
        DefaultListModel<User> model = new DefaultListModel<>();
        for (User user : users) {
            model.addElement(user);
        }
        userList.setModel(model);
    }
    
    private void loadMessages() {
        if (currentRoom == null) return;
        
        List<Message> messages = messageDAO.getRecentMessagesForRoom(currentRoom.getRoomId());
        StringBuilder sb = new StringBuilder();
        
        for (Message message : messages) {
            sb.append(message.toString()).append("\n");
        }
        
        chatArea.setText(sb.toString());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Add MySQL driver JAR to classpath
        addJarToClasspath();
        
        SwingUtilities.invokeLater(() -> {
            new chatApplication().setVisible(true);
        });
    }
    
    private static void addJarToClasspath() {
        try {
            File jarFile = new File("Backend/lib/mysql-connector-j-9.4.0.jar");
            if (jarFile.exists()) {
                System.setProperty("java.class.path", 
                    System.getProperty("java.class.path") + File.pathSeparator + jarFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not add MySQL driver to classpath: " + e.getMessage());
        }
    }
}
