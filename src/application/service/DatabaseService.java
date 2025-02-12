package application.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import application.model.User;

public class DatabaseService {

    private final String dbUrl = "jdbc:postgresql://localhost:5432/office_supply";  // Replace with your database URL
    private final String dbUser = "postgres";   // Replace with your database username
    private final String dbPassword = "chris237"; // Replace with your database password

    
    
    
    public static class UserCredentials {
    	
    	
        private final String hashedPassword;
        private final String salt;

        public UserCredentials(String hashedPassword, String salt) {
            this.hashedPassword = hashedPassword;
            this.salt = salt;
        }

        public String hashedPassword() {
            return hashedPassword;
        }

        public String salt() {
            return salt;
        }
    }

    public Optional<UserCredentials> getUserCredentials(String username) throws SQLException {
        String sql = "SELECT password, salt FROM users WHERE username = ?";  // Adjust table/column names to match your database

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    String salt = rs.getString("salt");
                    return Optional.of(new UserCredentials(hashedPassword, salt));
                } else {
                    return Optional.empty(); // User not found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user credentials: " + e.getMessage());  // Proper error logging is crucial
            throw e; // Re-throw the exception to be handled by the caller
        }
    }


    public boolean createUser(String username, String hashedPassword, String role, String salt, String address) throws SQLException {
        String sql = "INSERT INTO users (username, role, address, password, salt) VALUES (?, ?, ?, ?, ?)"; // Adjust table/column names

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, role);
            pstmt.setString(3, address);
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, salt);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Returns true if the insertion was successful

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;
        }
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {
            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getInt("user_id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password")); //Consider security implications of retrieving password
                user.setRole(resultSet.getString("role"));
                user.setSalt(resultSet.getString("salt"));
                user.setAddress(resultSet.getString("address"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, role = ?, address = ? WHERE user_id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getRole());
            preparedStatement.setString(3, user.getAddress());
            preparedStatement.setInt(4, user.getUserId());
    
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Returns true if the update was successful
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if there was an error
        }
    }
    
    // Example method: Check if a username already exists (important for registration)
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                } else {
                    return false;  // Handle the case where the query fails
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        DatabaseService dbService = new DatabaseService();
        try {
            Optional<UserCredentials> user = dbService.getUserCredentials("testuser");
            if (user.isPresent()) {
                System.out.println("User found: Hashed password = " + user.get().hashedPassword() + ", Salt = " + user.get().salt());
            } else {
                System.out.println("User not found");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
