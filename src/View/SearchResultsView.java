
package View;

import Controller.UserController;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.User;
public class SearchResultsView {
    private JFrame resultsFrame;
    private UserController userController;
    private JTabbedPane tabbedPane;
    private String searchQuery;
    private int currentLoggedInUserId;


    public SearchResultsView(String searchQuery, UserController controller) {
        this.searchQuery = searchQuery;
        this.userController = controller;
        this.searchQuery = searchQuery;
        this.userController = controller;
        this.currentLoggedInUserId = controller.getCurrentLoggedInUserId();
        // Create and set up the frame
        resultsFrame = new JFrame("Search Results for: " + searchQuery);
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.setSize(800, 600);
        resultsFrame.setLocationRelativeTo(null);
        
        // Create tabbed pane for users and issues
        tabbedPane = new JTabbedPane();
        
        // Load and display results
        loadResults();
        
        resultsFrame.add(tabbedPane);
        resultsFrame.setVisible(true);
    }
    
    private void loadResults() {
        // Search for users
        ArrayList<User> userResults = userController.searchUsers(searchQuery);
        JPanel usersPanel = createUsersPanel(userResults);
        tabbedPane.addTab("Users (" + userResults.size() + ")", new ImageIcon("Assets/user_icon.png"), usersPanel);
        
        // Search for issues
        ArrayList<model.Issue> issueResults = userController.searchIssues(searchQuery);
        JPanel issuesPanel = createIssuesPanel(issueResults);
        tabbedPane.addTab("Issues (" + issueResults.size() + ")", new ImageIcon("Assets/issue_icon.png"), issuesPanel);
    }
    
    private JPanel createUsersPanel(ArrayList<User> users) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        System.out.println("Creating users panel with " + users.size() + " users");
        // Create header with search info
        JLabel headerLabel = new JLabel("Found " + users.size() + " users matching \"" + searchQuery + "\"");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(headerLabel, BorderLayout.NORTH);
         
        if (users.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No users found matching your search.");
            noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(noResultsLabel, BorderLayout.CENTER);
            
            return panel;
        }
        
        
        // READ OPERATION: Create table model with users data - Displaying data in JTable
        String[] columnNames = {"ID", "Username", "Full Name", "Email", "Admin", "Blocked"};
        Object[][] data = new Object[users.size()][6];
        
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            data[i][0] = user.getId();
            data[i][1] = user.getUsername();
            data[i][2] = user.getFullName();
            data[i][3] = user.getEmail();
            data[i][4] = user.isAdmin() ? "Yes" : "No";
            data[i][5] = user.isBlocked() ? "Yes" : "No";
        }
        
        // Create JTable with users data using DefaultTableModel
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        JTable usersTable = new JTable(tableModel);
        usersTable.setFillsViewportHeight(true);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setRowHeight(25);
        usersTable.setAutoCreateRowSorter(true); // Enable sorting
        
        // Set column widths
        usersTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Username
        usersTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Full Name
        usersTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        usersTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Admin
        usersTable.getColumnModel().getColumn(5).setPreferredWidth(60);  // Blocked
        
        // Add mouse listener for double-clicking on users
        usersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = usersTable.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < users.size()) {
                        User selectedUser = users.get(selectedRow);
                        openUserProfile(selectedUser);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add instruction label
        JLabel instructionLabel = new JLabel("Double-click a user row to view their profile");
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(instructionLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createIssuesPanel(ArrayList<model.Issue> issues) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create header with search info
        JLabel headerLabel = new JLabel("Found " + issues.size() + " issues matching \"" + searchQuery + "\"");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Debug output
        System.out.println("Creating issues panel with " + issues.size() + " issues");
        for (model.Issue issue : issues) {
            System.out.println("Issue in panel: " + issue.getTitle() + " - " + issue.getStatus());
        }
        
        if (issues.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No issues found matching your search.");
            noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(noResultsLabel, BorderLayout.CENTER);
            return panel;
        }
        
        // Create table model with issues data - READ OPERATION: Displaying data in JTable
        String[] columnNames = {"ID", "Title", "Status", "Creator", "Likes", "Created At"};
        Object[][] data = new Object[issues.size()][6];
        
        for (int i = 0; i < issues.size(); i++) {
            model.Issue issue = issues.get(i);
            User creator = userController.getUserById(issue.getUserId());
            String creatorName = (creator != null) ? creator.getUsername() : "Unknown";
            
            data[i][0] = issue.getId();
            data[i][1] = issue.getTitle();
            data[i][2] = issue.getStatus();
            data[i][3] = creatorName;
            data[i][4] = issue.getLikes();
            data[i][5] = issue.getCreatedAt() != null ? issue.getCreatedAt().toString() : "N/A";
        }
        
        // Create JTable with issues using DefaultTableModel for better control
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        
        JTable issuesTable = new JTable(model);
        issuesTable.setFillsViewportHeight(true);
        issuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issuesTable.setRowHeight(25);
        
        // Set column widths
        issuesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        issuesTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Title
        issuesTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Status
        issuesTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Creator
        issuesTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Likes
        issuesTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Created At
        
        // Add mouse listener for double-clicking on issues
        issuesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = issuesTable.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < issues.size()) {
                        model.Issue selectedIssue = issues.get(selectedRow);
                        openIssueDetails(selectedIssue);
                    }
                }
            }
        });
        
        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(issuesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add instruction label
        JLabel instructionLabel = new JLabel("Double-click an issue row to view details");
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(instructionLabel, BorderLayout.SOUTH);
        
        return panel;
    }
// In the SearchResultsView class, modify the openUserProfile method:

private void openUserProfile(User user) {
    // Calculate stats for user
    int issuesSubmitted = userController.countIssuesByUserId(user.getId());
    int likesReceived = userController.countLikesReceivedByUserId(user.getId());
    int commentsReceived = userController.countCommentsReceivedByUserId(user.getId());
    int commentsMade = userController.countCommentsMadeByUserId(user.getId());
    
    // Create a default profile picture 
    BufferedImage fallbackImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = fallbackImg.createGraphics();
    g2d.setColor(new Color(0, 102, 204));
    g2d.fillRect(0, 0, 100, 100);
    g2d.dispose();
    ImageIcon profilePic = new ImageIcon(fallbackImg);
    
    // Get the currently logged in user's ID (you need to pass this to SearchResultsView)
    int currentLoggedInUserId = userController.getCurrentLoggedInUserId();
    
    // Open the profile view in read-only mode (no edit button) if it's not the current user
    boolean isCurrentUser = (currentLoggedInUserId == user.getId());
    
    new ProfileView(user.getUsername(), user.getEmail(), profilePic, 
                   issuesSubmitted, likesReceived, commentsReceived, commentsMade, isCurrentUser);
}
    
    private  void openIssueDetails(model.Issue issue) {
        // Build a more comprehensive dialog with better formatting
        User creator = userController.getUserById(issue.getUserId());
        String creatorName = (creator != null) ? creator.getUsername() : "Unknown";
        
        String message = "<html><body style='width: 400px'>" +
                        "<h2>" + issue.getTitle() + "</h2>" +
                        "<p><b>Status:</b> " + issue.getStatus() + "</p>" +
                        "<p><b>Created by:</b> " + creatorName + "</p>" +
                        "<p><b>Posted on:</b> " + issue.getCreatedAt() + "</p>" +
                        "<p><b>Description:</b><br>" + issue.getDescription() + "</p>" +
                        "</body></html>";
        
        // Try creating a custom dialog instead of JOptionPane
        JDialog detailDialog = new JDialog(resultsFrame, "Issue Details", true);
        detailDialog.setLayout(new BorderLayout());
        
        JEditorPane contentPane = new JEditorPane("text/html", message);
        contentPane.setEditable(false);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(contentPane);
        detailDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        detailDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        detailDialog.setSize(500, 400);
        detailDialog.setLocationRelativeTo(resultsFrame);
        detailDialog.setVisible(true);
    }
    // Custom cell renderer for user list
    class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                     int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            // Create user icon
            BufferedImage userIcon = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = userIcon.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 102, 204));
            g2d.fillOval(0, 0, 20, 20);
            g2d.dispose();
            
            label.setIcon(new ImageIcon(userIcon));
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            return label;
        }
    }
    
}