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
}
