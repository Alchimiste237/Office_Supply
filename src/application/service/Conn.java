package application.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conn {
	
	 private final String dbUrl = "jdbc:postgresql://localhost:5432/office_supply";  // Replace with your database URL
	    private final String dbUser = "postgres";   // Replace with your database username
	    private final String dbPassword = "chris237"; // Replace with your database password
	
	public Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser,  dbPassword);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return connection;
    }
}

