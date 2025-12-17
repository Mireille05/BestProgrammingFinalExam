package Controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Notice;
import model.Report;
import model.User;

public class AdminController {
    private Connection connection;
    private UserController userController;
    private ReportController reportController;
    private IssueController issueController;

    public AdminController() {
        try {
            String url = "jdbc:postgresql://localhost:5432/help_flow";
            String user = "postgres";
            String pass = "kubem";
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Admin DB connected!");
            
            // Initialize other controllers
            userController = new UserController();
            reportController = new ReportController();
            issueController = new IssueController();
        } catch (Exception e) {
            System.out.println(" Admin DB connection failed");
            e.printStackTrace();
        }
    }
    
    /**
     * Get all reports in the system
     * @return List of reports
     */
    public List<Report> getReports() {
        return reportController.getAllReports();
    }
    
    /**
     * Block a user by setting is_blocked to true
     * @param userId The user ID to block
     * @return true if successful, false otherwise
     */
    public boolean blockUser(int userId) {
        String sql = "UPDATE users SET is_blocked = true, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int result = stmt.executeUpdate();
            System.out.println("✅ User blocked successfully: " + userId);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("❌ Failed to block user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Unblock a user by setting is_blocked to false
     * @param userId The user ID to unblock
     * @return true if successful, false otherwise
     */
    public boolean unblockUser(int userId) {
        String sql = "UPDATE users SET is_blocked = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int result = stmt.executeUpdate();
            System.out.println("✅ User unblocked successfully: " + userId);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("❌ Failed to unblock user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Toggle admin status for a user
     * @param userId The user ID
     * @return true if successful, false otherwise
     */
    public boolean toggleAdmin(int userId) {
        // First get current admin status
        String checkSql = "SELECT is_admin FROM users WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, userId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                boolean currentStatus = rs.getBoolean("is_admin");
                boolean newStatus = !currentStatus;
                
                String updateSql = "UPDATE users SET is_admin = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setBoolean(1, newStatus);
                    updateStmt.setInt(2, userId);
                    int result = updateStmt.executeUpdate();
                    System.out.println("✅ Admin status toggled for user " + userId + " to " + newStatus);
                    return result > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to toggle admin status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete a user and all associated data
     * @param userId The user ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        try {
            connection.setAutoCommit(false);
            
            // Delete user's reports
            String deleteReports = "DELETE FROM reports WHERE reported_by = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteReports)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
            
            // Delete user's comments
            String deleteComments = "DELETE FROM comments WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteComments)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
            
            // Delete user's likes
            String deleteLikes = "DELETE FROM likes WHERE user_id = ? OR liked_user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteLikes)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
            
            // Delete user's issues (cascade will handle comments and likes)
            String deleteIssues = "DELETE FROM issues WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteIssues)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
            
            // Finally delete the user
            String deleteUser = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteUser)) {
                stmt.setInt(1, userId);
                int result = stmt.executeUpdate();
                
                connection.commit();
                connection.setAutoCommit(true);
                System.out.println("✅ User deleted successfully: " + userId);
                return result > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("❌ Failed to delete user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Add a new user (for admin to create users)
     * @param user The user object to add
     * @return true if successful, false otherwise
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (full_name, username, email, password, is_admin, is_blocked) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setBoolean(5, user.isAdmin());
            stmt.setBoolean(6, user.isBlocked());
            int result = stmt.executeUpdate();
            System.out.println("✅ User added successfully: " + user.getUsername());
            return result > 0;
        } catch (SQLException e) {
            System.out.println("❌ Failed to add user: " + e.getMessage());
            if (e.getSQLState().equals("23505")) {
                System.out.println("Username or email already exists!");
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete an issue and all associated comments and likes
     * @param issueId The issue ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteIssue(int issueId) {
        try {
            // Start transaction
            connection.setAutoCommit(false);
            
            // Delete comments associated with the issue
            String deleteComments = "DELETE FROM comments WHERE issue_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteComments)) {
                stmt.setInt(1, issueId);
                stmt.executeUpdate();
            }
            
            // Delete likes associated with the issue
            String deleteLikes = "DELETE FROM likes WHERE issue_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteLikes)) {
                stmt.setInt(1, issueId);
                stmt.executeUpdate();
            }
            
            // Delete reports associated with the issue
            String deleteReports = "DELETE FROM reports WHERE issue_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteReports)) {
                stmt.setInt(1, issueId);
                stmt.executeUpdate();
            }
            
            // Delete the issue
            String deleteIssue = "DELETE FROM issues WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteIssue)) {
                stmt.setInt(1, issueId);
                int result = stmt.executeUpdate();
                
                // Commit transaction
                connection.commit();
                connection.setAutoCommit(true);
                
                return result > 0;
            }
        } catch (SQLException e) {
            try {
                // Rollback transaction in case of error
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Failed to delete issue");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Post a new notice for all users
     * @param title Notice title
     * @param content Notice content
     * @param postedBy User ID of the admin posting the notice
     * @return true if successful, false otherwise
     */
    public boolean postNotice(String title, String content, int postedBy) {
        // The notices table has both 'message' (text) and 'content' (varchar) columns
        // We'll use 'message' for the main content and 'content' for a short summary
        String sql = "INSERT INTO notices (title, message, content, posted_by, status) VALUES (?, ?, ?, ?, 'active')";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, content); // Full message in text field
            stmt.setString(3, content.length() > 255 ? content.substring(0, 252) + "..." : content); // Summary in content field
            stmt.setInt(4, postedBy);
            int result = stmt.executeUpdate();
            System.out.println(" Notice posted successfully: " + title);
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Failed to post notice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update a notice
     * @param noticeId The notice ID
     * @param title New title
     * @param content New content
     * @return true if successful, false otherwise
     */
    public boolean updateNotice(int noticeId, String title, String content) {
        String sql = "UPDATE notices SET title = ?, message = ?, content = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            stmt.setString(3, content.length() > 255 ? content.substring(0, 252) + "..." : content);
            stmt.setInt(4, noticeId);
            int result = stmt.executeUpdate();
            System.out.println(" Notice updated successfully: " + noticeId);
            return result > 0;
        } catch (SQLException e) {
            System.out.println(" Failed to update notice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all notices ordered by creation date
     * @return List of notices
     */
    public List<Notice> getAllNotices() {
        List<Notice> notices = new ArrayList<>();
        String sql = "SELECT * FROM notices ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Notice notice = new Notice();
                notice.setId(rs.getInt("id"));
                notice.setTitle(rs.getString("title"));
                // Use message field for content (it's the text field)
                String message = rs.getString("message");
                if (message != null) {
                    notice.setContent(message);
                } else {
                    notice.setContent(rs.getString("content")); // Fallback to content field
                }
                notice.setPostedBy(rs.getInt("posted_by"));
                notice.setCreatedAt(rs.getTimestamp("created_at"));
                notices.add(notice);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get notices");
            e.printStackTrace();
        }
        
        return notices;
    }
    
    /**
     * Delete a notice
     * @param noticeId The notice ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteNotice(int noticeId) {
        String sql = "DELETE FROM notices WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, noticeId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Failed to delete notice");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all users in the system
     * @return List of users
     */

     /**
 * Get all users in the system
 * @return List of users
 */




 public model.AdminStats getSystemStats() {
    model.AdminStats stats = new model.AdminStats();
    
    try {
        // Total users
        String userSql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(userSql)) {
            if (rs.next()) {
                stats.setTotalUsers(rs.getInt(1));
            }
        }
        
        // Total issues
        String issueSql = "SELECT COUNT(*) FROM issues";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(issueSql)) {
            if (rs.next()) {
                stats.setTotalIssues(rs.getInt(1));
            }
        }
        
        // Total comments
        String commentSql = "SELECT COUNT(*) FROM comments";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(commentSql)) {
            if (rs.next()) {
                stats.setTotalComments(rs.getInt(1));
            }
        }
        
        // Active reports
        String reportSql = "SELECT COUNT(*) FROM reports";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(reportSql)) {
            if (rs.next()) {
                stats.setActiveReports(rs.getInt(1));
            }
        }
        
        // Recent activity - issues created in the last 7 days
        String recentActivitySql = "SELECT COUNT(*) FROM issues WHERE created_at >= NOW() - INTERVAL '7 days'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(recentActivitySql)) {
            if (rs.next()) {
                stats.setRecentActivity(rs.getInt(1));
            }
        }
        
    } catch (SQLException e) {
        System.out.println(" Failed to get system stats");
        e.printStackTrace();
    }
    
    return stats;
}



public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    String sql = "SELECT * FROM users ORDER BY id";
    
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            User user = new User(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getBoolean("is_admin"),
                rs.getBoolean("is_blocked")
            );
            user.setCreatedAt(rs.getTimestamp("created_at"));
            user.setUpdatedAt(rs.getTimestamp("updated_at"));
            users.add(user);
        }
    } catch (SQLException e) {
        System.out.println(" Failed to get users");
        e.printStackTrace();
    }
    
    return users;
}
  
    
    /**
     * Update report status
     * @param reportId The report ID
     * @param status New status (pending, resolved, dismissed)
     * @return true if successful, false otherwise
     */
    public boolean updateReportStatus(int reportId, String status) {
        String sql = "UPDATE reports SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.toLowerCase());
            stmt.setInt(2, reportId);
            int result = stmt.executeUpdate();
            System.out.println(" Report status updated: " + reportId + " -> " + status);
            return result > 0;
        } catch (SQLException e) {
            System.out.println(" Failed to update report status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete reported content (issue or comment)
     * @param reportId The report ID
     * @return true if successful, false otherwise
     */
    public boolean deleteReportedContent(int reportId) {
        try {
            // First get the report to find what to delete
            String getReportSql = "SELECT issue_id, comment_id FROM reports WHERE id = ?";
            try (PreparedStatement getStmt = connection.prepareStatement(getReportSql)) {
                getStmt.setInt(1, reportId);
                ResultSet rs = getStmt.executeQuery();
                
                if (rs.next()) {
                    Integer issueId = rs.getObject("issue_id") != null ? rs.getInt("issue_id") : null;
                    Integer commentId = rs.getObject("comment_id") != null ? rs.getInt("comment_id") : null;
                    
                    connection.setAutoCommit(false);
                    
                    if (issueId != null) {
                        // Delete the issue (cascade will handle comments and likes)
                        deleteIssue(issueId);
                    } else if (commentId != null) {
                        // Delete the comment
                        String deleteCommentSql = "DELETE FROM comments WHERE id = ?";
                        try (PreparedStatement delStmt = connection.prepareStatement(deleteCommentSql)) {
                            delStmt.setInt(1, commentId);
                            delStmt.executeUpdate();
                        }
                    }
                    
                    // Delete the report itself
                    String deleteReportSql = "DELETE FROM reports WHERE id = ?";
                    try (PreparedStatement delStmt = connection.prepareStatement(deleteReportSql)) {
                        delStmt.setInt(1, reportId);
                        delStmt.executeUpdate();
                    }
                    
                    connection.commit();
                    connection.setAutoCommit(true);
                    System.out.println("Reported content deleted: " + reportId);
                    return true;
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println(" Failed to delete reported content: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    
    
    // Inner class to hold system statistics
    public class AdminStats {
        private int totalUsers;
        private int totalIssues;
        private int totalComments;
        private int activeReports;
        private int recentActivity;
        
        public int getTotalUsers() {
            return totalUsers;
        }
        
        public void setTotalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
        }
        
        public int getTotalIssues() {
            return totalIssues;
        }
        
        public void setTotalIssues(int totalIssues) {
            this.totalIssues = totalIssues;
        }
        
        public int getTotalComments() {
            return totalComments;
        }
        
        public void setTotalComments(int totalComments) {
            this.totalComments = totalComments;
        }
        
        public int getActiveReports() {
            return activeReports;
        }
        
        public void setActiveReports(int activeReports) {
            this.activeReports = activeReports;
        }
        
        public int getRecentActivity() {
            return recentActivity;
        }
        
        public void setRecentActivity(int recentActivity) {
            this.recentActivity = recentActivity;
        }
    }

}