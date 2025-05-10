package com.academics;
import com.database.DatabaseManager;
import com.users.Student;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private int courseId;
    private String title;
    private String description;
    private int creditHours;
    private List<Integer> prerequisites;
    private String schedule;
    private String instructor;
    private int department = -1;
    private int maxCapacity = 30;
    private List<String> enrolledStudents;
    public DatabaseManager db = new DatabaseManager();

    // Constructor
    public Course(int courseId, String title, String description, int creditHours,
                  List<Integer> prerequisites, String schedule, String instructor, int maxCapacity) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.creditHours = creditHours;
        this.prerequisites = prerequisites;
        this.schedule = schedule;
        this.instructor = instructor;
        this.maxCapacity = maxCapacity;
        this.enrolledStudents = new ArrayList<>();
    }

    public Course(int courseId, String title, String description, int creditHours, List<Integer> prerequisites, String schedule, String instructor, int maxCapacity, List<String> enrolledStudents, int department) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.creditHours = creditHours;
        this.prerequisites = prerequisites;
        this.schedule = schedule;
        this.instructor = instructor;
        this.maxCapacity = maxCapacity;
        this.enrolledStudents = enrolledStudents;
        this.department = department;
    }

    public boolean addStudent(Student student) {
        if (enrolledStudents.contains(student.getStudentId())) {
            throw new IllegalArgumentException("Student already exists in course");
        }

        // Check if student is in database
        boolean inDB = false;
        ArrayList<String[]> dbStudents = db.getAllStudents();

        for (String[] dbStudent : dbStudents) {
            if (dbStudent[0].equals(student.getStudentId())) {
                inDB = true;
                break;
            }
        }

        if (enrolledStudents.size() >= maxCapacity) {
            System.out.println("Course is full.");
            return false;
        }

        if (inDB) {
            enrolledStudents.add(student.getStudentId());
            return true;
        } else {
            System.out.println("Student not found in database.");
            return false;
        }
    }


    public boolean removeStudent(Student student) {
        if (!enrolledStudents.contains(student.getStudentId())) {
            return false;
        }

        enrolledStudents.remove(student.getStudentId());
        return true;
    }


    // Method to check if prerequisites are satisfied
    public boolean isPrerequisiteSatisfied(List<Integer> completedCourseIds) {
        if (prerequisites == null || prerequisites.isEmpty()) {
            return true;
        }
        if (completedCourseIds == null) {
            return false;
        }
        // Check if all prerequisites are satisfied
        return completedCourseIds.containsAll(prerequisites);
    }

    public boolean isStudentEnrolled(String studentId) {
        return enrolledStudents.contains(studentId);
    }

    public int getAvailableSeats() {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than zero.");
        }
        if (enrolledStudents == null) {
            return maxCapacity; // If enrolledStudents is null, all seats are available
        }
        if (enrolledStudents.size() > maxCapacity) {
            throw new IllegalStateException("Enrolled students exceed max capacity.");
        }
        return maxCapacity - enrolledStudents.size();
    }



    // Getters and Setters


    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public List<Integer> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Integer> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public List<String> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", creditHours=" + creditHours +
                ", prerequisites=" + prerequisites +
                ", schedule='" + schedule + '\'' +
                ", instructor='" + instructor + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", enrolledStudents=" + enrolledStudents +
                '}';
    }
}
