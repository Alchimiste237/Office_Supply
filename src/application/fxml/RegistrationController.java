package application.fxml;

import application.service.DatabaseService;
import application.service.EncryptionService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class RegistrationController {

    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField addressField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
  private ComboBox<String> roleComboBox;
  
  @FXML
  public void initialize() {
      roleComboBox.getItems().addAll("Employee", "Admin", "Manager");
      roleComboBox.setValue("Employee");
}

    private final DatabaseService databaseService = new DatabaseService();
    private final EncryptionService encryptionService = new EncryptionService();

    @FXML
    public void handleRegisterButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String address = addressField.getText();
;        String role = roleComboBox.getValue();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Please enter username and password.");
            return;
        }

        try {
            if (databaseService.usernameExists(username)) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
                return;
            }

            String salt = encryptionService.generateSalt();
            String hashedPassword = encryptionService.hashPassword(password, salt);

            boolean success = databaseService.createUser(username, hashedPassword,role , salt, address);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User registered successfully!");
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
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue("Employee");
    }
}
