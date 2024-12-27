package utils;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OpenCVCaptureHandler {

    static {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // Variable to store the last captured image path
    private static String lastCapturedImagePath = null;

    /**
     * Captures an image from the default webcam and saves it to the specified directory.
     *
     * @param saveDirectory The directory where the image will be saved.
     * @return The full path of the saved image, or null if the capture failed.
     */
    public static String captureAndSaveImage(String saveDirectory) {
        // Open the default camera (camera index 0)
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.err.println("Error: Unable to open the camera.");
            return null;
        }

        // Create a matrix to hold the image frame
        Mat frame = new Mat();

        // Read a single frame from the camera
        if (camera.read(frame)) {
            // Ensure the save directory exists
            File directory = new File(saveDirectory);
            if (!directory.exists()) {
                directory.mkdirs();  // Creates the directory if it doesn't exist
            }

            // Generate a timestamped filename for the image
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filePath = saveDirectory + File.separator + "image_" + timestamp + ".png";

            // Save the frame as an image file
            boolean success = Imgcodecs.imwrite(filePath, frame);
            if (success) {
                // Save the file path to the class-level variable
                lastCapturedImagePath = filePath;
                System.out.println("Image saved successfully at: " + filePath);
                return filePath;
            } else {
                System.err.println("Error: Failed to save the image.");
            }
        } else {
            System.err.println("Error: Unable to capture a frame from the camera.");
        }

        // Release the camera and return null if capture failed
        camera.release();
        return null;
    }

    /**
     * Get the path of the last captured image.
     * 
     * @return The file path of the last captured image or null if no image was captured.
     */
    public static String getLastCapturedImagePath() {
        return lastCapturedImagePath;
    }
}
