import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.models.User;
import backend.dao.UserDAO;

public class RegisterDialog extends JDialog implements ActionListener {
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField fullNameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    private UserDAO userDAO;
    private boolean registrationSuccessful = false;
    
    public RegisterDialog(Frame parent) {
        super(parent, "Register New User", true);
        userDAO = new UserDAO();
        initializeGUI();
    }
    
    private void initializeGUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setSize(400, 350);
        setLocationRelativeTo(getParent());
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 20, 0);
        add(titleLabel, gbc);
        
        // Reset gridwidth
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Email:"), gbc);
        emailField = new JTextField(15);
        gbc.gridx = 1;
        add(emailField, gbc);
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Full Name:"), gbc);
        fullNameField = new JTextField(15);
        gbc.gridx = 1;
        add(fullNameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Confirm Password:"), gbc);
        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        buttonPanel.add(registerButton);
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        add(buttonPanel, gbc);
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        gbc.gridy = 7;
        add(statusLabel, gbc);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            handleRegistration();
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }
    
    private void handleRegistration() {
        // Get form data
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        
        // Validate input
        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("All fields are required");
            return;
        }
        
        if (username.length() < 3) {
            statusLabel.setText("Username must be at least 3 characters");
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            statusLabel.setText("Please enter a valid email address");
            return;
        }
        
        if (password.length() < 6) {
            statusLabel.setText("Password must be at least 6 characters");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            return;
        }
        
        // Check if username already exists
        if (userDAO.usernameExists(username)) {
            statusLabel.setText("Username already exists");
            return;
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            statusLabel.setText("Email already registered");
            return;
        }
        
        // Create new user
        User newUser = new User(username, password, email, fullName);
        
        if (userDAO.registerUser(newUser)) {
            registrationSuccessful = true;
            JOptionPane.showMessageDialog(this, 
                "Registration successful! You can now login with your credentials.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            statusLabel.setText("Registration failed. Please try again.");
        }
    }
    
    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }
}