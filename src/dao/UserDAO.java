package dao;

import model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Method to check login credentials and return the correct User object
    public static User authenticateUser(String email, String password) {
        User user = null;
        // Using PreparedStatement prevents SQL Injection (Good Coding Practice)
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String role = rs.getString("role");

                // Factory Pattern logic: Create specific object based on Role
                switch (role) {
                    case "ADMIN":
                        user = new Admin(id, name, email, password);
                        break;
                    case "INSTRUCTOR":
                        String dept = rs.getString("department");
                        user = new Instructor(id, name, email, password, dept);
                        break;
                    case "STUDENT":
                        user = new Student(id, name, email, password);
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user; // Returns null if login fails, or a valid User object if successful
    }
    // 1. Get ALL Users (For Admin View)
    public static java.util.List<User> getAllUsers() {
        java.util.List<User> users = new java.util.ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");

                // Factory Logic to create specific objects
                User u = null;
                switch (role) {
                    case "ADMIN": u = new Admin(id, name, email, password); break;
                    case "INSTRUCTOR": u = new Instructor(id, name, email, password, rs.getString("department")); break;
                    case "STUDENT": u = new Student(id, name, email, password); break;
                }
                if (u != null) users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // 2. Add New User
    public static boolean addUser(String name, String email, String password, String role) {
        String query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Delete User
    public static boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // This catches cases where you try to delete an Instructor who has courses (Foreign Key Constraint)
            System.out.println("Cannot delete user: They might be linked to existing courses or enrollments.");
            e.printStackTrace();
            return false;
        }
    }
}