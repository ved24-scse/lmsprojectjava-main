package view;

import dao.CourseDAO;
import model.Course;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboardView extends JFrame {
    private Student student;

    // Components for "Available Courses" tab
    private JTable availableCoursesTable;
    private DefaultTableModel availableCoursesModel;

    // Components for "My Courses" tab
    private JTable myCoursesTable;
    private DefaultTableModel myCoursesModel;

    public StudentDashboardView(Student student) {
        this.student = student;

        // 1. Window Setup
        setTitle("Student Dashboard - " + student.getName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 2. Create Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // 3. Add Tab 1: Available Courses (Enroll)
        JPanel enrollPanel = createEnrollPanel();
        tabbedPane.addTab("Available Courses", enrollPanel);

        // 4. Add Tab 2: My Enrolled Courses (View)
        // CRITICAL FIX: We call the method that creates the TABLE, not a placeholder label
        JPanel myCoursesPanel = createMyCoursesPanel();
        tabbedPane.addTab("My Courses", myCoursesPanel);

        add(tabbedPane);
    }

    // ---------------------------------------------------------
    // TAB 1: AVAILABLE COURSES
    // ---------------------------------------------------------
    private JPanel createEnrollPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table Setup
        String[] columns = {"ID", "Course Name", "Description"};
        availableCoursesModel = new DefaultTableModel(columns, 0);
        availableCoursesTable = new JTable(availableCoursesModel);

        // Load Data
        loadAvailableCourses();

        // Enroll Button
        JButton enrollButton = new JButton("Enroll in Selected Course");
        enrollButton.setFont(new Font("Arial", Font.BOLD, 14));
        enrollButton.addActionListener(e -> enrollInCourse());

        // Add to Panel
        panel.add(new JScrollPane(availableCoursesTable), BorderLayout.CENTER);
        panel.add(enrollButton, BorderLayout.SOUTH);

        return panel;
    }

    private void loadAvailableCourses() {
        List<Course> courses = CourseDAO.getAllCourses();
        availableCoursesModel.setRowCount(0);
        for (Course c : courses) {
            availableCoursesModel.addRow(new Object[]{c.getCourseId(), c.getCourseName(), c.getDescription()});
        }
    }

    private void enrollInCourse() {
        int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow != -1) {
            int courseId = (int) availableCoursesModel.getValueAt(selectedRow, 0);

            boolean success = CourseDAO.enrollStudent(student.getId(), courseId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Successfully Enrolled!");
                // Auto-refresh the "My Courses" tab immediately
                loadEnrolledCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Enrollment Failed (You might already be enrolled).");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll.");
        }
    }

    // ---------------------------------------------------------
    // TAB 2: MY COURSES
    // ---------------------------------------------------------
    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table Setup
        String[] columns = {"Course ID", "Course Name", "Description", "Instructor ID", "Grade"};
        myCoursesModel = new DefaultTableModel(columns, 0);
        myCoursesTable = new JTable(myCoursesModel);

        // Load Data Immediately
        loadEnrolledCourses();

        // Refresh Button
        JButton refreshButton = new JButton("Refresh My List");
        refreshButton.addActionListener(e -> loadEnrolledCourses());

        // Add to Panel
        panel.add(new JScrollPane(myCoursesTable), BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private void loadEnrolledCourses() {
        // Debug print
        System.out.println("Refreshing My Courses for Student ID: " + student.getId());

        myCoursesModel.setRowCount(0); // Clear old data

        // IMPORTANT: Use the new method we made in CourseDAO that fetches grades!
        List<model.EnrolledCourse> courses = CourseDAO.getEnrolledCoursesWithGrades(student.getId());

        for (model.EnrolledCourse c : courses) {
            myCoursesModel.addRow(new Object[]{
                    c.getCourseId(),
                    c.getCourseName(),
                    c.getDescription(),
                    c.getInstructorId(),
                    c.getGrade() // <--- This puts the grade in the 5th column
            });
        }
    }
}