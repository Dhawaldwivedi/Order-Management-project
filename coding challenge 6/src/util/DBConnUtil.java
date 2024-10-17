package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;


public class DBConnUtil {

    // Hardcoded username and password
    private static final String USERNAME = "root";
    private static final String PASSWORD = "12345678";

    // Static method to get the database connection
    public static Connection getDBConn(String propertyFileName) {
        Connection conn = null;
        try {
            // Use the connection string from DBPropertyUtil
            String connectionString = DBPropertyUtil.getDBConnectionString(propertyFileName);
            conn = DriverManager.getConnection(connectionString, USERNAME, PASSWORD);
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
        }
        return conn;
    }
    // Utility method to close ResultSet, PreparedStatement, and Connection
    public static void close(ResultSet rs, PreparedStatement ps, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Overloaded method to close PreparedStatement and Connection only
    public static void close(PreparedStatement ps, Connection conn) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


