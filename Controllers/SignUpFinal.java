package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.BCryptPasswordUtility;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;

public class SignUpFinal {


	    @FXML
	    private TextField addressField, cityField, stateField, companyNameField, workAddressField, employeeIdField, departmentField, jobTitleField;

	    // Database credentials
	    private static final String DB_URL = "jdbc:mysql://localhost:3306/cypherstackdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	    private static final String DB_USER = "root";
	    private static final String DB_PASSWORD = "password";

	    @FXML
	    private void handleSignUp(ActionEvent event) {
	        try {
	            // Access the static map from SignUpFirst
	            Map<String, String> signUpData = controllers.SignUpFirst.getSignUpData();

	            // Add additional data to the map
	            signUpData.put("address", addressField.getText());
	            signUpData.put("city", cityField.getText());
	            signUpData.put("state", stateField.getText());
	            signUpData.put("companyName", companyNameField.getText());
	            signUpData.put("workAddress", workAddressField.getText());
	            signUpData.put("employeeId", employeeIdField.getText());
	            signUpData.put("department", departmentField.getText());
	            signUpData.put("jobTitle", jobTitleField.getText());

	            // Database insertion
	            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	                String query = "INSERT INTO users (first_name, last_name, email, contact, role, password_hash, username, address, city, state, company_name, work_address, employee_id, department, job_title) "
	                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	                PreparedStatement statement = connection.prepareStatement(query);

	                // Get the plain password from the signUpData map
	                String plainPassword = signUpData.get("password");

	                // Hash the password before inserting it into the database
	                String hashedPassword = BCryptPasswordUtility.hashPassword(plainPassword);

	                // Setting parameters from signUpData map
	                statement.setString(1, signUpData.get("firstName"));
	                statement.setString(2, signUpData.get("lastName"));
	                statement.setString(3, signUpData.get("email"));
	                statement.setString(4, signUpData.get("contact"));
	                statement.setString(5, signUpData.get("role"));
	                statement.setString(6, hashedPassword); // Insert the hashed password here
	                statement.setString(7, signUpData.get("username"));
	                statement.setString(8, signUpData.get("address"));
	                statement.setString(9, signUpData.get("city"));
	                statement.setString(10, signUpData.get("state"));
	                statement.setString(11, signUpData.get("companyName"));
	                statement.setString(12, signUpData.get("workAddress"));
	                statement.setString(13, signUpData.get("employeeId"));
	                statement.setString(14, signUpData.get("department"));
	                statement.setString(15, signUpData.get("jobTitle"));
	                // Execute the update
	                statement.executeUpdate();
	                showAlert(Alert.AlertType.INFORMATION, "Sign Up Successful", "You have been signed up successfully!");

	                // Load the dashboard
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/loginpage.fxml"));
	                AnchorPane root = loader.load();
	                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	                stage.setScene(new Scene(root));
	                stage.setMaximized(false);
	                stage.show();

	            } catch (Exception e) {
	                showAlert(Alert.AlertType.ERROR, "Error", "Error occurred: " + e.getMessage());
	            }
	        } catch (Exception e) {
	            showAlert(Alert.AlertType.ERROR, "Error", "Could not retrieve data from SignUpFirst: " + e.getMessage());
	        }
	    }

	    private void showAlert(Alert.AlertType alertType, String title, String message) {
	        Alert alert = new Alert(alertType);
	        alert.setTitle(title);
	        alert.setContentText(message);
	        alert.show();
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
	}


