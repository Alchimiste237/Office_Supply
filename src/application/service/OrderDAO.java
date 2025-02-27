package application.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import application.model.Order;
import application.model.OrderItem;
import javafx.collections.ObservableList;

public class OrderDAO {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/office_supply";
    private static final String DB_USER = "postgres";    
    private static final String DB_PASSWORD = "chris237";
    public List<Order> getOrdersBySupplierId(int supplierId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE supplier_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setSupplierId(rs.getInt("supplier_id"));
                order.setOrderDate(rs.getDate("order_date").toLocalDate());
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setOrderStatus(rs.getString("status"));
                order.setUserId(rs.getInt("user_id")); 
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions properly in a real application
        }
        return orders;
    }

    public static boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Returns true if the update was successful

        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions properly
            return false;
        }
    }


    // Other methods for creating, deleting, and retrieving orders

    public static void main(String[] args) {  //Simple Test
        OrderDAO orderDAO = new OrderDAO();
        List<Order> orders = orderDAO.getOrdersBySupplierId(1); // Replace 1 with your supplier id
        orders.forEach(System.out::println);

        boolean updated = OrderDAO.updateOrderStatus(1, "Shipped"); //Example
        System.out.println("Update successful: " + updated);
    }

	public int createOrder(Order order, ObservableList<OrderItem> orderItems) {
		String orderSql = "INSERT INTO orders (supplier_id, buyer_id, order_date, total_amount, order_status, delivery_address) VALUES (?, ?, ?, ?, ?, ?) RETURNING order_id";
	    String orderItemSql = "INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
	    int orderId = -1;

	    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	         PreparedStatement orderPstmt = conn.prepareStatement(orderSql);
	         PreparedStatement orderItemPstmt = conn.prepareStatement(orderItemSql)) {

	        

	        orderPstmt.setInt(1, order.getSupplierId());
	        orderPstmt.setInt(2, order.getUserId());
	        orderPstmt.setDate(3, java.sql.Date.valueOf(order.getOrderDate()));
	        orderPstmt.setDouble(4, order.getTotalAmount());
	        orderPstmt.setString(5, order.getOrderStatus());
	        

	        ResultSet rs = orderPstmt.executeQuery();

	        if (rs.next()) {
	            orderId = rs.getInt(1);
	        } else {
	            conn.rollback();
	            return -1; // Order creation failed
	        }

	        // Insert Order Items
	        for (OrderItem item : orderItems) {
	            orderItemPstmt.setInt(1, orderId);
	            orderItemPstmt.setInt(2, item.getSupplyId());
	            orderItemPstmt.setString(3, item.getSupplyName());
	            orderItemPstmt.setInt(4, item.getQuantity());
	            orderItemPstmt.setDouble(5, item.getUnitPrice());
	            orderItemPstmt.addBatch();  // Use batching for efficiency
	        }

	        int[] itemResults = orderItemPstmt.executeBatch();
	        for (int result : itemResults) {
	            if (result == Statement.EXECUTE_FAILED) {
	                conn.rollback();
	                return -1; // Order item creation failed
	            }
	        }

	        
	    } catch (SQLException e) {
	        e.printStackTrace(); // Log the exception properly!
	       
	        return -1; //An error occurred
	    }

	    return orderId;
	}
}
