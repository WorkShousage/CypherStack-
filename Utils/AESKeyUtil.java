package utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.util.Base64;

public class AESKeyUtil {

    // Generate a new AES key
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // Choose 128, 192, or 256 bits
        return keyGen.generateKey();
    }

    // Encode the key to Base64
    public static String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // Decode the Base64 string to a SecretKey
    public static SecretKey decodeKey(String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    // Validate the key length
    public static void validateKeyLength(byte[] keyBytes) throws InvalidKeyException {
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new InvalidKeyException("Invalid AES key length: " + keyBytes.length + " bytes");
        }
    }
}
