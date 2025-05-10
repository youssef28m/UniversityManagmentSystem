package com.users;

import com.academics.Course;
import com.database.DatabaseManager;
import com.system.Department;
import com.system.University;
import com.util.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminStaff extends User {

    private String staffId;
    private int departmentId = -1;
    private String role; // E.g., "Student Affairs", "Registrar", "Faculty Affairs"
    private DatabaseManager db = new DatabaseManager();

    public AdminStaff(String userId, String username, String password, String name,
                      String email, String contactInfo, String staffId,
                      int departmentId, String role) {
        super(userId, username, password, name, email, contactInfo);
        this.staffId = staffId;
        this.departmentId = departmentId;
        this.role = role;
        setUserType(UserType.ADMIN_STAFF);
    }

    @Override
    boolean updateProfile() {
        if (db == null) {
            return false;
        }
        String[] userData = {getUsername(), getName(), getEmail(), getContactInfo(), UserType.ADMIN_STAFF.getDisplayName(), };
        try {
            boolean success = db.updateUserProfile(getUserId(), userData);

            if (success) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            // Catch any unexpected exceptions
            System.out.println("Failed: An error occurred while updating the profile - " + e.getMessage());
            return false;
        }
    }

    public Student registerStudent(University university, String name, String email,
                                   String contactInfo, String password) {
        // Create a new Student object
        String studentId = Helper.generateStudentId();
        String userId = "S" + studentId;

        Student newStudent = new Student();
        newStudent.setUserId(userId);
        newStudent.setUsername(name.toLowerCase().replaceAll("\\s+", ""));
        newStudent.setPassword(password);
        newStudent.setName(name);
        newStudent.setEmail(email);
        newStudent.setContactInfo(contactInfo);
        newStudent.setStudentId(studentId);
        newStudent.setAdmissionDate(Helper.getCurrentDate());
        newStudent.setAcademicStatus(Student.AcademicStatus.ACTIVE);

        // Register the student through the university
        boolean success = university.registerStudent(newStudent);

        return success ? newStudent : null;
    }

    public Course createCourse(University university, String title, String description,
                               int creditHours, int maxCapacity, int departmentId) {
        // Get the department
        Department department = university.getDepartment(departmentId);

        if (department == null) {
            return null; // Department doesn't exist
        }

        // Create a new Course object
        Course newCourse = new Course();
        newCourse.setTitle(title);
        newCourse.setDescription(description);
        newCourse.setCreditHours(creditHours);
        newCourse.setMaxCapacity(maxCapacity);
        newCourse.setDepartment(department.getDepartmentId());

        // Offer the course through the university
        boolean success = university.offerCourse(newCourse);

        return success ? newCourse : null;
    }

    public boolean assignFaculty(Faculty facultyMember, Course course) {
        if (facultyMember == null || course == null) {
            System.out.println("Faculty assignment failed: Invalid faculty member or course.");
            return false;
        }

        if (db.getCourse(course.getCourseId()) == null) {
            System.out.println("Course does not exist.");
            return false;
        }

        if (db.getFaculty(facultyMember.getFacultyId()) == null) {
            System.out.println("Faculty does not exist.");
            return false;
        }

        if(facultyMember.addCourse(course)) {
            return true;
        } else {
            return false;
        }

    }

    public String generateEnrollmentReport(University university) {
        StringBuilder report = new StringBuilder();
        report.append("ENROLLMENT REPORT\n");
        report.append("=================\n\n");

        ArrayList<Course> courses = university.getCourses();

        // Sort courses by department
        Map<String, List<Course>> coursesByDepartment = new HashMap<>();

        for (Course course : courses) {
            String deptName = db.getDepartment(course.getDepartment()).getName();
            if (!coursesByDepartment.containsKey(deptName)) {
                coursesByDepartment.put(deptName, new ArrayList<>());
            }
            coursesByDepartment.get(deptName).add(course);
        }

        // Generate report content by department
        for (String deptName : coursesByDepartment.keySet()) {
            report.append("Department: ").append(deptName).append("\n");
            report.append("-----------------------------------------\n");

            List<Course> deptCourses = coursesByDepartment.get(deptName);
            for (Course course : deptCourses) {
                int enrolled = course.getEnrolledStudents().size();
                int capacity = course.getMaxCapacity();

                report.append(String.format("%-10s %-30s %3d/%-3d (%d%%)\n",
                        "COURSE " + course.getCourseId(),
                        course.getTitle(),
                        enrolled,
                        capacity,
                        capacity > 0 ? (enrolled * 100 / capacity) : 0));
            }
            report.append("\n");
        }

        // Add summary
        report.append("SUMMARY\n");
        report.append("-------\n");
        report.append("Total Courses: ").append(courses.size()).append("\n");
        report.append("Total Departments: ").append(coursesByDepartment.size()).append("\n");

        return report.toString();
    }

    public String generateDepartmentReport(University university, int departmentId) {
        Department department = university.getDepartment(departmentId);

        if (department == null) {
            return "Department not found";
        }

        StringBuilder report = new StringBuilder();
        report.append("DEPARTMENT REPORT: ").append(department.getName()).append("\n");
        report.append("=======================================\n\n");

        // Faculty information
        List<Faculty> facultyMembers = department.getFacultyList();
        report.append("Faculty Members: ").append(facultyMembers.size()).append("\n");
        for (Faculty faculty : facultyMembers) {
            report.append(" - ").append(faculty.getName())
                    .append(" (Courses: ").append(faculty.getCoursesTeaching().size()).append(")\n");
        }

        // Course information
        List<Course> departmentCourses = department.getOfferedCourses();
        report.append("\nCourses Offered: ").append(departmentCourses.size()).append("\n");
        for (Course course : departmentCourses) {
            int enrolled = course.getEnrolledStudents().size();
            report.append(" - ").append(course.getCourseId()).append(": ")
                    .append(course.getTitle())
                    .append(" (Enrollment: ").append(enrolled)
                    .append("/").append(course.getMaxCapacity()).append(")\n");
        }

        return report.toString();
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
