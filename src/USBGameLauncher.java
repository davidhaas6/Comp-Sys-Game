import java.io.IOException;

/**
 * Created by David on 6/1/2016.
 */
public class USBGameLauncher {
    public static void main(String[] args) {
        String ROOT_PATH = USBGameLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1);
        String JRE_PATH = ROOT_PATH + "jre/";
        String GAME_PATH = ROOT_PATH + "Game.jar"; // Resources folder and jar should be in here
        String command = JRE_PATH + "bin/javaw.exe -jar \"" + GAME_PATH + "\"";
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

