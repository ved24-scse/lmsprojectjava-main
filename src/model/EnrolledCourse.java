package model;

public class EnrolledCourse extends Course {
    private String grade;

    public EnrolledCourse(int id, String name, String desc, int instructorId, String grade) {
        super(id, name, desc, instructorId); // Call parent constructor
        this.grade = grade;
    }

    // In model/EnrolledCourse.java

    public String getGrade() {
        // Simply display NULL if the grade is not set, otherwise show the grade
        return (grade == null) ? "—" : grade;
    }
}