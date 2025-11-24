package view;

import com.formdev.flatlaf.FlatClientProperties; // Import this for round corners
import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginView() {
        // 1. Window Setup
        setTitle("LMS Portal");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout()); // Centers everything perfectly

        // 2. Create a container panel for the form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // Padding inside the card
        formPanel.setBackground(new Color(60, 63, 65)); // Slightly lighter than background
        // Optional: Make the panel itself rounded (if supported by your OS/Look)
        formPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 20");

        // 3. Modern Fonts
        Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // 4. Title
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(220, 220, 220));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("Please sign in to continue");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitleLabel.setForeground(new Color(150, 150, 150));
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 5. Email Field (Sized & Rounded)
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(labelFont);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        emailField.setFont(fieldFont);
        emailField.setPreferredSize(new Dimension(300, 40)); // FIX: Fixed height (40px)
        emailField.setMaximumSize(new Dimension(300, 40));   // Prevent stretching
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "student@lms.com");
        emailField.putClientProperty(FlatClientProperties.STYLE, "arc: 10"); // Round corners

        // 6. Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(labelFont);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••••");
        passwordField.putClientProperty(FlatClientProperties.STYLE, "arc: 10"); // Round corners
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true"); // 'Eye' icon

        // 7. Login Button (Blue & Bold)
        loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setBackground(new Color(58, 108, 229)); // Modern Blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(300, 45));
        loginButton.setMaximumSize(new Dimension(300, 45));
        loginButton.putClientProperty(FlatClientProperties.STYLE, "arc: 10"); // Round corners
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 8. Add Logic
        loginButton.addActionListener(e -> handleLogin());

        // 9. Assemble the Card (Add vertical gaps "Strut")
        formPanel.add(titleLabel);
        formPanel.add(subTitleLabel);
        formPanel.add(Box.createVerticalStrut(30)); // Gap
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30)); // Big Gap before button
        formPanel.add(loginButton);

        // Add the form panel to the center of the screen
        add(formPanel);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Validation to prevent empty queries
        if(email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email and password.");
            return;
        }

        User user = UserDAO.authenticateUser(email, password);

        if (user != null) {
            user.openDashboard();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}