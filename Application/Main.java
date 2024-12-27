package application;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;

	@Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/loginpage.fxml"));
        primaryStage.setTitle("Login Page");
        primaryStage.setScene(new Scene(root)); // You can omit the width and height for full screen
        primaryStage.setMaximized(true); // Set the stage to full screen
        primaryStage.show();
    }

    public void showDashboardScene() throws IOException {
        // Load dashboard.fxml and set it as the primary scene
        Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/views/dashboardManager.fxml"));
        Scene dashboardScene = new Scene(dashboardRoot);

        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(dashboardScene);
    }

    public void showFileManagementScene() throws IOException {
        // Load fileManagement.fxml and set it as the primary scene
        Parent fileManagementRoot = FXMLLoader.load(getClass().getResource("/views/fileManager.fxml"));
        Scene fileManagementScene = new Scene(fileManagementRoot);

        primaryStage.setTitle("File Management");
        primaryStage.setScene(fileManagementScene);
    }

    public void openFileUploadWindow() {
        // Open a new stage for the file upload window
        Stage fileUploadStage = new Stage();
        fileUploadStage.setTitle("Upload File");

        // FileChooser for selecting files
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");

        File selectedFile = fileChooser.showOpenDialog(fileUploadStage);
        if (selectedFile != null) {
            System.out.println("File Selected: " + selectedFile.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
