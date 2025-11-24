package model;

import view.InstructorDashboardView;

public class Instructor extends User {
    private String department;

    public Instructor(int id, String name, String email, String password, String department) {
        super(id, name, email, password, "INSTRUCTOR");
        this.department = department;
    }

    public String getDepartment() { return department; }

    @Override
    public void openDashboard() {
        new InstructorDashboardView(this).setVisible(true);
    }
}
