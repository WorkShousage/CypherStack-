package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Parent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SignUpFirst {
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField contactField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button continueButton;
    
    @FXML
    private Hyperlink logInLink;
    
    @FXML
    private TextField usernameField;

    // Data storage for temporary storage before database entry
    private static Map<String, String> signUpData = new HashMap<>();

    @FXML
    public void initialize() {
        // Populate roleComboBox with example roles
        roleComboBox.getItems().addAll( "Manager","Employee", "Intern");
        setComboBoxCellFactory(roleComboBox);
        passwordField.setOnMouseClicked(this::showPasswordInfoPopup);

    
        
     // Attach the event handler to the fields (email, username, password)
        emailField.setOnKeyReleased(event -> handleFieldKeyRelease(event, emailField, usernameField, passwordField));
        usernameField.setOnKeyReleased(event -> handleFieldKeyRelease(event, emailField, usernameField, passwordField));
        passwordField.setOnKeyReleased(event -> handleFieldKeyRelease(event, emailField, usernameField, passwordField));
    }
    private void handleFieldKeyRelease(KeyEvent event, TextField emailField, TextField usernameField, PasswordField passwordField) {
        // Extract the values of the fields
        String email = emailField.getText();
        String username = usernameField.getText();

        // Check if any of the fields have an existing value in the database
        boolean exists = checkIfExists(email, username);

        // If the fields have matching values, reset the style of the clicked field
        if (exists) {
            // Get the field that triggered the event
            TextInputControl field = (TextInputControl) event.getSource();
            resetFieldStyles(field);  // Reset style for the field that triggered the event
        }
        
        // Validate the password field
        
    }


        // You could add additional logic here to handle other types of validation if needed
    
    public boolean validatePassword(TextInputControl passwordField) {
        String password = passwordField.getText();

        // Reset prompt text and style initially
        passwordField.setPromptText("Enter a strong password.");
        passwordField.setStyle("");  // Reset styles

        boolean valid = true;
        
        // 1. Check for length (between 12 and 16 characters)
        if (password.length() < 12 || password.length() > 16) {
            passwordField.setPromptText("Password must be between 12 and 16 characters.");
            passwordField.setStyle("-fx-border-color: red; -fx-background-color: #2e2e2e; -fx-background-radius:5; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
            valid = false;
        }
        // 2. Check for complexity: uppercase, lowercase, digits, and special characters
        else if (!password.matches(".*[A-Z].*")) {
            passwordField.setPromptText("Password must contain at least one uppercase letter.");
            passwordField.setStyle("-fx-border-color: red; -fx-background-color: #2e2e2e; -fx-background-radius:5; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
            valid = false;
        }
        else if (!password.matches(".*[a-z].*")) {
            passwordField.setPromptText("Password must contain at least one lowercase letter.");
            passwordField.setStyle("-fx-border-color: red; -fx-background-color: #2e2e2e; -fx-background-radius:5; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
            valid = false;
        }
        else if (!password.matches(".*[0-9].*")) {
            passwordField.setPromptText("Password must contain at least one digit.");
            passwordField.setStyle("-fx-border-color: red; -fx-background-color: #2e2e2e; -fx-background-radius:5; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
            valid = false;
        }
        else if (!password.matches(".*[!@#$%^&*].*")) {
            passwordField.setPromptText("Password must contain at least one special character (e.g., !, @, #, $, %, ^, &).");
            passwordField.setStyle("-fx-border-color: red; -fx-background-color: #2e2e2e; -fx-background-radius:5; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
            valid = false;
        }

        // 3. Check for common words (e.g., "password", "123456", "qwerty", "admin")
        String[] commonWords = {"password", "123456", "qwerty", "admin"};
        for (String word : commonWords) {
            if (password.toLowerCase().contains(word)) {
                passwordField.setPromptText("Password contains a common phrase. Avoid using easily guessable phrases.");
                passwordField.setStyle("-fx-border-color: red; -fx-background-color: #2e2e2e; -fx-background-radius:5; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
                valid = false;
                break;
            }
        }

        // 4. Check for personal information (e.g., "birthdate", "name")
        if (password.toLowerCase().contains("birthdate") || password.toLowerCase().contains("name")) {
            passwordField.setPromptText("Password contains personal information, which should be avoided.");
            passwordField.setStyle("-fx-border-color: red; -fx-background-color: #2e2e2e; -fx-background-radius:5; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
            valid = false;
        }

        // 5. Check for dictionary words (example: "the", "and", "that")
        if (isDictionaryWord(password)) {
            passwordField.setPromptText("Password contains a dictionary word, which should be avoided.");
            passwordField.setStyle("-fx-border-color: red; -fx-background-color: #2e2e2e; -fx-background-radius:5; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
            valid = false;
        }

        // If password is valid, reset prompt text and style
        if (valid) {
            passwordField.setPromptText("Password is strong.");
            passwordField.setStyle("-fx-border-color: green; -fx-background-color: #2e2e2e; -fx-background-radius:5; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        }
        return valid;
    }

    // Helper method to check if a word is in the dictionary
    private boolean isDictionaryWord(String password) {
        String[] dictionary = {"the", "and", "that", "there", "hello"};  // Example dictionary words
        for (String word : dictionary) {
            if (password.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }


    @FXML
    private void handleContinue(ActionEvent event) {
    	
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String contact = contactField.getText();
        String role = roleComboBox.getValue();
        String password = passwordField.getText();
        String username = usernameField.getText();

        // Validate input
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || contact.isEmpty() || role == null || password.isEmpty() || username.isEmpty()) {
            showAlert(AlertType.ERROR, "Invalid Input", "All fields are required. Please fill them out.");
            return;
        }
        
        boolean isValid = validatePassword(passwordField);
        if (!isValid) {
            showAlert(AlertType.ERROR, "Invalid Password", "Please enter a valid password that meets all requirements.");
            return;
        }
        
        // Store data in memory
        signUpData.put("firstName", firstName);
        signUpData.put("lastName", lastName);
        signUpData.put("email", email);
        signUpData.put("contact", contact);
        signUpData.put("role", role);
        signUpData.put("password", password);
        signUpData.put("username", username);

        // Navigate to SignUpFinal
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignUpFinal.fxml"));
            StackPane stackpane = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(stackpane));
            stage.setMaximized(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Unable to load the next page.");
        }
    }
    @FXML
    private void handleLogIn(ActionEvent event) {
        try {
            // Load the LoginPage.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Unable to load the Login page.");
        }
    }

    // Handle the input changes for the specified fields
    
    private boolean checkIfExists(String email, String username) {
        boolean exists = false;

        // Database connection details (update with your own)
        String dbUrl = "jdbc:mysql://localhost:3306/cypherstackdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String dbUsername = "root";
        String dbPassword = "password";

        // SQL queries to check if the email or username exists
        String checkEmailQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
        String checkUsernameQuery = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {

            // Check if email exists
            try (PreparedStatement stmt = connection.prepareStatement(checkEmailQuery)) {
                stmt.setString(1, email);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        // If email exists, update the prompt text and reset the field
                        emailField.setPromptText("Email already exists");
                        emailField.setText(""); // Clear the email field
                        exists = true;
                    }
                }
            }

            // Check if username exists
            if (!exists) {
                try (PreparedStatement stmt = connection.prepareStatement(checkUsernameQuery)) {
                    stmt.setString(1, username);
                    try (ResultSet resultSet = stmt.executeQuery()) {
                        if (resultSet.next() && resultSet.getInt(1) > 0) {
                            // If username exists, update the prompt text and reset the field
                            usernameField.setPromptText("Username already exists");
                            usernameField.setText(""); // Clear the username field
                            exists = true;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }



    private void resetFieldStyles(TextInputControl field) {
        // Reset the styles for the clicked field only
        field.setStyle("-fx-background-color: #2e2e2e; -fx-text-fill: white; -fx-prompt-text-fill: gray; -fx-font-family: 'System Bold'; -fx-font-size: 14;");
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void showPasswordInfoPopup(MouseEvent event) {
        // Create a new Stage (window)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Password Guidelines");

        // Create a VBox to hold the instructions
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.TOP_LEFT);

        // Style the VBox container
        vbox.setStyle(
            "-fx-background-color: #2e2e2e;" +  // Dark background
            "-fx-padding: 40px;" +              // Padding
            "-fx-background-radius: 10px;"      // Rounded corners
        );

        // Create the text with instructions
        Text instructionText = new Text(
            "To create a strong password, please follow these guidelines:\n\n" +
            "1. Length: Your password must be at least 12 characters and no longer than 16 characters.\n\n" +
            "2. Complexity: Your password should include:\n\n" +
            "   - Uppercase letters (A-Z)\n" +
            "   - Lowercase letters (a-z)\n" +
            "   - Numbers (0-9)\n" +
            "   - Special characters (!, @, #, $, %, ^, &, etc.)\n\n" +
            "3. Unpredictability: Avoid common phrases like 'password123', 'qwerty', or anything personal like your name.\n\n" +
            "4. No Personal Information: Do not use your birthdate or any identifiable information.\n\n" +
            "6. Avoid Dictionary Words: Do not use common dictionary words.\n\n" 
            
            
        );

        // Apply styling to the text
        instructionText.setStyle(
            "-fx-fill: white;" +              // White text color
            "-fx-font-size: 14px;" +  
            "-fx-padding: 20px;" +// Font size
            "-fx-font-family: 'Century Gothic';"    // System font
                     // Bold text
        );

        // Wrap the text to ensure it fits
        instructionText.setWrappingWidth(400);

        // Create a ScrollPane to handle potential overflow
        ScrollPane scrollPane = new ScrollPane(instructionText);
        scrollPane.setStyle(
            "-fx-background: #2e2e2e;" +        // Dark background
            "-fx-border-color: transparent;" + // Remove border (optional)
            "-fx-background-insets: 0;"        // Prevent padding gaps
        );
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(400);

        // Add the scrollpane to the VBox
        vbox.getChildren().add(scrollPane);

        // Create a close button
        Button closeButton = new Button("Close");
        closeButton.setStyle(
            "-fx-background-color: #3e3e3e;" +  // Dark button background
            "-fx-text-fill: white;" +           // White text
            "-fx-padding: 8px 15px;" +          // Padding
            "-fx-background-radius: 5px;"       // Rounded corners
        );
        closeButton.setOnAction(e -> popupStage.close());

        // Add close button to the VBox
        vbox.getChildren().add(closeButton);

        // Create the scene
        Scene popupScene = new Scene(vbox, 500, 500);

        // Set the scene to the stage
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.showAndWait();
    }

    
    
    private void setComboBoxCellFactory(ComboBox<String> comboBox) {
        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item);
                            setStyle("-fx-background-color: #2e2e2e; -fx-text-fill: white;");
                        } else {
                            setText(null);
                            setStyle("");
                        }
                    }
                };
            }
        });
    }

    public static Map<String, String> getSignUpData() {
        return signUpData;
    }
}
