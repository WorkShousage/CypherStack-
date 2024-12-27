package utils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESService {

    // Hardcoded Base64 AES Key (256-bit)
    private static final String HARD_CODED_KEY = "YOUR_BASE64_ENCODED_AES_KEY_HERE"; // Replace with a valid Base64 key

    // Encrypt a file
    public void encryptFile(File inputFile, File outputFile, SecretKey secretKey) throws Exception {
        validateKeyLength(secretKey.getEncoded());

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Generate a secure random IV
        byte[] iv = new byte[16];
        SecureRandom random = SecureRandom.getInstanceStrong();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Initialize cipher in encryption mode
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

            // Write the IV to the output file
            fos.write(iv);

            // Encrypt the file data
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    // Decrypt a file
    public void decryptFile(File inputFile, File outputFile, SecretKey secretKey) throws Exception {
        validateKeyLength(secretKey.getEncoded());

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            // Read the IV from the input file
            byte[] iv = new byte[16];
            if (fis.read(iv) != iv.length) {
                throw new IOException("Invalid input file format: IV not found.");
            }
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Initialize cipher in decryption mode
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            try (CipherInputStream cis = new CipherInputStream(fis, cipher)) {
                // Decrypt the file data
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    // Method to get hardcoded SecretKey
    public SecretKey getHardcodedSecretKey() throws Exception {
        return getSecretKeyFromBase64(HARD_CODED_KEY);
    }

    // Helper method to convert Base64 string to SecretKey
    public SecretKey getSecretKeyFromBase64(String base64Key) throws Exception {
        String modifiedBase64Key = base64Key.replace('-', '+').replace('_', '/');
        int paddingLength = (4 - (modifiedBase64Key.length() % 4)) % 4;
        modifiedBase64Key += "=".repeat(paddingLength);

        byte[] keyBytes = Base64.getDecoder().decode(modifiedBase64Key);
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Validate the length of the AES key
    private void validateKeyLength(byte[] keyBytes) throws InvalidKeyException {
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new InvalidKeyException("Invalid AES key length: " + keyBytes.length + " bytes. Valid lengths are 16, 24, or 32 bytes.");
        }
    }
}