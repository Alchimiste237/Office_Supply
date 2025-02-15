package application.fxml;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import application.model.User;
import application.service.DatabaseService;
import application.service.EncryptionService;
import application.service.ProductDAO;
import application.service.UserDAO;
import application.model.Supply;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminDashboardController{

    // User Table
    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userRoleColumn;

    // Product Table
    @FXML private TableView<Supply> productTableView;
    @FXML private TableColumn<Supply, Integer> productIdColumn;
    @FXML private TableColumn<Supply, String> productNameColumn;
    @FXML private TableColumn<Supply, Integer> productPriceColumn;
    @FXML private TableColumn<Supply, String> productSupplierColumn;

    @FXML private TextField productNameField;
    @FXML private TextField productPriceField;
    @FXML private ComboBox<String> supplierComboBox;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<Supply> productList = FXCollections.observableArrayList();
    private ObservableList<String> supplierNames = FXCollections.observableArrayList();
    private List<User> suppliers;

    // Data Access (Replace with your actual data access logic)
    private UserDAO userDAO = new UserDAO();
    private ProductDAO productDAO = new ProductDAO();
    
    private EncryptionService encryptionService = new EncryptionService();
    private DatabaseService databaseService = new DatabaseService();

    @FXML
    public void initialize() {
        // User Table Setup
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        loadUsers();

        // Product Table Setup
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productSupplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName")); // Display Supplier Name
        loadProducts();

        // Load Suppliers into ComboBox
        loadSuppliers();
    }

    // *** User Management ***
    private void loadUsers() {
        userList.clear();
        userList.addAll(databaseService.getAllUsers());
        userTableView.setItems(userList);
    }

    @FXML
    void handleAddUser(ActionEvent event) {
        // Implement your add user logic here
    	
    	
    	Dialog<User> dialog = createAddUserDialog();
        dialog.showAndWait().ifPresent(newUser -> {
            try {
                createUser(newUser);
            } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user: " + e.getMessage());
                e.printStackTrace();
            }
        });
    	
    	
        // Open a dialog, get user details, create a new user, and refresh the table
        System.out.println("Add User clicked");
        loadUsers();  // Refresh the table after adding
    }
    
    

    private void createUser(User user) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException{
        String role = user.getRole();
        String userName = user.getUsername();
        String Userpassword = user.getPassword();
        String UserAddress = user.getAddress();


        if(!role.equalsIgnoreCase("Supplier")) {
            if (userName == null || userName.isEmpty() || Userpassword == null || Userpassword.isEmpty() || UserAddress == null || UserAddress.isEmpty()) {
                System.out.println("you are a supplier");
            	showAlert(Alert.AlertType.ERROR, "Registration Failed", "Please enter username, password, and address.");
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
                    loadUsers(); // Refresh after creating the user
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
                    loadUsers(); // Refresh after creating the user
                } else {
                    showAlert(Alert.AlertType.ERROR, "Registration Failed", "Failed to create user. Please try again.");
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Database error: " + e.getMessage());
                e.printStackTrace(); // Log the error for debugging
            }
        }
    }

    @FXML
    void handleUpdateUser(ActionEvent event) {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Dialog<User> dialog = createUpdateUserDialog(selectedUser);
            dialog.showAndWait().ifPresent(updatedUser -> {
                try {
                	System.out.println(selectedUser +" "+ updatedUser);
                    updateUser(selectedUser,updatedUser);
                } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user ok: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "No User Selected", "Please select a user to update.");
        }
    }
    // Method to handle user update
    private void updateUser(User selected, User updatedUser) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Hash the password if it has been changed
        String newPassword = updatedUser.getPassword();
        String salt;
        String hashedPassword;

        //Only update the password if a new password was entered
        if (newPassword != null && !newPassword.isEmpty()) {
            // Generate a new salt and hash the password
            salt = encryptionService.generateSalt();
            hashedPassword = encryptionService.hashPassword(newPassword, salt);
        } else {
            // If the password field is empty, retrieve the existing salt and password
            Optional<DatabaseService.UserCredentials> userCredentials = databaseService.getUserCredentials(selected.getUsername());
            if (userCredentials.isPresent()) {
                hashedPassword = userCredentials.get().hashedPassword();
                salt = userCredentials.get().salt();
            } else {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not retrieve existing credentials.");
                return;
            }
        }


        // Create a new User object with the updated information
        User userToUpdate = new User();
        userToUpdate.setUserId(selected.getUserId());
        userToUpdate.setUsername(updatedUser.getUsername());
        userToUpdate.setPassword(hashedPassword);
        userToUpdate.setRole(updatedUser.getRole());
        userToUpdate.setSalt(salt);
        userToUpdate.setAddress(updatedUser.getAddress());
        System.out.println(userToUpdate);

        boolean success = databaseService.updateUser(userToUpdate);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Update Successful", "User updated successfully!");
            loadUsers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update user. Please try again.");
        }
    }
    @FXML
    void handleDeleteUser(ActionEvent event) {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                deleteUser(selectedUser);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No User Selected", "Please select a user to delete.");
        }
    }

    // Method to handle user deletion
    private void deleteUser(User user) throws SQLException {
        boolean success = databaseService.deleteUser(user.getUserId());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Deletion Successful", "User deleted successfully!");
            loadUsers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete user. Please try again.");
        }
    }

    // *** Product Management ***
    private void loadProducts() {
        productList.clear();
        productList.addAll(productDAO.getAllProducts());
        productTableView.setItems(productList);
        
        
        
        
    }

    private void loadSuppliers() {
        suppliers = userDAO.getUsersByRole("Supplier");
        supplierNames.clear();
        for (User supplier : suppliers) {
            supplierNames.add(supplier.getUsername());
        }
        supplierComboBox.setItems(supplierNames);
    }

    @FXML
    void handleAddProduct(ActionEvent event) {
        String name = productNameField.getText();
        String p = productPriceField.getText();
        int price = Integer.parseInt(p); // Handle potential NumberFormatException
        String supplierName = supplierComboBox.getValue();

        if (name != null && price > 0 && supplierName != null) {
            // Find the Supplier ID
            int supplierId = -1; // Or throw an exception
            for (User supplier : suppliers) {
                if (supplier.getUsername().equals(supplierName)) {
                    supplierId = supplier.getUserId();
                    break;
                }
            }

            if (supplierId != -1) {
                Supply newProduct = new Supply();
                newProduct.setName(name);
                newProduct.setPrice(price);
                newProduct.setSupplierId(supplierId);

                productDAO.addProduct(newProduct);
                loadProducts();
                clearProductFields();
            } else {
                showAlert(null, "Error", "Could not find supplier ID.");
            }
        } else {
            showAlert(null, "Missing Information", "Please fill in all product details.");
        }
    }

    @FXML
    void handleUpdateProduct(ActionEvent event) {
        Supply selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            //Get the new values
            String name = productNameField.getText();
            int price = Integer.parseInt(productPriceField.getText()); // Handle potential NumberFormatException
            String supplierName = supplierComboBox.getValue();

            //Find the supplierId from supplier name
            int supplierId = -1; // Or throw an exception
            for (User supplier : suppliers) {
                if (supplier.getUsername().equals(supplierName)) {
                    supplierId = supplier.getUserId();
                    break;
                }
            }

            //Set the value to be updated
            selectedProduct.setName(name);
            selectedProduct.setPrice(price);
            selectedProduct.setSupplierId(supplierId);

            productDAO.updateProduct(selectedProduct);
            loadProducts();
            clearProductFields();
        } else {
            showAlert(null, "No Product Selected", "Please select a product to update.");
        }
    }

    @FXML
    void handleDeleteProduct(ActionEvent event) {
        Supply selectedProduct = productTableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            productDAO.deleteProduct(selectedProduct.getId());
            loadProducts();
        } else {
            showAlert(null, "No Product Selected", "Please select a product to delete.");
        }
    }
    
    // Helper methods to create dialogs for add and update operations
    private Dialog<User> createAddUserDialog() {
        // Implement your dialog creation logic here using Dialog, DialogPane, etc.
        // Add TextFields for username, password, role, address etc
        // Ensure proper validation within the dialog
    	 Dialog<User> dialog = new Dialog<>();
         dialog.setTitle("Add New User");
         dialog.setHeaderText("Enter user details:");

         // Set the button types
         ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
         dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

         // Create the username, password, role, and address labels and fields
         GridPane grid = new GridPane();
         grid.setHgap(10);
         grid.setVgap(10);
         grid.setPadding(new Insets(20, 150, 10, 10));

         TextField username = new TextField();
         username.setPromptText("Username");
         PasswordField password = new PasswordField();
         password.setPromptText("Password");
         ComboBox<String> role = new ComboBox<>();
         role.getItems().addAll("Admin", "Employee", "Manager", "Supplier");
         role.setPromptText("Role"); //Added Prompt text for better UX
         TextArea address = new TextArea();
         address.setPromptText("Address");
         address.setWrapText(true);


         grid.add(new Label("Username:"), 0, 0);
         grid.add(username, 1, 0);
         grid.add(new Label("Password:"), 0, 1);
         grid.add(password, 1, 1);
         grid.add(new Label("Role:"), 0, 2);
         grid.add(role, 1, 2);
         grid.add(new Label("Address:"), 0, 3);
         grid.add(address, 1, 3);

         dialog.getDialogPane().setContent(grid);

         // Request focus on the username field by default.
         Platform.runLater(username::requestFocus);

         // Convert the result to a User object when the add button is clicked.
         dialog.setResultConverter(dialogButton -> {
             if (dialogButton == addButtonType) {
                 if(username.getText().isEmpty() || password.getText().isEmpty() || role.getValue() == null || address.getText().isEmpty()) {
                     Alert alert = new Alert(Alert.AlertType.ERROR);
                     alert.setTitle("Input Error");
                     alert.setHeaderText(null);
                     alert.setContentText("Please fill in all fields.");
                     alert.showAndWait();
                     return null; // Return null to prevent closing the dialog and prompting to re-enter data.
                 }
                 return new User(username.getText(), address.getText(),password.getText(), role.getValue());
             }
             return null;
         });

         return dialog;
    }

    private Dialog<User> createUpdateUserDialog(User user) {
        // Implement your dialog creation logic here for updating user details
        // Set initial values from the selected user
    	Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Update User");
        dialog.setHeaderText("Update user details:");

        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the username, password, role, and address labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        username.setText(user.getUsername()); //Pre-populate with existing user data
        PasswordField password = new PasswordField();
        password.setPromptText("Password");  //No default password for security
        ComboBox<String> role = new ComboBox<>();
        role.getItems().addAll("Admin", "Employee", "Manager", "Supplier");
        role.setPromptText("Role");
        role.setValue(user.getRole());  //Pre-populate with existing user data
        TextArea address = new TextArea();
        address.setPromptText("Address");
        address.setText(user.getAddress()); //Pre-populate with existing user data
        address.setWrapText(true);

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(role, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(address, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        // Convert the result to a User object when the add button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
               if(username.getText().isEmpty() || role.getValue() == null || address.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Input Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Please fill in all fields (except password).");
                    alert.showAndWait();
                    return null;
               }

               // If the password field is empty, we preserve the original password.
               // Otherwise, we will update it with the new value.
               String updatedPassword = password.getText().isEmpty() ? user.getPassword() : password.getText();

               return new User(username.getText(), address.getText(), updatedPassword, role.getValue());
            }
            return null;
        });

        return dialog;
    }



    private void clearProductFields() {
        productNameField.clear();
        productPriceField.clear();
        supplierComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

