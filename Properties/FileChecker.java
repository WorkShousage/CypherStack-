package properties;

import java.io.File;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import controllers.fileManagerController;

public class FileChecker {

    private StackPane stackPane;
    private fileManagerController fileManagerController;

    public FileChecker(StackPane stackPane, fileManagerController fileManagerController) {
        this.stackPane = stackPane;
        this.fileManagerController = fileManagerController;
    }

    public void checkAndCreateGrids() {
        String folderPath = "C:\\CypherStack\\CypherStack\\src\\vault";
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".cyp"));

            if (files != null) {
                for (File file : files) {
                    // Check if a GridPane for this file already exists
                    if (!isGridPanePresent(file.getName())) {
                        // Assuming the user email can be fetched dynamically; using a placeholder here
                        String userEmail = "user@example.com";
                        // Call createNewGridPane from FileManagerController
                        fileManagerController.createNewGridPane(file.getName(), userEmail);
                    }
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No .cyp files found in the vault.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Vault folder does not exist or is not a directory.");
        }
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


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
