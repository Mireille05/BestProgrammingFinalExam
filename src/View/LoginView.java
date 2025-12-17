package View;

import Controller.UserController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView {

    private JFrame loginFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginView() {
        // Modern calm color palette
        Color primaryColor = new Color(91, 155, 213); // Soft Blue
        Color secondaryColor = new Color(155, 126, 222); // Lavender
        Color backgroundColor = new Color(248, 249, 250); // Very Light Gray
        Color textColor = new Color(44, 62, 80); // Dark Gray
        Color accentColor = new Color(107, 203, 159); // Soft Mint
        
        loginFrame = new JFrame("Login - Help Flow");
        loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.getContentPane().setBackground(backgroundColor);
        loginFrame.setLayout(new BorderLayout());

        // Main container with centered content
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(backgroundColor);
        GridBagConstraints mainGbc = new GridBagConstraints();
        
        // Welcome section
        JLabel loginLabel = new JLabel("<html><div style='text-align: center;'><h1 style='color: #5B9BD5; font-size: 36px; margin: 0 0 10px 0; font-weight: 600;'>Welcome to Help Flow</h1><p style='color: #6C757D; font-size: 16px; margin: 0;'>Login to your account</p></div></html>");
        
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.insets = new Insets(0, 0, 40, 0);
        mainContainer.add(loginLabel, mainGbc);

        // Form panel with fixed width and shadow effect
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(450, 400));
        formPanel.setMaximumSize(new Dimension(450, 400));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(50, 50, 50, 50)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.anchor = GridBagConstraints.WEST;

        usernameField = new JTextField(25);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        int row = 0;
        addFormRow(formPanel, gbc, row, "Username:", usernameField);
        row += 2;

        passwordField = new JPasswordField(25);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        addFormRow(formPanel, gbc, row, "Password:", passwordField);
        row += 2;

        loginButton = new JButton("Login");
        loginButton.setBackground(primaryColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setPreferredSize(new Dimension(350, 45));
        loginButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(25, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        registerButton = new JButton("Create Account");
        registerButton.setForeground(primaryColor);
        registerButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryColor, 2, true),
            BorderFactory.createEmptyBorder(12, 0, 12, 0)
        ));
        registerButton.setBackground(Color.WHITE);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setPreferredSize(new Dimension(350, 45));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setFocusPainted(false);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(registerButton, gbc);

        // Add form panel to main container
        mainGbc.gridy = 1;
        mainGbc.insets = new Insets(0, 0, 0, 0);
        mainContainer.add(formPanel, mainGbc);

        loginFrame.getContentPane().add(mainContainer, BorderLayout.CENTER);
        loginFrame.setVisible(true);

        // Login Button Action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                UserController userController = new UserController();
                User loggedInUser = userController.loginAndGetUser(username, password);

                if (loggedInUser != null) {
                    loginFrame.dispose(); // Close current window
                    
                    if (loggedInUser.isAdmin()) {
                        // Open Admin Dashboard if user is an admin
                        new AdminDashboardView(loggedInUser);
                    } else {
                        // Open regular user home page
                        new HomePageView(username, null);
                    }
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid credentials. Please try again.");
                }
            }
        });

        // Register Button Action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginFrame.dispose(); // Close Login window
                new RegisterView(); // Open Register View
            }
        });
    }

    //  Helper Method to add form rows
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
}