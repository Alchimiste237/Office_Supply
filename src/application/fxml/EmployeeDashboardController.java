package application.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

import java.sql.*; // Important for Database Connectivity
import java.util.Optional;

public class EmployeeDashboardController {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TextField productNameField;

    @FXML
    private TextField quantityNeededField;
    
    @FXML
    private TextArea reasonField;

    private ObservableList<Product> productList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        // Initialize Table Columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Load data from database
        loadProducts();

        // Set the data to the table
        productTable.setItems(productList);

    }



    private void loadProducts() {
        productList.clear(); // Clear existing data

        try {
            Connection connection = DatabaseConnector.getConnection(); // Get connection from helper class (see below)
            String query = "SELECT supply_id, name, quantity FROM supplies"; // Replace 'products' with your table name
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("supply_id");
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                Product product = new Product(id, name, quantity);
                productList.add(product);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to load products: " + e.getMessage());
        }
    }


    @FXML
    void handleRequestProduct(ActionEvent event) {
        String productName = productNameField.getText();
        String quantityNeededText = quantityNeededField.getText();
        String reason = reasonField.getText();

        if (productName == null || productName.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Input Error", "Please enter a product name.");
            return;
        }

        if (quantityNeededText == null || quantityNeededText.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Input Error", "Please enter the quantity needed.");
            return;
        }

        int quantityNeeded;
        try {
            quantityNeeded = Integer.parseInt(quantityNeededText);
            if (quantityNeeded <= 0) {
                showAlert(AlertType.WARNING, "Input Error", "Quantity needed must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Input Error", "Invalid quantity format.  Please enter a number.");
            return;
        }

        //Confirm Request
        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Product Request");
        confirmationAlert.setHeaderText("Request product: " + productName);
        confirmationAlert.setContentText("Are you sure you want to request " + quantityNeeded + " of " + productName + "?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            //Process the request
            submitProductRequest(productName, quantityNeeded, reason);
        }


    }


    private void submitProductRequest(String productName, int quantityNeeded, String reason) {
        try {
            Connection connection = DatabaseConnector.getConnection();

            //Insert into request table (assuming you have a 'requests' table)
            String insertQuery = "INSERT INTO requests (supplies_name, quantity_requested, reason ,status) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, productName);
            preparedStatement.setInt(2, quantityNeeded);
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, "Pending");  // Set initial status (e.g., Pending)

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(AlertType.INFORMATION, "Request Successful", "Product request submitted successfully.");
                // Optionally, clear the input fields after a successful request
                productNameField.clear();
                quantityNeededField.clear();

            } else {
                showAlert(AlertType.ERROR, "Request Failed", "Failed to submit product request.");
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Error submitting product request: " + e.getMessage());
        }
    }



    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



    //Inner Class to represent Product
    public static class Product {
        private int id;
        private String name;
        private int quantity;

        public Product(int id, String name, int quantity) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }

        public void setId(int id){ this.id = id;}
        public void setName(String name){this.name = name;}
        public void setQuantity(int quantity){this.quantity = quantity;}

    }


    //Simple Database Connector class.  Replace with your preferred method.
    public static class DatabaseConnector {

        private static final String DB_URL = "jdbc:postgresql://localhost:5432/office_supply"; // Replace with your database URL
        private static final String DB_USER = "postgres"; // Replace with your database username
        private static final String DB_PASSWORD = "chris237"; // Replace with your database password

        public static Connection getConnection() throws SQLException {
            try {
                Class.forName("org.postgresql.Driver"); // Replace with your database driver class
            } catch (ClassNotFoundException e) {
                System.err.println("Database driver not found: " + e.getMessage());
                throw new SQLException("Database driver not found", e);
            }
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }


}
