package users;
import java.util.ArrayList;

public class Faculty extends User {

    private String facultyId;
    private int department = -1;
    private String expertise;
    private ArrayList<String> coursesTeaching;


    public Faculty(String userId, String username, String password, String name, String email, String contactInfo, String facultyId,  String expertise) {
        super(userId, username, password, name, email, contactInfo);
        this.facultyId = facultyId;
        this.expertise = expertise;
    }

    public Faculty(String userId, String username, String password, String name, String email, String contactInfo, String facultyId, int department, String expertise, ArrayList<String> coursesTeaching) {
        super(userId, username, password, name, email, contactInfo);
        this.facultyId = facultyId;
        this.department = department;
        this.expertise = expertise;
        this.coursesTeaching = coursesTeaching;
    }

    @Override
    String updateProfile() {
        return "";
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public int getDepartment() {
        return department;
    }

    public void setDepartment(int department) {
        this.department = department;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public ArrayList<String> getCoursesTeaching() {
        return coursesTeaching;
    }

    public void setCoursesTeaching(ArrayList<String> coursesTeaching) {
        this.coursesTeaching = coursesTeaching;
    }
}
