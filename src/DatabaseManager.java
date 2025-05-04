import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

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
        String userTable = "CREATE TABLE IF NOT EXISTS User (" +
                "userId TEXT PRIMARY KEY," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "email TEXT," +
                "contactinfo TEXT" +
                "userType TEXT NOT NULL" +
                ");";

        String studentTable = "CREATE TABLE IF NOT EXISTS Student (" +
                "studentid TEXT PRIMARY KEY," +
                "admissionDate TEXT," +
                "academicStatus TEXT," +
                "userId TEXT NOT NULL," +
                "FOREIGN KEY (userId) REFERENCES User(userId)" +
                ");";

        try (var conn = connect(); var stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(studentTable);
            System.out.println("Tables has been created succefully");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    //------------------------------------------------------------------------------//


    // ----------------------------------------------------------------------------- //
    // User Managment
    // ----------------------------------------------------------------------------- //
    public boolean createUser(User user) {
        String sql = "INSERT INTO User(userId,username,password,name,email,contactinfo,userType) " +
                "VALUES(?,?,?,?,?,?,?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getContactInfo());
            pstmt.setString(7, user.getUserType().getDisplayName());
            pstmt.executeUpdate();
            System.out.println("user created succefully");
            return true;

        } catch (SQLException e) {
            System.err.println("Failed to add user: " + e.getMessage());
            return false;
        }
    }

    // return an arraylist of all users
    // the array formate is {userid ,username, password, name, email, contactinfo, usertype}
    public ArrayList<String[]> getAllUsers() {
        String sql = "SELECT * FROM User";

        ArrayList<String[]> users = new ArrayList<>();

        try (var conn = connect();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql))
        {
         while (rs.next()) {
             String userid = rs.getString("userid");
             String username = rs.getString("username");
             String password = rs.getString("password");
             String name = rs.getString("name");
             String email = rs.getString("email");
             String contactinfo = rs.getString("contactinfo");
             String userType = rs.getString("userType");

             // Create an array for each user and add to the ArrayList
             String[] user = {userid, username,password ,name , email, contactinfo, userType};
             users.add(user);
         }
        } catch (SQLException e){
            System.err.println("Error getting users: " + e.getMessage());
            return new ArrayList<>();
        }
        return users;
    }

    public ArrayList<String[]> getUsersByType(String userType) {
        String sql = "SELECT * FROM User WHERE userType = ?";

        ArrayList<String[]> users = new ArrayList<>();

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userType);  // Set the userType to the query parameter
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String[] user = new String[7]; // Create an array to store user data
                    user[0] = rs.getString("userId");
                    user[1] = rs.getString("username");
                    user[2] = rs.getString("password");
                    user[3] = rs.getString("name");
                    user[4] = rs.getString("email");
                    user[5] = rs.getString("contactinfo");
                    user[6] = rs.getString("userType");

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
        String sql = "SELECT * FROM user WHERE userid = ?";
        String[] user = null;

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userid);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new String[7];
                user[0] = rs.getString("userid");
                user[1] = rs.getString("username");
                user[2] = rs.getString("password");
                user[3] = rs.getString("name");
                user[4] = rs.getString("email");
                user[5] = rs.getString("contactinfo");
                user[6] = rs.getString("userType");
            }

        } catch (SQLException e){
            System.err.println("Error getting users: " + e.getMessage());
        }
        return user;
    }

    // take user data {username, name ,email, contactinfo, userType}
    public boolean updateUserProfile(String userId, String[] userData) {
        String sql = "UPDATE User SET username = ?, name = ?, email = ?, contactinfo = ?, userType = ? WHERE userId = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userData[0]); // username
            pstmt.setString(2, userData[1]); // name
            pstmt.setString(3, userData[2]); // email
            pstmt.setString(4, userData[3]); // contactinfo
            pstmt.setString(5, userData[4]); // userType
            pstmt.setString(6, userId);      // WHERE userId

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(String userId, String newPassword) {
        String sql = "UPDATE User SET password = ? WHERE userId = ?";

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
        String sql = "DELETE FROM User WHERE userId = ?";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------------------------------------------------------- //

}




