package com.users;

import com.database.DatabaseManager;
import com.util.Helper;

import java.util.List;

public class SystemAdmin extends User {

    private String adminId;
    private String securityLevel;
    private DatabaseManager db = new DatabaseManager();

    // Constructor with all fields
    public SystemAdmin(String userType, String userId, String username, String password, String name,
                       String email, String contactInfo, String adminId, String securityLevel) {
        super(userType, userId, username, password, name, email, contactInfo);
        validateSecurityLevel(securityLevel);
        this.adminId = adminId;
        this.securityLevel = securityLevel;
        setUserType(UserType.SYSTEM_ADMIN);
    }

    // Constructor with auto-generated admin ID
    public SystemAdmin(String userType, String username, String password, String name,
                       String email, String contactInfo, String securityLevel) {
        super(userType, username, password, name, email, contactInfo);
        validateSecurityLevel(securityLevel);
        setAdminId();
        this.securityLevel = securityLevel;
        setUserType(UserType.SYSTEM_ADMIN);
    }

    // Default constructor for DatabaseManager compatibility
    public SystemAdmin() {
        this.securityLevel = "Low";
    }

    @Override
    public boolean updateProfile() {
        if (db == null) {
            System.out.println("DatabaseManager not initialized.");
            return false;
        }

        String[] userData = {getUsername(), getName(), getEmail(), getContactInfo(), UserType.SYSTEM_ADMIN.getDisplayName()};
        try {
            boolean userUpdated = db.updateUserProfile(getUserId(), userData);
            boolean adminUpdated = db.updateSystemAdmin(this);
            return userUpdated && adminUpdated;
        } catch (Exception e) {
            System.out.println("Failed to update profile: " + e.getMessage());
            return false;
        }
    }

    public void createUser(User user) {
        if (user == null) {
            System.out.println("Cannot create null user.");
            return;
        }

        if (db == null) {
            System.out.println("DatabaseManager not initialized.");
            return;
        }

        try {
            boolean userAdded = db.addUser(user);
            boolean specificAdded = false;

            if (user instanceof Student) {
                specificAdded = db.addStudent((Student) user);
            } else if (user instanceof Faculty) {
                specificAdded = db.addFaculty((Faculty) user);
            } else if (user instanceof AdminStaff) {
                specificAdded = db.addAdminStaff((AdminStaff) user);
            } else if (user instanceof SystemAdmin) {
                specificAdded = db.addSystemAdmin((SystemAdmin) user);
            }

            if (userAdded && specificAdded) {
                System.out.println(user.getClass().getSimpleName() + " created successfully: " + user.getUsername());
            } else {
                System.out.println("Failed to create " + user.getClass().getSimpleName() + ": " + user.getUsername());
            }
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    // Validation for security level
    private void validateSecurityLevel(String securityLevel) {
        if (securityLevel == null || !List.of("Low", "Medium", "High").contains(securityLevel)) {
            throw new IllegalArgumentException("Invalid security level. Must be Low, Medium, or High.");
        }
    }

    // Getters and Setters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId() {
        if (db == null) {
            this.adminId = "240001";
            return;
        }
        List<String[]> dbAdmins = db.getAllSystemAdmin();
        if (dbAdmins.isEmpty()) {
            this.adminId = "240001";
        } else {
            String[] lastAdmin = dbAdmins.get(dbAdmins.size() - 1);
            String lastAdminId = lastAdmin[0];
            int id = Integer.parseInt(lastAdminId) + 1;
            this.adminId = String.valueOf(id);
        }
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        validateSecurityLevel(securityLevel);
        this.securityLevel = securityLevel;
        if (db != null) {
            db.updateSystemAdmin(this);
        }
    }
}