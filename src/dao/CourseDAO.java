package dao;

import model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    // 1. Method to get ALL available courses (For the "Enroll" tab)
    public static List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM courses";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course c = new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("description"),
                        rs.getInt("instructor_id")
                );
                courses.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // 2. Method to Enroll a Student (This meets the Transaction Management requirement)
    public static boolean enrollStudent(int studentId, int courseId) {
        String query = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Updated Method: Returns EnrolledCourse objects which include the GRADE
    public static List<model.EnrolledCourse> getEnrolledCoursesWithGrades(int studentId) {
        List<model.EnrolledCourse> courses = new ArrayList<>();

        // Query fetches Course details AND the Grade from enrollments table
        String query = "SELECT c.course_id, c.course_name, c.description, c.instructor_id, e.grade " +
                "FROM courses c " +
                "JOIN enrollments e ON c.course_id = e.course_id " +
                "WHERE e.student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.EnrolledCourse ec = new model.EnrolledCourse(
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("description"),
                        rs.getInt("instructor_id"),
                        rs.getString("grade") // <--- Fetching the grade!
                );
                courses.add(ec);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    public static boolean addCourse(String name, String description, int instructorId) {
        String query = "INSERT INTO courses (course_name, description, instructor_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setInt(3, instructorId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Get Courses owned by specific Instructor
    public static List<Course> getCoursesByInstructor(int instructorId) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM courses WHERE instructor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("description"),
                        instructorId
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // In dao/CourseDAO.java

    // In dao/CourseDAO.java

    // 6. Update Student Grade (For Grading Tab)
    public static boolean updateGrade(int studentId, int courseId, String grade) {
        // 1st ? is Grade, 2nd is studentId, 3rd is courseId
        String query = "UPDATE enrollments SET grade = ? WHERE student_id = ? AND course_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Ensure auto-commit is TRUE for instant saving
            conn.setAutoCommit(true);

            // MAPPING: 1st ? = grade, 2nd ? = studentId, 3rd ? = courseId
            stmt.setString(1, grade.trim());       // Sets the grade value
            stmt.setInt(2, studentId);       // Checks the student's ID
            stmt.setInt(3, courseId);        // Checks the course's ID

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            } else {
                // This is the path your code is taking if rowsAffected is 0
                System.out.println("DEBUG: Grade update executed but 0 rows affected. Check IDs.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("ERROR: SQL Exception during grade update.");
            e.printStackTrace();
            return false;
        }
    }
    // In dao/CourseDAO.java

    public static boolean deleteCourse(int courseId) {
        String query = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
