package com.system;

import com.users.*;
import com.academics.*;
import com.database.DatabaseManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a university management system.
 */
public class University {
    private DatabaseManager db;
    private ArrayList<Department> departments;
    private ArrayList<User> users;
    private ArrayList<Course> courses;
    private HashMap<String, String> academicCalendar; // Key: Event name, Value: Date

    /**
     * Initializes the University management system.
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
     * Loads initial data from the database, including users, departments, and courses.
     */
    public void loadInitialData() {
        // Load users
        ArrayList<String[]> usersData = db.getAllUsers();
        for (String[] userData : usersData) {
            String userId = userData[0];
            String userType = userData[1];

            // Create appropriate user object based on type
            switch (userType) {
                case "Student":
                    Student student = db.getStudentByUserId(userId);
                    if (student != null) users.add(student);
                    break;
                case "Faculty":
                    Faculty faculty = db.getFaculty(userId);
                    if (faculty != null) users.add(faculty);
                    break;
                case "Admin Staff":
                    AdminStaff adminStaff = db.getAdminStaffByUserId(userId);
                    if (adminStaff != null) users.add(adminStaff);
                    break;
                case "System Admin":
                    SystemAdmin systemAdmin = db.getSystemAdminByUserId(userId);
                    if (systemAdmin != null) users.add(systemAdmin);
                    break;
                default:
                    break;
            }
        }

        // Load departments
        List<Department> dbDepartments = db.getAllDepartments();
        if (dbDepartments != null && !dbDepartments.isEmpty()) {
            departments.addAll(dbDepartments);
        }

        // Load courses
        ArrayList<List<Object>> dbCourses = db.getAllCourses();
        if (dbCourses != null && !dbCourses.isEmpty()) {
            for (List<Object> courseData : dbCourses) {
                Course course = db.getCourse((Integer) courseData.get(0));
                if (course != null && !courses.contains(course)) {
                    courses.add(course);
                }
            }
        }
    }

    /**
     * Authenticates a user based on ID and password.
     *
     * @param userId   User ID
     * @param password User password
     * @return User object if authenticated, null otherwise
     */
    public User authenticateUser(String userId, String password) {
        String[] userData = db.getOneUser(userId);
        if (userData != null && userData[2].equals(password)) {
            String userType = userData[6];
            switch (userType) {
                case "Student":
                    return db.getStudentByUserId(userId);
                case "Faculty":
                    return db.getFacultyByUserId(userId);
                case "System Admin":
                    return db.getSystemAdminByUserId(userId);
                case "Admin Staff":
                    return db.getAdminStaffByUserId(userId);
                default:
                    return null;
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
        if (success && !courses.contains(course)) {
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
        return db.getCourse(courseId);
    }
}