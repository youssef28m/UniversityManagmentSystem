
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


       public void addStudent(String student) {
        if (enrolledStudents.size() < maxCapacity) {
            System.out.println("Enter your name:");
            Scanner s = new Scanner(System.in);
            String student = s.next();
            System.out.println("Enter your ID:");
            Scanner s = new Scanner(System.in);
            String studentId = s.next();
            enrolledStudents.add(student);
            enrolledStudents.add(studentId);
            System.out.println("Student " + student + " has been added to the course.");
        } else {
            System.out.println("Cannot add student " + student + ": Course is full.");
        }

    }

    public void removeStudent(String student) {
        if (enrolledStudents.contains(student)) {
            System.out.println("Enter your name:");
            Scanner s = new Scanner(System.in);
            String student = s.next();
            System.out.println("Enter your ID:");
            Scanner s = new Scanner(System.in);
            String studentId = s.next();
            enrolledStudents.remove(student);
            enrolledStudents.remove(studentId);
            System.out.println("Student " + student + " has been removed from the course.");
        } else {
            System.out.println("Student " + student + " is not enrolled in the course.");
        }
    }

    public boolean isPrerequisiteSatisfied(List<String> completedCourses) {
        if (prerequisites == null || prerequisites.isEmpty()) {
            return true; // No prerequisites
        }
        String[] prerequisiteArray = prerequisites.split(",");
        List<String> prerequisites = Arrays.asList(prerequisiteArray);
        // Check if all prerequisites are satisfied
        return completedCourses.containsAll(prerequisites);
    

    public int getAvailableSeats(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than zero.");
        }
        if (enrolledStudents == null) {
            enrolledStudents = new ArrayList<>();
        }
        if (enrolledStudents.size() > maxCapacity) {
            throw new IllegalStateException("Enrolled students exceed max capacity.");
        }
        return maxCapacity - enrolledStudents.size();
    }
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
