import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:UniversityManagementSystem.db";

    // Establish connection to SQLite
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
        return conn;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO User(userId,username,password,name,email,contactinfo) " +
                "VALUES(?,?,?,?,?,?)";

        try (var conn = connect(); var pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getContactInfo());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Failed to add user: " + e.getMessage());
            return false;
        }
    }

    public void createTables() {
        String userTable = "CREATE TABLE IF NOT EXISTS User (" +
                "userId TEXT PRIMARY KEY," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "email TEXT," +
                "contactinfo TEXT" +
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

}










