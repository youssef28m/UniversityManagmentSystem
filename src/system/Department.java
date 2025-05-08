package system;
import users.*;
import academics.*;
import java.util.ArrayList;
import java.util.List;

public class Department {

    private String departmentId;
    private String name;
    private ArrayList<Faculty> faculty;
    private ArrayList<Course> offeredCourses;

    public Department(String departmentId, String name) {
        this.departmentId = departmentId;
        this.name = name;
    }

    public void addFaculty(Faculty facultyMember) {
        // Method to add a faculty member
    }

    public void removeFaculty(Faculty facultyMember) {
        // Method to remove a faculty member
    }

    public void addCourse(Course course) {
        // Method to add a course
    }

    public List<Faculty> getFacultyList() {
        // Method to return the list of faculty
        return null;
    }

    // Getters and setters
}

