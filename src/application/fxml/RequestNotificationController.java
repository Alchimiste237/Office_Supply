package application.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class RequestNotificationController {

    @FXML
    private TableView<Request> requestTable;

    @FXML
    private TableColumn<Request, Integer> idColumn;

    @FXML
    private TableColumn<Request, String> productNameColumn;

    @FXML
    private TableColumn<Request, Integer> quantityColumn;

    @FXML
    private TableColumn<Request, String> reasonColumn;
    
    @FXML
    private TableColumn<Request, String> statusColumn;

    private ObservableList<Request> requestList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize Table Columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityRequested"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reasonOk"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load data from database
        loadRequests();

        // Set the data to the table
        requestTable.setItems(requestList);
    }

    private void loadRequests() {
        requestList.clear();

        try {
            Connection connection = DatabaseConnector.getConnection();
            String query = "SELECT id, supplies_name, quantity_requested, reason,status FROM requests";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String productName = resultSet.getString("supplies_name");
                int quantityRequested = resultSet.getInt("quantity_requested");
                String reasonOk = resultSet.getString("reason");
                String status = resultSet.getString("status");
                Request request = new Request(id, productName, quantityRequested, reasonOk,status);
                requestList.add(request);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load requests: " + e.getMessage());
        }
    }

    @FXML
    void handleAcceptRequest(ActionEvent event) {
        Request selectedRequest = requestTable.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a request to accept.");
            return;
        }

        updateRequestStatus(selectedRequest.getId(), "Approved");
    }

    @FXML
    void handleRejectRequest(ActionEvent event) {
        Request selectedRequest = requestTable.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a request to reject.");
            return;
        }

        updateRequestStatus(selectedRequest.getId(), "Rejected");
    }

    private void updateRequestStatus(int requestId, String newStatus) {
        try {
            Connection connection = DatabaseConnector.getConnection();
            String updateQuery = "UPDATE requests SET status = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, requestId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Request " + newStatus + " successfully.");
                loadRequests(); // Refresh the table
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update request status.");
            }

            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating request status: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public static class Request {
        private int id;
        private String productName;
        private int quantityRequested;
        private String reason;
        private String status;

        public Request(int id, String productName, int quantityRequested, String reason,String status) {
            this.id = id;
            this.productName = productName;
            this.quantityRequested = quantityRequested;
            this.reason = reason;
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public String getProductName() {
            return productName;
        }
        public String getReason() {
            return reason;
        }

        public int getQuantityRequested() {
            return quantityRequested;
        }

        public String getStatus() {
            return status;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
        public void setReason(String reason) {
            this.reason = reason;
        }


        public void setQuantityRequested(int quantityRequested) {
            this.quantityRequested = quantityRequested;
        }

        public void setStatus(String status) {
            this.status = status;
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
