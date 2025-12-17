package View;

import javax.swing.*;

import Controller.UserController;
import model.User;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ProfileView {

    private JFrame profileFrame;

    public ProfileView(String username, String email, ImageIcon profilePic,
                       int issuesSubmitted, int likesReceived,
                       int commentsReceived, int commentsMade, boolean allowEditing) {

        // Modern calm color palette - consistent with other views
        Color primaryColor = new Color(91, 155, 213); // Soft Blue
        Color backgroundColor = new Color(248, 249, 250); // Very Light Gray
        Color textColor = new Color(44, 62, 80); // Dark Gray
        Color accentColor = new Color(107, 203, 159); // Soft Mint

        // ==== Frame Setup ====
        profileFrame = new JFrame("Profile - " + username + " | Help Flow");
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setSize(650, 700);
        profileFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // ==== Header Panel ====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("User Profile");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==== Profile Panel ====
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // ==== Profile Picture ====
        gbc.gridy = 0;
        JLabel profileLabel = new JLabel();

        // Create a fallback image in case loading fails
        BufferedImage fallbackImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = fallbackImg.createGraphics();
        g2d.setColor(new Color(0, 102, 204));
        g2d.fillRect(0, 0, 100, 100);
        g2d.dispose();
        ImageIcon defaultPic = new ImageIcon(fallbackImg);

        // Try to load from file system
        try {
            File imageFile = new File("Assets/LogoHelpFlow.png");
            if (imageFile.exists()) {
                defaultPic = new ImageIcon(imageFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            // Keep using the fallback image
        }

        ImageIcon finalProfilePic = (profilePic != null) ? profilePic : defaultPic;

        Image image = finalProfilePic.getImage();
        Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        profileLabel.setIcon(new ImageIcon(scaledImage));
        profilePanel.add(profileLabel, gbc);
        
        // ==== Username ====
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 5, 0);
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        usernameLabel.setForeground(textColor);
        profilePanel.add(usernameLabel, gbc);

        // ==== Email ====
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 20, 0);
        JLabel emailLabel = new JLabel(email);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(108, 117, 125));
        profilePanel.add(emailLabel, gbc);

        // ==== Stats Panel ====
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
                "Activity Summary",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                primaryColor
        ));

        // Create styled stat cards
        statsPanel.add(createStatCard("Issues Submitted", String.valueOf(issuesSubmitted), primaryColor));
        statsPanel.add(createStatCard("Likes Received", String.valueOf(likesReceived), accentColor));
        statsPanel.add(createStatCard("Comments Received", String.valueOf(commentsReceived), new Color(155, 126, 222)));
        statsPanel.add(createStatCard("Comments Made", String.valueOf(commentsMade), new Color(255, 159, 64)));

        profilePanel.add(statsPanel, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        // ==== Edit Profile Button (Conditional) ====
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 10, 0);
        if (allowEditing) {
            JButton editProfileButton = new JButton("✏️ Edit Profile");
            editProfileButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            editProfileButton.setBackground(primaryColor);
            editProfileButton.setForeground(Color.WHITE);
            editProfileButton.setPreferredSize(new Dimension(180, 45));
            editProfileButton.setFocusPainted(false);
            editProfileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editProfileButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            profilePanel.add(editProfileButton, gbc);
            
            // ==== Edit Button Action ====
            editProfileButton.addActionListener(e -> {
                // Create UserController first
                UserController controller = new UserController();
                
                // Look up the complete user from the database
                User user = controller.getUserByUsername(username);
                
                if (user != null) {
                    // Now we have the user with correct ID from database
                    new EditProfilePageView(user, controller);
                } else {
                    JOptionPane.showMessageDialog(profileFrame, 
                        "Error: Could not retrieve user data from database", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        } else {
            // Add a label indicating this is someone else's profile
            JLabel viewOnlyLabel = new JLabel("👤 Viewing " + username + "'s profile");
            viewOnlyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            viewOnlyLabel.setForeground(new Color(108, 117, 125));
            profilePanel.add(viewOnlyLabel, gbc);
        }

        // ==== Add Profile Panel to Main Panel ====
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(backgroundColor);
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.insets = new Insets(20, 20, 20, 20);
        centerContainer.add(profilePanel, centerGbc);
        
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        profileFrame.setContentPane(mainPanel);
        profileFrame.setVisible(true);
    }
    
    // Helper method to create styled stat cards
    private JPanel createStatCard(String label, String value, Color color) {
        Color textColor = new Color(44, 62, 80); // Dark Gray
        
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelLabel.setForeground(textColor);
        labelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(labelLabel, BorderLayout.SOUTH);
        
        return card;
    }
}