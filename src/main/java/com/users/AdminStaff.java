package com.users;

import com.academics.Course;
import com.database.DatabaseManager;
import com.system.Department;
import com.system.University;
import com.util.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an administrative staff member with role-based responsibilities.
 */
public class AdminStaff extends User {

    private String staffId;
    private int departmentId;
    private String role;
    private final DatabaseManager db = new DatabaseManager();
    private static final List<String> VALID_ROLES = Arrays.asList(
            "Registrar", "Student Affairs", "Faculty Affairs", "Admissions"
    );

    /**
     * Constructor with full details.
     *
     * @param userType     User type (should be ADMIN_STAFF).
     * @param userId       Unique user ID.
     * @param username     Username for login.
     * @param password     Password for login.
     * @param name         Full name.
     * @param email        Email address.
     * @param contactInfo  Contact information.
     * @param departmentId Department ID.
     * @param role         Staff role.
     * @param staffId      Unique staff ID.
     * @param db           DatabaseManager instance.
     * @throws IllegalArgumentException If role or departmentId is invalid.
     */
    public AdminStaff(String userType, String userId, String username, String password, String name,
                      String email, String contactInfo, int departmentId, String role, String staffId) {
        super(userType, userId, username, password, name, email, contactInfo);
        validateRole(role);
        validateDepartmentId(departmentId, db);
        this.staffId = staffId;
        this.departmentId = departmentId;
        this.role = role;
        setUserType(UserType.ADMIN_STAFF);
    }

    /**
     * Constructor with auto-generated staff ID.
     *
     * @param userType     User type (should be ADMIN_STAFF).
     * @param username     Username for login.
     * @param password     Password for login.
     * @param name         Full name.
     * @param email        Email address.
     * @param contactInfo  Contact information.
     * @param departmentId Department ID.
     * @param role         Staff role.
     * @param db           DatabaseManager instance.
     * @throws IllegalArgumentException If role or departmentId is invalid.
     */
    public AdminStaff(String userType, String username, String password, String name,
                      String email, String contactInfo, int departmentId, String role) {
        super(userType, username, password, name, email, contactInfo);
        validateRole(role);
        validateDepartmentId(departmentId, db);
        this.departmentId = departmentId;
        this.role = role;
        setUserType(UserType.ADMIN_STAFF);
        setStaffId();
    }

    /**
     * Default constructor for DatabaseManager compatibility.
     */
    public AdminStaff() {
        this.departmentId = -1;
    }

    /**
     * Updates the admin staff's profile.
     *
     * @return true if update is successful, false otherwise.
     */
    @Override
    public boolean updateProfile() {
        if (db == null) {
            return false;
        }
        String[] userData = {getUsername(), getName(), getEmail(), getContactInfo(), UserType.ADMIN_STAFF.getDisplayName()};
        try {
            boolean userUpdated = db.updateUserProfile(getUserId(), userData);
            boolean staffUpdated = db.updateAdminStaff(this);
            return userUpdated && staffUpdated;
        } catch (Exception e) {
            System.err.println("Failed to update profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registers a new student.
     * @param name        Student's name.
     * @param email       Student's email.
     * @param contactInfo Student's contact info.
     * @param password    Student's password.
     * @return Created Student object, or null if registration fails.
     * @throws SecurityException If role is not authorized.
     */
    public Student registerStudent(String name, String email, String contactInfo, String password) {
        restrictByRole("Student Affairs", "Admissions");
        if (db == null) {
            throw new IllegalStateException("DatabaseManager not initialized.");
        }
        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Name, email, and password cannot be empty.");
        }

        String username = name.toLowerCase().replaceAll("\\s+", "");
        Student newStudent = new Student("Student", username, password, name, email, contactInfo,Helper.getCurrentDate(),"Active");


        if (db.addUser(newStudent) && db.addStudent(newStudent)) {
            return newStudent;
        }
        return null;
    }

    /**
     * Creates a new course.
     *
     * @param title        Course title.
     * @param description  Course description.
     * @param creditHours  Credit hours.
     * @param maxCapacity  Maximum capacity.
     * @param departmentId Department ID.
     * @return Created Course object, or null if creation fails.
     * @throws SecurityException If role is not authorized.
     */
    public Course createCourse(String title, String description, int creditHours, int maxCapacity, int departmentId) {
        restrictByRole("Registrar");
        if (db == null) {
            throw new IllegalStateException("DatabaseManager not initialized.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be empty.");
        }
        if (creditHours <= 0) {
            throw new IllegalArgumentException("Credit hours must be positive.");
        }
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be positive.");
        }
        validateDepartmentId(departmentId, db);

        Course newCourse = new Course();
        newCourse.setCourseId(generateCourseId());
        newCourse.setTitle(title);
        newCourse.setDescription(description);
        newCourse.setCreditHours(creditHours);
        newCourse.setMaxCapacity(maxCapacity);
        newCourse.setDepartment(departmentId);

        boolean success = db.addCourse(newCourse);
        return success ? newCourse : null;
    }

    /**
     * Assigns a faculty member to a course.
     *
     * @param faculty Faculty member.
     * @param course  Course to assign.
     * @return true if assignment is successful, false otherwise.
     * @throws SecurityException If role is not authorized.
     */
    public boolean assignFaculty(Faculty faculty, Course course) {
        restrictByRole("Faculty Affairs");
        if (db == null) {
            throw new IllegalStateException("DatabaseManager not initialized.");
        }
        if (faculty == null || course == null) {
            throw new IllegalArgumentException("Faculty and course cannot be null.");
        }
        if (db.getCourse(course.getCourseId()) == null) {
            throw new IllegalArgumentException("Course does not exist.");
        }
        if (db.getFaculty(faculty.getFacultyId()) == null) {
            throw new IllegalArgumentException("Faculty does not exist.");
        }

        return faculty.addCourse(course);
    }

    /**
     * Generates an enrollment report.
     *
     * @param university University instance for accessing courses.
     * @return Enrollment report as a string.
     * @throws SecurityException If role is not authorized.
     */
    public String generateEnrollmentReport(University university) {
        restrictByRole("Registrar", "Student Affairs");
        if (db == null) {
            throw new IllegalStateException("DatabaseManager not initialized.");
        }
        StringBuilder report = new StringBuilder();
        report.append("ENROLLMENT REPORT\n");
        report.append("=================\n\n");

        ArrayList<Course> courses = university.getCourses();
        Map<String, List<Course>> coursesByDepartment = new HashMap<>();

        for (Course course : courses) {
            String deptName = db.getDepartment(course.getDepartment()) != null
                    ? db.getDepartment(course.getDepartment()).getName()
                    : "Unknown";
            coursesByDepartment.computeIfAbsent(deptName, k -> new ArrayList<>()).add(course);
        }

        for (String deptName : coursesByDepartment.keySet()) {
            report.append("Department: ").append(deptName).append("\n");
            report.append("-----------------------------------------\n");
            List<Course> deptCourses = coursesByDepartment.get(deptName);
            for (Course course : deptCourses) {
                int enrolled = course.getEnrolledStudents().size();
                int capacity = course.getMaxCapacity();
                report.append(String.format("%-10s %-30s %3d/%-3d (%d%%)\n",
                        "COURSE " + course.getCourseId(),
                        truncateString(course.getTitle(), 30),
                        enrolled,
                        capacity,
                        capacity > 0 ? (enrolled * 100 / capacity) : 0));
            }
            report.append("\n");
        }

        report.append("SUMMARY\n");
        report.append("-------\n");
        report.append("Total Courses: ").append(courses.size()).append("\n");
        report.append("Total Departments: ").append(coursesByDepartment.size()).append("\n");

        return report.toString();
    }

    /**
     * Generates a department report.
     *
     * @param departmentId Department ID.
     * @param university   University instance for accessing department data.
     * @return Department report as a string.
     * @throws SecurityException If role is not authorized.
     */
    public String generateDepartmentReport(int departmentId, University university) {
        restrictByRole("Registrar", "Faculty Affairs");
        if (db == null) {
            throw new IllegalStateException("DatabaseManager not initialized.");
        }
        validateDepartmentId(departmentId, db);
        Department department = university.getDepartment(departmentId);
        if (department == null) {
            return "Department not found";
        }

        StringBuilder report = new StringBuilder();
        report.append("DEPARTMENT REPORT: ").append(department.getName()).append("\n");
        report.append("=======================================\n\n");

        List<Faculty> facultyMembers = department.getFacultyList();
        report.append("Faculty Members: ").append(facultyMembers.size()).append("\n");
        for (Faculty faculty : facultyMembers) {
            report.append(" - ").append(faculty.getName())
                    .append(" (Courses: ").append(faculty.getCoursesTeaching().size()).append(")\n");
        }

        List<Course> departmentCourses = department.getOfferedCourses();
        report.append("\nCourses Offered: ").append(departmentCourses.size()).append("\n");
        for (Course course : departmentCourses) {
            int enrolled = course.getEnrolledStudents().size();
            report.append(" - ").append(course.getCourseId()).append(": ")
                    .append(truncateString(course.getTitle(), 30))
                    .append(" (Enrollment: ").append(enrolled)
                    .append("/").append(course.getMaxCapacity()).append(")\n");
        }

        return report.toString();
    }

    /**
     * Views all students.
     *
     * @return List of all students.
     * @throws SecurityException If role is not authorized.
     */
    public List<Student> viewAllStudents() {
        restrictByRole("Student Affairs", "Admissions");
        if (db == null) {
            throw new IllegalStateException("DatabaseManager not initialized.");
        }
        List<Student> students = new ArrayList<>();
        ArrayList<String[]> studentData = db.getAllStudents();
        for (String[] data : studentData) {
            Student student = db.getStudent(data[0]);
            if (student != null) {
                students.add(student);
            }
        }
        return students;
    }

    /**
     * Views all courses.
     *
     * @return List of all courses.
     * @throws SecurityException If role is not authorized.
     */
    public List<Course> viewAllCourses() {
        restrictByRole("Registrar");
        if (db == null) {
            throw new IllegalStateException("DatabaseManager not initialized.");
        }
        List<Course> courses = new ArrayList<>();
        ArrayList<List<Object>> courseData = db.getAllCourses();
        for (List<Object> data : courseData) {
            Course course = db.getCourse((Integer) data.get(0));
            if (course != null) {
                courses.add(course);
            }
        }
        return courses;
    }

    /**
     * Views all faculty members.
     *
     * @return List of all faculty members.
     * @throws SecurityException If role is not authorized.
     */
    public List<Faculty> viewAllFaculty() {
        restrictByRole("Faculty Affairs");
        if (db == null) {
            throw new IllegalStateException("DatabaseManager not initialized.");
        }
        List<Faculty> faculty = new ArrayList<>();
        ArrayList<String[]> facultyData = db.getAllFaculty();
        for (String[] data : facultyData) {
            Faculty f = db.getFaculty(data[0]);
            if (f != null) {
                faculty.add(f);
            }
        }
        return faculty;
    }

    /**
     * Validates the role.
     *
     * @param role Role to validate.
     * @throws IllegalArgumentException If role is invalid.
     */
    private void validateRole(String role) {
        if (role == null || !VALID_ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role. Valid roles are: " + String.join(", ", VALID_ROLES));
        }
    }

    /**
     * Validates the department ID.
     *
     * @param departmentId Department ID.
     * @param db           DatabaseManager instance.
     * @throws IllegalArgumentException If department ID is invalid.
     */
    private void validateDepartmentId(int departmentId, DatabaseManager db) {
        if (db == null || departmentId <= 0 || db.getDepartment(departmentId) == null) {
            throw new IllegalArgumentException("Invalid department ID.");
        }
    }

    /**
     * Restricts access based on role.
     *
     * @param allowedRoles Allowed roles.
     * @throws SecurityException If role is not authorized.
     */
    private void restrictByRole(String... allowedRoles) {
        if (!Arrays.asList(allowedRoles).contains(role)) {
            throw new SecurityException("Unauthorized: Role '" + role + "' cannot perform this action.");
        }
    }

    /**
     * Generates a unique course ID.
     *
     * @return New course ID.
     */
    private int generateCourseId() {
        if (db == null) {
            return 1001;
        }
        ArrayList<List<Object>> courses = db.getAllCourses();
        if (courses.isEmpty()) {
            return 1001;
        }
        int maxId = courses.stream()
                .mapToInt(course -> (Integer) course.get(0))
                .max()
                .orElse(1000);
        return maxId + 1;
    }

    /**
     * Truncates a string.
     *
     * @param input     String to truncate.
     * @param maxLength Maximum length.
     * @return Truncated string.
     */
    private String truncateString(String input, int maxLength) {
        if (input == null) return "";
        return input.length() <= maxLength ? input : input.substring(0, maxLength - 3) + "...";
    }

    // Getters and Setters

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId() {
        if (db == null) {
            this.staffId = "240001";
            return;
        }
        List<String[]> dbAdminStaff = db.getAllStaff();
        if (dbAdminStaff.isEmpty()) {
            this.staffId = "240001";
        } else {
            String[] lastStaff = dbAdminStaff.get(dbAdminStaff.size() - 1);
            String lastStaffId = lastStaff[0];
            int id = Integer.parseInt(lastStaffId) + 1;
            this.staffId = String.valueOf(id);
        }
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        validateDepartmentId(departmentId, db);
        this.departmentId = departmentId;
        if (db != null) {
            db.updateAdminStaff(this);
        }
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        validateRole(role);
        this.role = role;
        if (db != null) {
            db.updateAdminStaff(this);
        }
    }

    public static List<String> getValidRoles() {
        return new ArrayList<>(VALID_ROLES);
    }
}