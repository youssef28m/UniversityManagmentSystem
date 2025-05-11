package com;
import com.users.*;
import java.util.Scanner;
import com.system.University;

public class ConsoleUI {
    private Scanner scanner;
    private User currentUser;
    private University university;

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
    private void displayStudentMenu() {
        // Student-specific menu options
    }

    private void displayFacultyMenu() {
        // Faculty-specific menu options
    }

    private void displayAdminMenu() {
        // Admin-specific menu options
    }

    private void displaySystemAdminMenu() {
        // System Admin-specific menu options
    }
}
