import java.time.LocalDate;


public class Enrollment {
    private String studentId;
    private int courseId;
    private String enrollmentDate;
    private int grade;
    private String status;

    public Enrollment(String studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = LocalDate.now().toString();
        this.status = "Enrolled";
    }

    public void assignGrade(String grade) {
        // Method not implemented
    }

    public String getStatus() {
        // Method not implemented
        return null;
    }

    public void withdraw() {
        // Method not implemented
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public int getGrade() {
        return grade;
    }

    public String getStatusValue() {
        return status;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
