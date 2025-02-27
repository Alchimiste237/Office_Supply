package application.fxml;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Optional;

import application.service.DatabaseService;
import application.service.EncryptionService;
import application.service.getData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    private DatabaseService databaseService = new DatabaseService();
    private EncryptionService encryptionService = new EncryptionService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLoginButton() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            showAlert("Error", "Please enter a username and password.");
            return;
        }

        // Retrieve user credentials from the database
        Optional<DatabaseService.UserCredentials> userCredentials = databaseService.getUserCredentials(username);

        if (userCredentials.isPresent()) {
            String hashedPassword = userCredentials.get().hashedPassword();
            String salt = userCredentials.get().salt();

            // Hash the entered password with the retrieved salt
            String loginHashedPassword = encryptionService.hashPassword(password, salt);

            if (hashedPassword.equals(loginHashedPassword)) {
                // Retrieve user role
                String role = databaseService.getUserRole(username); // Implement this method to get the user's role
                getData.username = username;
                switch (role.toLowerCase()) {
                    case "admin":
                        loadPage("AdminDashboard.fxml");
                        break;
                    case "employee":
                        loadPage("employee.fxml");
                        break;
                    case "manager":
                        loadPage("ManagerDashboard.fxml");
                        break;
                    case "supplier":
                        loadPage("SupplierDashboard.fxml");
                        break;
                    default:
                        showAlert("Error", "Unknown role.");
                        break;
                }
            } else {
                showAlert("Error", "Invalid username or password.");
            }
        } else {
            showAlert("Error", "User not found.");
        }
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load page: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}