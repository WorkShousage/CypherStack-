package utils;

import javax.sound.sampled.*;

public class SoundPlayer {

    /**
     * Plays a sound from the given file path.
     * 
     * @param soundFilePath The path to the sound file to be played.
     */
    public static void playSound(String soundFilePath) {
        try {
            // Load the audio file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new java.io.File(soundFilePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // Play the sound
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
