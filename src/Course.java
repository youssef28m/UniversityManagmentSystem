import java.util.ArrayList;
import java.util.List;

public class Course {

    private int courseId;
    private String title;
    private String description;
    private int creditHours;
    private ArrayList<Integer> prerequisites;
    private String schedule;
    private String instructor;
    private int maxCapacity;
    private ArrayList<String> enrolledStudents;

    // Constructor
    public Course(int courseId, String title, String description, int creditHours,
                  ArrayList<Integer> prerequisites, String schedule, String instructor, int maxCapacity) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.creditHours = creditHours;
        this.prerequisites = prerequisites;
        this.schedule = schedule;
        this.maxCapacity = maxCapacity;
        this.instructor = instructor;
    }


    public void addStudent(String studentId) {
        // Method to be implemented
    }

    public void removeStudent(String studentId) {
        // Method to be implemented
    }

    public boolean isPrerequisiteSatisfied(String studentId) {
        // Method to be implemented
        return false;
    }

    public int getAvailableSeats() {
        // Method to be implemented
        return 0;
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

    public ArrayList<Integer> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(ArrayList<Integer> prerequisites) {
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

    public void setEnrolledStudents(ArrayList<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
