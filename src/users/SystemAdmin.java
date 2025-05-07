package users;

public class SystemAdmin extends User {

    private String adminId;
    private String securityLevel;

    public SystemAdmin(String userId, String username, String password, String name,
                       String email, String contactInfo, String adminId,
                       String securityLevel) {
        super(userId, username, password, name, email, contactInfo);
        this.adminId = adminId;
        this.securityLevel = securityLevel;
    }

    @Override
    String updateProfile() {
        return "";
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }
    public void createUser(User user) {
        // Save the user to storage (e.g., a text file or database)
        FileManager.saveUser(user);
        System.out.println("User created: " + user.getUsername() + " (" + user.getClass().getSimpleName() + ")");
    }
    public void modifySystemSettings(String setting, String value) {
        // Simple settings modification logic
        System.out.println("System setting updated: " + setting + " = " + value);
        // You can expand this to store in a configuration file
    }
    public void backupData() {
        try {
            FileManager.backupAllData(); // This method should copy all files to a backup folder
            System.out.println("System data backup completed successfully.");
        } catch (Exception e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }
    public void managePermissions(User user, String newRole) {
        user.setRole(newRole); // Requires setRole() method in User class
        FileManager.saveUser(user); // Save the updated user information
        System.out.println("Permissions updated: " + user.getUsername() + " is now a " + newRole);
    }
     
    
}
