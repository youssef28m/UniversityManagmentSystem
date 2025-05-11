package com.system;
import com.users.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.academics.*;
import com.database.DatabaseManager;



public class University {
    private DatabaseManager db;
    private ArrayList<Department> departments;
    private ArrayList<User> users;
    private ArrayList<Course> courses;
    private HashMap<String, String> academicCalendar; // Key: Event name, Value: Date

    /**
     * Initializes the University management system
     */
    public University() {
        this.db = new DatabaseManager();
        this.departments = new ArrayList<>();
        this.users = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.academicCalendar = new HashMap<>();

        db.createTables();

        // Load initial data from database
        loadInitialData();
    }

    /**
     * Loads initial data from the database
     */
    private void loadInitialData() {
        // Load users
        ArrayList<String[]> usersData = db.getAllUsers();
        for (String[] userData : usersData) {
            String userId = userData[0];
            String userType = userData[1];

            // Create appropriate user object based on type
            switch (userType) {
                case "student":
                    Student student = db.getStudent(userId);
                    if (student != null) users.add(student);
                    break;
                case "faculty":
                    Faculty faculty = db.getFaculty(userId);
                    if (faculty != null) users.add(faculty);
                    break;
                // Add other user types as needed
            }
        }
        // todo load departments, courses
    }

    /**
     * Authenticates a user based on ID and password
     * @param userId   User ID
     * @param password User password
     * @return User object if authenticated, null otherwise
     */
    public User authenticateUser(String userId, String password) {
        String[] userData = db.getOneUser(userId);
        if (userData != null) {
            // Assuming password is stored at index 2
            if (userData[2].equals(password)) {
                // Determine user type and return appropriate object
                String userType = userData[1]; // Assuming user type is at index 1

                switch (userType) {
                    case "student":
                        return db.getStudent(userId);
                    case "faculty":
                        return db.getFaculty(userId);
                    case "admin":
                        // todo make getSystemAdmin
                        //return db.getSystemAdmin(userId);
                    case "adminstaff":
                        // todo make getAdminStaff
                        //return db.getAdminStaff(userId);
                    default:
                        return null;
                }
            }
        }
        return null;
    }

    public boolean registerStudent(Student student) {

        if (db.addUser(student)) {
            boolean success = db.addStudent(student);

            if (success) {
                users.add(student);
            }
            return success;
        }
        return false;
    }

    public boolean hireFaculty(Faculty faculty) {
        if (db.addUser(faculty)) {
            boolean success = db.addFaculty(faculty);
            if (success) {
                users.add(faculty);
            }

            return success;
        }
        return false;
    }

    public boolean createDepartment(String name, int id) {
        Department department = new Department(id, name);

        boolean success = db.createDepartment(department);

        if (success) {
            departments.add(department);
        }

        return success;
    }

    public boolean offerCourse(Course course) {
        boolean success = db.addCourse(course);

        if (success) {
            courses.add(course);
        }

        return success;
    }


    public ArrayList<Department> getDepartments() {
        return new ArrayList<>(departments);
    }

    public ArrayList<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>(users);
    }

    public Department getDepartment(int departmentId) {
        for (Department dept : departments) {
            if (dept.getDepartmentId() == departmentId) {
                return dept;
            }
        }
        return db.getDepartment(departmentId);
    }

    public Course getCourse(int courseId) {
        for (Course course : courses) {
            if (course.getCourseId() == courseId) {
                return course;
            }
        }
        // If not found in local cache, try database
        return db.getCourse(courseId);
    }
}