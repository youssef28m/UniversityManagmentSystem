import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Enrollment{
    private String student;
    private int course;
    private String enrollmentDate;
    private int grade;
    private String status;
    private DatabaseManager db;

    public Enrollment(String student, int course, String enrollmentDate, int grade, String status, DatabaseManager db) {
        setCourse(course);
        this.student = student;
        setEnrollmentDate(enrollmentDate);
        assignGrade(grade);
        setStatus(status);
        this.db = db;
    }

    public Enrollment(String student, int course, String status, DatabaseManager db) {
        setCourse(course);
        this.student = student;
        this.enrollmentDate = LocalDate.now().toString();
        setStatus(status);
        this.db = db;
    }

    public Enrollment(String student, int course,  int grade, String status, DatabaseManager db) {
        setCourse(course);
        this.student = student;
        this.enrollmentDate = LocalDate.now().toString();
        assignGrade(grade);
        setStatus(status);
        this.db = db;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int courseId) {
        ArrayList<List<Object>> courses = db.getAllCourses();
        ArrayList<Integer> coursesIds = new ArrayList<>();

        for (List<Object> course : courses) {
            coursesIds.add((Integer) course.get(0));
        }
        if (coursesIds.contains(courseId)) {
            this.course = courseId;
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

        List<String> validStatuses = List.of("Active", "Withdrawn", "Completed", "Pending", "Failed");

        if (status != null && validStatuses.contains(status)) {
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
        if (db.getStudentsInCourse(course).contains(student)) {
            this.status = "Withdrawn";
            db.updateEnrollment(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "student='" + student + '\'' +
                ", course=" + course +
                ", enrollmentDate='" + enrollmentDate + '\'' +
                ", grade=" + grade +
                ", status='" + status + '\'' +
                ", db=" + db +
                '}';
    }
}
