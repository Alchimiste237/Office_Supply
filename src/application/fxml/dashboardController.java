package application.fxml;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import application.fxml.ManagerDashboardController.DatabaseConnector;
import application.fxml.ManagerDashboardController.Supplier;
import application.model.User;
import application.service.Conn;
import application.service.DatabaseService;
import application.service.EncryptionService;
import application.service.getData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class dashboardController{

	
	
	
	public void logout(ActionEvent event) {
		System.out.println("CLICK LOGOUT");
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Messsage");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");
        Optional<ButtonType> option =   alert.showAndWait();
        
        if(option.get().equals(ButtonType.OK)) {
        	logout.getScene().getWindow().hide();
        	Parent root = null;
			try {
				root = FXMLLoader.load(getClass().getResource("login.fxml"));
				Scene inventoryScene = new Scene(root);

		        // Get the Stage information
		        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		        window.setScene(inventoryScene);
		        window.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
		
	}
	
	
	
    @FXML
    private Button AddBtn;

    @FXML
    private Button DeleteBtn;

    @FXML
    private Button ManageUserBtn;

    @FXML
    private TextField name;

    @FXML
    private Button UpdateBtn;

    @FXML
    private TextField address;

    @FXML
    private Button logout;

    @FXML
    private Button manageProductBtn;

    @FXML
    private AnchorPane manageProduct;

    @FXML
    private AnchorPane manageUser;

    @FXML
    private TextField password;

    @FXML
    private ComboBox<String> productCategories;
    
    @FXML
    private ComboBox<String> productSupplier;

    @FXML
    private TextField productDescription;
    
    @FXML
    private TableView<User> userTable;
    
    @FXML
    private TableColumn<User, Integer> userId;

    @FXML
    private TableColumn<User, String> ColName;

    @FXML
    private TableColumn<?, ?> productDisId;

    @FXML
    private TableColumn<User, String> colRole;

    @FXML
    private TableColumn<?, ?> productDisName;

    @FXML
    private TableColumn<User, String> ColAddress;

    @FXML
    private TableColumn<?, ?> productDisPrice;

    @FXML
    private TableColumn<User, String> colCreateDate;

    @FXML
    private TableColumn<?, ?> productDisSupplier;

    @FXML
    private TableColumn<?, ?> productDisSupplier1;

    @FXML
    private TableColumn<?, ?> productDisSupplier2;

    @FXML
    private ImageView productImage;

    @FXML
    private Button productImportBtn1;

    @FXML
    private TextField productName;

    @FXML
    private TextField productPrice;

    @FXML
    private TextField productSearchBtn1;

    
    @FXML
    private ComboBox<String> roleBox;

    @FXML
    private TextField search;
    
    @FXML
    private Label username;
    
    static int Control = 15;

    @FXML
    void switchToProduct(ActionEvent event) {
    	Control = 5;
    	manageUser.setVisible(false);
    	manageProduct.setVisible(true);
    	System.out.print(Control);
    }
    
    @FXML
    void switchToUser(ActionEvent event) {
    	Control = 15;
    	manageUser.setVisible(true);
    	manageProduct.setVisible(false);
    	System.out.print(Control);
    }
    
    
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    private Conn databaseService = new Conn();
    
    @FXML
    public void initialize() {
        roleBox.getItems().addAll("Employee", "Admin", "Manager", "Supplier");
        roleBox.setValue("Employee");
        
        productCategories.getItems().addAll("Office Furniture", "Eletronics", "paper Goods", "Accessories", "Writing Instrument");
        productCategories.setValue("Office Furniture");
        
        loadSuppliers();
        
        userId.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        ColName.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        ColAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCreateDate.setCellValueFactory(new PropertyValueFactory<>("CreatedAt"));

        try {
            loadInventoryData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userTable.setItems(userList);     
        
  }
    
    private void loadSuppliers() {
        supplierList.clear();
        try {
            Connection connection = DatabaseConnector.getConnection();
            String query = "SELECT id, username FROM users WHERE role = 'supplier'"; // Fetch suppliers
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
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
    
    private void loadInventoryData() throws SQLException {
        String sql = "SELECT user_id, username, role, address, CreatedAt FROM users";
        try (Connection conn = databaseService.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User supply = new User();
                supply.setUserId(rs.getInt("user_id"));
                supply.setUsername(rs.getString("username"));
                supply.setDate(rs.getString("CreatedAt"));
                supply.setRole(rs.getString("role"));
                supply.setAddress(rs.getString("address"));
                userList.add(supply);
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            throw e; // Re-throw to be handled in the initialize method
        }
    }
    
    
	
public void displayUsername() {
		
		String user = getData.username;
		username.setText(user.substring(0, 1).toUpperCase() + user.substring(1));
	}
@FXML
public void handleRegisterUserAction() throws NoSuchAlgorithmException, InvalidKeySpecException {
	final DatabaseService databaseService = new DatabaseService();
    final EncryptionService encryptionService = new EncryptionService();

    String userName = name.getText();
    String Userpassword = password.getText();
    String UserAddress = address.getText();
    String role = roleBox.getValue();

   if(Control == 15) {
	   if(!role.equalsIgnoreCase("Supplier")) {
	    	if (username == null || userName.isEmpty() || password == null || Userpassword.isEmpty() || UserAddress == null || UserAddress.isEmpty()) {
	            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Please enter username and password.");
	            return;
	        }
	    	 try {
	    	        if (databaseService.usernameExists(userName)) {
	    	            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
	    	            return;
	    	        }
	    	        

	    	        String salt = encryptionService.generateSalt();
	    	        String hashedPassword = encryptionService.hashPassword(Userpassword, salt);

	    	        boolean success = databaseService.createUser(userName, hashedPassword,role , salt, UserAddress);

	    	        if (success) {
	    	            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "   "+ role +" registered successfully!");
	    	            // Optionally, clear the fields or redirect to a login page
	    	            clearFields();
	    	        } else {
	    	            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Failed to create user. Please try again.");
	    	        }

	    	    } catch (SQLException e) {
	    	        showAlert(Alert.AlertType.ERROR, "Registration Failed", "Database error: " + e.getMessage());
	    	        e.printStackTrace(); // Log the error for debugging
	    	    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
	    	        showAlert(Alert.AlertType.ERROR, "Registration Failed", "Encryption error: " + e.getMessage());
	    	        e.printStackTrace(); // Log the error for debugging
	    	    }
	    }else {
	    	 try {
	    	        if (databaseService.usernameExists(userName)) {
	    	            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
	    	            return;
	    	        }
	    	        

	    	        String salt = null;
	    	        String hashedPassword = null;

	    	        boolean success = databaseService.createUser(userName, hashedPassword,role , salt, UserAddress);

	    	        if (success) {
	    	            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Supplier registered successfully!");
	    	            // Optionally, clear the fields or redirect to a login page
	    	            clearFields();
	    	        } else {
	    	            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Failed to create user. Please try again.");
	    	        }

	    	    } catch (SQLException e) {
	    	        showAlert(Alert.AlertType.ERROR, "Registration Failed", "Database error: " + e.getMessage());
	    	        e.printStackTrace(); // Log the error for debugging
	    	    }
	    }
   }else {
	   
//	   	String pName = productName.getText();
//	    String Dist = productDescription.getText();
//	    String price = productPrice.getText();
//	    String categorie = productCategories.getValue();
//	    String sup = productSupplier.getValue();
	   
   }
   
    }
    

    
private void showAlert(Alert.AlertType alertType, String title, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}

private void clearFields() {
	name.clear();
    password.clear();
    roleBox.setValue("Employee");
    address.clear();
}




	

}
