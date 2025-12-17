package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/help_flow";
    private static final String USER = "postgres";
    private static final String PASSWORD = "kubem";
 



    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

