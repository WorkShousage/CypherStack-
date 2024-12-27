package controllers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class progressBarController {

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label statusLabel;

    // Method to update the progress on the ProgressBar
    public void updateProgress(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }

    // Method to update the label text
    public void setStatus(String status) {
        Platform.runLater(() -> statusLabel.setText(status));
    }

    // Method to start encryption simulation and show progress
    public void startEncryption() {
        // Start background thread to simulate encryption
        new Thread(() -> {
            try {
                // Initially set label to "Encrypting..."
                setStatus("Encrypting...");

                // Simulate initial preparation (20%)
                setStatus("Preparing encryption...");
                updateProgress(0.2);
                Thread.sleep(2000); // 2 seconds at 20%

                // Move to 50%
                setStatus("Processing file...");
                updateProgress(0.5);
                Thread.sleep(3000); // 3 seconds at 50%

                // Move to 75%
                setStatus("Generating encryption key...");
                updateProgress(0.75);
                Thread.sleep(2500); // 2.5 seconds at 75%

                // Move to 90%
                setStatus("Finalizing encryption...");
                updateProgress(0.9);
                Thread.sleep(2500); // 2.5 seconds at 90%

                // Complete encryption
                updateProgress(1.0);
                setStatus("Encryption Complete");

                // Final pause
                Thread.sleep(1000);

                // Optional: Simulate closing or switching UI
                Platform.runLater(() -> {
                    // Close or switch the UI as needed after the process is done.
                    // Example: progressStage.close(); 
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
                setStatus("Error during encryption");
            }
        }).start();
    }
}