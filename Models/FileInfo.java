package models;  // Assuming you want to put it in a models package

import java.io.Serializable;
import java.util.Objects;

public class FileInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;
    private String userEmail;
    private String filePath;  // Optional: store file path if needed
    private long timestamp;   // Optional: track when file was added

    // Constructor
    public FileInfo(String fileName, String userEmail) {
        this.fileName = fileName;
        this.userEmail = userEmail;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getFileName() {
        return fileName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // Optional: toString for debugging
    @Override
    public String toString() {
        return "FileInfo{" +
               "fileName='" + fileName + '\'' +
               ", userEmail='" + userEmail + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }

    // Optional: equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return Objects.equals(fileName, fileInfo.fileName) &&
               Objects.equals(userEmail, fileInfo.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, userEmail);
    }
}
