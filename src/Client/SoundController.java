package Client;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by David on 3/3/2016.
 */
public class SoundController implements Constants {
    private static SoundController controller = new SoundController();
    private static String musicSound = RESOURCE_PATH + "ChibiNinja.wav";
    private static String jumpSound = RESOURCE_PATH + "Jump.wav";
    private static String victorySound = RESOURCE_PATH + "victory.wav";
    private static String twentySecondSound = RESOURCE_PATH + "20seconds.wav";
    private static String buttonClickSound = RESOURCE_PATH + "menu_select.wav";
    private static String crushSound = RESOURCE_PATH + "crush.wav";
    private static String dieSound = RESOURCE_PATH + "die.wav";
    private static String bossMusicSound = RESOURCE_PATH + "boss.wav";
    private static HashMap<SoundPlayer, String> soundPlayers = new HashMap<>();
    private static float gain = 0;
    private static boolean initiated = false;

    static Thread cleanerThread = new Thread() {
        public void run() {

        }
    };

    public static void init() {
        if (!initiated) {
            Timer cleanTimer = new Timer();
            /*cleanTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    HashMap<SoundPlayer, String> newSoundPlayers = new HashMap<SoundPlayer, String>();
                    for (SoundPlayer sp : soundPlayers.keySet())
                        try {
                            if (sp.clip.isOpen() || sp.clip.isActive() || sp.clip.isRunning()) {
                                soundPlayers.remove(sp);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }, 1, 300);*/
            // cleanerThread.start();
            initiated = true;
        }
    }

    public static void setGain(float newGain) {
        gain = newGain;
        changeAllVolumes();
    }

    private static void changeAllVolumes() {
        Set<SoundPlayer> keys = soundPlayers.keySet();
        for (SoundPlayer player : keys)
            player.setVolume(gain);
    }

    private static void setClipVolume(String clipPath, float gain) {
        Set<SoundPlayer> keys = soundPlayers.keySet();
        for (SoundPlayer player : keys) {
            if (player.path.equals(clipPath)) {
                player.setVolume(gain);
            }
        }
    }

    public static void playMusic() {
        loopMusic(DEFAULT_MUSIC_LOOP_COUNT);
    }

    private static void loopMusic(int loopCount) {
        SoundPlayer music = controller.new SoundPlayer(musicSound, loopCount);
        music.start();
        soundPlayers.put(music, musicSound);
    }

    public static void playVictorySound() {
        playSound(victorySound);
    }

    public static void play20SecondMusic() {
        playSound(twentySecondSound);
    }

    public static void playJumpSound() {
        playSound(jumpSound);
    }

    public static void playCrushSound() {
        playSound(crushSound);
    }

    public static void playDeathSound() {
        setClipVolume(musicSound, gain - 20);
        setClipVolume(twentySecondSound, gain - 20);

        playSound(dieSound);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setClipVolume(musicSound, gain);
                setClipVolume(twentySecondSound, gain);
            }
        }, 2000);

    }

    public static void playBossMusic() {
        playSound(bossMusicSound);
    }

    public static void playButtonClickSound() {
        playSound(buttonClickSound);
    }

    private static void playSound(String soundName) {
        SoundPlayer sound = controller.new SoundPlayer(soundName);
        sound.start();
        soundPlayers.put(sound, soundName);
    }

    public static void stopAllSounds() {
        Set<SoundPlayer> keys = soundPlayers.keySet();
        try {
            for (SoundPlayer player : keys)
                try {
                    player.interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        try {
            Set<SoundPlayer> keys = soundPlayers.keySet();
            for (SoundPlayer player : keys)
                if (player.path.equals(musicSound))
                    player.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop20SecondMusic() {
        Set<SoundPlayer> keys = soundPlayers.keySet();
        for (SoundPlayer player : keys)
            if (player.path.equals(twentySecondSound))
                player.interrupt();
    }

    public static void stopBossMusic() {
        Set<SoundPlayer> keys = soundPlayers.keySet();
        for (SoundPlayer player : keys)
            if (player.path.equals(bossMusicSound))
                player.interrupt();
    }

    private class SoundPlayer extends Thread {
        public Clip clip;
        String path;
        private AudioInputStream audioInputStream;
        private int loopCount;

        public SoundPlayer(String audioPath) {
            path = audioPath;
            loopCount = 0;
        }

        public SoundPlayer(String audioPath, int loopCount) {
            path = audioPath;
            this.loopCount = loopCount;
        }

        public void run() {
            try {
                audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
                AudioFormat format = audioInputStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);

                clip = (Clip) AudioSystem.getLine(info);
                clip.open(audioInputStream);
                clip.start();
                setVolume(gain);


                if (loopCount > 0)
                    clip.loop(loopCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            try {
                clip.stop();
                clip.close();
                audioInputStream.close();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        public void setVolume(float decibels) {
            //TODO Find out why there's sometimes a null pointer exception here
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(decibels);
            } catch (Exception e) {
                //System.err.println(e.getMessage());
            }
        }
    }
}
