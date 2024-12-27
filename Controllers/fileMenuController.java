package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.AESService;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;

public class fileMenuController {

    @FXML
    private GridPane gridPane; // Link to your first GridPane from the FXML

    @FXML
    private Button browse; // Button to browse files

    @FXML
    private Button uploadButton; // Button to upload files

    @FXML
    private Label name; // Label to display file name

    @FXML
    private Label size; // Label to display file size

    @FXML
    private Label type; // Label to display file type

    private File selectedFile; // To store the selected file

    private final AESService aesService = new AESService(); // Instance of AES encryption backend

    private String secretKey; // Store the generated AES key

    private fileManagerController mainController;
    
    @FXML
    private RadioButton localStorageRadio;

    @FXML
    private RadioButton cloudStorageRadio;

    // Method for the "Browse" button to open a file chooser dialog
    @FXML
    private void browseFiles(ActionEvent event) {
        // Check if any radio button is selected
        if (!localStorageRadio.isSelected() && !cloudStorageRadio.isSelected()) {
            // Highlight radio buttons in red if none are selected
            localStorageRadio.setStyle("-fx-border-color: red; -fx-border-radius: 5; -fx-border-width: 2;");
            cloudStorageRadio.setStyle("-fx-border-color: red; -fx-border-radius: 5; -fx-border-width: 2;");
            
            // Show an alert to inform the user
            showAlert(Alert.AlertType.WARNING, "Storage Selection", "Please select a storage type before browsing files.");
            return;
        }

        // Reset radio button styles to original
        localStorageRadio.setStyle("-fx-border-color: #0078D7; -fx-border-radius: 5; -fx-border-width: 2;");
        cloudStorageRadio.setStyle("-fx-border-color: #0078D7; -fx-border-radius: 5; -fx-border-width: 2;");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        selectedFile = fileChooser.showOpenDialog(((Stage) ((Button) event.getSource()).getScene().getWindow()));

        if (selectedFile != null) {
            name.setText(selectedFile.getName());
            size.setText(selectedFile.length() / 1024 + " KB");
            type.setText(getFileExtension(selectedFile));
        } else {
            name.setText("No file selected");
            size.setText("-");
            type.setText("-");
        }
    }

    // Add these methods to handle radio button selection and reset styles
    @FXML
    private void handleLocalStorageRadio() {
        localStorageRadio.setStyle("-fx-border-color: #0078D7; -fx-border-radius: 5; -fx-border-width: 2;");
        cloudStorageRadio.setStyle("-fx-border-color: #0078D7; -fx-border-radius: 5; -fx-border-width: 2;");
        cloudStorageRadio.setSelected(false);
    }

    @FXML
    private void handleCloudStorageRadio() {
        cloudStorageRadio.setStyle("-fx-border-color: #0078D7; -fx-border-radius: 5; -fx-border-width: 2;");
        localStorageRadio.setStyle("-fx-border-color: #0078D7; -fx-border-radius: 5; -fx-border-width: 2;");
        localStorageRadio.setSelected(false);
    }
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex > 0 && dotIndex < fileName.length() - 1) ? fileName.substring(dotIndex + 1) : "Unknown";
    }

    @FXML
    private void uploadFile(ActionEvent event) {
        if (selectedFile != null) {
            try {
                // Load the progress bar FXML
                FXMLLoader progressLoader = new FXMLLoader(getClass().getResource("/views/progressBar.fxml"));
                Parent progressRoot = progressLoader.load();
                progressBarController progressController = progressLoader.getController();

                // Create a new stage for the progress bar
                Stage progressStage = new Stage();
                progressStage.initModality(Modality.APPLICATION_MODAL);
                progressStage.setTitle("Encrypting File");
                progressStage.setScene(new Scene(progressRoot));
                progressStage.show();

                // Start encryption in a background thread
                new Thread(() -> {
                    try {
                        // Define the vault directory path
                        Path vaultDirectory = Paths.get("C:\\CypherStack\\CypherStack\\src\\vault");

                        // Update progress to initial state
                        Platform.runLater(() -> {
                            progressController.setStatus("Preparing encryption...");
                            progressController.updateProgress(0.1);
                        });

                        // Ensure the vault directory exists, create it if not
                        if (!Files.exists(vaultDirectory)) {
                            Files.createDirectories(vaultDirectory);
                        }

                        // Update progress
                        Platform.runLater(() -> {
                            progressController.setStatus("Setting up encryption key...");
                            progressController.updateProgress(0.2);
                        });

                        // Use AES Service to manage encryption
                        AESService aesService = new AESService();

                        // If no existing key, generate a new one
                        SecretKey secretKeyObj;
                        Path keyFilePath = Paths.get("aes_key.txt");
                        if (!Files.exists(keyFilePath)) {
                            // Generate a new AES key
                            secretKeyObj = generateSecureAESKey();
                            
                            // Encode the key to Base64 and save
                            String base64Key = Base64.getEncoder().encodeToString(secretKeyObj.getEncoded());
                            Files.write(keyFilePath, base64Key.getBytes());
                        } else {
                            // Read existing key
                            String savedBase64Key = new String(Files.readAllBytes(keyFilePath));
                            secretKeyObj = aesService.getSecretKeyFromBase64(savedBase64Key);
                        }

                        // Update progress
                        Platform.runLater(() -> {
                            progressController.setStatus("Preparing file for encryption...");
                            progressController.updateProgress(0.4);
                        });

                        // Define the output file path for the encrypted file
                        String fileNameWithoutExtension = selectedFile.getName().replaceFirst("[.][^.]+$", "");
                        Path encryptedFilePath = vaultDirectory.resolve(fileNameWithoutExtension + ".cyp");

                        // Get the original file extension and store it in a .ext file
                        String originalExtension = getFileExtension(selectedFile.getName());
                        Path extensionFilePath = vaultDirectory.resolve(fileNameWithoutExtension + ".ext");
                        Files.write(extensionFilePath, originalExtension.getBytes());

                        // Update progress
                        Platform.runLater(() -> {
                            progressController.setStatus("Encrypting file...");
                            progressController.updateProgress(0.6);
                        });

                        // Encrypt the file
                        aesService.encryptFile(selectedFile, encryptedFilePath.toFile(), secretKeyObj);

                        // Update progress
                        Platform.runLater(() -> {
                            progressController.setStatus("Finalizing encryption...");
                            progressController.updateProgress(0.8);
                        });

                        // Update UI and load file manager
                        Platform.runLater(() -> {
                            try {
                                // Update progress to complete
                                progressController.setStatus("Encryption Complete");
                                progressController.updateProgress(1.0);

                                // Close progress stage after a short delay
                                Timeline timeline = new Timeline(new KeyFrame(
                                    javafx.util.Duration.seconds(1), 
                                    event1 -> progressStage.close()
                                ));
                                timeline.play();

                                // Update name text
                                name.setText("Encrypted: " + encryptedFilePath.getFileName());

                                // Load the fileManager.fxml and pass encrypted file details
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fileManager.fxml"));
                                Parent root = loader.load();

                                // Get the controller of the fileManager view
                                fileManagerController fileManagerController = loader.getController();

                                // Assuming userEmail is available and you have it in the context
                                String userEmail = "user@example.com"; // Replace with actual user email

                                // The call to createNewGridPane has been removed from here
                                // fileManagerController.createNewGridPane(encryptedFilePath.getFileName().toString(), userEmail);

                                // Get the current stage from the event
                                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                                // Close the popup (upload screen)
                                stage.close();

                                // Switch to the new scene (file manager view)
                                Stage mainStage = new Stage();
                                mainStage.setScene(new Scene(root));
                                mainStage.setMaximized(true);
                                mainStage.show();

                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "UI Error", "Failed to load file manager.");
                            }
                        });

                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            e.printStackTrace();
                            progressController.setStatus("Encryption failed");
                            progressStage.close();
                            showAlert(Alert.AlertType.ERROR, "Encryption Error", "Failed to encrypt the file. Please try again.");
                        });
                    }
                }).start();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Progress Bar Error", "Could not load progress bar.");
            }
        } else {
            name.setText("No file selected");
            showAlert(Alert.AlertType.WARNING, "File Selection", "Please select a file to upload.");
        }
    }


    // Method to get the file extension (e.g., .txt, .jpg, etc.)
    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index == -1) ? "" : fileName.substring(index);  // returns the file extension (e.g., .txt)
    }

    // Helper method to generate a secure AES key
    private SecretKey generateSecureAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 256-bit key
        return keyGen.generateKey();
    }



    public void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setMainController(fileManagerController mainController) {
        this.mainController = mainController;
    }
}
