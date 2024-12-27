package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthentication {

    // Database credentials
	 private static final String DB_URL = "jdbc:mysql://localhost:3306/cypherstackdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	 private static final String DB_USER = "root";
	 private static final String DB_PASSWORD = "password";


    /**
     * Verifies if the password matches the hashed password in the database for the given email.
     *
     * @param email    The user's email.
     * @param password The plaintext password to verify.
     * @return true if the password matches, false otherwise.
     */
    public static boolean verifyPasswordForEmail(String email, String password) {
        // SQL query to fetch the hashed password for the given email
        String query = "SELECT password_hash FROM users WHERE email = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the email parameter in the query
            preparedStatement.setString(1, email);

            // Execute the query and retrieve the result
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the hashed password from the database
                    String hashedPassword = resultSet.getString("password_hash");

                    // Use BCryptPasswordUtility to verify the password
                    return BCryptPasswordUtility.verifyPassword(password, hashedPassword);
                }
            }

        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            e.printStackTrace();
        }

        // Return false if the user does not exist or an error occurs
        return false;
    }
    
    public static boolean isValidEmail(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, email);  // Set the email parameter in the query
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);  // Get the count of rows matching the email
                return count > 0;  // If count > 0, email exists in the database
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;  // If there is an error or no email match, return false
    }
}
