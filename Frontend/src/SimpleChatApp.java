import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import backend.models.*;
import backend.dao.*;

public class SimpleChatApp extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JLabel statusLabel;
    private JLabel welcomeLabel;
    private JLabel onlineUsersLabel;
    private JList<User> onlineUsersList;
    
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private ChatRoomDAO chatRoomDAO;
    private User currentUser;
    private ChatRoom currentRoom;
    private Timer refreshTimer;
    
    private JPanel loginPanel;
    private JPanel chatPanel;
    private JPanel mainContainer;
    
    // WhatsApp-inspired color scheme
    private static final Color PRIMARY_COLOR = new Color(37, 211, 102);      // WhatsApp green
    private static final Color PRIMARY_DARK = new Color(0, 150, 136);        // Darker green
    private static final Color SECONDARY_COLOR = new Color(17, 27, 33);      // WhatsApp dark background
    private static final Color CHAT_BG = new Color(11, 20, 26);              // Chat background
    private static final Color MESSAGE_SENT = new Color(0, 95, 82);          // Sent message bubble
    private static final Color MESSAGE_RECEIVED = new Color(32, 44, 51);     // Received message bubble
    private static final Color ACCENT_COLOR = new Color(37, 211, 102);       // Green accent
    private static final Color DANGER_COLOR = new Color(244, 67, 54);        // Red
    private static final Color TEXT_COLOR = new Color(241, 241, 242);        // Light text
    private static final Color TEXT_SECONDARY = new Color(134, 150, 160);    // Secondary text
    private static final Color INPUT_BG = new Color(32, 44, 51);             // Input background
    private static final Color BORDER_COLOR = new Color(42, 57, 66);         // Borders
    
    public SimpleChatApp() {
        userDAO = new UserDAO();
        messageDAO = new MessageDAO();
        chatRoomDAO = new ChatRoomDAO();
        
        setupGUI();
        showLoginPanel();
    }
    
    private void setupGUI() {
        setTitle("💬 WhatsApp Style Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 500));
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set dark theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Override some UI defaults for dark theme
            UIManager.put("Panel.background", SECONDARY_COLOR);
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("TextField.background", INPUT_BG);
            UIManager.put("TextField.foreground", TEXT_COLOR);
            UIManager.put("TextArea.background", CHAT_BG);
            UIManager.put("TextArea.foreground", TEXT_COLOR);
        } catch (Exception e) {
            // Fallback to default
        }
        
        // Enable responsive behavior
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> adjustLayoutForSize());
            }
        });
        
        // Create main container with CardLayout for switching between panels
        mainContainer = new JPanel(new CardLayout());
        mainContainer.setBackground(SECONDARY_COLOR);
        
        createLoginPanel();
        createChatPanel();
        
        mainContainer.add(loginPanel, "login");
        mainContainer.add(chatPanel, "chat");
        
        add(mainContainer);
        
        // Show login panel initially
        CardLayout cl = (CardLayout) mainContainer.getLayout();  
        cl.show(mainContainer, "login");
        
        setVisible(true);
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());
        loginPanel.setBackground(SECONDARY_COLOR);
        
        // Create main login card
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(new Color(32, 44, 51));
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        loginCard.setMaximumSize(new Dimension(450, 600));
        
        // Title
        JLabel titleLabel = new JLabel("💬 WhatsApp Style Chat");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(titleLabel);
        
        loginCard.add(Box.createVerticalStrut(15));
        
        JLabel subtitleLabel = new JLabel("Connect and communicate instantly");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(subtitleLabel);
        
        loginCard.add(Box.createVerticalStrut(30));
        
        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usernameLabel.setForeground(TEXT_COLOR);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginCard.add(usernameLabel);
        
        loginCard.add(Box.createVerticalStrut(8));
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        usernameField.setBackground(INPUT_BG);
        usernameField.setForeground(TEXT_COLOR);
        usernameField.setCaretColor(TEXT_COLOR);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginCard.add(usernameField);
        
        loginCard.add(Box.createVerticalStrut(20));
        
        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginCard.add(passwordLabel);
        
        loginCard.add(Box.createVerticalStrut(5));
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.addActionListener(this);
        loginCard.add(passwordField);
        
        loginCard.add(Box.createVerticalStrut(30));
        
        // Login button
        loginButton = createStyledButton("🔐 Sign In", PRIMARY_COLOR, Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(this);
        loginCard.add(loginButton);
        
        loginCard.add(Box.createVerticalStrut(20));
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(DANGER_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(statusLabel);
        
        loginCard.add(Box.createVerticalStrut(20));
        
        // Demo credentials info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(176, 216, 255), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel demoLabel = new JLabel("💡 Demo Accounts:");
        demoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        demoLabel.setForeground(PRIMARY_COLOR);
        demoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(demoLabel);
        
        infoPanel.add(Box.createVerticalStrut(8));
        
        String[] demoAccounts = {"admin / password123", "john_doe / password123", "jane_smith / password123"};
        for (String account : demoAccounts) {
            JLabel accountLabel = new JLabel("• " + account);
            accountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            accountLabel.setForeground(TEXT_COLOR);
            accountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(accountLabel);
        }
        
        loginCard.add(infoPanel);
        
        // Center the login card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(SECONDARY_COLOR);
        centerPanel.add(loginCard);
        
        loginPanel.add(centerPanel, BorderLayout.CENTER);
    }
    
    private void createChatPanel() {
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(SECONDARY_COLOR);
        
        // Top header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        welcomeLabel = new JLabel("💬 General Chat");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfoPanel.setBackground(PRIMARY_COLOR);
        
        JButton logoutButton = createStyledButton("🚪 Logout", DANGER_COLOR, Color.WHITE);
        logoutButton.addActionListener(this);
        userInfoPanel.add(logoutButton);
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(userInfoPanel, BorderLayout.EAST);
        
        // Main content area
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Chat area with modern styling
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatArea.setBackground(CHAT_BG);
        chatArea.setForeground(TEXT_COLOR);
        chatArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Right sidebar for online users
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        
        onlineUsersLabel = new JLabel("👥 Online Users");
        onlineUsersLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        onlineUsersLabel.setForeground(TEXT_COLOR);
        
        onlineUsersList = new JList<User>();
        onlineUsersList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        onlineUsersList.setBackground(Color.WHITE);
        onlineUsersList.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JScrollPane usersScrollPane = new JScrollPane(onlineUsersList);
        usersScrollPane.setBorder(null);
        usersScrollPane.setPreferredSize(new Dimension(180, 150));
        
        sidebarPanel.add(onlineUsersLabel, BorderLayout.NORTH);
        sidebarPanel.add(usersScrollPane, BorderLayout.CENTER);
        
        // Message input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageField.setBackground(INPUT_BG);
        messageField.setForeground(TEXT_COLOR);
        messageField.setCaretColor(TEXT_COLOR);
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        messageField.setPreferredSize(new Dimension(0, 50));
        messageField.addActionListener(this);
        
        // Add placeholder-like behavior
        messageField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                messageField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                messageField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 2),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
        });
        
        sendButton = createStyledButton("📤", ACCENT_COLOR, Color.WHITE);
        sendButton.addActionListener(this);
        sendButton.setPreferredSize(new Dimension(60, 50));
        sendButton.setToolTipText("Send message");
        
        JPanel messageInputPanel = new JPanel(new BorderLayout());
        messageInputPanel.setBackground(SECONDARY_COLOR);
        messageInputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        messageInputPanel.add(messageField, BorderLayout.CENTER);
        messageInputPanel.add(Box.createHorizontalStrut(10), BorderLayout.AFTER_LINE_ENDS);
        
        inputPanel.add(messageInputPanel, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Layout assembly
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(sidebarPanel, BorderLayout.EAST);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        chatPanel.add(headerPanel, BorderLayout.NORTH);
        chatPanel.add(mainPanel, BorderLayout.CENTER);
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 2),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(true);
        
        // Ensure text is always readable with high contrast
        if (textColor.equals(Color.WHITE)) {
            // For white text, ensure background is dark enough
            if (getBrightness(bgColor) > 128) {
                button.setForeground(Color.BLACK);
            }
        }
        
        // Add hover and press effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalBg = bgColor;
            private Color hoverBg = new Color(
                Math.max(0, Math.min(255, bgColor.getRed() + 30)),
                Math.max(0, Math.min(255, bgColor.getGreen() + 30)),
                Math.max(0, Math.min(255, bgColor.getBlue() + 30))
            );
            
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverBg);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(hoverBg.darker(), 3),
                    BorderFactory.createEmptyBorder(14, 24, 14, 24)
                ));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalBg);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(originalBg.darker(), 2),
                    BorderFactory.createEmptyBorder(15, 25, 15, 25)
                ));
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(originalBg.darker());
            }
            
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (button.contains(evt.getPoint())) {
                    button.setBackground(hoverBg);
                } else {
                    button.setBackground(originalBg);
                }
            }
        });
        
        return button;
    }
    
    // Helper method to calculate brightness of a color
    private int getBrightness(Color color) {
        return (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
    }
    
    // Method to adjust layout based on window size (responsive design)
    private void adjustLayoutForSize() {
        int width = getWidth();
        int height = getHeight();
        
        // Adjust font sizes based on window size
        Font headerFont = new Font("Segoe UI", Font.BOLD, width < 600 ? 16 : 18);
        
        // Update components if they exist
        if (welcomeLabel != null) {
            welcomeLabel.setFont(headerFont);
        }
        
        // Adjust message input height based on window height
        if (messageField != null) {
            int inputHeight = height < 500 ? 35 : 45;
            messageField.setPreferredSize(new Dimension(0, inputHeight));
            messageField.revalidate();
        }
        
        // Adjust login panel responsiveness
        if (loginPanel != null) {
            loginPanel.revalidate();
            loginPanel.repaint();
        }
    }
    
    private void showLoginPanel() {
        CardLayout cl = (CardLayout) mainContainer.getLayout();
        cl.show(mainContainer, "login");
    }
    
    private void showChatPanel() {
        CardLayout cl = (CardLayout) mainContainer.getLayout();
        cl.show(mainContainer, "chat");
        
        // Load default room
        List<ChatRoom> rooms = chatRoomDAO.getAllRooms();
        if (!rooms.isEmpty()) {
            currentRoom = (ChatRoom) rooms.get(0);
            loadMessages();
            loadOnlineUsers();
            startAutoRefresh();
        }
        
        // Update welcome message with user info
        if (currentUser != null && welcomeLabel != null) {
            welcomeLabel.setText("💬 Welcome, " + currentUser.getFullName());
        }
    }
    
    private void startAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
        
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (currentRoom != null) {
                            loadMessages();
                            loadOnlineUsers();
                        }
                    }
                });
            }
        }, 3000, 3000); // Refresh every 3 seconds
    }
    
    private void loadOnlineUsers() {
        if (onlineUsersList != null) {
            List<User> users = userDAO.getOnlineUsers();
            DefaultListModel<User> model = new DefaultListModel<User>();
            for (int i = 0; i < users.size(); i++) {
                User user = (User) users.get(i);
                model.addElement(user);
            }
            onlineUsersList.setModel(model);
            
            // Update online users label
            if (onlineUsersLabel != null) {
                onlineUsersLabel.setText("👥 Online (" + users.size() + ")");
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            handleLogin();
        } else if (e.getSource() == sendButton || e.getSource() == messageField) {
            handleSendMessage();
        } else if (e.getActionCommand().equals("Logout")) {
            handleLogout();
        }
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password");
            return;
        }
        
        User user = userDAO.loginUser(username, password);
        if (user != null) {
            currentUser = user;
            showChatPanel();
        } else {
            statusLabel.setText("Invalid credentials");
        }
    }
    
    private void handleSendMessage() {
        if (currentUser == null || currentRoom == null) return;
        
        String text = messageField.getText().trim();
        if (text.isEmpty()) return;
        
        Message message = new Message(currentUser.getUserId(), currentRoom.getRoomId(), text);
        if (messageDAO.sendMessage(message)) {
            messageField.setText("");
            loadMessages();
        }
    }
    
    private void handleLogout() {
        if (currentUser != null) {
            userDAO.updateUserOnlineStatus(currentUser.getUserId(), false);
        }
        
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
        
        currentUser = null;
        currentRoom = null;
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        showLoginPanel();
    }
    
    private void loadMessages() {
        if (currentRoom == null) return;
        
        List<Message> messages = messageDAO.getRecentMessagesForRoom(currentRoom.getRoomId());
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < messages.size(); i++) {
            Message msg = (Message) messages.get(i);
            // Format messages nicely
            String timeStr = msg.getSentAt() != null ? 
                msg.getSentAt().toString().substring(11, 16) : "now";
            String senderName = msg.getSenderName() != null ? 
                msg.getSenderName() : "User" + msg.getSenderId();
            
            sb.append("┌─ ").append(senderName).append(" • ").append(timeStr).append("\n");
            sb.append("│  ").append(msg.getMessageText()).append("\n");
            sb.append("└─────────────────────────────────────────\n\n");
        }
        
        if (messages.isEmpty()) {
            sb.append("💬 Welcome to the chat! Start a conversation...\n");
        }
        
        chatArea.setText(sb.toString());
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SimpleChatApp().setVisible(true);
            }
        });
    }
}