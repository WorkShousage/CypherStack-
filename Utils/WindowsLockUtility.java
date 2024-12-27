package utils;

public class WindowsLockUtility {
	public static void lockComputer() {
        try {
            // Use ProcessBuilder to run the lock command
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "rundll32", "user32.dll,LockWorkStation"
            );
            processBuilder.start(); // Start the process
            System.out.println("Computer locked successfully.");
        } catch (Exception e) {
            System.err.println("Failed to lock the computer.");
            e.printStackTrace();
        }
    }

}
