package com.users;

import com.database.DatabaseManager;

import java.util.ArrayList;

public abstract class User {
    private String userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String contactInfo;
    private UserType userType;
    private DatabaseManager db = new DatabaseManager();

    public User() {}


    public User(String userType ,String username, String password, String name, String email, String contactInfo) {
        this.userType = UserType.valueOf(userType.toUpperCase());
        setUserId();
        setUsername(username);
        setPassword(password);
        this.name = name;
        setEmail(email);
        this.contactInfo = contactInfo;
    }

    public User(String userType ,String userId ,String username, String password, String name, String email, String contactInfo) {
        this.userType = UserType.valueOf(userType.toUpperCase());
        this.userId = userId;
        setUsername(username);
        setPassword(password);
        this.name = name;
        setEmail(email);
        this.contactInfo = contactInfo;
    }


    abstract boolean updateProfile();

    public enum UserType {

        STUDENT("Student"),
        FACULTY("Faculty"),
        ADMIN_STAFF("Admin Staff"),
        SYSTEM_ADMIN("System Admin");

        private final String displayName;

        UserType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public String getUserId() {
        return userId;
    }


    public void setUserId() {
        ArrayList<String[]> users = db.getUsersByType(userType.getDisplayName());
        String prefix;

        switch (userType) {
            case STUDENT:
                prefix = "S";
                break;
            case FACULTY:
                prefix = "F";
                break;
            case ADMIN_STAFF:
                prefix = "A";
                break;
            case SYSTEM_ADMIN:
                prefix = "X";
                break;
            default:
                throw new IllegalStateException("Unexpected user type: " + userType);
        }

        if (users.isEmpty()) {
            this.userId = prefix + "240001";
        } else {
            String[] lastUser = users.get(users.size() - 1);
            String lastUserId = lastUser[0]; // assuming user ID is at index 0
            String numberPart = lastUserId.substring(1); // remove the prefix
            int number = Integer.parseInt(numberPart) + 1;
            this.userId = prefix + String.format("%06d", number);
        }
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 charcters long");
        } else {
            this.username = username;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password.length() < 6 ) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        } else {
            this.password = password;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email.matches(".*@.*")) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Email is not valid");
        }
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
