import java.io.IOException;

/**
 * Created by David on 11/16/2015.
 */

class test {
    public static void main(String[] args) {
        String JRE_PATH = "G:/jre/";
        String GAME_PATH = "C:\\Users\\David\\IdeaProjects\\Game prototype\\Game.jar"; // Resources folder and jar should be in here
        String command = JRE_PATH + "bin/javaw.exe -jar \"" + GAME_PATH + "\"";
        try {
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
class test {
    public static void main(String[] args) {
        String JRE_PATH = "G:/jre/"; // bin and lib folder for jre 7+ should be here
        String GAME_PATH = "C:\\Users\\David\\IdeaProjects\\Game prototype\\Game.jar"; // Resources folder and jar should be in here
        String command = JRE_PATH + "bin/javaw.exe -jar \" " + GAME_PATH + "\"";
        try {
            Process p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 */