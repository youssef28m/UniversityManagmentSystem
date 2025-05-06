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

    public  double calculateGPA(double[] grades, int[] credits) {
        double totalPoints = 0;
        int totalCredits = 0;

        for (int i = 0; i < grades.length; i++) {
            double gpa = gradeToGPA(grades[i]);
            totalPoints += gpa * credits[i];
            totalCredits += credits[i];
        }

        if (totalCredits == 0) return 0.0;
        return Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }        

    
}
