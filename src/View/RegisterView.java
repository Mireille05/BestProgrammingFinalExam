package View;

import Controller.UserController;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.User;

public class RegisterView {

    private JFrame registerFrame;
    private JTextField fullNameField, usernameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, backToLoginButton;

    public RegisterView() {
        // Modern calm color palette
        Color primaryColor = new Color(91, 155, 213); // Soft Blue
        Color secondaryColor = new Color(155, 126, 222); // Lavender
        Color backgroundColor = new Color(248, 249, 250); // Very Light Gray
        Color textColor = new Color(44, 62, 80); // Dark Gray
        Color accentColor = new Color(107, 203, 159); // Soft Mint

        registerFrame = new JFrame("Register - Help Flow");
        registerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registerFrame.setLocationRelativeTo(null);
        registerFrame.getContentPane().setBackground(backgroundColor);
        registerFrame.setLayout(new BorderLayout());

        // Main container with centered content
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(backgroundColor);
        GridBagConstraints mainGbc = new GridBagConstraints();
        
        // Welcome section
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'><h1 style='color: #5B9BD5; font-size: 36px; margin: 0 0 10px 0; font-weight: 600;'>Join Help Flow</h1><p style='color: #6C757D; font-size: 16px; margin: 0;'>Create your account to get started</p></div></html>");
        
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.insets = new Insets(0, 0, 40, 0);
        mainContainer.add(welcomeLabel, mainGbc);

        // Form panel with fixed width
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(500, 600));
        formPanel.setMaximumSize(new Dimension(500, 600));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(50, 50, 50, 50)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        fullNameField = new JTextField(25);
        styleTextField(fullNameField);
        addFormRow(formPanel, gbc, row, "Full Name:", fullNameField);
        row += 2;

        usernameField = new JTextField(25);
        styleTextField(usernameField);
        addFormRow(formPanel, gbc, row, "Username:", usernameField);
        row += 2;

        emailField = new JTextField(25);
        styleTextField(emailField);
        addFormRow(formPanel, gbc, row, "Email:", emailField);
        row += 2;

        passwordField = new JPasswordField(25);
        stylePasswordField(passwordField);
        addFormRow(formPanel, gbc, row, "Password:", passwordField);
        row += 2;

        confirmPasswordField = new JPasswordField(25);
        stylePasswordField(confirmPasswordField);
        addFormRow(formPanel, gbc, row, "Confirm Password:", confirmPasswordField);
        row += 2;

        registerButton = new JButton("Create Account");
        registerButton.setBackground(primaryColor);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        registerButton.setPreferredSize(new Dimension(400, 45));
        registerButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(25, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(registerButton, gbc);

        backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setForeground(primaryColor);
        backToLoginButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryColor, 2, true),
            BorderFactory.createEmptyBorder(12, 0, 12, 0)
        ));
        backToLoginButton.setBackground(Color.WHITE);
        backToLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backToLoginButton.setPreferredSize(new Dimension(400, 45));
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginButton.setFocusPainted(false);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(backToLoginButton, gbc);

        // Add form panel to main container
        mainGbc.gridy = 1;
        mainGbc.insets = new Insets(0, 0, 0, 0);
        mainContainer.add(formPanel, mainGbc);

        registerFrame.getContentPane().add(mainContainer, BorderLayout.CENTER);
        registerFrame.setVisible(true);

        //  Register Button Logic
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fullName = fullNameField.getText().trim();
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

                // BUSINESS VALIDATION 1: Required Fields Validation
                if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(registerFrame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // BUSINESS VALIDATION 2: Email Format Validation
                if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    JOptionPane.showMessageDialog(registerFrame, "Please enter a valid email address!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // BUSINESS VALIDATION 3: Password Confirmation Match
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(registerFrame, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // BUSINESS VALIDATION 4: Password Minimum Length
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(registerFrame, "Password must be at least 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // BUSINESS VALIDATION 6: Username Length Validation
                if (username.length() < 3 || username.length() > 50) {
                    JOptionPane.showMessageDialog(registerFrame, "Username must be between 3 and 50 characters!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
              UserController userController = new UserController();
                User user = new User(fullName, username, email, password);
               boolean success = userController.registerUser(user);

        
                if (success) {
                    JOptionPane.showMessageDialog(registerFrame, "Account created successfully! 🎉");
                    registerFrame.dispose();
                    new LoginView();
                } else {
                    JOptionPane.showMessageDialog(registerFrame, "Username or Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //  Back to Login Button
        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerFrame.dispose();
                new LoginView();
            }
        });
    }

    //  Helper
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int y, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelComponent.setForeground(new Color(44, 62, 80));
        panel.add(labelComponent, gbc);

        gbc.gridy = y + 1;
        gbc.insets = new Insets(5, 0, 12, 0);
        panel.add(field, gbc);
        
        // Reset insets for next row
        gbc.insets = new Insets(12, 10, 12, 10);
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
    }
    
    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
    }
}
