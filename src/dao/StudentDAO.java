package dao;

import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // Get all students enrolled in a specific course
    public static List<Student> getStudentsByCourse(int courseId) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT u.id, u.name, u.email " +
                "FROM users u " +
                "JOIN enrollments e ON u.id = e.student_id " +
                "WHERE e.course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Create student object (Password is hidden/dummy here for security)
                students.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"), ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}