package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

public class IntrusionDataHandler {

	 private static final String DB_URL = "jdbc:mysql://localhost:3306/cypherstackdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	 private static final String DB_USER = "root";
	 private static final String DB_PASSWORD = "password";


    /**
     * Inserts intrusion data into the database.
     * @param imageFileLocation The location where the image was saved.
     * @param deviceLocation The physical or network location of the device.
     * @param additionalInfo Any additional information (optional).
     * @param deviceId The device identifier (optional).
     */
    
    public static String getPublicIP() {
        try {
            // Use a service that returns the public IP address as plain text.
            @SuppressWarnings("deprecation")
			URL url = new URL("http://checkip.amazonaws.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            // Read the response from the service
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            // Return the public IP address
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to fetch public IP address.";
        }
    }
    
    
    public static void insertIntrusionData(String imageFileLocation, String deviceLocation, String additionalInfo, String deviceId) {
        String query = "INSERT INTO Intrusions (image_file_location, detection_time, device_location, additional_info, device_id) "
                     + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the parameters for the query
            preparedStatement.setString(1, imageFileLocation);
            preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis())); // Current time
            preparedStatement.setString(3, deviceLocation);
            preparedStatement.setString(4, additionalInfo);
            preparedStatement.setString(5, deviceId);

            // Execute the insert query
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Intrusion data inserted successfully.");
            } else {
                System.err.println("Failed to insert intrusion data.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error inserting intrusion data: " + e.getMessage());
        }
    }}
