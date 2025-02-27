package application.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.model.User;

public class SupplierDAO {

	private String url = "jdbc:postgresql://localhost:5432/office_supply";
	 private String user = "postgres";
	 private String password = "chris237";

	 public List<User> getAllSupplier() {
	     List<User> users = new ArrayList<>();
	     String role = "Supplier";
	     // Implement your data access logic here (e.g., from a database)
	     // Replace this with actual database retrieval
	     try (Connection connection = DriverManager.getConnection(url, user, password);
//	          Statement statement = connection.createStatement();
//	          ResultSet resultSet = statement.executeQuery("SELECT * FROM users where role = ?")) {

	    		 PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users where role = ?")) {

	         preparedStatement.setString(1, role);
	         ResultSet resultSet = preparedStatement.executeQuery();
	    		 
	         while (resultSet.next()) {
	             User user = new User();
	             user.setUserId(resultSet.getInt("user_id"));
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
	
	
}
