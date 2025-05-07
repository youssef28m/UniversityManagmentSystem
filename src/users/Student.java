package users;

import java.util.ArrayList;

public class Student extends User {
    private String studentId;
    private String admissionDate;
    private AcademicStatus academicStatus = AcademicStatus.ACTIVE;
    private ArrayList<Integer> enrolledCourses = new ArrayList<>();

    public Student(String userId, String username, String password, String name, String email, String contactInfo,
                   String studentId, String admissionDate, String academicStatus, ArrayList<Integer> enrolledCourses) {
        super(userId, username, password, name, email, contactInfo);
        this.studentId = studentId;
        this.admissionDate = admissionDate;
        this.academicStatus = AcademicStatus.fromString(academicStatus);
        this.enrolledCourses = enrolledCourses;
        setUserType(UserType.STUDENT);
    }

    public Student(String userId, String username, String password, String name, String email, String contactInfo,
                   String studentId, String admissionDate, String academicStatus) {
        super(userId, username, password, name, email, contactInfo);
        this.studentId = studentId;
        this.admissionDate = admissionDate;
        this.academicStatus = AcademicStatus.fromString(academicStatus);
        setUserType(UserType.STUDENT);
    }

    public enum AcademicStatus {

        ACTIVE("Active"),
        ON_PROBATION("On Probation"),
        GRADUATED("Graduated");

        private final String displayName;

        AcademicStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static AcademicStatus fromString(String status) {
            try {
                return AcademicStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return AcademicStatus.ACTIVE;
            }
        }
    }

    @Override
    public String updateProfile() {
        return "";
    }


    //----------------------------------------------------------//
    // geters and setters
    //----------------------------------------------------------//
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getacademicStatus() {
        return academicStatus.getDisplayName();
    }

    public void setacademicStatus(AcademicStatus academicStatus) {
        this.academicStatus = academicStatus;
    }

    public ArrayList<Integer> getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(ArrayList<Integer> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
    public  double gradeToGPA(double grade) {
        if (grade >= 90) return 4.0;
        else if (grade >= 85) return 3.7;
        else if (grade >= 80) return 3.3;
        else if (grade >= 75) return 3.0;
        else if (grade >= 70) return 2.7;
        else if (grade >= 65) return 2.3;
        else if (grade >= 60) return 2.0;
        else if (grade >= 55) return 1.7;
        else if (grade >= 50) return 1.0;
        else return 0.0;
    }

          
    public void registerForCourse(Course course) {
        if (course.getAvailableSeats() <= 0) {
            System.out.println("Registration failed: No available seats in " + course.getTitle());
            return;
        }
        if (!course.isPrerequisiteSatisfied(this)) {
            System.out.println("Registration failed: Prerequisites not satisfied.");
            return;
        }
        if (enrolledCourses.contains(course)) {
            System.out.println("You are already registered in this course.");
            return;
        }
        enrolledCourses.add(course);
        course.addStudent(this);
        System.out.println("Successfully registered for " + course.getTitle());
    }
    public void dropCourse(Course course) {
        if (!enrolledCourses.contains(course)) {
            System.out.println("You are not registered in this course.");
            return;
        }
        enrolledCourses.remove(course);
        course.removeStudent(this);
        System.out.println("Successfully dropped " + course.getTitle());
    }    
    public void viewGrades() {
        if (enrolledCourses.isEmpty()) {
            System.out.println("You are not enrolled in any courses.");
            return;
        }
        System.out.println("Grades for " + getName() + ":");
        for (Course course : enrolledCourses) {
            Enrollment enrollment = course.getEnrollmentForStudent(this);
            String grade = (enrollment != null) ? enrollment.getGrade() : "N/A";
            System.out.println(course.getTitle() + ": " + grade);
        }
    }
    public void viewEnrolledCourses() {
        if (enrolledCourses.isEmpty()) {
            System.out.println("You are not enrolled in any courses.");
        }
        System.out.println("Enrolled courses for " + getName() + ":");
        for (Course course : enrolledCourses) {
            System.out.println(course.getTitle());
        }
    } 
    public double calculateGPA() {
        double totalPoints = 0.0;
        int totalCourses = enrolledCourses.size();
        if (totalCourses == 0) return 0.0;

        for (int courseId : enrolledCourses) {
            Course course = Course.getCourseById(courseId);
            if (course != null) {
                Enrollment enrollment = course.getEnrollmentForStudent(this);
                if (enrollment != null) {
                    double grade = enrollment.getGrade();
                    totalPoints += gradeToGPA(grade);
                }
            }
        }
        return totalPoints / totalCourses;
    }     
    
}
