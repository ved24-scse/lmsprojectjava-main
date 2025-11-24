package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Update these with your local database details
    private static final String URL = "jdbc:mysql://localhost:3306/lms_db";
    private static final String USER = "root";
    private static final String PASSWORD = "mysqlpassword"; // <--- CHANGE THIS

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            // System.out.println("Database connected successfully!"); // Debug line

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }
        return conn;
    }
}