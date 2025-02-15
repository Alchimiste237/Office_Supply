package application.service;


import application.model.Supply;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

 // JDBC URL, username and password of MySQL server
 private String url = "jdbc:postgresql://localhost:5432/office_supply";
 private String user = "postgres";
 private String password = "chris237";

 public List<Supply> getAllProducts() {
     List<Supply> products = new ArrayList<>();
     // Implement your data access logic here (e.g., from a database)
     try (Connection connection = DriverManager.getConnection(url, user, password);
          Statement statement = connection.createStatement();
          ResultSet resultSet = statement.executeQuery("SELECT p.supply_id, p.name, p.quantity, p.supplier_id, u.username AS supplier_name " +
                         "FROM supplies p " +
        		  "JOIN users u ON p.supplier_id = u.user_id")) {

         while (resultSet.next()) {
         Supply product = new Supply();
             product.setId(resultSet.getInt("supply_id"));
             product.setName(resultSet.getString("name"));
             product.setPrice(resultSet.getInt("quantity"));
             product.setSupplierId(resultSet.getInt("supplier_id"));
             product.setSupplierName(resultSet.getString("supplier_name"));
             
             products.add(product);
               	
//               	int id = resultSet.getInt("supply_id");
//                String name = resultSet.getString("name");
//                int quantity = resultSet.getInt("quantity");
//                int supplierId = resultSet.getInt("supplier_id");
//                String supplierName = resultSet.getString("supplier_name");  // Get Supplier Name
//                Supply product = new Supply(id, name, quantity, supplierId, supplierName); //Product constructor has been updated
//                
               	
              
//             products.add(product);
             
             
         }

     } catch (SQLException e) {
         e.printStackTrace();
     }
     return products;
 }

 public void addProduct(Supply product) {
     // Implement your data access logic here (e.g., inserting into a database)
     try (Connection connection = DriverManager.getConnection(url, user, password);
          PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO supplies (name, quantity, supplier_id) VALUES (?, ?, ?)")) {

         preparedStatement.setString(1, product.getName());
         preparedStatement.setDouble(2, product.getPrice());
         preparedStatement.setInt(3, product.getSupplierId());

         preparedStatement.executeUpdate();

     } catch (SQLException e) {
         e.printStackTrace();
     }
 }

 public void updateProduct(Supply product) {
     // Implement your data access logic here (e.g., updating a database)
     try (Connection connection = DriverManager.getConnection(url, user, password);
          PreparedStatement preparedStatement = connection.prepareStatement("UPDATE supplies SET name = ?, quantity = ?, supplier_id = ? WHERE supply_id = ?")) {

         preparedStatement.setString(1, product.getName());
         preparedStatement.setDouble(2, product.getPrice());
         preparedStatement.setInt(3, product.getSupplierId());
         preparedStatement.setInt(4, product.getId());

         preparedStatement.executeUpdate();

     } catch (SQLException e) {
         e.printStackTrace();
     }
 }

 public void deleteProduct(int productId) {
     // Implement your data access logic here (e.g., deleting from a database)
     try (Connection connection = DriverManager.getConnection(url, user, password);
          PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM supplies WHERE supply_id = ?")) {

         preparedStatement.setInt(1, productId);

         preparedStatement.executeUpdate();

     } catch (SQLException e) {
         e.printStackTrace();
     }
 }

 // Add other CRUD operations as needed
}

