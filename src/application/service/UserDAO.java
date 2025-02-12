package application.service;

//UserDAO.java  (Example - Adapt to your database/data source)
import application.model.User;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class UserDAO {

 // JDBC URL, username and password of MySQL server
 private String url = "jdbc:postgresql://localhost:5432/office_supply";
 private String user = "postgres";
 private String password = "chris237";

 public List<User> getAllUsers() {
     List<User> users = new ArrayList<>();
     // Implement your data access logic here (e.g., from a database)
     // Replace this with actual database retrieval
     try (Connection connection = DriverManager.getConnection(url, user, password);
          Statement statement = connection.createStatement();
          ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

         while (resultSet.next()) {
             User user = new User();
             user.setUserId(resultSet.getInt("id"));
             user.setUsername(resultSet.getString("username"));
             user.setRole(resultSet.getString("role"));
             user.setAddress(resultSet.getString("address"));
             users.add(user);
         }

     } catch (SQLException e) {
         e.printStackTrace();
     }
     return users;
 }

 public List<User> getUsersByRole(String role) {
     List<User> users = new ArrayList<>();
     // Implement your data access logic here (e.g., from a database)
     try (Connection connection = DriverManager.getConnection(url, user, password);
          PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE role = ?")) {

         preparedStatement.setString(1, role);
         ResultSet resultSet = preparedStatement.executeQuery();

         while (resultSet.next()) {
             User user = new User();
             user.setUserId(resultSet.getInt("user_id"));
             user.setUsername(resultSet.getString("username"));
             user.setAddress(resultSet.getString("address"));
             user.setRole(resultSet.getString("role"));
             users.add(user);
         }

     } catch (SQLException e) {
         e.printStackTrace();
     }
     return users;
 }

 public void deleteUser(int userId) {
     // Implement your data access logic here (e.g., deleting from a database)
     try (Connection connection = DriverManager.getConnection(url, user, password);
          PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {

         preparedStatement.setInt(1, userId);
         preparedStatement.executeUpdate();

     } catch (SQLException e) {
         e.printStackTrace();
     }
 }

 // Add other CRUD operations as needed
}

