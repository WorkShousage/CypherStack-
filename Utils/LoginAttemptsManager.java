package utils;

import java.util.concurrent.ConcurrentHashMap;

public class LoginAttemptsManager {
    private static final ConcurrentHashMap<String, Integer> attemptsMap = new ConcurrentHashMap<>();

    public static int getAttempts(String email) {
        return attemptsMap.getOrDefault(email, 0);
    }

    public static void updateAttempts(String email, int attempts) {
        attemptsMap.put(email, attempts);
    }

    public static void resetAttempts(String email) {
        attemptsMap.remove(email);
    }
}
