import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SoundManager {
    private static final String[] AUDIO_FOLDERS = {"resources/musics", "../resources/musics", "musics", "music"};
    private static Clip backgroundClip;

    public static void playRotate() {
        play("rotation.wav");
    }

    public static void playDrop() {
        play("touch floor.wav");
    }

    public static void playClear() {
        play("delete line.wav");
    }

    public static void playExplosion() {
        play("explosion.wav");
    }

    public static void playGameOver() {
        play("gameover.wav");
    }

    public static void playBackground() {
        stopBackground();
        new Thread(() -> {
            try {
                File soundFile = findFile("white_labyrinth.wav");
                if (soundFile == null) {
                    return;
                }
                try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile)) {
                    backgroundClip = AudioSystem.getClip();
                    backgroundClip.open(audioInputStream);
                    backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
                    backgroundClip.start();
                }
            } catch (Exception ignored) {
            }
        }).start();
    }

    public static void stopBackground() {
        if (backgroundClip != null && backgroundClip.isOpen()) {
            backgroundClip.stop();
            backgroundClip.close();
            backgroundClip = null;
        }
    }

    private static void play(String fileName) {
        new Thread(() -> {
            try {
                File soundFile = findFile(fileName);
                if (soundFile == null) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.addLineListener(event -> {
                        if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                    clip.start();
                }
            } catch (Exception ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        }).start();
    }

    private static File findFile(String fileName) {
        for (String folder : AUDIO_FOLDERS) {
            File file = new File(folder, fileName);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
}
