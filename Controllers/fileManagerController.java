package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.AESService;

import javax.crypto.SecretKey;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

public class fileManagerController {

    private fileManagerController mainController;
    @FXML
    private Button addFile;

    @FXML
    private StackPane stackPane;

    private double translateX = 320;
    private double translateY = 200;
    private static final double TRANSLATE_INCREMENT = 120;

    private AESService aesService = new AESService();  // Assuming AESService is properly initialized
    private SecretKey secretKey;  // Assuming this is your AES secret key

    @FXML
    private void initialize() {
        if (stackPane == null) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "StackPane is not initialized.");
            return;
        }

        // Create an instance of FileManagerController and pass it to FileChecker
        fileManagerController fileManagerController = this;  // Assuming this is the current controller
        properties.FileChecker fileChecker = new properties.FileChecker(stackPane, fileManagerController);
        
        // Call the method to check files and create grids as soon as the page is initialized
        fileChecker.checkAndCreateGrids();
    }


    @FXML
    private void addNewFile(ActionEvent event) {
        try {
            // Load the fileMenu.fxml for the popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fileMenu.fxml"));
            Parent fileMenuView = loader.load();

            // Get the controller for fileMenu
            fileMenuController fileMenuController = loader.getController();

            // Pass the current controller (this) to the popup so it can call back to add files
            fileMenuController.setMainController(this);

            // Create a new Stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Upload File");
            popupStage.setScene(new Scene(fileMenuView));
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow()); // Set owner window
            popupStage.initModality(Modality.WINDOW_MODAL); // Block interaction with the main window
            popupStage.setResizable(false);
            popupStage.showAndWait(); // Wait for the popup to close
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Popup Error", "Unable to open the upload file window.");
        }
    }

    public void createNewGridPane(String fileName, String userEmail) {
        if (stackPane == null) {
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "StackPane is not initialized.");
            return;
        }

        // Check if a GridPane with the same fileName already exists
        if (isGridPanePresent(fileName)) {
            // If the GridPane already exists, skip creation and show an alert
            showAlert(Alert.AlertType.INFORMATION, "File Already Exists", "The GridPane for this file is already created.");
            return;
        }

        // Create a unique GridPane for each call
        GridPane newGridPane = new GridPane();
        newGridPane.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(50.0));
        newGridPane.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(200.0));
        newGridPane.getRowConstraints().add(new javafx.scene.layout.RowConstraints(25.0));
        newGridPane.getRowConstraints().add(new javafx.scene.layout.RowConstraints(25.0));

        // Add file name label
        Label fileNameLabel = new Label(fileName);
        fileNameLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        fileNameLabel.setFont(new javafx.scene.text.Font("System Bold", 15));
        fileNameLabel.setWrapText(true);
        newGridPane.add(fileNameLabel, 1, 0);

        // Add user email label
        Label userEmailLabel = new Label(userEmail);
        userEmailLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        userEmailLabel.setFont(new javafx.scene.text.Font(14));
        newGridPane.add(userEmailLabel, 1, 1);

        // Create file icon
        ImageView fileIcon = new ImageView(new Image("views/images/filesicon.png"));
        fileIcon.setFitHeight(100.0);
        fileIcon.setFitWidth(50.0);
        fileIcon.setPreserveRatio(true);
        fileIcon.setPickOnBounds(true);

        // Add the ImageView to the GridPane at column 0, row 0, spanning 1 column and 2 rows
        newGridPane.add(fileIcon, 0, 0, 1, 2);

        // Add the mouse click event handler using a lambda
        newGridPane.setOnMouseClicked(event -> handleGridPaneClick(event));
        fileNameLabel.setOnMouseClicked(event -> handleGridPaneClick(event));
        userEmailLabel.setOnMouseClicked(event -> handleGridPaneClick(event));
        fileIcon.setOnMouseClicked(event -> handleGridPaneClick(event));

        // Set the position for the new GridPane
        newGridPane.setTranslateX(translateX);
        newGridPane.setTranslateY(translateY);

        // Increment translateY for the next GridPane
        translateY += 100;

        // Add the new GridPane to the StackPane
        stackPane.getChildren().add(newGridPane);
    }
    private boolean isGridPanePresent(String fileName) {
        // Iterate over StackPane children and check if the GridPane with the same fileName exists
        for (Node node : stackPane.getChildren()) {
            if (node instanceof GridPane) {
                // Check if this GridPane's file name matches
                GridPane gridPane = (GridPane) node;
                
                // Check if the file name label is at index 1 (based on your provided code)
                Node labelNode = gridPane.getChildren().get(1); // Label is at index 1
                if (labelNode instanceof javafx.scene.control.Label) {
                    javafx.scene.control.Label label = (javafx.scene.control.Label) labelNode;
                    if (label.getText().equals(fileName)) {
                        return true; // GridPane already exists for this file name
                    }
                }
            }
        }
        return false; // No existing GridPane with the same file name
    }

    public void loadAndDisplayFiles() {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream("uploadedFiles.properties")) {
            properties.load(inputStream);

            // Iterate through each file entry in the properties file
            boolean filesLoaded = false;
            for (String key : properties.stringPropertyNames()) {
                if (key.endsWith(".fileName")) {  // Check if the key corresponds to a file name
                    String fileName = properties.getProperty(key); // Get the file name
                    String userEmail = properties.getProperty(key.replace(".fileName", ".userEmail")); // Get user email

                    // Create a new GridPane for each file
                    createNewGridPane(fileName, userEmail);
                    filesLoaded = true;
                }
            }

            // If no files were loaded, display a message or create an empty GridPane
            if (!filesLoaded) {
                createNewGridPane("No files available", "N/A");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "File Load Error", "Unable to load uploaded files.");
        }
    }

    @FXML
    private void handleGridPaneClick(MouseEvent event) {
        try {
            if (event.getSource() instanceof Label) {
                Label fileNameLabel = (Label) event.getSource();
                final String fileName = fileNameLabel.getText();

                // Load the progress bar FXML
                FXMLLoader progressLoader = new FXMLLoader(getClass().getResource("/views/progressBar.fxml"));
                Parent progressRoot = progressLoader.load();
                progressBarController progressController = progressLoader.getController();

                // Create a new stage for the progress bar
                Stage progressStage = new Stage();
                progressStage.initModality(Modality.APPLICATION_MODAL);
                progressStage.setTitle("Decrypting File");
                progressStage.setScene(new Scene(progressRoot));
                progressStage.show();

                // Start decryption in a background thread
                new Thread(() -> {
                    try {
                        // Update progress to initial state
                        Platform.runLater(() -> {
                            progressController.setStatus("Preparing decryption...");
                            progressController.updateProgress(0.1);
                        });

                        Path vaultDirectory = Paths.get("C:\\CypherStack\\CypherStack\\src\\vault");
                        Path encryptedFilePath = vaultDirectory.resolve(fileName);
                        Path extensionFilePath = vaultDirectory.resolve(fileName.replace(".cyp", ".ext")); // Path to the .ext file
                        Path keyFilePath = Paths.get("aes_key.txt");

                        // Update progress
                        Platform.runLater(() -> {
                            progressController.setStatus("Validating files...");
                            progressController.updateProgress(0.2);
                        });

                        // Validate file existence
                        if (!Files.exists(encryptedFilePath)) {
                            Platform.runLater(() -> {
                                progressStage.close();
                                showAlert(Alert.AlertType.WARNING, "File Not Found", "The encrypted file does not exist.");
                            });
                            return;
                        }

                        // Validate key file existence
                        if (!Files.exists(keyFilePath)) {
                            Platform.runLater(() -> {
                                progressStage.close();
                                showAlert(Alert.AlertType.ERROR, "Key Error", "Encryption key is missing. Unable to decrypt.");
                            });
                            return;
                        }

                        // Update progress
                        Platform.runLater(() -> {
                            progressController.setStatus("Loading encryption key...");
                            progressController.updateProgress(0.3);
                        });

                        // Read the AES key securely
                        String base64Key = new String(Files.readAllBytes(keyFilePath)).trim();
                        SecretKey secretKey = aesService.getSecretKeyFromBase64(base64Key);

                        // Update progress
                        Platform.runLater(() -> {
                            progressController.setStatus("Creating temporary storage...");
                            progressController.updateProgress(0.4);
                        });

                        // Create a secure temporary file for decrypted data
                        Path tempDirectory = Files.createTempDirectory("CypherStack_Decrypted_");
                        tempDirectory.toFile().deleteOnExit();

                        // Extract the original file name without the encrypted extension (e.g., .cyp)
                        String originalFileName = removeEncryptedExtension(fileName);
                        Path tempDecryptedFile = tempDirectory.resolve(originalFileName);

                        // Update progress
                        Platform.runLater(() -> {
                            progressController.setStatus("Decrypting file...");
                            progressController.updateProgress(0.6);
                        });

                        // Decrypt the file
                        try {
                            aesService.decryptFile(encryptedFilePath.toFile(), tempDecryptedFile.toFile(), secretKey);
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                progressStage.close();
                                showAlert(Alert.AlertType.ERROR, "Decryption Failed",
                                        "Could not decrypt the file. It may be corrupted or the wrong key was used.");
                            });
                            return;
                        }

                        // Update progress
                        Platform.runLater(() -> {
                            progressController.setStatus("Restoring file extension...");
                            progressController.updateProgress(0.8);
                        });

                        // Restore the original file extension
                        String originalFileExtension = getOriginalFileExtensionFromExtFile(extensionFilePath);
                        Path finalDecryptedFile = Paths.get(tempDirectory.toString(), originalFileName + originalFileExtension);

                        // Rename the decrypted file with the original extension
                        Files.move(tempDecryptedFile, finalDecryptedFile);

                        // Update progress
                        Platform.runLater(() -> {
                            progressController.setStatus("Decryption Complete");
                            progressController.updateProgress(1.0);
                        });

                        // Open the decrypted file
                        File decryptedFile = finalDecryptedFile.toFile();
                        if (decryptedFile.exists()) {
                            Platform.runLater(() -> {
                                try {
                                    // Close progress stage after a short delay
                                    Timeline timeline = new Timeline(new KeyFrame(
                                        javafx.util.Duration.seconds(1), 
                                        event1 -> progressStage.close()
                                    ));
                                    timeline.play();

                                    Desktop.getDesktop().open(decryptedFile);
                                } catch (IOException e) {
                                    showAlert(Alert.AlertType.ERROR, "Open File Error",
                                            "Could not open the decrypted file. " + e.getMessage());
                                }
                            });
                        } else {
                            Platform.runLater(() -> {
                                progressStage.close();
                                showAlert(Alert.AlertType.ERROR, "File Error", "Decrypted file was not created successfully.");
                            });
                        }

                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            e.printStackTrace();
                            progressStage.close();
                            showAlert(Alert.AlertType.ERROR, "Decryption Error",
                                    "An unexpected error occurred: " + e.getMessage());
                        });
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Progress Bar Error", "Could not load progress bar.");
        }
    }

    
    // Method to get the original file extension from the .ext file
    private String getOriginalFileExtensionFromExtFile(Path extensionFilePath) throws IOException {
        if (Files.exists(extensionFilePath)) {
            return new String(Files.readAllBytes(extensionFilePath)).trim();
        } else {
            throw new IOException("Original file extension not found.");
        }
    }


    // Method to remove the .cyp extension from the encrypted file name
    private String removeEncryptedExtension(String fileName) {
        return fileName.replaceFirst("[.][^.]+$", "");  // Removes the file extension
    }

    


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
