package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import Controller.IssueController;
import Controller.UserController;
import model.Issue;
import model.Comment;
import model.User;

public class IssueDetailView {
    private JFrame detailFrame;
    private Issue issue;
    private IssueController issueController;
    private UserController userController;
    private int userId;
    private JTextArea commentField;
    private JPanel commentsPanel;

    public IssueDetailView(Issue issue, int currentUserId) {
        this.issue = issue;
        this.userId = currentUserId;
        this.issueController = new IssueController();
        this.userController = new UserController();
        
        // Modern calm color palette - consistent with other views
        Color primaryColor = new Color(91, 155, 213); // Soft Blue
        Color backgroundColor = new Color(248, 249, 250); // Very Light Gray
        Color textColor = new Color(44, 62, 80); // Dark Gray
        Color accentColor = new Color(107, 203, 159); // Soft Mint
        
        // Create and set up the frame
        detailFrame = new JFrame("Issue Details | Help Flow");
        detailFrame.setSize(900, 700);
        detailFrame.setLocationRelativeTo(null);
        detailFrame.setLayout(new BorderLayout());
        detailFrame.getContentPane().setBackground(backgroundColor);
        
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout(0, 15));
        mainContainer.setBackground(backgroundColor);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Issue details panel at the top
        JPanel detailsPanel = createDetailsPanel();
        
        // Comments section in the middle
        JPanel commentsSection = createCommentsSection();
        
        // Comment input at the bottom
        JPanel inputPanel = createCommentInputPanel();
        
        // Add all components to the main container
        mainContainer.add(detailsPanel, BorderLayout.NORTH);
        mainContainer.add(commentsSection, BorderLayout.CENTER);
        mainContainer.add(inputPanel, BorderLayout.SOUTH);
        
        // Add main container to frame
        detailFrame.add(mainContainer, BorderLayout.CENTER);
        
        detailFrame.setVisible(true);
    }
    
    private JPanel createDetailsPanel() {
        Color primaryColor = new Color(91, 155, 213);
        Color backgroundColor = new Color(248, 249, 250);
        Color textColor = new Color(44, 62, 80);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Title and status
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel(issue.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        
        JLabel statusLabel = new JLabel("Status: " + issue.getStatus());
        statusLabel.setForeground(getStatusColor(issue.getStatus()));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getStatusColor(issue.getStatus()), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(statusLabel, BorderLayout.EAST);
        titlePanel.setOpaque(false);
        
        // Creator info
        User creator = userController.getUserById(issue.getUserId());
        String creatorName = (creator != null) ? creator.getUsername() : "Unknown";
        
        JLabel creatorLabel = new JLabel("👤 Posted by: " + creatorName + " • 📅 " + (issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : "N/A"));
        creatorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        creatorLabel.setForeground(new Color(108, 117, 125));
        
        // Description
        JTextArea descriptionArea = new JTextArea(issue.getDescription() != null ? issue.getDescription() : "No description provided.");
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setForeground(textColor);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Reactions
        JPanel reactionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        reactionsPanel.setOpaque(false);
        reactionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        int likeCount = issueController.getLikeCount(issue.getId());
        int dislikeCount = issueController.getDislikeCount(issue.getId());
        boolean hasLiked = issueController.hasUserLikedIssue(userId, issue.getId());
        boolean hasDisliked = issueController.hasUserDislikedIssue(userId, issue.getId());
        
        final boolean[] liked = {hasLiked};
        final boolean[] disliked = {hasDisliked};
        final int[] likeCountHolder = {likeCount};
        final int[] dislikeCountHolder = {dislikeCount};
        
        JButton likeButton = new JButton("👍 " + likeCount);
        likeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        likeButton.setBackground(hasLiked ? new Color(107, 203, 159) : new Color(240, 240, 240));
        likeButton.setForeground(hasLiked ? Color.WHITE : new Color(60, 60, 60));
        likeButton.setFocusPainted(false);
        likeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        likeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        likeButton.setPreferredSize(new Dimension(120, 40));
        
        JButton dislikeButton = new JButton("👎 " + dislikeCount);
        dislikeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dislikeButton.setBackground(hasDisliked ? new Color(255, 107, 107) : new Color(240, 240, 240));
        dislikeButton.setForeground(hasDisliked ? Color.WHITE : new Color(60, 60, 60));
        dislikeButton.setFocusPainted(false);
        dislikeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dislikeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        dislikeButton.setPreferredSize(new Dimension(120, 40));
        
        reactionsPanel.add(likeButton);
        reactionsPanel.add(dislikeButton);
        
        // Add action listeners for reaction buttons
        likeButton.addActionListener(e -> {
                String username = userController.getUserById(userId).getUsername();
        
            if (!liked[0]) {
                boolean success = issueController.likeIssue(new model.Like(issue.getId(), username), userId);
                if (success) {
                    liked[0] = true;
                    likeCountHolder[0]++;
                    likeButton.setText("👍 " + likeCountHolder[0]);
                    likeButton.setBackground(new Color(107, 203, 159));
                    likeButton.setForeground(Color.WHITE);
                    
                    if (disliked[0]) {
                        boolean undislike = issueController.undislikeIssue(userId, issue.getId());
                        if (undislike) {
                            disliked[0] = false;
                            dislikeCountHolder[0] = Math.max(0, dislikeCountHolder[0] - 1);
                            dislikeButton.setText("👎 " + dislikeCountHolder[0]);
                            dislikeButton.setBackground(new Color(240, 240, 240));
                            dislikeButton.setForeground(new Color(60, 60, 60));
                        }
                    }
                } else {
                    if (issueController.hasUserLikedIssue(userId, issue.getId())) {
                        liked[0] = true;
                        likeCountHolder[0] = issueController.getLikeCount(issue.getId());
                        likeButton.setText("👍 " + likeCountHolder[0]);
                        likeButton.setBackground(new Color(107, 203, 159));
                        likeButton.setForeground(Color.WHITE);
                        JOptionPane.showMessageDialog(detailFrame, "You've already liked this issue earlier. Tap again to remove your like.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(detailFrame, "Unable to like this issue. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                boolean success = issueController.unlikeIssue(userId, issue.getId());
                if (success) {
                    liked[0] = false;
                    likeCountHolder[0] = Math.max(0, likeCountHolder[0] - 1);
                    likeButton.setText("👍 " + likeCountHolder[0]);
                    likeButton.setBackground(new Color(240, 240, 240));
                    likeButton.setForeground(new Color(60, 60, 60));
                } else {
                    JOptionPane.showMessageDialog(detailFrame, "Failed to unlike this issue. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        dislikeButton.addActionListener(e -> {
            String username = userController.getUserById(userId).getUsername();
            
            if (!disliked[0]) {
                model.Like like = new model.Like(issue.getId(), username);
                boolean success = issueController.dislikeIssue(like, userId);
                
                if (success) {
                    disliked[0] = true;
                    dislikeCountHolder[0]++;
                    dislikeButton.setText("👎 " + dislikeCountHolder[0]);
                    dislikeButton.setBackground(new Color(255, 107, 107));
                    dislikeButton.setForeground(Color.WHITE);
                    
                    if (liked[0]) {
                        boolean unlike = issueController.unlikeIssue(userId, issue.getId());
                        if (unlike) {
                            liked[0] = false;
                            likeCountHolder[0] = Math.max(0, likeCountHolder[0] - 1);
                            likeButton.setText("👍 " + likeCountHolder[0]);
                            likeButton.setBackground(new Color(240, 240, 240));
                            likeButton.setForeground(new Color(60, 60, 60));
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(detailFrame, "You've already disliked this issue.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                boolean success = issueController.undislikeIssue(userId, issue.getId());
                if (success) {
                    disliked[0] = false;
                    dislikeCountHolder[0] = Math.max(0, dislikeCountHolder[0] - 1);
                    dislikeButton.setText("👎 " + dislikeCountHolder[0]);
                    dislikeButton.setBackground(new Color(240, 240, 240));
                    dislikeButton.setForeground(new Color(60, 60, 60));
                } else {
                    JOptionPane.showMessageDialog(detailFrame, "Failed to remove dislike. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        
        
        // Assemble the details panel
        panel.add(titlePanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(creatorLabel, BorderLayout.NORTH);
        centerPanel.add(descriptionArea, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(reactionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCommentsSection() {
        Color primaryColor = new Color(91, 155, 213);
        Color backgroundColor = new Color(248, 249, 250);
        Color textColor = new Color(44, 62, 80);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel commentsLabel = new JLabel("💬 Comments");
        commentsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        commentsLabel.setForeground(textColor);
        commentsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        commentsPanel.setBackground(backgroundColor);
        
        // Load comments
        loadComments();
        
        JScrollPane scrollPane = new JScrollPane(commentsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(backgroundColor);
        scrollPane.getViewport().setBackground(backgroundColor);
        
        panel.add(commentsLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadComments() {
        commentsPanel.removeAll();
        
        List<Comment> comments = issueController.getCommentsForIssue(issue.getId());
        
        if (comments.isEmpty()) {
            JLabel noCommentsLabel = new JLabel("💭 No comments yet. Be the first to comment!");
            noCommentsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noCommentsLabel.setForeground(new Color(108, 117, 125));
            noCommentsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            commentsPanel.add(Box.createVerticalStrut(30));
            commentsPanel.add(noCommentsLabel);
        } else {
            for (Comment comment : comments) {
                commentsPanel.add(createCommentPanel(comment));
                commentsPanel.add(Box.createVerticalStrut(10)); // Add spacing between comments
            }
        }
        
        commentsPanel.revalidate();
        commentsPanel.repaint();
    }
    
    private JPanel createCommentPanel(Comment comment) {
        Color backgroundColor = new Color(248, 249, 250);
        Color textColor = new Color(44, 62, 80);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        User commenter = userController.getUserById(comment.getUserId());
        String commenterName = (commenter != null) ? commenter.getUsername() : "Unknown User";
        
        JLabel userLabel = new JLabel("👤 " + commenterName);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(textColor);
        
        JLabel dateLabel = new JLabel("📅 " + (comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : "N/A"));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(108, 117, 125));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        headerPanel.add(userLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        JTextArea contentArea = new JTextArea(comment.getContent());
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentArea.setForeground(textColor);
        contentArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCommentInputPanel() {
        Color primaryColor = new Color(91, 155, 213);
        Color backgroundColor = new Color(248, 249, 250);
        Color textColor = new Color(44, 62, 80);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel addCommentLabel = new JLabel("💬 Add a comment:");
        addCommentLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addCommentLabel.setForeground(textColor);
        addCommentLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        commentField = new JTextArea(4, 20);
        commentField.setLineWrap(true);
        commentField.setWrapStyleWord(true);
        commentField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commentField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 217, 224), 1, true),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JScrollPane scrollPane = new JScrollPane(commentField);
        scrollPane.setBorder(null);
        
        JButton postButton = new JButton("📝 Post Comment");
        postButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        postButton.setBackground(primaryColor);
        postButton.setForeground(Color.WHITE);
        postButton.setFocusPainted(false);
        postButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        postButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        postButton.addActionListener(e -> postComment());
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(postButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(addCommentLabel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void postComment() {
        String content = commentField.getText().trim();
        if (!content.isEmpty()) {
            Comment comment = new Comment();
            comment.setIssueId(issue.getId());
            comment.setContent(content);
            
            if (issueController.addComment(comment, userId)) {
                commentField.setText("");
                loadComments();
            } else {
                JOptionPane.showMessageDialog(detailFrame, 
                    "Failed to post comment. Please try again.",
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(detailFrame, 
                "Comment cannot be empty.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Color getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "open":
                return new Color(0, 150, 0);  // Green
            case "in progress":
                return new Color(255, 165, 0);  // Orange
            case "closed":
                return new Color(200, 0, 0);  // Red
            default:
                return Color.BLACK;
        }
    }
}