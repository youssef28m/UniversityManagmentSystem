public class Enrollment{
private String student;
private String course;
private String enrollmentDate;
private int grade;
private String status;
public Enrollment(String student, String course, String enrollmentDate, int grade, String status) {
    this.student = student;
    this.course = course;
    this.enrollmentDate = enrollmentDate;
    this.grade = grade;
    this.status = status;}
public String getStudent() {
    return student;}
public void setStudent(String student) {
    this.student = student;}
public String getCourse() {
    return course;}
public void setCourse(String course) {
    this.course = course;}
public String getEnrollmentDate() {
    return enrollmentDate;}
public void setEnrollmentDate(String enrollmentDate) {
    this.enrollmentDate = enrollmentDate;}
public int getGrade() {
    return grade;}
public void setGrade(int grade) {
    this.grade = grade;}
public String getStatus() {
    return status;}
public void setStatus(String status) {
    this.status = status;}
public int assignGrade(int grade) {
    this.grade = grade;
    return this.grade;}
    public boolen getStatus() {
        if( status.equals("active")) {
            return true;
        } else (status.equals("inactive")) {
            return false;
        } 
    }
public void setStatus(boolean status) {
        if(status == true) {
            this.status = "active";
        } else {
            this.status = "inactive";
        }
    }
    public void withdraw(){
            if (enrolledStudents.contains(student)) {
                enrolledStudents.remove(student);
                System.out.println(studentName + " has been withdrawn from the system.");
            } else {
                System.out.println("Student " + studentName + " is not enrolled.");
            }
        
    }






































}