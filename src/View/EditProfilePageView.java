package View;

import Controller.UserController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class EditProfilePageView {

    private JFrame editFrame;
    private UserController userController;
    private User currentUser;

    public EditProfilePageView(User user, UserController controller) {
        this.currentUser = user;
        this.userController = controller;
        String username = user.getUsername();
        String email = user.getEmail();
    
        BufferedImage fallbackImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = fallbackImg.createGraphics();
        g2d.setColor(new Color(0, 102, 204));
        g2d.fillRect(0, 0, 100, 100);
        g2d.dispose();
        ImageIcon defaultPic = new ImageIcon(fallbackImg);
        
        
        try {
            File imageFile = new File("Assets/LogoHelpFlow.png");
            if (imageFile.exists()) {
                defaultPic = new ImageIcon(imageFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        ImageIcon finalProfilePic = defaultPic;
        
        // Modern calm color palette - consistent with other views
        Color primaryColor = new Color(91, 155, 213); // Soft Blue
        Color backgroundColor = new Color(248, 249, 250); // Very Light Gray
        Color textColor = new Color(44, 62, 80); // Dark Gray
        
        editFrame = new JFrame("Edit Profile | Help Flow");
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.setSize(500, 600);
        editFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("Edit Your Profile");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBackground(Color.WHITE);
        editPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel profileLabel = new JLabel();
        profileLabel.setIcon(resizeAndRoundIcon(finalProfilePic, 120, 120));
        profileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        editPanel.add(profileLabel, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 10, 8, 10);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usernameLabel.setForeground(textColor);
        gbc.gridx = 0;
        editPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(username, 20);
        styleTextField(usernameField);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        editPanel.add(usernameField, gbc);
        
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.insets = new Insets(15, 10, 8, 10);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(textColor);
        gbc.gridx = 0;
        editPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField(email, 20);
        styleTextField(emailField);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        editPanel.add(emailField, gbc);
        
        gbc.gridy++;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Password (optional):");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(textColor);
        gbc.gridx = 0;
        editPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        stylePasswordField(passwordField);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        editPanel.add(passwordField, gbc);
        
        JButton saveButton = new JButton("💾 Save Changes");
        saveButton.setBackground(primaryColor);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        saveButton.setPreferredSize(new Dimension(200, 45));
        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(25, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        editPanel.add(saveButton, gbc);
        
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(backgroundColor);
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.insets = new Insets(20, 20, 20, 20);
        centerContainer.add(editPanel, centerGbc);
        
        mainPanel.add(centerContainer, BorderLayout.CENTER);
        editFrame.setContentPane(mainPanel);
        editFrame.setVisible(true);
        saveButton.addActionListener(e -> {
            System.out.println("Save button clicked");
            
            // First, check if controller is null
            if (userController == null) {
                System.out.println("ERROR: UserController is null!");
                JOptionPane.showMessageDialog(editFrame, 
                    "Internal error: Controller not found.", 
                    "System Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if user is null or has invalid ID
            if (currentUser == null) {
                System.out.println("ERROR: Current user is null!");
                JOptionPane.showMessageDialog(editFrame, 
                    "Internal error: User data not found.", 
                    "System Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            System.out.println("Current user ID: " + currentUser.getId());
            
            // Check if the user ID is valid
            if (currentUser.getId() <= 0) {
                System.out.println("ERROR: Invalid user ID: " + currentUser.getId());
                JOptionPane.showMessageDialog(editFrame, 
                    "Cannot update profile: Invalid user ID", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String newUsername = usernameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();
            
            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(editFrame, 
                    "Username and email cannot be empty!", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            System.out.println("Attempting to update user with ID: " + currentUser.getId());
            System.out.println("New username: " + newUsername);
            System.out.println("New email: " + newEmail);
            System.out.println("Password change requested: " + (!newPassword.isEmpty()));
            
            boolean updated = userController.updateUser(
                currentUser.getId(), 
                newUsername, 
                newEmail, 
                newPassword);
            
            System.out.println("Update result: " + (updated ? "SUCCESS" : "FAILED"));
                
            if (updated) {
                currentUser.setUsername(newUsername);
                currentUser.setEmail(newEmail);
                if (!newPassword.isEmpty()) {
                    currentUser.setPassword(newPassword);
                }
                JOptionPane.showMessageDialog(editFrame, 
                    "Profile updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                editFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(editFrame, 
                    "Failed to update profile. Please try again.", 
                    "Update Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    private ImageIcon resizeAndRoundIcon(ImageIcon originalIcon, int width, int height) {
        Image img = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage roundedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = roundedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillRoundRect(0, 0, width, height, width, height);
        g2d.setComposite(AlphaComposite.SrcIn);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return new ImageIcon(roundedImage);
    }
    
    // Helper methods for consistent styling
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