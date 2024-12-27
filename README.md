## **File Encryption Software (AES-Based with Intrusion Detection)**

This repository contains a robust **File Encryption Software** that leverages an extended version of the AES algorithm for secure file encryption. It provides an additional layer of protection by converting files into the `.cyp` encrypted format and incorporates an **Intrusion Detection System (IDS)** to monitor unauthorized access attempts.

### **How It Works:**

- **File Conversion & Encryption:**  
  The software converts files into a secure `.cyp` encrypted format using an extended version of the AES encryption algorithm. This ensures that files are encrypted during upload and remain inaccessible until decrypted via the software.  

- **Decryption on Access:**  
  Every time you access a `.cyp` file, the software automatically decrypts it, allowing you to open and view your file seamlessly. The decryption process occurs with **negligible speed impact**, ensuring smooth access to your files.

- **AES Extended Algorithm:**  
  The encryption uses an extended version of the AES algorithm for improved security, providing stronger encryption compared to standard AES. This ensures that the data remains protected against modern encryption-breaking techniques.

- **File Extensions & Association:**  
  All files encrypted using the software are saved with a `.cyp` extension, which is exclusive to the software, preventing unauthorized access through standard file readers or applications.

- **Intrusion Detection System (IDS):**  
  The software features an **Intrusion Detection System (IDS)** that monitors and tracks access attempts. If an incorrect password is entered three times consecutively, the IDS triggers a security protocol:
  - It captures and stores the user's **image** (via webcam).
  - Records the **location** of the access attempt (using IP geolocation).
  - Logs the **time** and **date** of the failed access attempt.  

  These logs can be accessed by the user or admin for reviewing unauthorized access attempts.

### **Key Features:**
- AES extended encryption algorithm for stronger data protection.
- Conversion of files to `.cyp` encrypted format, which can only be accessed through the software.
- Decrypts files seamlessly with negligible performance impact.
- Intrusion Detection System that tracks failed password attempts and captures user data (image, location, time).
- Full control over file encryption and decryption process.
  
### **Setup & Installation:**
- **Requirements:** Java (latest version recommended).
- **Installation Instructions:**  
  1. Clone the repository.  
  2. Compile and run the software using the provided instructions in the `README` file.  
  3. Follow the prompts to convert files into `.cyp` format and begin encryption.

### **Contribution:**
Feel free to fork the repository and contribute! If you have suggestions for improvements or encounter any bugs, submit an issue or pull request.

---
