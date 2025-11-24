package model;

import view.StudentDashboardView;

public class Student extends User {
    // Specific field for student, e.g., GPA or enrollment year
    private double gpa;

    public Student(int id, String name, String email, String password) {
        super(id, name, email, password, "STUDENT");
    }

    @Override
    public void openDashboard() {
        StudentDashboardView dashboard = new StudentDashboardView(this);
        dashboard.setVisible(true);
    }
}
