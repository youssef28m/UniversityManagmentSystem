package com.database;

import com.academics.*;
import com.system.*;
import com.users.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:UniversityManagementSystem.db";

    // ----------------------------------------------------------------------------- //
    // Initialization and Connection
    // ----------------------------------------------------------------------------- //
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
        return conn;
    }

    public void createTables() {
        String userTable = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id TEXT PRIMARY KEY," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "email TEXT," +
                "contact_info TEXT," +
                "user_type TEXT NOT NULL" +
                ");";

        String studentTable = "CREATE TABLE IF NOT EXISTS students (" +
                "student_id TEXT PRIMARY KEY," +
                "admissionDate TEXT," +
                "academicStatus TEXT," +
                "user_id TEXT NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ");";

        String departmentTable = "CREATE TABLE IF NOT EXISTS departments (" +
                "department_id INTEGER PRIMARY KEY," +
                "name TEXT UNIQUE NOT NULL" +
                ");";

        String facultyTable = "CREATE TABLE IF NOT EXISTS faculty (" +
                "faculty_id TEXT PRIMARY KEY," +
                "user_id TEXT UNIQUE NOT NULL," +
                "department_id INTEGER," +
                "expertise TEXT," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (department_id) REFERENCES departments(department_id)" +
                ");";

        String adminStaffTable = "CREATE TABLE IF NOT EXISTS admin_staff (" +
                "staff_id TEXT PRIMARY KEY," +
                "user_id TEXT UNIQUE NOT NULL," +
                "department_id INTEGER," +
                "role TEXT NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE," +
                "FOREIGN KEY (department_id) REFERENCES departments(department_id)" +
                ");";

        String systemAdminTable = "CREATE TABLE IF NOT EXISTS system_admin (" +
                "admin_id TEXT PRIMARY KEY," +
                "user_id TEXT UNIQUE NOT NULL," +
                "security_level TEXT," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                ");";

        String courseTable = "CREATE TABLE IF NOT EXISTS courses (" +
                "course_id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "credit_hours INTEGER NOT NULL," +
                "department_id INTEGER," +
                "instructor_id TEXT," +
                "max_capacity INTEGER," +
                "schedule TEXT," +
                "FOREIGN KEY (department_id) REFERENCES departments(department_id)," +
                "FOREIGN KEY (instructor_id) REFERENCES faculty(faculty_id)" +
                ");";

        String enrollmentTable = "CREATE TABLE IF NOT EXISTS enrollments (" +
                "enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "student_id TEXT," +
                "course_id TEXT," +
                "enrollment_date TEXT," +
                "grade TEXT," +
                "status TEXT," +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE," +
                "FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE" +
                ");";

        String prerequisitesTable = "CREATE TABLE IF NOT EXISTS prerequisites (" +
                "prerequisit_course TEXT," +
                "course_id INTGER NOT NULL PRIMARY KEY," +
                "FOREIGN KEY (course_id) REFERENCES courses(course_id)" +
                ");";

        try (var conn = connect(); var stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(studentTable);
            stmt.execute(departmentTable);
            stmt.execute(facultyTable);
            stmt.execute(systemAdminTable);
            stmt.execute(adminStaffTable);
            stmt.execute(courseTable);
            stmt.execute(enrollmentTable);
            stmt.execute(prerequisitesTable);
            System.out.println("Tables have been created successfully");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    // ----------------------------------------------------------------------------- //
    // User Management
    // ----------------------------------------------------------------------------- //
    public boolean addUser(User user) {
        String sql = "INSERT INTO users(user_id, username, password, name, email, contact_info, user_type) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getContactInfo());
            pstmt.setString(7, user.getUserType().getDisplayName());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Failed to add user: " + e.getMessage());
            return false;
        }
    }

    // return an arraylist of all users
    // the array format is {userid, username, password, name, email, contact_info, user_type}
    public ArrayList<String[]> getAllUsers() {
        String sql = "SELECT * FROM users";

        ArrayList<String[]> users = new ArrayList<>();

        try (var conn = connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql))
        {
            while (rs.next()) {
                String userid = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String contactInfo = rs.getString("contact_info");
                String userType = rs.getString("user_type");

                // Create an array for each user and add to the ArrayList
                String[] user = {userid, username, password, name, email, contactInfo, userType};
                users.add(user);
            }
        } catch (SQLException e){
            System.err.println("Error getting users: " + e.getMessage());
            return new ArrayList<>();
        }
        return users;
    }

    public ArrayList<String[]> getUsersByType(String userType) {
        String sql = "SELECT * FROM users WHERE user_type = ?";

        ArrayList<String[]> users = new ArrayList<>();

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userType);  // Set the userType to the query parameter
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] user = new String[7]; // Create an array to store user data
                    user[0] = rs.getString("user_id");
                    user[1] = rs.getString("username");
                    user[2] = rs.getString("password");
                    user[3] = rs.getString("name");
                    user[4] = rs.getString("email");
                    user[5] = rs.getString("contact_info");
                    user[6] = rs.getString("user_type");

                    users.add(user);  // Add user data to the list
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users by type: " + e.getMessage());
            return new ArrayList<>();  // Return an empty list in case of error
        }

        return users;  // Return the list of users
    }

    // return null when the user is not found
    public String[] getOneUser(String userid) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        String[] user = null;

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userid);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new String[7];
                    user[0] = rs.getString("user_id");
                    user[1] = rs.getString("username");
                    user[2] = rs.getString("password");
                    user[3] = rs.getString("name");
                    user[4] = rs.getString("email");
                    user[5] = rs.getString("contact_info");
                    user[6] = rs.getString("user_type");
                }
            }
        } catch (SQLException e){
            System.err.println("Error getting user: " + e.getMessage());
        }
        return user;
    }

    // take user data {username, name, email, contact_info, user_type}
    public boolean updateUserProfile(String userId, String[] userData) {
        String sql = "UPDATE users SET username = ?, name = ?, email = ?, contact_info = ?, user_type = ? WHERE user_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userData[0]); // username
            pstmt.setString(2, userData[1]); // name
            pstmt.setString(3, userData[2]); // email
            pstmt.setString(4, userData[3]); // contact_info
            pstmt.setString(5, userData[4]); // user_type
            pstmt.setString(6, userId);      // WHERE user_id

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(String userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }


    // ----------------------------------------------------------------------------- //
    // Student Management
    // ----------------------------------------------------------------------------- //
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students(student_id, admissionDate, academicStatus, user_id) "+
                "VALUES(?,?,?,?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql) ) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getAdmissionDate());
            pstmt.setString(3, student.getAcademicStatus());
            pstmt.setString(4, student.getUserId());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("error creating student " + e.getMessage());
            return false;
        }
    }

    public Student getStudent(String studentId) {
        String sql = "SELECT s.student_id, s.admissionDate, s.academicStatus, s.user_id, " +
                "u.username, u.password, u.name, u.email, u.contact_info, u.user_type " +
                "FROM students s JOIN users u ON s.user_id = u.user_id " +
                "WHERE s.student_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    List<Integer> enrolledCourses = getEnrolledCourses(studentId);
                    return new Student(
                            rs.getString("user_type"),
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("contact_info"),
                            rs.getString("student_id"),
                            rs.getString("admissionDate"),
                            rs.getString("academicStatus"),
                            enrolledCourses
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student: " + e.getMessage());
        }
        return null;
    }

    public Student getStudentByUserId(String userId) {
        String sql = "SELECT s.student_id, s.admissionDate, s.academicStatus, s.user_id, " +
                "u.username, u.password, u.name, u.email, u.contact_info, u.user_type " +
                "FROM students s JOIN users u ON s.user_id = u.user_id " +
                "WHERE u.user_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    List<Integer> enrolledCourses = getEnrolledCourses(rs.getString("student_id"));
                    return new Student(
                            rs.getString("user_type"),
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("contact_info"),
                            rs.getString("student_id"),
                            rs.getString("admissionDate"),
                            rs.getString("academicStatus"),
                            enrolledCourses
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<String[]> getAllStudents() {
        String sql = "SELECT student_id, admissionDate, academicStatus, user_id FROM students";

        ArrayList<String[]> students = new ArrayList<>();

        try (var conn = connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String admissionDate = rs.getString("admissionDate");
                String academicStatus = rs.getString("academicStatus");
                String userId = rs.getString("user_id");

                // Create an array for each student and add to the ArrayList
                String[] student = {studentId, admissionDate, academicStatus, userId};
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error getting students: " + e.getMessage());
            return new ArrayList<>();
        }
        return students;
    }

    public boolean updateStudent(Student student) {
        String userSql = "UPDATE users SET username = ?, password = ?, name = ?, email = ?, contact_info = ?, user_type = ? WHERE user_id = ?";
        String studentSql = "UPDATE students SET admissionDate = ?, academicStatus = ? WHERE student_id = ?";

        try (var conn = connect()) {
            conn.setAutoCommit(false); // Start transaction

            // Update users table
            try (var userPstmt = conn.prepareStatement(userSql)) {
                userPstmt.setString(1, student.getUsername());
                userPstmt.setString(2, student.getPassword());
                userPstmt.setString(3, student.getName());
                userPstmt.setString(4, student.getEmail());
                userPstmt.setString(5, student.getContactInfo());
                userPstmt.setString(6, student.getUserType().getDisplayName());
                userPstmt.setString(7, student.getUserId());
                userPstmt.executeUpdate();
            }

            // Update students table
            try (var studentPstmt = conn.prepareStatement(studentSql)) {
                studentPstmt.setString(1, student.getAdmissionDate());
                studentPstmt.setString(2, student.getAcademicStatus());
                studentPstmt.setString(3, student.getStudentId());
                studentPstmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            try (var conn = connect()) {
                conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            return false;
        }
    }

    public boolean deleteStudent(String studentId) {
        String sql = "DELETE FROM users WHERE user_id = (SELECT user_id FROM students WHERE student_id = ?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                System.err.println("No student found with student_id: " + studentId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }
    // ----------------------------------------------------------------------------- //
    // Faculty Management
    // ----------------------------------------------------------------------------- //
    public boolean addFaculty(Faculty faculty) {
        String sql = "INSERT INTO faculty(faculty_id, user_id, department_id, expertise) " +
                "VALUES(?, ?, ?, ?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, faculty.getFacultyId());
            pstmt.setString(2, faculty.getUserId());
            if (faculty.getDepartment() != 0) {
                pstmt.setInt(3, faculty.getDepartment());
            }
            pstmt.setString(4, faculty.getExpertise());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error creating faculty: " + e.getMessage());
            return false;
        }
    }

    public boolean updateFaculty(Faculty faculty) {
        String userSql = "UPDATE users SET username = ?, password = ?, name = ?, email = ?, contact_info = ?, user_type = ? WHERE user_id = ?";
        String facultySql = "UPDATE faculty SET department_id = ?, expertise = ? WHERE faculty_id = ?";

        try (var conn = connect()) {
            conn.setAutoCommit(false);

            try (var userPstmt = conn.prepareStatement(userSql)) {
                userPstmt.setString(1, faculty.getUsername());
                userPstmt.setString(2, faculty.getPassword());
                userPstmt.setString(3, faculty.getName());
                userPstmt.setString(4, faculty.getEmail());
                userPstmt.setString(5, faculty.getContactInfo());
                userPstmt.setString(6, faculty.getUserType().getDisplayName());
                userPstmt.setString(7, faculty.getUserId());
                userPstmt.executeUpdate();
            }

            try (var facultyPstmt = conn.prepareStatement(facultySql)) {
                facultyPstmt.setInt(1, faculty.getDepartment());
                facultyPstmt.setString(2, faculty.getExpertise());
                facultyPstmt.setString(3, faculty.getFacultyId());
                facultyPstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error updating faculty: " + e.getMessage());
            try (var conn = connect()) {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback error: " + rollbackEx.getMessage());
            }
            return false;
        }
    }

    public boolean deleteFaculty(String facultyId) {
        String sql = "DELETE FROM users WHERE user_id = (SELECT user_id FROM faculty WHERE faculty_id = ?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, facultyId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                System.err.println("No faculty found with faculty_id: " + facultyId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting faculty: " + e.getMessage());
            return false;
        }
    }

    public Faculty getFaculty(String facultyId) {
        String sql = "SELECT f.faculty_id, f.user_id, f.department_id, f.expertise, " +
                "u.username, u.password, u.name, u.email, u.contact_info, u.user_type " +
                "FROM faculty f JOIN users u ON f.user_id = u.user_id " +
                "WHERE f.faculty_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, facultyId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ArrayList<Course> coursesTeaching = getCoursesTeaching(facultyId);

                    return new Faculty(
                            rs.getString("user_type"),
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("contact_info"),
                            rs.getString("faculty_id"),
                            rs.getInt("department_id"),
                            rs.getString("expertise"),
                            coursesTeaching
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving faculty: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Course> getCoursesTeaching(String facultyId) {
        String sql = "SELECT course_id FROM courses WHERE instructor_id = ?";
        ArrayList<Course> coursesTeaching = new ArrayList<>();

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, facultyId);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    coursesTeaching.add(getCourse(rs.getInt("course_id")));
                }
            }
            return coursesTeaching;

        } catch (SQLException e) {
            System.err.println("Error retrieving courses Teaching : " + e.getMessage());
        }
        return null;
    }

    public ArrayList<String[]> getAllFaculty() {
        String sql = "SELECT faculty_id, user_id, department_id, expertise FROM faculty";

        ArrayList<String[]> facultyList = new ArrayList<>();

        try (var conn = connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String facultyId = rs.getString("faculty_id");
                String userId = rs.getString("user_id");
                String departmentId = rs.getString("department_id");
                String expertise = rs.getString("expertise");

                String[] faculty = {facultyId, userId, departmentId, expertise};
                facultyList.add(faculty);
            }

        } catch (SQLException e) {
            System.err.println("Error getting faculty: " + e.getMessage());
            return new ArrayList<>();
        }

        return facultyList;
    }

    public Faculty getFacultyByUserId(String userId) {
        String sql = "SELECT f.faculty_id, f.user_id, f.department_id, f.expertise, " +
                "u.username, u.password, u.name, u.email, u.contact_info, u.user_type " +
                "FROM faculty f JOIN users u ON f.user_id = u.user_id " +
                "WHERE f.user_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ArrayList<Course> coursesTeaching = getCoursesTeaching(rs.getString("faculty_id"));
                    return new Faculty(
                            rs.getString("user_type"),
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("contact_info"),
                            rs.getString("faculty_id"),
                            rs.getInt("department_id"),
                            rs.getString("expertise"),
                            coursesTeaching
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving faculty by user ID: " + e.getMessage());
        }
        return null;
    }


    // ----------------------------------------------------------------------------- //
    // SystemAdmin Management
    // ----------------------------------------------------------------------------- //
    public boolean addSystemAdmin(SystemAdmin admin) {
        String sql = "INSERT INTO system_admin(admin_id, user_id, security_level) " +
                "VALUES(?, ?, ?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getAdminId());
            pstmt.setString(2, admin.getUserId());
            pstmt.setString(3, admin.getSecurityLevel());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error creating system admin: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<String[]> getAllSystemAdmin() {
        String sql = "SELECT admin_id, user_id, security_level FROM system_admin";

        ArrayList<String[]> adminList = new ArrayList<>();

        try (var conn = connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String adminId = rs.getString("admin_id");
                String userId = rs.getString("user_id");
                String securityLevel = rs.getString("security_level");

                String[] admin = {adminId, userId, securityLevel};
                adminList.add(admin);
            }

        } catch (SQLException e) {
            System.err.println("Error getting system admins: " + e.getMessage());
            return new ArrayList<>();
        }

        return adminList;
    }

    public boolean updateSystemAdmin(SystemAdmin admin) {
        String userSql = "UPDATE users SET username = ?, password = ?, name = ?, email = ?, contact_info = ?, user_type = ? WHERE user_id = ?";
        String adminSql = "UPDATE system_admin SET security_level = ? WHERE admin_id = ?";

        try (var conn = connect()) {
            conn.setAutoCommit(false);

            try (var userPstmt = conn.prepareStatement(userSql)) {
                userPstmt.setString(1, admin.getUsername());
                userPstmt.setString(2, admin.getPassword());
                userPstmt.setString(3, admin.getName());
                userPstmt.setString(4, admin.getEmail());
                userPstmt.setString(5, admin.getContactInfo());
                userPstmt.setString(6, admin.getUserType().getDisplayName());
                userPstmt.setString(7, admin.getUserId());
                userPstmt.executeUpdate();
            }

            try (var adminPstmt = conn.prepareStatement(adminSql)) {
                adminPstmt.setString(1, admin.getSecurityLevel());
                adminPstmt.setString(2, admin.getAdminId());
                adminPstmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error updating system admin: " + e.getMessage());
            try (var conn = connect()) {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback error: " + rollbackEx.getMessage());
            }
            return false;
        }
    }

    public boolean deleteSystemAdmin(String adminId) {
        String sql = "DELETE FROM users WHERE user_id = (SELECT user_id FROM system_admin WHERE admin_id = ?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, adminId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                System.err.println("No system admin found with admin_id: " + adminId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting system admin: " + e.getMessage());
            return false;
        }
    }

    public SystemAdmin getSystemAdmin(String adminId) {
        String sql = "SELECT sa.admin_id, sa.user_id, sa.security_level, " +
                "u.username, u.password, u.name, u.email, u.contact_info, u.user_type " +
                "FROM system_admin sa JOIN users u ON sa.user_id = u.user_id " +
                "WHERE sa.admin_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, adminId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new SystemAdmin(
                            rs.getString("user_type"),
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("contact_info"),
                            rs.getString("admin_id"),
                            rs.getString("security_level")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving system admin: " + e.getMessage());
        }
        return null;
    }

    public SystemAdmin getSystemAdminByUserId(String userId) {
        String sql = "SELECT sa.admin_id, sa.user_id, sa.security_level, " +
                "u.username, u.password, u.name, u.email, u.contact_info, u.user_type " +
                "FROM system_admin sa JOIN users u ON sa.user_id = u.user_id " +
                "WHERE sa.user_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new SystemAdmin(
                            rs.getString("user_type"),
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("contact_info"),
                            rs.getString("admin_id"),
                            rs.getString("security_level")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving system admin by user ID: " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------------------------- //
    // AdminStaff Management
    // ----------------------------------------------------------------------------- //
    public boolean addAdminStaff(AdminStaff staff) {
        String sql = "INSERT INTO admin_staff(staff_id, user_id, department_id, role) " +
                "VALUES(?, ?, ?, ?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, staff.getStaffId());
            pstmt.setString(2, staff.getUserId());
            pstmt.setInt(3, staff.getDepartmentId());
            pstmt.setString(4, staff.getRole());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error creating admin staff: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAdminStaff(AdminStaff staff) {
        String sql = "UPDATE admin_staff SET department_id = ?, role = ? WHERE staff_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, staff.getDepartmentId());
            pstmt.setString(2, staff.getRole());
            pstmt.setString(3, staff.getStaffId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating admin staff: " + e.getMessage());
            return false;
        }
    }

    public AdminStaff getAdminStaff(String staffId) {
        String sql = "SELECT u.user_id, u.username, u.password, u.name, u.email, u.contact_info, "
                + "a.staff_id, a.department_id, a.role "
                + "FROM users u "
                + "JOIN admin_staff a ON u.user_id = a.user_id "
                + "WHERE a.staff_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, staffId);

            var rs = pstmt.executeQuery();
            if (rs.next()) {
                String userType = rs.getString("user_type");
                String userId = rs.getString("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String contactInfo = rs.getString("contact_info");
                String retrievedStaffId = rs.getString("staff_id");
                int departmentId = rs.getInt("department_id");
                String role = rs.getString("role");

                return new AdminStaff(userType, userId, username, password, name, email, contactInfo,
                        departmentId, role , retrievedStaffId);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving Admin Staff: " + e.getMessage());
        }

        return null;
    }

    public ArrayList<String[]> getAllStaff() {
        String sql = "SELECT staff_id, user_id, department_id, role FROM admin_staff";

        ArrayList<String[]> staffList = new ArrayList<>();

        try (var conn = connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String staffId = rs.getString("staff_id");
                String userId = rs.getString("user_id");
                String departmentId = rs.getString("department_id");
                String role = rs.getString("role");

                // Create an array for each staff and add to the list
                String[] staff = {staffId, userId, departmentId, role};
                staffList.add(staff);
            }

        } catch (SQLException e) {
            System.err.println("Error getting admin staff: " + e.getMessage());
            return new ArrayList<>();
        }

        return staffList;
    }

    public AdminStaff getAdminStaffByUserId(String userId) {
        String sql = "SELECT u.user_id, u.username, u.password, u.name, u.email, u.contact_info, " +
                "a.staff_id, a.department_id, a.role, u.user_type " +
                "FROM users u " +
                "JOIN admin_staff a ON u.user_id = a.user_id " +
                "WHERE a.user_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new AdminStaff(
                            rs.getString("user_type"),
                            rs.getString("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("contact_info"),
                            rs.getInt("department_id"),
                            rs.getString("role"),
                            rs.getString("staff_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving admin staff by user ID: " + e.getMessage());
        }
        return null;
    }


    // ----------------------------------------------------------------------------- //
    // Course Management
    // ----------------------------------------------------------------------------- //
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses(course_id, title, description, credit_hours,instructor_id, max_capacity, schedule, department_id)"
                + "VALUES(?,?,?,?,?,?,?,?);";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, course.getCourseId());
            pstmt.setString(2, course.getTitle());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getCreditHours());
            pstmt.setString(5, course.getInstructor());
            pstmt.setInt(6, course.getMaxCapacity());
            pstmt.setString(7, course.getSchedule());
            pstmt.setInt(8, course.getDepartment());
            pstmt.executeUpdate();

            addPrerequisites(course.getCourseId(), course.getPrerequisites());
            return true;

        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
            return false;
        }
    }

    public Course getCourse(int courseId) {
        String courseData = "SELECT course_id, title, description, credit_hours, " +
                "instructor_id, max_capacity, schedule, department_id " +
                "FROM courses " +
                "WHERE course_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(courseData)) {
            pstmt.setInt(1, courseId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int course_id = rs.getInt("course_id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    int credit_hours = rs.getInt("credit_hours");
                    String schedule = rs.getString("schedule");
                    String instructor_id = rs.getString("instructor_id");
                    int max_capacity = rs.getInt("max_capacity");
                    int department = rs.getInt("department_id");

                    List<Integer> prerequisites = getCoursePrerequisites(courseId);
                    List<String> enrolledStudents = getStudentsInCourse(courseId);

                    return new Course(course_id, title, description, credit_hours,
                            prerequisites, schedule, instructor_id, max_capacity, enrolledStudents, department);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving course data: " + e.getMessage());
        }

        return null; // return null if not found or error occurs
    }


    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET title = ?, description = ?, credit_hours = ?, instructor_id = ?, max_capacity = ?, schedule = ?, department_id = ? WHERE course_id = ?;";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getTitle());
            pstmt.setString(2, course.getDescription());
            pstmt.setInt(3, course.getCreditHours());
            pstmt.setString(4, course.getInstructor());
            pstmt.setInt(5, course.getMaxCapacity());
            pstmt.setString(6, course.getSchedule());
            pstmt.setInt(7, course.getDepartment());
            pstmt.setInt(8, course.getCourseId());
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?;";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            return false;
        }
    }

    /*
     Retrieves all courses from the database.
     @return ArrayList<List<Object>> where each inner List represents a course with the following structure:
        - Index 0: course_id (Integer)
        - Index 1: title (String)
        - Index 2: description (String)
        - Index 3: credit_hours (Integer)
        - Index 4: instructor_id (String)
        - Index 5: max_capacity (Integer)
        - Index 6: schedule (String)
     */
    public ArrayList<List<Object>> getAllCourses() {
        String sql = "SELECT * FROM courses;";
        ArrayList<List<Object>> coursesList = new ArrayList<>();

        try (var conn = connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                List<Object> courseData = new ArrayList<>();
                courseData.add(rs.getInt("course_id"));
                courseData.add(rs.getString("title"));
                courseData.add(rs.getString("description"));
                courseData.add(rs.getInt("credit_hours"));
                courseData.add(rs.getString("instructor_id"));
                courseData.add(rs.getInt("max_capacity"));
                courseData.add(rs.getString("schedule"));
                courseData.add(rs.getInt("department_id"));
                coursesList.add(courseData);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving courses: " + e.getMessage());
        }

        return coursesList;
    }

    public boolean addPrerequisites(int courseId, List<Integer> prerequisites) {
        String sql = "INSERT INTO prerequisites(prerequisit_course, course_id) VALUES(?,?);";

        for (int i =0; i < prerequisites.size(); i++) {
            try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, prerequisites.get(i));
                pstmt.setInt(2, courseId);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Error adding prerequisites: " + e.getMessage());
                return false;
            }
        }

        return true;
    }

    public boolean deletePrerequisite(int courseId, int prerequisiteId) {
        String sql = "DELETE FROM prerequisites WHERE course_id = ? AND prerequisit_course = ?;";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, prerequisiteId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting prerequisite: " + e.getMessage());
            return false;
        }
    }

    /*
     * Retrieves all prerequisites for a specific course.
     * @return ArrayList<Integer> containing the IDs of all prerequisite courses
     *         Returns an empty ArrayList if the course has no prerequisites or if an error occurs
     */
    public List<Integer> getCoursePrerequisites(int courseId) {
        String sql = "SELECT prerequisit_course FROM prerequisites WHERE course_id = ?;";
        ArrayList<Integer> prerequisites = new ArrayList<>();

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            var rs = pstmt.executeQuery();

            while (rs.next()) {
                prerequisites.add(rs.getInt("prerequisit_course"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving course prerequisites: " + e.getMessage());
        }

        return prerequisites;
    }


    // ----------------------------------------------------------------------------- //
    // enrollment Management
    // ----------------------------------------------------------------------------- //

    public boolean enrollStudent(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments(student_id, course_id, enrollment_date, status)" +
                "VALUES(?,?,?,?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, enrollment.getStudent().getStudentId());
            pstmt.setInt(2, enrollment.getCourse().getCourseId());
            pstmt.setString(3, enrollment.getEnrollmentDate());
            pstmt.setString(4, enrollment.getStatus());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error enrolling student: " + e.getMessage());
            return false;
        }
    }

    public List<Integer> getEnrolledCourses(String studentId) {
        String sql = "SELECT course_id FROM enrollments WHERE student_id = ?";
        List<Integer> enrolledCourses = new ArrayList<>();

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrolledCourses.add(rs.getInt("course_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving enrolled courses: " + e.getMessage());
        }

        return enrolledCourses;
    }

    public Enrollment getEnrollment(String studentId, int courseId) {
        String sql = "SELECT enrollment_date, grade, status FROM enrollments WHERE student_id = ? AND course_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setInt(2, courseId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Fetch Student and Course objects
                    Student student = getStudent(studentId);
                    Course course = getCourse(courseId);

                    // Check if both Student and Course were found
                    if (student != null && course != null) {
                        return new Enrollment(
                                student,
                                course,
                                rs.getString("enrollment_date"),
                                rs.getInt("grade"),
                                rs.getString("status")
                        );
                    } else {
                        System.err.println("Student or Course not found for enrollment");
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving enrollment: " + e.getMessage());
        }
        return null;
    }

    public boolean removeStudentFromCourse(String studentId, int courseId) {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setInt(2, courseId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error removing student from course: " + e.getMessage());
            return false;
        }
    }

    public boolean updateEnrollment(Enrollment enrollment) {
        String sql = "UPDATE enrollments SET grade = ?, status = ? WHERE student_id = ? AND course_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getGrade());
            pstmt.setString(2, enrollment.getStatus());
            pstmt.setString(3, enrollment.getStudent().getStudentId());
            pstmt.setInt(4, enrollment.getCourse().getCourseId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating enrollment: " + e.getMessage());
            return false;
        }
    }

    public List<String> getStudentsInCourse(int courseId) {
        List<String> studentIds = new ArrayList<>();
        String sql = "SELECT student_id FROM enrollments WHERE course_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    studentIds.add(rs.getString("student_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students in course: " + e.getMessage());
        }

        return studentIds;
    }

    // ----------------------------------------------------------------------------- //
    // department Management
    // ----------------------------------------------------------------------------- //

    public boolean createDepartment(Department department) {
        String sql = "INSERT INTO departments (name) VALUES (?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, department.getName());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating department: " + e.getMessage());
            return false;
        }
    }

    public Department getDepartment(int departmentId) {
        String sql = "SELECT * FROM departments WHERE department_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Department(
                            getDeparmentsCourses(departmentId),
                            getDepartmentsFaculty(departmentId),
                            rs.getString("name"),
                            rs.getInt("department_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving department: " + e.getMessage());
        }
        return null;
    }

    public Department getDepartment(String name) {
        String sql = "SELECT * FROM departments WHERE name = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Department(
                            getDeparmentsCourses(rs.getInt("department_id")),
                            getDepartmentsFaculty(rs.getInt("department_id")),
                            rs.getString("name"),
                            rs.getInt("department_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving department by name: " + e.getMessage());
        }
        return null;
    }

    public List<Faculty> getDepartmentsFaculty(int departmentId) {
        String sql = "SELECT user_id FROM faculty WHERE department_id = ?";
        List<Faculty> facultyList = new ArrayList<>();
        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    facultyList.add(getFaculty(rs.getString("user_id")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving departments faculty: " + e.getMessage());
        }
        return facultyList;
    }

    public List<Course> getDeparmentsCourses(int departmentId) {
        String sql = "SELECT course_id FROM courses WHERE department_id = ?";
        List<Course> courseList = new ArrayList<>();
        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courseList.add(getCourse(rs.getInt("course_id")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving departments courses: " + e.getMessage());
        }
        return courseList;
    }

    public boolean updateDepartment(Department department) {
        String sql = "UPDATE departments SET name = ? WHERE department_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, department.getName());
            pstmt.setInt(2, department.getDepartmentId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating department: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDepartment(int departmentId) {
        String sql = "DELETE FROM departments WHERE department_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting department: " + e.getMessage());
            return false;
        }
    }

    public List<Department> getAllDepartments() {
        String sql = "SELECT * FROM departments";
        List<Department> departments = new ArrayList<>();

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql); var rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int departmentId = rs.getInt("department_id");
                String name = rs.getString("name");

                Department department = new Department(
                        getDeparmentsCourses(departmentId),
                        getDepartmentsFaculty(departmentId),
                        name,
                        departmentId
                );

                departments.add(department);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all departments: " + e.getMessage());
        }

        return departments;
    }


}



