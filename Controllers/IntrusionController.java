package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntrusionController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cypherstackdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    @FXML
    private GridPane gridPane;  // Make sure this matches the fx:id in your FXML
    @FXML
    private Label detctionTime;
    @FXML
    private Label idLabel;
    @FXML
    private Label Location;
    @FXML
    private Label addinfo;
    @FXML
    private Label deviceid;
    @FXML
    private ImageView CapturedImage;

    private String filePath; // Ensure this is initialized properly

    // Initialize the controller
    @FXML
    public void initialize() {
        // Set up the click handler for the CapturedImage
        CapturedImage.setOnMouseClicked((MouseEvent event) -> {
            if (filePath != null && !filePath.isEmpty()) {
                File imageFile = new File(filePath);
                if (imageFile.exists()) {
                    try {
                        Desktop.getDesktop().open(imageFile);
                        System.out.println("Opening image: " + imageFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error opening image file.");
                    }
                } else {
                    System.out.println("File not found: " + filePath);
                }
            } else {
                System.out.println("No file path provided.");
            }
        });

        // Load data from the database
        loadData();
    }

    // Load an image into the ImageView
    public void loadImage(String imagePath) {
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {
            CapturedImage.setImage(new Image(imageFile.toURI().toString()));
        } else {
            System.err.println("Image file not found at: " + imagePath);
        }
    }

    @FXML
    private void OpenfileManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fileManager.fxml"));
            AnchorPane fileManager = loader.load();

            Stage stage = (Stage) gridPane.getScene().getWindow(); // Get the current stage
            stage.setScene(new Scene(fileManager));
            stage.setTitle("Dashboard");
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load dashboard. Please try again.");
        }
    }

    @FXML
    public void loadData() {
        String query = "SELECT * FROM intrusions LIMIT 1";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            if (rs.next()) {
                try {
                    int id = rs.getInt("id");
                    String imageFileLocation = rs.getString("image_file_location");
                    String detectionTime = rs.getString("detection_time");
                    String deviceLocation = rs.getString("device_location");
                    String additionalInfo = rs.getString("additional_info");
                    String deviceId = rs.getString("device_id");

                    filePath = imageFileLocation;

                    // Update UI elements
                    idLabel.setText(String.valueOf(id));
                    loadImage(imageFileLocation);
                    detctionTime.setText(detectionTime);
                    Location.setText(deviceLocation);
                    addinfo.setText(additionalInfo);
                    deviceid.setText(deviceId);

                } catch (Exception e) {
                    logAndShowError("Error while updating UI with database data", e);
                }
            }
        } catch (SQLException e) {
            logAndShowError("Error while fetching data from the database", e);
        } catch (Exception e) {
            logAndShowError("Unexpected error occurred", e);
        }
    }

    private void logAndShowError(String message, Exception e) {
        e.printStackTrace();
        showAlert(message + ": " + e.getMessage());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
