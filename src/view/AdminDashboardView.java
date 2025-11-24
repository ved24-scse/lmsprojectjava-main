package view;

import dao.CourseDAO;
import dao.UserDAO;
import model.Admin;
import model.Course;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardView extends JFrame {
    private Admin admin;

    // TAB 1: User Management Components
    private JTable userTable;
    private DefaultTableModel userModel;
    private JTextField nameField, emailField, passField;
    private JComboBox<String> roleBox;

    // TAB 2: Course Management Components
    private JTable allCoursesTable;
    private DefaultTableModel allCoursesModel;

    public AdminDashboardView(Admin admin) {
        this.admin = admin;
        setTitle("Admin Dashboard - " + admin.getName());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use Tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: User Management
        tabbedPane.addTab("Manage Users", createUserPanel());

        // Tab 2: Course Management
        tabbedPane.addTab("Manage Courses", createCoursePanel());

        add(tabbedPane);
    }

    // =================================================================================
    // TAB 1 LOGIC: USER MANAGEMENT
    // =================================================================================
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 1. User Table
        String[] cols = {"ID", "Name", "Email", "Role"};
        userModel = new DefaultTableModel(cols, 0);
        userTable = new JTable(userModel);
        loadUsers(); // Load data immediately
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        // 2. Input Form (Bottom)
        JPanel formPanel = new JPanel(new GridLayout(2, 1));
        JPanel inputPanel = new JPanel(new FlowLayout());

        nameField = new JTextField(15);
        emailField = new JTextField(15);
        passField = new JTextField(10);
        String[] roles = {"STUDENT", "INSTRUCTOR", "ADMIN"};
        roleBox = new JComboBox<>(roles);

        inputPanel.add(new JLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(new JLabel("Email:")); inputPanel.add(emailField);
        inputPanel.add(new JLabel("Pass:")); inputPanel.add(passField);
        inputPanel.add(new JLabel("Role:")); inputPanel.add(roleBox);

        // 3. Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Create User");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh List");

        addButton.addActionListener(e -> addUser());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUsers());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        formPanel.add(inputPanel);
        formPanel.add(buttonPanel);

        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadUsers() {
        userModel.setRowCount(0);
        List<User> users = UserDAO.getAllUsers();
        for (User u : users) {
            userModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole()});
        }
    }

    private void addUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String pass = passField.getText();
        String role = (String) roleBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        boolean success = UserDAO.addUser(name, email, pass, role);
        if (success) {
            JOptionPane.showMessageDialog(this, "User Created Successfully!");
            loadUsers();
            nameField.setText("");
            emailField.setText("");
            passField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Error creating user (Email might exist).");
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.");
            return;
        }

        int userId = (int) userModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete User ID: " + userId + "?");

        if (confirm == JOptionPane.YES_OPTION) {
            if (UserDAO.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this, "User Deleted.");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting user.");
            }
        }
    }

    // =================================================================================
    // TAB 2 LOGIC: COURSE MANAGEMENT
    // =================================================================================
    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 1. Course Table
        String[] cols = {"ID", "Course Name", "Description", "Instructor ID"};
        allCoursesModel = new DefaultTableModel(cols, 0);
        allCoursesTable = new JTable(allCoursesModel);
        loadAllCourses(); // Load data immediately

        // 2. Buttons
        JPanel btnPanel = new JPanel();
        JButton deleteBtn = new JButton("Delete Selected Course");
        JButton refreshBtn = new JButton("Refresh List");

        deleteBtn.addActionListener(e -> deleteSelectedCourse());
        refreshBtn.addActionListener(e -> loadAllCourses());

        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        panel.add(new JScrollPane(allCoursesTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadAllCourses() {
        allCoursesModel.setRowCount(0);
        List<Course> courses = CourseDAO.getAllCourses();
        for (Course c : courses) {
            allCoursesModel.addRow(new Object[]{c.getCourseId(), c.getCourseName(), c.getDescription(), c.getInstructorId()});
        }
    }

    private void deleteSelectedCourse() {
        int row = allCoursesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to delete.");
            return;
        }

        int courseId = (int) allCoursesModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete Course ID: " + courseId + "?");

        if (confirm == JOptionPane.YES_OPTION) {
            if (CourseDAO.deleteCourse(courseId)) {
                JOptionPane.showMessageDialog(this, "Course Deleted.");
                loadAllCourses();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting course.");
            }
        }
    }
}