package com.system;
import com.academics.Course;
import com.database.DatabaseManager;
import com.users.Faculty;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Department {

    private int departmentId;
    private String name;
    private List<Faculty> faculty = new ArrayList<>();
    private List<Course> offeredCourses = new ArrayList<>();
    private DatabaseManager db = new DatabaseManager();

    public Department(int departmentId, String name) {
        this.departmentId = departmentId;
        setName(name);
    }

    public Department(List<Course> offeredCourses, List<Faculty> faculty, String name, int departmentId) {
        this.offeredCourses = offeredCourses;
        this.faculty = faculty;
        setName(name);
        this.departmentId = departmentId;
    }

    public boolean addFaculty(Faculty facultyMember) throws SQLException {

        if (facultyMember == null) {
            throw new IllegalArgumentException("Faculty member cannot be null");
        }

        if(faculty.contains(facultyMember)) {
            System.out.println("faculty already exists ");
            return false;
        }

        if(db.getFaculty(facultyMember.getFacultyId()) == null) {
            System.out.println("faculty does not exists");
            return false;
        }

        facultyMember.setDepartment(departmentId);
        if(db.updateFaculty(facultyMember)) {
            faculty.add(facultyMember);
            return true;
        } else {
            throw new SQLException("Failed to update faculty department in DB.");
        }
    }

    public boolean removeFaculty(Faculty facultyMember) throws SQLException {
        if (!faculty.contains(facultyMember)) {
            System.out.println("Faculty not found in department.");
            return false;
        }

        facultyMember.setDepartment(-1);
        if (db.updateFaculty(facultyMember)) {
            faculty.remove(facultyMember);
            return true;
        } else {
            throw new SQLException("Failed to update faculty department in DB.");
        }
    }

    public boolean addCourse(Course course) throws SQLException {

        if (course == null) {
            throw new IllegalArgumentException("course cannot be null");
        }

        if(offeredCourses.contains(course)) {
            System.out.println("course already exists ");
            return false;
        }

        if(db.getCourse(course.getCourseId()) == null) {
            System.out.println("course does not exists");
            return false;
        }

        course.setDepartment(departmentId);
        if(db.updateCourse(course)) {
            offeredCourses.add(course);
            return true;
        } else {
            throw new SQLException("Failed to update course department id in DB.");
        }
    }

    public boolean removeCourse(Course course) throws SQLException {
        if (!offeredCourses.contains(course)) {
            System.out.println("Course not found in department.");
            return false;
        }

        course.setDepartment(-1);
        if (db.updateCourse(course)) {
            offeredCourses.remove(course);
            return true;
        } else {
            throw new SQLException("Failed to update course department in DB.");
        }
    }

    public List<Faculty> getFacultyList() {
        return faculty;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        this.name = name;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public List<Course> getOfferedCourses() {
        return new ArrayList<>(offeredCourses);
    }

    public void setOfferedCourses(List<Course> offeredCourses) {
        this.offeredCourses = new ArrayList<>(offeredCourses);
    }
}

