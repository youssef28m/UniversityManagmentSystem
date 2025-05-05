public class Course extends User {
private int courseid;
private String title;
private String description;
private int creditHours;
private String  prerequisites;
private String  schedule;
private String instructor;
private String enrolledstudents;
public Course(String userId,String name,private int courseid, String title,String description,int creditHours,String  prerequisites,String  schedule,String instructor,String enrolledstudents){
    super(userId,name)
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
        enrolledStudents.add(student);
    System.out.println("Student " + student + " has been added to the course.");
    }

    public void removeStudent(String student) {
        enrolledStudents.remove(student);
        System.out.println("Student " + student + " has been removed from the course.");
    }

    public boolean isPrerequisiteSatisfied(List<String> completedCourses) {
        return completedCourses.containsAll(prerequisites);
    

    public int getAvailableSeats(int maxCapacity) {
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