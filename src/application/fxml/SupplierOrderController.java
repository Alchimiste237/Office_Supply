package application.fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

import application.model.Order;
import application.service.OrderDAO;

public class SupplierOrderController {

    private final OrderDAO orderDAO = new OrderDAO();
    private int supplierId;  // Set this from the login process!

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, Integer> orderIdCol;

    @FXML
    private TableColumn<Order, LocalDate> orderDateCol;

    @FXML
    private TableColumn<Order, Double> totalAmountCol;

    @FXML
    private TableColumn<Order, String> orderStatusCol;

    @FXML
    private TableColumn<Order, String> buyerNameCol;

    @FXML
    private TableColumn<Order, String> deliveryAddressCol;

    @FXML
    private TextField orderIdField;

    @FXML
    private ComboBox<String> statusComboBox;

    private final ObservableList<Order> orders = FXCollections.observableArrayList();

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
        loadOrders(); //Load orders when supplier id is set.
    }

    @FXML
    public void initialize() {
        // Initialize columns (this happens after FXML is loaded)
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        orderStatusCol.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        buyerNameCol.setCellValueFactory(new PropertyValueFactory<>("buyerName"));
       

        orderTable.setItems(orders);

        // Initialize status ComboBox
        statusComboBox.getItems().addAll("Pending", "Processing", "Shipped", "Delivered", "Cancelled");
        statusComboBox.setValue("Pending");
    }

    private void loadOrders() {
        orders.clear();
        List<Order> orderList = orderDAO.getOrdersBySupplierId(supplierId);
        orders.addAll(orderList);
    }

    @FXML
    void handleUpdateStatus(ActionEvent event) {
        try {
            int orderId = Integer.parseInt(orderIdField.getText());
            String newStatus = statusComboBox.getValue();

            boolean updated = OrderDAO.updateOrderStatus(orderId, newStatus);
            if (updated) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Order status updated successfully!");
                alert.showAndWait();
                loadOrders(); // Refresh the table after update
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update order status.");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Order ID. Please enter a number.");
            alert.showAndWait();
        }
    }
}
