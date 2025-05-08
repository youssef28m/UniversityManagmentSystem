package users;
import academics.*;
import database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class Faculty extends User {

    private String facultyId;
    private int department = -1;
    private String expertise;
    private ArrayList<String> coursesTeaching;
    private DatabaseManager db = new DatabaseManager();


    public Faculty(String userId, String username, String password, String name, String email, String contactInfo, String facultyId,  String expertise) {
        super(userId, username, password, name, email, contactInfo);
        this.facultyId = facultyId;
        this.expertise = expertise;
    }

    public Faculty(String userId, String username, String password, String name, String email, String contactInfo, String facultyId, int department, String expertise, ArrayList<String> coursesTeaching) {
        super(userId, username, password, name, email, contactInfo);
        this.facultyId = facultyId;
        this.department = department;
        this.expertise = expertise;
        this.coursesTeaching = coursesTeaching;
    }

    @Override
    String updateProfile() {
        return "";
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

    public ArrayList<String> getCoursesTeaching() {
        return coursesTeaching;
    }

    public void setCoursesTeaching(ArrayList<String> coursesTeaching) {
        this.coursesTeaching = coursesTeaching;
    }

    public void assignGrades(Student student, Course course, String grade) {
        if (!coursesTeaching.contains(course)) {
            System.out.println("You do not teach this course.");
            return;
        }
        Enrollment enrollment = course.getEnrollmentForStudent(student);
        if (enrollment == null) {
            System.out.println("The student is not enrolled in this course.");
            return;
        }
        enrollment.assignGrade(grade);
        System.out.println("Grade assigned successfully to " + student.getName() + " in " + course.getTitle());
    }
    public void manageCourse(Course course) {
        if (!coursesTeaching.contains(course)) {
            System.out.println("You do not have permission to manage this course.");
            return;
        }

        // Placeholder for management actions (e.g., edit title, description, etc.)
        System.out.println("Managing course: " + course.getTitle());
        // Example: course.setTitle("New Title");
    }
    private String officeHours;

public void setOfficeHours(String officeHours) {
    this.officeHours = officeHours;
    System.out.println("Office hours updated to: " + officeHours);
}

public String getOfficeHours() {
    return officeHours;
}
public void viewStudentRoster(Course course) {
    if (!coursesTeaching.contains(course)) {
        System.out.println("You are not assigned to this course.");
        return;
    }

    List<Student> students = course.getEnrolledStudents();
    System.out.println("Students enrolled in " + course.getTitle() + ":");
    for (Student student : students) {
        System.out.println("- " + student.getName() + " (" + student.getStudentId() + ")");
    }
}



}
