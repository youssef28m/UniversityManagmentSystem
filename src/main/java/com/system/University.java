package com.system;
import com.users.*;

import java.sql.SQLException;
import java.util.List;
import com.academics.*;
import com.database.DatabaseManager;


public class University {
    private List<Department> departments;
    private List<User> users;
    private List<Course> courses;
    private DatabaseManager db = new DatabaseManager();

    public University(List<Department> departments, List<User> users, List<Course> courses) {
        this.departments = departments;
        this.users = users;
        this.courses = courses;
    }

    public void registerStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }

    }
    
    public void hireFaculty(Faculty faculty) {}

    public void createDepartment(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }

        // Check if a department with the same ID or name already exists
        if (departments.stream().anyMatch(d ->
                d.getDepartmentId() == department.getDepartmentId() || d.getName().equalsIgnoreCase(department.getName()))) {
            throw new IllegalArgumentException("A department with the same ID or name already exists");
        }
        // Add department to the list
        departments.add(department);
        System.out.println("Department " + department.getName() + " has been successfully created.");

        try {
            db.createDepartment(department);
        } catch (Exception e) {
            System.err.println("Failed to save the department to the database: " + e.getMessage());
        }
    }

    public void offerCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }


    }
}
