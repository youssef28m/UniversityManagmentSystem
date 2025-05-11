package com.users;
import com.academics.Course;
import com.academics.Enrollment;
import com.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class Faculty extends User {

    private String facultyId;
    private int department = -1;
    private String expertise;
    private String officeHours;
    private ArrayList<Course> coursesTeaching;
    private DatabaseManager db = new DatabaseManager();


    public Faculty(String userType,String username, String password, String name, String email, String contactInfo, String expertise) {
        super(userType, username, password, name, email, contactInfo);
        setFacultyId();
        this.expertise = expertise;
    }

    public Faculty(String userType, String username, String password, String name, String email, String contactInfo, String expertise, int department) {
        super(userType, username, password, name, email, contactInfo);
        setFacultyId();
        this.expertise = expertise;
        this.department = department;
    }

    public Faculty(String userType,String userId, String username, String password, String name, String email, String contactInfo, String facultyId, int department, String expertise, ArrayList<Course> coursesTeaching) {
        super(userType,userId, username, password, name, email, contactInfo);
        this.facultyId = facultyId;
        this.department = department;
        this.expertise = expertise;
        this.coursesTeaching = coursesTeaching;
    }

    @Override
    public boolean updateProfile() {
        if (db == null) {
            return false;
        }

        String[] userData = {getUsername(), getName(), getEmail(), getContactInfo(), UserType.FACULTY.getDisplayName(), };
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

    public boolean addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        if (department < 0) {
            throw new IllegalStateException("Faculty must be assigned to a valid department");
        }

        if (coursesTeaching == null) {
            coursesTeaching = new ArrayList<>();
        }

        if (coursesTeaching.contains(course)) {
            return false;
        }
        String oldInstructor = course.getInstructor();
        int oldDepartment = course.getDepartment();

        course.setInstructor(facultyId);
        course.setDepartment(department);

        if (db.updateCourse(course)) {
            coursesTeaching.add(course);
            return true;
        } else {
            course.setInstructor(oldInstructor);
            course.setDepartment(oldDepartment);
            return false;
        }
    }

    public void assignGrades(Student student, Course course, int grade) {
        boolean isCourseTeaching = false;

        if (coursesTeaching != null) {
            for (Course teachingCourse : coursesTeaching) {
                if (teachingCourse.getCourseId() == course.getCourseId()) {
                    isCourseTeaching = true;
                    break;
                }
            }
        }

        if (!isCourseTeaching) {
            System.out.println("You do not teach this course.");
            return;
        }

        Enrollment enrollment = db.getEnrollment(student.getStudentId(), course.getCourseId());
        if (enrollment == null) {
            System.out.println("The student is not registered in this course.");
        }

        boolean success = enrollment.assignGrade(grade);
        if (success) {
            db.updateEnrollment(enrollment);
        } else {
            System.out.println("Failed to update grade.");
        }
    }

    public void manageCourse(Course course) {
        if (coursesTeaching == null || !coursesTeaching.contains(course)) {
            System.out.println("Error: You are not assigned to teach " + course.getTitle() + ".");
            return;
        }

        boolean managing = true;
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        while (managing) {
            System.out.println("\n===== COURSE MANAGEMENT: " + course.getTitle() + " (" + course.getCourseId() + ") =====");
            System.out.println("1. Update course title");
            System.out.println("2. Update course description");
            System.out.println("3. Update schedule");
            System.out.println("4. Update maximum capacity");
            System.out.println("5. View enrolled students");
            System.out.println("6. Remove a student");
            System.out.println("7. View course details");
            System.out.println("0. Exit course management");
            System.out.print("Choose an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 0:
                        managing = false;
                        break;
                    case 1:
                        System.out.print("Enter new course title: ");
                        String newTitle = scanner.nextLine();
                        course.setTitle(newTitle);
                        if (db.updateCourse(course)) {
                            System.out.println("Course title updated successfully.");
                        } else {
                            System.out.println("Failed to update course title.");
                        }
                        break;
                    case 2:
                        System.out.print("Enter new course description: ");
                        String newDescription = scanner.nextLine();
                        course.setDescription(newDescription);
                        if (db.updateCourse(course)) {
                            System.out.println("Course description updated successfully.");
                        } else {
                            System.out.println("Failed to update course description.");
                        }
                        break;
                    case 3:
                        System.out.print("Enter new schedule (e.g., 'Mon,Wed 10:00-11:30'): ");
                        String newSchedule = scanner.nextLine();
                        course.setSchedule(newSchedule);
                        if (db.updateCourse(course)) {
                            System.out.println("Course schedule updated successfully.");
                        } else {
                            System.out.println("Failed to update course schedule.");
                        }
                        break;
                    case 4:
                        System.out.print("Enter new maximum capacity: ");
                        int newCapacity = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        if (newCapacity < course.getEnrolledStudents().size()) {
                            System.out.println("Error: New capacity cannot be less than current enrollment (" +
                                    course.getEnrolledStudents().size() + " students).");
                        } else {
                            course.setMaxCapacity(newCapacity);
                            if (db.updateCourse(course)) {
                                System.out.println("Maximum capacity updated successfully.");
                            } else {
                                System.out.println("Failed to update maximum capacity.");
                            }
                        }
                        break;
                    case 5:
                        viewStudentRoster(course);
                        break;
                    case 6:
                        System.out.print("Enter student ID to remove: ");
                        String studentId = scanner.nextLine();
                        Student student = db.getStudent(studentId);

                        if (student == null) {
                            System.out.println("Student not found.");
                        } else if (!course.isStudentEnrolled(studentId)) {
                            System.out.println("This student is not enrolled in this course.");
                        } else {
                            if (course.removeStudent(student)) {
                                if (db.updateCourse(course)) {
                                    System.out.println("Student removed from course successfully.");
                                } else {
                                    System.out.println("Failed to update course in database.");
                                }
                            } else {
                                System.out.println("Failed to remove student from course.");
                            }
                        }
                        break;
                    case 7:
                        displayCourseDetails(course);
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private void displayCourseDetails(Course course) {
        System.out.println("\n===== COURSE DETAILS =====");
        System.out.println("ID: " + course.getCourseId());
        System.out.println("Title: " + course.getTitle());
        System.out.println("Description: " + course.getDescription());
        System.out.println("Credit Hours: " + course.getCreditHours());
        System.out.println("Schedule: " + course.getSchedule());
        System.out.println("Instructor: " + course.getInstructor());
        System.out.println("Maximum Capacity: " + course.getMaxCapacity());
        System.out.println("Available Seats: " + course.getAvailableSeats());

        List<Integer> prereqs = course.getPrerequisites();
        if (prereqs != null && !prereqs.isEmpty()) {
            System.out.println("Prerequisites:");
            for (Integer prereqId : prereqs) {
                Course prereqCourse = db.getCourse(prereqId);
                if (prereqCourse != null) {
                    System.out.println("  - " + prereqCourse.getTitle() + " (" + prereqId + ")");
                } else {
                    System.out.println("  - Course ID: " + prereqId);
                }
            }
        } else {
            System.out.println("Prerequisites: None");
        }
        System.out.println("========================");
    }

    //-------------------------------//
    // getters and setters
    //-------------------------------//
    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
        System.out.println("Office hours updated to: " + officeHours);
    }

    public String getOfficeHours() {
        return officeHours;
    }

    public void viewStudentRoster(Course course) {

        if (coursesTeaching == null || !coursesTeaching.contains(course)) {
            System.out.println("Error: You are not assigned to teach " + course.getTitle() + ".");
            return;
        }

        List<String> studentIds = course.getEnrolledStudents();

        if (studentIds == null || studentIds.isEmpty()) {
            System.out.println("No students are currently enrolled in " + course.getTitle() + ".");
            return;
        }

        System.out.println("\n===== STUDENT ROSTER: " + course.getTitle() + " (" + course.getCourseId() + ") =====");
        System.out.println("Total enrolled: " + studentIds.size() + " students\n");

        System.out.printf("%-10s | %-30s\n", "ID", "Name");
        System.out.println("----------------------------------------------");

        for (String studentId : studentIds) {
            try {
                Student student = db.getStudent(studentId);
                if (student != null) {
                    System.out.printf("%-10s | %-30s\n", student.getStudentId(), student.getName());
                } else {
                    System.out.printf("%-10s | %-30s\n", studentId, "[Student record not found]");
                }
            } catch (Exception e) {
                System.out.printf("%-10s | %-30s\n", studentId, "[Error retrieving student data]");
            }
        }
        System.out.println("======================================================\n");
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId() {
        List<String[]> dbFaculty = db.getAllFaculty();
        if(dbFaculty.isEmpty()) {
            this.facultyId = "240001";
        } else {
            String[] lastStudent = dbFaculty.get(dbFaculty.size() - 1);
            String lastStudentId = lastStudent[0];
            int id = Integer.parseInt(lastStudentId) + 1;
            this.facultyId = String.valueOf(id);
        }
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

    public ArrayList<Course> getCoursesTeaching() {
        return coursesTeaching;
    }

}
