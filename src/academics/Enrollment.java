package academics;
import database.*;
import users.Student;
import util.Helper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Enrollment{
    private Student student;
    private Course course;
    private String enrollmentDate;
    private int grade;
    private String status;
    private DatabaseManager db = new DatabaseManager();

    public Enrollment(Student student, Course course, String enrollmentDate, int grade, String status) {
        setCourse(course);
        this.student = student;
        setEnrollmentDate(enrollmentDate);
        assignGrade(grade);
        setStatus(status);
    }

    public Enrollment(Student student, Course course, String status) {
        setCourse(course);
        this.student = student;
        this.enrollmentDate = LocalDate.now().toString();
        setStatus(status);
    }

    public Enrollment(Student student, Course course,  int grade, String status) {
        setCourse(course);
        this.student = student;
        this.enrollmentDate = LocalDate.now().toString();
        assignGrade(grade);
        setStatus(status);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        ArrayList<List<Object>> dbCourses = db.getAllCourses();
        ArrayList<Integer> courseIds = new ArrayList<>();

        for (List<Object> dbCourse : dbCourses) {
            courseIds.add((Integer) dbCourse.get(0));
        }
        if (courseIds.contains(course.getCourseId())) {
            this.course = course;
        } else {
            throw new IllegalArgumentException("there is not course with this id");
        }
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        if (Helper.isValidDate(enrollmentDate, "yyyy-MM-dd")) {
            this.enrollmentDate = enrollmentDate;
        } else {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd format.");
        }
    }

    public int getGrade() {
        return grade;
    }

    public void setStatus(String status) {

        List<String> validStatuses = List.of("active", "withdrawn", "completed");

        if (status != null && validStatuses.contains(status.toLowerCase())) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Invalid enrollment status. Valid options are: "
                    + String.join(", ", validStatuses));
        }
    }

    public boolean assignGrade(int grade) {
        if (grade >= 0 && grade <= 100) {
            this.grade = grade;
            return true;
        } else {
            return false;
        }
    }

    public String getStatus() {
        return status;
    }


    public boolean withdraw() {
        if (db.getStudentsInCourse(course.getCourseId()).contains(student.getStudentId())) {
            this.status = "Withdrawn";
            db.updateEnrollment(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "acadimics.Enrollment{" +
                "student='" + student.getStudentId() + '\'' +
                ", course=" + course.getCourseId() +
                ", enrollmentDate='" + enrollmentDate + '\'' +
                ", grade=" + grade +
                ", status='" + status + '\'' +
                '}';
    }
}