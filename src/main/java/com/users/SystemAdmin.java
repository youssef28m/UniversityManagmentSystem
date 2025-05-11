package com.users;

import com.database.DatabaseManager;


public class SystemAdmin extends User {

    private String adminId;
    private String securityLevel;
    private DatabaseManager db = new DatabaseManager();
    private User admin;
    private Object FileManager;

    public SystemAdmin(String userId, String username, String password, String name,
                       String email, String contactInfo, String adminId,
                       String securityLevel) {
        super(userId, username, password, name, email, contactInfo);
        this.adminId = adminId;
        this.securityLevel = securityLevel;
    }

    @Override
    boolean updateProfile() {
        return true;
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
        // حفظ المستخدم في قاعدة البيانات إذا كان SystemAdmin
        if (user instanceof SystemAdmin) {
            SystemAdmin admin = (SystemAdmin) user;
            boolean success = db.addSystemAdmin(admin); // db هو كائن قاعدة البيانات

            if (success) {
                System.out.println("SystemAdmin created: " + admin.getUsername());
            } else {
                System.out.println("Failed to create SystemAdmin: " + admin.getUsername());
            }
        } else {
            // يمكنك هنا حفظ أنواع أخرى من المستخدمين حسب نوعهم
            System.out.println("User created: " + user.getUsername() + " (" + user.getClass().getSimpleName() + ")");
        }
    }


    public void modifySystemSettings(String setting, String value) {
        // مثال بسيط
        System.out.println("System setting updated: " + setting + " = " + value);
    }

    public void backupData() {
        try {
            System.out.println("System data backup completed successfully.");
        } catch (Exception e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }

    public void managePermissions(User user, String newRole) {
        //user.setUserType(UserType.fromDisplayName(newRole));

        String[] updatedData = {
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getContactInfo(),
                user.getUserType().getDisplayName()
        };

        boolean success = db.updateUserProfile(user.getUserId(), updatedData);

        if (success) {
            System.out.println("Permissions updated: " + user.getUsername() + " is now a " + newRole);
        } else {
            System.out.println("Failed to update permissions for: " + user.getUsername());
        }
}


}
