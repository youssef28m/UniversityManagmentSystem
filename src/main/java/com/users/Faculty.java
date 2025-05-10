package com.users;
import com.academics.Course;
import com.database.DatabaseManager;

import java.util.ArrayList;

public class Faculty extends User {

    private String facultyId;
    private int department = -1;
    private String expertise;
    private ArrayList<Course> coursesTeaching;
    private DatabaseManager db = new DatabaseManager();


    public Faculty(String userId, String username, String password, String name, String email, String contactInfo, String facultyId, String expertise) {
        super(userId, username, password, name, email, contactInfo);
        this.facultyId = facultyId;
        this.expertise = expertise;
    }

    public Faculty(String userId, String username, String password, String name, String email, String contactInfo, String facultyId, String expertise, int department) {
        super(userId, username, password, name, email, contactInfo);
        this.facultyId = facultyId;
        this.expertise = expertise;
        this.department = department;
    }

    public Faculty(String userId, String username, String password, String name, String email, String contactInfo, String facultyId, int department, String expertise, ArrayList<Course> coursesTeaching) {
        super(userId, username, password, name, email, contactInfo);
        this.facultyId = facultyId;
        this.department = department;
        this.expertise = expertise;
        this.coursesTeaching = coursesTeaching;
    }

    @Override
    boolean updateProfile() {
        if (db == null) {
            return false;
        }

        String[] userData = {getUsername(), getName(), getEmail(), getContactInfo(), UserType.FACULTY.getDisplayName(), };
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

    public boolean addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (coursesTeaching == null) {
            coursesTeaching = new ArrayList<>();
        }
        if (coursesTeaching.contains(course)) {
            System.out.println("The course is already being taught by the faculty.");
            return false;
        }
        coursesTeaching.add(course);
        course.setInstructor(facultyId);
        if (db.updateCourse(course)) {
            return true;
        } else {
            throw new IllegalArgumentException("Failed to update course in DB.");
        }
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public ArrayList<Course> getCoursesTeaching() {
        return coursesTeaching;
    }

    public void setCoursesTeaching(ArrayList<Course> coursesTeaching) {
        this.coursesTeaching = coursesTeaching;
    }

//    public void assignGrades(Student student, Course course, String grade) {
//        if (!coursesTeaching.contains(course)) {
//            System.out.println("You do not teach this course.");
//            return;
//        }
//        Enrollment enrollment = course.getEnrolledStudents(student);
//        if (enrollment == null) {
//            System.out.println("The student is not enrolled in this course.");
//            return;
//        }
//        enrollment.assignGrade(grade);
//        System.out.println("Grade assigned successfully to " + student.getName() + " in " + course.getTitle());
//    }
//
//    public void manageCourse(Course course, String operation, String attribute) {
//
//
//        if (!coursesTeaching.contains(course)) {
//            System.out.println("You do not have permission to manage this course.");
//            return;
//        }
//
//        // Placeholder for management actions (e.g., edit title, description, etc.)
//        System.out.println("Managing course: " + course.getTitle());
//        // Example: course.setTitle("New Title");
//    }
//
//    private String officeHours;
//
//    public void setOfficeHours(String officeHours) {
//        this.officeHours = officeHours;
//        System.out.println("Office hours updated to: " + officeHours);
//    }
//
//    public String getOfficeHours() {
//        return officeHours;
//    }
//
//    public void viewStudentRoster(Course course) {
//        if (!coursesTeaching.contains(course)) {
//            System.out.println("You are not assigned to this course.");
//            return;
//        }
//
//        List<String> students = course.getEnrolledStudents();
//        System.out.println("Students enrolled in " + course.getTitle() + ":");
//        for (String studentId : students) {
//            Student dbStudent = db.getStudent(studentId);
//            System.out.println("- " + dbStudent.getName() + " (" + dbStudent.getStudentId() + ")");
//        }
//
//
//    }
}