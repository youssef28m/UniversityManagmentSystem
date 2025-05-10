package com.users;

public class AdminStaff extends User {

    private String staffId;
    private int departmentId;  // Using int to match the department_id INTEGER in database
    private String role;

    public AdminStaff(String userId, String username, String password, String name,
                      String email, String contactInfo, String staffId,
                      int departmentId, String role) {
        super(userId, username, password, name, email, contactInfo);
        this.staffId = staffId;
        this.departmentId = departmentId;
        this.role = role;
    }

    @Override
    String updateProfile() {
        return "";
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
