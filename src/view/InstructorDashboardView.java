package view;

import dao.CourseDAO;
import dao.StudentDAO;
import model.Course;
import model.Instructor;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorDashboardView extends JFrame {
    private Instructor instructor;

    // Tab 1 Components (Manage Courses)
    private JTable myCoursesTable;
    private DefaultTableModel myCoursesModel;

    // Tab 2 Components (Grading)
    private JComboBox<Course> courseSelector;
    private JTable gradingTable;
    private DefaultTableModel gradingModel;

    public InstructorDashboardView(Instructor instructor) {
        this.instructor = instructor;

        setTitle("Instructor Dashboard - " + instructor.getName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manage Courses", createCoursePanel());
        tabbedPane.addTab("Grade Students", createGradingPanel());

        add(tabbedPane);
    }

    // =================================================================================
    // TAB 1: MANAGE COURSES
    // =================================================================================
    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"ID", "Course Name", "Description"};
        myCoursesModel = new DefaultTableModel(cols, 0);
        myCoursesTable = new JTable(myCoursesModel);
        loadMyCourses();

        // Input Panel for New Course
        JPanel inputPanel = new JPanel(new GridLayout(1, 3));
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JButton addButton = new JButton("Add Course");

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Desc:"));
        inputPanel.add(descField);
        inputPanel.add(addButton);

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String desc = descField.getText();
            if (!name.isEmpty() && !desc.isEmpty()) {
                boolean success = CourseDAO.addCourse(name, desc, instructor.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Course Added!");
                    loadMyCourses(); // Refresh Table
                    loadCourseSelector(); // Refresh Dropdown in other tab
                    nameField.setText("");
                    descField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Error adding course.");
                }
            }
        });

        panel.add(new JScrollPane(myCoursesTable), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadMyCourses() {
        myCoursesModel.setRowCount(0);
        List<Course> courses = CourseDAO.getCoursesByInstructor(instructor.getId());
        for (Course c : courses) {
            myCoursesModel.addRow(new Object[]{c.getCourseId(), c.getCourseName(), c.getDescription()});
        }
    }

    // =================================================================================
    // TAB 2: GRADING STUDENTS
    // =================================================================================
    private JPanel createGradingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top: Course Selector
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Course to Grade:"));
        courseSelector = new JComboBox<>();
        loadCourseSelector();

        JButton loadStudentsBtn = new JButton("Load Students");
        loadStudentsBtn.addActionListener(e -> loadStudentsForGrading());

        topPanel.add(courseSelector);
        topPanel.add(loadStudentsBtn);

        // Center: Student Table
        String[] cols = {"Student ID", "Name", "Email", "Grade"};
        gradingModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only allow editing the Grade column (Index 3)
            }
        };
        gradingTable = new JTable(gradingModel);

        // Bottom: Save Button
        JButton saveButton = new JButton("Save Grades");
        saveButton.addActionListener(e -> saveGrades());

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(gradingTable), BorderLayout.CENTER);
        panel.add(saveButton, BorderLayout.SOUTH);

        return panel;
    }

    private void loadCourseSelector() {
        courseSelector.removeAllItems();
        List<Course> courses = CourseDAO.getCoursesByInstructor(instructor.getId());
        for (Course c : courses) {
            courseSelector.addItem(c);
        }
    }

    private void loadStudentsForGrading() {
        gradingModel.setRowCount(0);
        Course selected = (Course) courseSelector.getSelectedItem();

        if (selected != null) {
            List<Student> students = StudentDAO.getStudentsByCourse(selected.getCourseId());

            for (Student s : students) {
                // Initialize grade as empty string so it is clean for the instructor to type in
                String currentGrade = "";
                gradingModel.addRow(new Object[]{s.getId(), s.getName(), s.getEmail(), currentGrade});
            }
        }
    }

    private void saveGrades() {
        // --- CRITICAL FIX: Stop editing to ensure data is committed to model ---
        if (gradingTable.isEditing()) {
            gradingTable.getCellEditor().stopCellEditing();
        }
        // -----------------------------------------------------------------------

        Course selected = (Course) courseSelector.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first.");
            return;
        }

        int courseId = selected.getCourseId();
        boolean saveSuccess = true;
        int successfullyUpdated = 0;

        int rows = gradingModel.getRowCount();
        for (int i = 0; i < rows; i++) {
            try {
                // 1. SAFELY RETRIEVE IDs
                Object studentIdObj = gradingModel.getValueAt(i, 0);
                int studentId;

                // Handle case where ID might be String or Integer
                if (studentIdObj instanceof Integer) {
                    studentId = (int) studentIdObj;
                } else if (studentIdObj instanceof String) {
                    studentId = Integer.parseInt((String) studentIdObj);
                } else {
                    studentId = Integer.parseInt(studentIdObj.toString());
                }

                // 2. RETRIEVE GRADE
                Object gradeObj = gradingModel.getValueAt(i, 3);
                String grade = (gradeObj == null) ? null : gradeObj.toString().trim();

                // Skip saving if the grade cell is empty
                if (grade == null || grade.isEmpty()) {
                    continue;
                }

                // 3. CALL DATABASE
                if (CourseDAO.updateGrade(studentId, courseId, grade)) {
                    successfullyUpdated++;
                } else {
                    saveSuccess = false;
                }
            } catch (Exception e) {
                System.out.println("Error processing row " + i + ": " + e.getMessage());
                saveSuccess = false;
            }
        }

        // 4. FEEDBACK TO USER
        if (saveSuccess && successfullyUpdated > 0) {
            JOptionPane.showMessageDialog(this, successfullyUpdated + " Grades Updated Successfully!");
        } else if (successfullyUpdated == 0 && rows > 0) {
            JOptionPane.showMessageDialog(this, "No grades changed. Type a grade and press 'Save'.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Errors occurred while saving. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}