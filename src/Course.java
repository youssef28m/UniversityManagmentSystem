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
        this.instructor = instructor;
        this.maxCapacity = maxCapacity;
        this.enrolledStudents = new ArrayList<>(); // Initialize the enrolledStudents list
    }

    public void addStudent(String student, String studentId) {
        if (enrolledStudents.size() < maxCapacity) {
            enrolledStudents.add(student);
            enrolledStudents.add(studentId);
            System.out.println("Student " + student + " has been added to the course.");
        } else {
            System.out.println("Cannot add student " + student + ": Course is full.");
        }
    }

    public void removeStudent(String student, String studentId) {
        if (enrolledStudents.contains(student)) {
            enrolledStudents.remove(student);
            enrolledStudents.remove(studentId);
            System.out.println("Student " + student + " has been removed from the course.");
        } else {
            System.out.println("Student " + student + " is not enrolled in the course.");
        }
    }

    public boolean isPrerequisiteSatisfied(List<Integer> completedCourseIds) {
        if (prerequisites == null || prerequisites.isEmpty()) {
            return true; // No prerequisites
        }
        // Check if all prerequisites are satisfied
        return completedCourseIds.containsAll(prerequisites);
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