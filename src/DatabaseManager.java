import users.*;
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
                "department_id INTEGER PRIMARY KEY AUTOINCREMENT," +
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
                "prerequisit_course TEXT PRIMARY KEY," +
                "course_id INTGER NOT NULL UNIQUE," +
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
            System.out.println("User created successfully");
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
            pstmt.setString(3, student.getacademicStatus());
            pstmt.setString(4, student.getUserId());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("error creating student " + e.getMessage());
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

    // ----------------------------------------------------------------------------- //
    // Course Management
    // ----------------------------------------------------------------------------- //
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses(course_id, title, description, credit_hours,instructor_id, max_capacity, schedule)"
                + "VALUES(?,?,?,?,?,?,?);";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, course.getCourseId());
            pstmt.setString(2, course.getTitle());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getCreditHours());
            pstmt.setString(5, course.getInstructor());
            pstmt.setInt(6, course.getMaxCapacity());
            pstmt.setString(7, course.getSchedule());
            pstmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET title = ?, description = ?, credit_hours = ?, instructor_id = ?, max_capacity = ?, schedule = ? WHERE course_id = ?;";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getTitle());
            pstmt.setString(2, course.getDescription());
            pstmt.setInt(3, course.getCreditHours());
            pstmt.setString(4, course.getInstructor());
            pstmt.setInt(5, course.getMaxCapacity());
            pstmt.setString(6, course.getSchedule());
            pstmt.setInt(7, course.getCourseId());
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
                coursesList.add(courseData);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving courses: " + e.getMessage());
        }

        return coursesList;
    }

    public boolean addPrerequisites(int courseId, ArrayList<Integer> prerequisites) {
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
    public ArrayList<Integer> getCoursePrerequisites(int courseId) {
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

    public boolean enrollStudent(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments(student_id, course_id, enrollment_date, status)" +
                "VALUES(?,?,?,?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getEnrollmentDate());
            pstmt.setString(4, enrollment.getStatusValue());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error enrolling student: " + e.getMessage());
            return false;
        }
    }

    public boolean removeStudentFromCourse(String studentId, String courseId) {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
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
            pstmt.setString(2, enrollment.getStatusValue());
            pstmt.setString(3, enrollment.getStudentId());
            pstmt.setInt(4, enrollment.getCourseId());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating enrollment: " + e.getMessage());
            return false;
        }
    }

    public List<String> getStudentsInCourse(String courseId) {
        List<String> studentIds = new ArrayList<>();
        String sql = "SELECT student_id FROM enrollments WHERE course_id = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
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

}



