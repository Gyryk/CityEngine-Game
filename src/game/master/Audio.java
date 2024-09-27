package game.master;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class Audio {
    public static void playSound(String soundPath) {
        try {
            File soundFile = new File(soundPath);
            if (soundFile.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(20f * (float) Math.log10(0.6f));
            } else {
                System.out.println("Can't find file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Clip loopMusic(String musicPath, float volume) {
        try {
            File musicFile = new File(musicPath);
            if (musicFile.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(20f * (float) Math.log10(volume));
                return clip;
            } else {
                System.out.println("Can't find file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Clip stopMusic(Clip clip) {
        clip.stop();
        return null;
    }
}
