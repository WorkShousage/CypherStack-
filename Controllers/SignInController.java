package controllers;

import java.io.IOException;

import utils.LoginAttemptsManager;
import utils.UserAuthentication;
import utils.SoundPlayer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SignInController {
    @FXML
    private TextField emailField; // Field for email input
    @FXML
    private TextField passwordField; // Field for password input
    @FXML
    private Button signInButton; // Button to trigger sign-in
    @FXML
    private Label statusLabel; // Label to display status messages
    @FXML
    private Label errorLabel;
    @FXML
    private Hyperlink createAccountLink;

    
//    String saveDirectory = utils.OpenCVCaptureHandler.getLastCapturedImagePath();
    String imageDirectory = "C:\\CypherStack\\CypherStack\\intruderInfo\\IntruderImage";
    String deviceLocation = utils.IntrusionDataHandler.getPublicIP();
    String additionalInfo = "Intrusion detected, image captured for analysis.";
    String deviceId = "Camera001";
    

    
    
    @FXML
    public void initialize() {
        // Set the action for the sign-in button
        signInButton.setOnAction(event -> handleSignInButton());
        passwordField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleSignInButton();
            }});
        
    }

    @FXML
    private void handleSignInButton() {
        String email = emailField.getText(); // Get the email from the input field
        String password = passwordField.getText(); // Get the password from the input field

        // Track incorrect login attempts
        int maxAttempts = 3;
        int currentAttempts = getCurrentAttempts(email); // Retrieve current attempts from memory or a map

        // Check if the credentials are valid
        if (isValidCredentials(email, password)) {
            resetAttempts(email); // Reset the failed attempts counter
            loadDashboard();      // Load the dashboard
        } else {
            currentAttempts++;
            updateAttempts(email, currentAttempts); // Update the failed attempts counter

            if (currentAttempts >= maxAttempts) {
                // Lock the computer if the user fails 3 times
            	displayErrorMessage("Locking all Systems");
            	SoundPlayer.playSound("C:\\CypherStack\\CypherStack\\src\\audio\\audioFile.wav");
            	utils.OpenCVCaptureHandler.captureAndSaveImage(imageDirectory);
            	String saveDirectory = utils.OpenCVCaptureHandler.getLastCapturedImagePath();
            	utils.IntrusionDataHandler.insertIntrusionData(saveDirectory, deviceLocation, additionalInfo, deviceId);
            	
                utils.WindowsLockUtility.lockComputer();
            } else {
                // Display appropriate error messages
                if (!UserAuthentication.isValidEmail(email)) {
                    displayErrorMessage("Invalid email or password.");
                } else {
                    displayErrorMessage("Wrong password. Attempts remaining: " + (maxAttempts - currentAttempts));
                }
            }
        }
    }

    /**
     * Validates the credentials by verifying the email and password against the database.
     *
     * @param email    The email to validate.
     * @param password The password to validate.
     * @return true if the credentials are valid, false otherwise.
     */
    private boolean isValidCredentials(String email, String password) {
        return utils.UserAuthentication.verifyPasswordForEmail(email, password);
    }

   



    // Method to display an error message
    private void displayErrorMessage(String message) {
    	 errorLabel.setText(message); // Set the error message
         errorLabel.setVisible(true);
    }

    private void loadDashboard() {
        try {
            // Load the dashboard FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboardView.fxml"));
            AnchorPane fileManager = loader.load();

            // Create a new scene for the dashboard
            Stage stage = (Stage) emailField.getScene().getWindow(); // Get the current stage
            stage.setScene(new Scene(fileManager)); // Set the new scene
            stage.setTitle("Dashboard");
            stage.setMaximized(true);// Optionally set the title
            stage.show(); // Show the new scene
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions properly in production code
            // Optionally show an error message
            System.out.println("Failed to load dashboard. Please try again.");
        }
    }
    @FXML
    private void handleCreateAccount() {
        try {
            // Load the sign-up FXML file for the popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/signUpFirst.fxml"));
            Parent signupPage = loader.load();

            // Create a new Stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Sign Up");

            // Set the scene to the new signup page
            Scene scene = new Scene(signupPage);
            popupStage.setScene(scene);

            // Maximize the popup window or set its dimensions
            popupStage.setMaximized(true);

            // Show the popup window
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Optionally handle errors here (e.g., display a message to the user)
        }
    }
    
    /**
     * Resets the failed login attempts for a given email.
     *
     * @param email The email whose attempts to reset.
     */
    private void resetAttempts(String email) {
        // Reset attempts in memory or database (implement the method to store and retrieve attempts)
        LoginAttemptsManager.resetAttempts(email);
    }

    /**
     * Retrieves the current number of failed login attempts for a given email.
     *
     * @param email The email whose attempts to retrieve.
     * @return The current number of attempts.
     */
    private int getCurrentAttempts(String email) {
        // Retrieve attempts from memory or database (implement a storage mechanism)
        return LoginAttemptsManager.getAttempts(email);
    }

    /**
     * Updates the failed login attempts for a given email.
     *
     * @param email    The email whose attempts to update.
     * @param attempts The new number of attempts.
     */
    private void updateAttempts(String email, int attempts) {
        // Update attempts in memory or database (implement the method to store attempts)
        LoginAttemptsManager.updateAttempts(email, attempts);
    }



}