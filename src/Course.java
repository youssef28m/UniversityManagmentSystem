public class Course  {
private int courseid;
private String title;
private String description;
private int creditHours;
private String  prerequisites;
private String  schedule;
private String instructor;
private String enrolledstudents;
private List<String> enrolledStudents;
private int maxCapacity = 30; // Default maximum capacity
// Constructor
public Course(int courseid, String title,String description,int creditHours,String  prerequisites,String  schedule,String instructor,String enrolledstudents){
    this.courseid=courseid;
    this.title=title;
    this.description=description;
    this.creditHours=creditHours;
    this.prerequisites=prerequisites;
    this.schedule=schedule;
    this.instructor=instructor;
    this.enrolledstudents=enrolledstudents;

}
public int getcourseid(){
    return courseid;
}
public String gettitle(){
    return title;
}
public String getdescription(){
    return description;
}
public int getcreditHours(){
    return creditHours;
}
public String getprerequisites(){
    return prerequisites;
}public String getschedule(){
    return schedule;
}
public String getinstructor(){
    return instructor;
}
public String getenrolledstudents(){
    return enrolledstudents;
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


    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setEnrolledStudents(List<String> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }
}