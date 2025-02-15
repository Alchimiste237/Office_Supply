package application.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ManagerDashboardController {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TableColumn<Product, String> supplierColumn; // New Column

    @FXML
    private TextField productNameField;

    @FXML
    private TextField productQuantityField;

    @FXML
    private ComboBox<Supplier> supplierComboBox; // ComboBox for Supplier Selection

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();  // List of Suppliers

    @FXML
    public void initialize() {
        // Initialize Table Columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName")); // Map to the Supplier Name

        // Load data from database
        loadProducts();
        loadSuppliers(); // Load Suppliers into the ComboBox

        // Set the data to the table
        productTable.setItems(productList);

        // Set the items for the supplier ComboBox
        supplierComboBox.setItems(supplierList);

        //Selection listener to populate the input fields when a row is selected
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productNameField.setText(newSelection.getName());
                productQuantityField.setText(String.valueOf(newSelection.getQuantity()));

                // Find the matching supplier in the list and select it
                Supplier selectedSupplier = supplierList.stream()
                        .filter(supplier -> supplier.getId() == newSelection.getSupplierId())
                        .findFirst()
                        .orElse(null);

                supplierComboBox.setValue(selectedSupplier); // Select the supplier in the ComboBox
            }
        });
    }

    private void loadProducts() {
        productList.clear();

        try {
            Connection connection = DatabaseConnector.getConnection();
            String query = "SELECT p.supply_id, p.name, p.quantity, p.supplier_id, u.username AS supplier_name " +
                           "FROM supplies p " +
                           "JOIN users u ON p.supplier_id = u.user_id"; // JOIN to get supplier name
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("supply_id");
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                int supplierId = resultSet.getInt("supplier_id");
                String supplierName = resultSet.getString("supplier_name");  // Get Supplier Name
                Product product = new Product(id, name, quantity, supplierId, supplierName); //Product constructor has been updated
                productList.add(product);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load products: " + e.getMessage());
        }
    }

    private void loadSuppliers() {
        supplierList.clear();
        try {
            Connection connection = DatabaseConnector.getConnection();
            String query = "SELECT user_id, username FROM users WHERE role = 'Supplier'"; // Fetch suppliers
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                Supplier supplier = new Supplier(id, username);
                supplierList.add(supplier);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load suppliers: " + e.getMessage());
        }
    }

    @FXML
    void handleAddProduct(ActionEvent event) {
        String name = productNameField.getText();
        String quantityText = productQuantityField.getText();
        Supplier selectedSupplier = supplierComboBox.getValue(); // Get selected supplier

        if (name == null || name.trim().isEmpty() || quantityText == null || quantityText.trim().isEmpty() || selectedSupplier == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter name, quantity, and select a supplier.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Invalid quantity format. Please enter a number.");
            return;
        }

        try {
            Connection connection = DatabaseConnector.getConnection();
            String insertQuery = "INSERT INTO supplies (name, quantity, supplier_id) VALUES (?, ?, ?)"; // Include supplier_id
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setInt(3, selectedSupplier.getId()); // Set the supplier ID

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully.");
                loadProducts(); // Refresh the table
                clearInputFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add product.");
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error adding product: " + e.getMessage());
        }
    }

    @FXML
    void handleUpdateProduct(ActionEvent event) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a product to update.");
            return;
        }

        String name = productNameField.getText();
        String quantityText = productQuantityField.getText();
        Supplier selectedSupplier = supplierComboBox.getValue(); // Get the selected supplier

       if (name == null || name.trim().isEmpty() || quantityText == null || quantityText.trim().isEmpty() || selectedSupplier == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter name, quantity, and select a supplier.");
            return;
        }


        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Invalid quantity format. Please enter a number.");
            return;
        }

        try {
            Connection connection = DatabaseConnector.getConnection();
            String updateQuery = "UPDATE supplies SET name = ?, quantity = ?, supplier_id = ? WHERE supply_id = ?"; // Update supplier_id
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setInt(3, selectedSupplier.getId());  // Set the Supplier ID
            preparedStatement.setInt(4, selectedProduct.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully.");
                loadProducts(); // Refresh the table
                clearInputFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update product.");
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating product: " + e.getMessage());
        }
    }

    @FXML
    void handleDeleteProduct(ActionEvent event) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a product to delete.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Delete Product");
        confirmationAlert.setContentText("Are you sure you want to delete product: " + selectedProduct.getName() + "?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Connection connection = DatabaseConnector.getConnection();
                String deleteQuery = "DELETE FROM supplies WHERE supply_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, selectedProduct.getId());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Product deleted successfully.");
                    loadProducts(); // Refresh the table
                    clearInputFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete product.");
                }

                preparedStatement.close();
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error deleting product: " + e.getMessage());
            }
        }
    }

    private void clearInputFields() {
        productNameField.clear();
        productQuantityField.clear();
        supplierComboBox.setValue(null);  // Clear the selection in the ComboBox
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method to switch to the notifications view
    @FXML
    void handleSwitchToNotifications(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RequestNotification.fxml"));
            VBox notificationsView = loader.load();

            // Get the controller for the notification view
            RequestNotificationController notificationController = loader.getController();

            // Create a new scene and stage
            Scene scene = new Scene(notificationsView);
            Stage stage = new Stage();
            stage.setTitle("Product Requests");
            stage.setScene(scene);
            stage.show();

             // Optionally, close the current manager dashboard window
             ((Node)(event.getSource())).getScene().getWindow().hide();
             
             
             /* 
              
                private void loadEmployeeView(ActionEvent event) throws IOException {
        Parent inventoryRoot = FXMLLoader.load(getClass().getResource("employee.fxml"));
        Scene inventoryScene = new Scene(inventoryRoot);

        // Get the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(inventoryScene);
        window.show();
    }
               
              */
             

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load notifications view: " + e.getMessage());
        }
    }


    // Inner Class to represent Product
    public static class Product {
        private int id;
        private String name;
        private int quantity;
        private int supplierId; // Added
        private String supplierName; // Added

        public Product(int id, String name, int quantity, int supplierId, String supplierName) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.supplierId = supplierId;
            this.supplierName = supplierName;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public int getSupplierId() { return supplierId; }
        public String getSupplierName() { return supplierName; }

        public void setId(int id){ this.id = id;}
        public void setName(String name){this.name = name;}
        public void setQuantity(int quantity){this.quantity = quantity;}
        public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
        public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    }

    //Inner class to represent a Supplier
    public static class Supplier {
        private int id;
        private String username;

        public Supplier(int id, String username) {
            this.id = id;
            this.username = username;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }

        @Override
        public String toString() {
            return username; // Display the username in the ComboBox
        }
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

