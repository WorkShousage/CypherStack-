package utils;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordUtility {

    // Method to hash a plain password
    public static String hashPassword(String plainPassword) {
        // Generate a salt and hash the password
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Method to verify a plain password against a hashed password
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
