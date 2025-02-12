package application.controller;

import application.service.DatabaseService;
import application.service.EncryptionService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private DatabaseService databaseService = new DatabaseService();
    private EncryptionService encryptionService = new EncryptionService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLoginButton() {
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
                showAlert("Success", "Login successful!");
                // Proceed to the next step (e.g., open the main application window)

            } else {
                showAlert("Error", "Invalid username or password.");
            }
        } else {
            showAlert("Error", "User not found.");
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