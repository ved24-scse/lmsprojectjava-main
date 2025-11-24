package model;

public class Course {
    private int courseId;
    private String courseName;
    private String description;
    private int instructorId; // Links to the Instructor who teaches it

    public Course(int courseId, String courseName, String description, int instructorId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.instructorId = instructorId;
    }

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    @Override
    public String toString() {
        return courseName; // Useful for displaying course names in GUI Dropdowns (ComboBox)
    }
}