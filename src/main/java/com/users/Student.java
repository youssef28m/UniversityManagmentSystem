package com.users;

import com.academics.Course;
import com.academics.Enrollment;
import com.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private String studentId;
    private String admissionDate;
    private AcademicStatus academicStatus = AcademicStatus.ACTIVE;
    private List<Integer> enrolledCourses = new ArrayList<>();
    private DatabaseManager db = new DatabaseManager();

    public Student(String userId, String username, String password, String name, String email, String contactInfo,
                   String studentId, String admissionDate, String academicStatus, List<Integer> enrolledCourses) {
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

    public Student() {

    }

    public enum AcademicStatus {
        ACTIVE("Active"),
        PROBATION("Probation"),
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
    public boolean updateProfile() {
        // Ensure DatabaseManager is initialized
        if (db == null) {
            return false;
        }

        // Prepare user data for update
        String[] userData = {getUsername(), getName(), getEmail(), getContactInfo(), UserType.STUDENT.getDisplayName()};

        try {
            boolean success = db.updateUserProfile(getUserId(), userData);

            if (success) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            // Catch any unexpected exceptions
            System.out.println("Failed: An error occurred while updating the profile - " + e.getMessage());
            return false;
        }

    }


    public double gradeToGPA(double grade) {
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

    public boolean registerForCourse(Course course) {
        if (course == null) {
            System.out.println("Registration failed: Invalid course.");
            return false;
        }

        if (course.getAvailableSeats() <= 0) {
            System.out.println("Registration failed: No available seats in " + course.getTitle());
            return false;
        }

        List<Integer> completedCourses = getCompletedCourses();
        if (!course.isPrerequisiteSatisfied(completedCourses)) {
            System.out.println("Registration failed: Prerequisites not satisfied.");
            return false;
        }

        List<Integer> currentEnrollments = db.getEnrolledCourses(studentId);
        if (currentEnrollments.contains(course.getCourseId())) {
            System.out.println("You are already registered in " + course.getTitle() + ".");
            return false;
        }

        // Create enrollment and add to database
        Enrollment enrollment = new Enrollment(this, course, "active");
        boolean success = db.enrollStudent(enrollment);
        if (success) {
            try {
                course.addStudent(this); // Update course's student list
            } catch (Exception e) {
                System.err.println("Error adding student to course : " + e.getMessage());
            }
            return true;
        } else {
            return false;
        }
    }

    public List<Integer> getCompletedCourses() {

        List<Integer> completedCourses = new ArrayList<>();
        for (int courseId : enrolledCourses) {
            Enrollment enrollment = db.getEnrollment(studentId, courseId);
            if (enrollment != null) {
                if (enrollment.getStatus().equals("completed")) {
                    completedCourses.add(courseId);
                }
            }
        }

        return completedCourses;
    }

    public boolean dropCourse(Course course) {
        if (course == null) {
            System.out.println("Drop failed: Invalid course.");
            return false;
        }

        List<Integer> currentEnrollments = db.getEnrolledCourses(studentId);
        if (!currentEnrollments.contains(course.getCourseId())) {
            System.out.println("You are not registered in " + course.getTitle() + ".");
            return false;
        }

        boolean success = db.removeStudentFromCourse(studentId, course.getCourseId());
        if (success) {
            try {
                course.removeStudent(this); // Update course's student list
            } catch (Exception e) {
                System.out.println("error removing student from course :" + e.getMessage());
            }
            return true;
        } else {
            System.out.println("Drop failed: Database error.");
            return false;
        }
    }

    public void viewGrades() {
        List<Integer> courseIds = db.getEnrolledCourses(studentId);
        if (courseIds.isEmpty()) {
            System.out.println("You are not enrolled in any courses.");
            return;
        }

        System.out.println("Grades for " + getName() + ":");
        for (int courseId : courseIds) {
            Course course = db.getCourse(courseId);
            if (course != null) {
                Enrollment enrollment = db.getEnrollment(studentId, courseId);
                String grade = (enrollment != null && enrollment.getGrade() > 0)
                        ? String.valueOf(enrollment.getGrade())
                        : "N/A";
                System.out.println(course.getTitle() + ": " + grade);
            }
        }
    }

    public void viewEnrolledCourses() {
        List<Integer> courseIds = db.getEnrolledCourses(studentId);
        if (courseIds.isEmpty()) {
            System.out.println("You are not enrolled in any courses.");
            return;
        }

        System.out.println("Enrolled courses for " + getName() + ":");
        for (int courseId : courseIds) {
            Course course = db.getCourse(courseId);
            if (course != null) {
                System.out.println(course.getTitle());
            }
        }
    }

    public double calculateGPA() {
        List<Integer> courseIds = db.getEnrolledCourses(studentId);
        if (courseIds.isEmpty()) {
            return 0.0;
        }

        double totalPoints = 0.0;
        int validCourses = 0;
        for (int courseId : courseIds) {
            Enrollment enrollment = db.getEnrollment(studentId, courseId);
            if (enrollment != null && enrollment.getGrade() > 0) { // Only count graded courses
                totalPoints += gradeToGPA(enrollment.getGrade());
                validCourses++;
            }
        }

        double gpa = validCourses > 0 ? totalPoints / validCourses : 0.0;
        return Math.round(gpa * 100.0) / 100.0; // Round to two decimal places
    }

    //----------------------------------------------------------//
    // Getters and setters
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

    public void setAcademicStatus(AcademicStatus academicStatus) {
        this.academicStatus = academicStatus;
    }

    public List<Integer> getEnrolledCourses() {
        return db.getEnrolledCourses(studentId); // Fetch from database
    }

    public void setEnrolledCourses(List<Integer> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
}