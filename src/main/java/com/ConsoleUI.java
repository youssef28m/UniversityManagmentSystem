package com;
import com.academics.Course;
import com.academics.Enrollment;
import com.database.DatabaseManager;
import com.users.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.system.University;

public class ConsoleUI {
    private Scanner scanner;
    private User currentUser;
    private University university;
    private DatabaseManager db=new DatabaseManager();

    public ConsoleUI(University university) {
        this.scanner = new Scanner(System.in);
        this.university = university;
    }

    public void start() {
        boolean running = true;
        while (running) {
            displayLoginMenu();
            running = processLoginChoice();
        }
    }

    private void displayLoginMenu() {
        System.out.println("==== UNIVERSITY MANAGEMENT SYSTEM ====");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Enter your choice: ");
    }

    private boolean processLoginChoice() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        switch (choice) {
            case 1:
                handleLogin();
                return true;
            case 2:
                System.out.println("Thank you for using University Management System!");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    private void handleLogin() {
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        currentUser = university.authenticateUser(userId, password);

        if (currentUser != null) {
            System.out.println("Login successful!");
            displayUserMenu();
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    private void displayUserMenu() {
        if (currentUser instanceof Student) {
            displayStudentMenu();
        } else if (currentUser instanceof Faculty) {
            displayFacultyMenu();
        } else if (currentUser instanceof AdminStaff) {
            displayAdminMenu();
        } else if (currentUser instanceof SystemAdmin) {
            displaySystemAdminMenu();
        }
    }

    // Methods for different user menus and their functionality

    //---------------------------------------------------------//
    // display student
    //---------------------------------------------------------//
    private void displayStudentMenu() {
        Student student = (Student) currentUser;
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n╔══════════════════════════════════════════════════════╗");
            System.out.println("║                    STUDENT PORTAL                    ║");
            System.out.println("╠══════════════════════════════════════════════════════╣");
            System.out.println("║ Welcome, " + student.getName());
            System.out.println("║ Student ID: " + student.getStudentId());
            System.out.println("║ Status: " + student.getAcademicStatus());
            System.out.println("╚══════════════════════════════════════════════════════╝");

            System.out.println("\n=== Student Menu ===");
            System.out.println("1. View Available Courses");
            System.out.println("2. Register for a Course");
            System.out.println("3. Drop a Course");
            System.out.println("4. View Enrolled Courses");
            System.out.println("5. View Grades");
            System.out.println("6. Calculate GPA");
            System.out.println("7. Update Profile");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    displayAvailableCourses();
                    break;
                case 2:
                    registerForCourse(student);
                    break;
                case 3:
                    dropCourse(student);
                    break;
                case 4:
                    viewEnrolledCourses(student);
                    break;
                case 5:
                    viewGrades(student);
                    break;
                case 6:
                    calculateGPA(student);
                    break;
                case 7:
                    updateStudentProfile(student);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (choice != 0);
    }

    private void displayAvailableCourses() {
        System.out.println("\n=== Available Courses ===");
        ArrayList<List<Object>> courses = db.getAllCourses();

        if (courses == null || courses.isEmpty()) {
            System.out.println("No courses available.");
        } else {
            System.out.println("ID    Course Name                       Dept ID");
            System.out.println("----  --------------------------------  -------");
            for (List<Object> courseData : courses) {
                if (courseData.size() >= 8) {
                    try {
                        Integer courseId = (Integer) courseData.get(0);
                        String title = (String) courseData.get(1);
                        Integer deptId = (Integer) courseData.get(7);
                        System.out.printf("%-5d %-33s %-7d%n",
                                courseId,
                                truncateString(title, 33),
                                deptId);
                    } catch (ClassCastException e) {
                        System.out.println("Invalid course data format.");
                    }
                }
            }
        }
        waitForEnter();
    }

    private void registerForCourse(Student student) {
        System.out.println("\n=== Register for a Course ===");
        System.out.print("Enter course ID to register: ");
        try {
            int courseId = Integer.parseInt(scanner.nextLine().trim());
            Course course = db.getCourse(courseId);
            if (course == null) {
                System.out.println("Course not found.");
            } else {
                boolean registered = student.registerForCourse(course);
                if (registered) {
                    System.out.println("Successfully registered for the course.");
                } else {
                    System.out.println("Failed to register for the course. It may be full or prerequisites not met.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid course ID.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        waitForEnter();
    }

    private void dropCourse(Student student) {
        System.out.println("\n=== Drop a Course ===");
        System.out.print("Enter course ID to drop: ");
        try {
            int courseId = Integer.parseInt(scanner.nextLine().trim());
            Course course = db.getCourse(courseId);
            if (course == null) {
                System.out.println("Course not found.");
            } else {
                boolean dropped = student.dropCourse(course);
                if (dropped) {
                    System.out.println("Successfully dropped the course.");
                } else {
                    System.out.println("Failed to drop the course. You may not be enrolled.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid course ID.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        waitForEnter();
    }

    private void viewEnrolledCourses(Student student) {
        System.out.println("\n=== Enrolled Courses ===");
        List<Integer> enrolledCourseIds = student.getEnrolledCourses();
        if (enrolledCourseIds == null || enrolledCourseIds.isEmpty()) {
            System.out.println("You are not enrolled in any courses.");
        } else {
            System.out.println("ID    Course Name                       Dept ID");
            System.out.println("----  --------------------------------  -------");
            for (Integer courseId : enrolledCourseIds) {
                Course course = db.getCourse(courseId);
                if (course != null) {
                    System.out.printf("%-5d %-33s %-7d%n",
                            course.getCourseId(),
                            truncateString(course.getTitle(), 33),
                            course.getDepartment());
                }
            }
        }
        waitForEnter();
    }

    private void viewGrades(Student student) {
        System.out.println("\n=== View Grades ===");
        List<Integer> enrolledCourseIds = student.getEnrolledCourses();
        if (enrolledCourseIds == null || enrolledCourseIds.isEmpty()) {
            System.out.println("You are not enrolled in any courses.");
        } else {
            System.out.println("Course ID    Course Name                       Grade");
            System.out.println("----------   --------------------------------  -----");
            for (Integer courseId : enrolledCourseIds) {
                Enrollment enrollment = db.getEnrollment(student.getStudentId(), courseId);
                if (enrollment != null) {
                    Course course = enrollment.getCourse();
                    int grade = enrollment.getGrade();
                    System.out.printf("%-12d %-33s %d%n",
                            course.getCourseId(),
                            truncateString(course.getTitle(), 33),
                            grade);
                }
            }
        }
        waitForEnter();
    }

    private void calculateGPA(Student student) {
        System.out.println("\n=== Calculate GPA ===");
        double gpa = student.calculateGPA();
        System.out.println("Your GPA: " + gpa);
        waitForEnter();
    }

    private void updateStudentProfile(Student student) {
        System.out.println("\n=== Update Profile ===");
        System.out.println("Current Information:");
        System.out.println("Name: " + student.getName());
        System.out.println("Email: " + student.getEmail());
        System.out.println("Contact Info: " + student.getContactInfo());

        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Name");
        System.out.println("2. Email");
        System.out.println("3. Contact Information");
        System.out.println("0. Cancel");

        System.out.print("\nEnter your choice: ");
        try {
            int updateChoice = Integer.parseInt(scanner.nextLine().trim());

            switch (updateChoice) {
                case 0:
                    System.out.println("Update canceled.");
                    return;
                case 1:
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine().trim();
                    if (!newName.isEmpty()) {
                        student.setName(newName);
                    } else {
                        System.out.println("Name cannot be empty.");
                        waitForEnter();
                        return;
                    }
                    break;
                case 2:
                    System.out.print("Enter new email: ");
                    String newEmail = scanner.nextLine().trim();
                    if (!newEmail.isEmpty()) {
                        student.setEmail(newEmail);
                    } else {
                        System.out.println("Email cannot be empty.");
                        waitForEnter();
                        return;
                    }
                    break;
                case 3:
                    System.out.print("Enter new contact information: ");
                    String newContact = scanner.nextLine().trim();
                    if (!newContact.isEmpty()) {
                        student.setContactInfo(newContact);
                    } else {
                        System.out.println("Contact information cannot be empty.");
                        waitForEnter();
                        return;
                    }
                    break;
                default:
                    System.out.println("Invalid option.");
                    waitForEnter();
                    return;
            }

            boolean updated = student.updateProfile();
            if (updated) {
                System.out.println("\nProfile updated successfully.");
            } else {
                System.out.println("\nFailed to update profile.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        waitForEnter();
    }

    private String truncateString(String input, int maxLength) {
        if (input == null) return "";
        return input.length() <= maxLength ? input : input.substring(0, maxLength - 3) + "...";
    }

    private void waitForEnter() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    //---------------------------------------------------------//
    // display faculty
    //---------------------------------------------------------//

    private void displayFacultyMenu() {
        Faculty faculty = (Faculty) currentUser;
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n╔══════════════════════════════════════════════════════╗");
            System.out.println("║                    FACULTY PORTAL                    ║");
            System.out.println("╠══════════════════════════════════════════════════════╣");
            System.out.println("║ Welcome, " + faculty.getName());
            System.out.println("║ Faculty ID: " + faculty.getFacultyId());
            System.out.println("║ Department ID: " + (faculty.getDepartment() >= 0 ? faculty.getDepartment() : "Not Assigned"));
            System.out.println("║ Expertise: " + faculty.getExpertise());
            System.out.println("║ Office Hours: " + (faculty.getOfficeHours() != null ? faculty.getOfficeHours() : "Not Set"));
            System.out.println("╚══════════════════════════════════════════════════════╝");

            System.out.println("\n=== Faculty Menu ===");
            System.out.println("1. View Courses Teaching");
            System.out.println("2. Manage Course");
            System.out.println("3. Assign Grades");
            System.out.println("4. View Student Roster");
            System.out.println("5. Add Course");
            System.out.println("6. Update Office Hours");
            System.out.println("7. Update Profile");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    displayCoursesTeaching(faculty);
                    break;
                case 2:
                    manageCourse(faculty);
                    break;
                case 3:
                    assignGrades(faculty);
                    break;
                case 4:
                    viewStudentRoster(faculty);
                    break;
                case 5:
                    addCourse(faculty);
                    break;
                case 6:
                    updateOfficeHours(faculty);
                    break;
                case 7:
                    updateFacultyProfile(faculty);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (choice != 0);
    }

    private void displayCoursesTeaching(Faculty faculty) {
        System.out.println("\n=== Courses Teaching ===");
        ArrayList<Course> courses = faculty.getCoursesTeaching();

        if (courses == null || courses.isEmpty()) {
            System.out.println("You are not teaching any courses.");
        } else {
            System.out.println("ID    Course Name                       Dept ID");
            System.out.println("----  --------------------------------  -------");
            for (Course course : courses) {
                System.out.printf("%-5d %-33s %-7d%n",
                        course.getCourseId(),
                        truncateString(course.getTitle(), 33),
                        course.getDepartment());
            }
        }
        waitForEnter();
    }

    private void manageCourse(Faculty faculty) {
        System.out.println("\n=== Manage Course ===");
        ArrayList<Course> courses = faculty.getCoursesTeaching();

        if (courses == null || courses.isEmpty()) {
            System.out.println("You are not teaching any courses.");
            waitForEnter();
            return;
        }

        System.out.println("Select a course to manage:");
        for (int i = 0; i < courses.size(); i++) {
            System.out.printf("%d. %s (ID: %d)%n", i + 1, courses.get(i).getTitle(), courses.get(i).getCourseId());
        }
        System.out.print("Enter course number (0 to cancel): ");

        try {
            int courseIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (courseIndex == -1) {
                System.out.println("Cancelled.");
                waitForEnter();
                return;
            }
            if (courseIndex >= 0 && courseIndex < courses.size()) {
                faculty.manageCourse(courses.get(courseIndex));
            } else {
                System.out.println("Invalid course number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        waitForEnter();
    }

    private void assignGrades(Faculty faculty) {
        System.out.println("\n=== Assign Grades ===");
        ArrayList<Course> courses = faculty.getCoursesTeaching();

        if (courses == null || courses.isEmpty()) {
            System.out.println("You are not teaching any courses.");
            waitForEnter();
            return;
        }

        System.out.println("Select a course:");
        for (int i = 0; i < courses.size(); i++) {
            System.out.printf("%d. %s (ID: %d)%n", i + 1, courses.get(i).getTitle(), courses.get(i).getCourseId());
        }
        System.out.print("Enter course number (0 to cancel): ");

        try {
            int courseIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (courseIndex == -1) {
                System.out.println("Cancelled.");
                waitForEnter();
                return;
            }
            if (courseIndex >= 0 && courseIndex < courses.size()) {
                Course course = courses.get(courseIndex);
                List<String> studentIds = course.getEnrolledStudents();

                if (studentIds == null || studentIds.isEmpty()) {
                    System.out.println("No students enrolled in this course.");
                    waitForEnter();
                    return;
                }

                System.out.println("Enrolled Students:");
                for (int i = 0; i < studentIds.size(); i++) {
                    Student student = db.getStudent(studentIds.get(i));
                    System.out.printf("%d. %s (ID: %s)%n", i + 1, student != null ? student.getName() : "Unknown", studentIds.get(i));
                }
                System.out.print("Select student number (0 to cancel): ");

                int studentIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (studentIndex == -1) {
                    System.out.println("Cancelled.");
                    waitForEnter();
                    return;
                }
                if (studentIndex >= 0 && studentIndex < studentIds.size()) {
                    Student student = db.getStudent(studentIds.get(studentIndex));
                    if (student != null) {
                        System.out.print("Enter grade (0-100): ");
                        try {
                            int grade = Integer.parseInt(scanner.nextLine().trim());
                            faculty.assignGrades(student, course, grade);
                            System.out.println("Grade assigned successfully.");
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter a valid grade.");
                        }
                    } else {
                        System.out.println("Student not found.");
                    }
                } else {
                    System.out.println("Invalid student number.");
                }
            } else {
                System.out.println("Invalid course number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        waitForEnter();
    }

    private void viewStudentRoster(Faculty faculty) {
        System.out.println("\n=== View Student Roster ===");
        ArrayList<Course> courses = faculty.getCoursesTeaching();

        if (courses == null || courses.isEmpty()) {
            System.out.println("You are not teaching any courses.");
            waitForEnter();
            return;
        }

        System.out.println("Select a course:");
        for (int i = 0; i < courses.size(); i++) {
            System.out.printf("%d. %s (ID: %d)%n", i + 1, courses.get(i).getTitle(), courses.get(i).getCourseId());
        }
        System.out.print("Enter course number (0 to cancel): ");

        try {
            int courseIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (courseIndex == -1) {
                System.out.println("Cancelled.");
                waitForEnter();
                return;
            }
            if (courseIndex >= 0 && courseIndex < courses.size()) {
                faculty.viewStudentRoster(courses.get(courseIndex));
            } else {
                System.out.println("Invalid course number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        waitForEnter();
    }

    private void addCourse(Faculty faculty) {
        System.out.println("\n=== Add Course ===");
        System.out.print("Enter course ID: ");
        try {
            int courseId = Integer.parseInt(scanner.nextLine().trim());
            Course course = db.getCourse(courseId);
            if (course == null) {
                System.out.println("Course not found in the database.");
            } else {
                try {
                    boolean added = faculty.addCourse(course);
                    if (added) {
                        System.out.println("Course added successfully.");
                    } else {
                        System.out.println("Failed to add course. It may already be assigned or there was an error.");
                    }
                } catch (IllegalStateException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid course ID.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        waitForEnter();
    }

    private void updateOfficeHours(Faculty faculty) {
        System.out.println("\n=== Update Office Hours ===");
        System.out.print("Enter new office hours (e.g., Mon 10:00-12:00): ");
        String officeHours = scanner.nextLine().trim();
        if (!officeHours.isEmpty()) {
            faculty.setOfficeHours(officeHours);
        } else {
            System.out.println("Office hours cannot be empty.");
        }
        waitForEnter();
    }

    private void updateFacultyProfile(Faculty faculty) {
        System.out.println("\n👤 UPDATE PROFILE:");
        System.out.println("Current Information:");
        System.out.println("Name: " + faculty.getName());
        System.out.println("Email: " + faculty.getEmail());
        System.out.println("Contact Info: " + faculty.getContactInfo());
        System.out.println("Expertise: " + faculty.getExpertise());

        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Name");
        System.out.println("2. Email");
        System.out.println("3. Contact Information");
        System.out.println("4. Expertise");
        System.out.println("0. Cancel");

        System.out.print("\nEnter your choice: ");
        try {
            int updateChoice = Integer.parseInt(scanner.nextLine().trim());

            switch (updateChoice) {
                case 0:
                    System.out.println("Update canceled.");
                    return;
                case 1:
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine().trim();
                    if (!newName.isEmpty()) {
                        faculty.setName(newName);
                    } else {
                        System.out.println("⚠️ Name cannot be empty.");
                        waitForEnter();
                        return;
                    }
                    break;
                case 2:
                    System.out.print("Enter new email: ");
                    String newEmail = scanner.nextLine().trim();
                    if (!newEmail.isEmpty()) {
                        faculty.setEmail(newEmail);
                    } else {
                        System.out.println("⚠️ Email cannot be empty.");
                        waitForEnter();
                        return;
                    }
                    break;
                case 3:
                    System.out.print("Enter new contact information: ");
                    String newContact = scanner.nextLine().trim();
                    if (!newContact.isEmpty()) {
                        faculty.setContactInfo(newContact);
                    } else {
                        System.out.println("⚠️ Contact information cannot be empty.");
                        waitForEnter();
                        return;
                    }
                    break;
                case 4:
                    System.out.print("Enter new expertise: ");
                    String newExpertise = scanner.nextLine().trim();
                    if (!newExpertise.isEmpty()) {
                        faculty.setExpertise(newExpertise);
                    } else {
                        System.out.println("⚠️ Expertise cannot be empty.");
                        waitForEnter();
                        return;
                    }
                    break;
                default:
                    System.out.println("⚠️ Invalid option.");
                    waitForEnter();
                    return;
            }

            // Save changes
            boolean updated = faculty.updateProfile();
            if (updated) {
                // Update faculty-specific fields in the database
                if (db.updateFaculty(faculty)) {
                    System.out.println("\n✅ Profile updated successfully.");
                } else {
                    System.out.println("\n❌ Failed to update faculty-specific information.");
                }
            } else {
                System.out.println("\n❌ Failed to update profile.");
            }

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("⚠️ An error occurred: " + e.getMessage());
        }

        waitForEnter();
    }

    //---------------------------------------------------------//
    // display admin
    //---------------------------------------------------------//

    private void displayAdminMenu() {
        // Admin-specific menu options
    }

    //---------------------------------------------------------//
    // display system admin
    //---------------------------------------------------------//

    private void displaySystemAdminMenu() {
        // System Admin-specific menu options

    }
}
