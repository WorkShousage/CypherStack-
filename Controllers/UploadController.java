package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class UploadController {

    @FXML
    private RadioButton localStorageRadioButton;

    @FXML
    private RadioButton cloudStorageRadioButton;

    @FXML
    private Button browseButton;

    @FXML
    private Button uploadButton;

    @FXML
    private Label fileNameLabel; // You may want to add a Label in your FXML to display the selected file name

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        // Initialize any necessary components or set default values
        browseButton.setOnAction(event -> handleBrowse());
        uploadButton.setOnAction(event -> handleUpload());
    }

    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            fileNameLabel.setText(selectedFile.getName()); // Assuming you have a label to show the file name
        }
    }

    private void handleUpload() {
        String selectedStorage = localStorageRadioButton.isSelected() ? "Local Storage" : "Cloud Storage";
        String fileName = fileNameLabel.getText();
        
        // Add your upload logic here
        if (fileName.isEmpty()) {
            System.out.println("No file selected.");
            return;
        }

        System.out.println("Uploading " + fileName + " to " + selectedStorage);
        // Implement your upload logic here
    }
}