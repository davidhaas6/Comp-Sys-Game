package Tools;

import Client.Menus.GameMenu;

import javax.swing.*;
import java.awt.*;

/**
 * Created by david on 2/8/16.
 */
public class MenuTester implements Constants_LevelEditor {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame();

        jFrame.setResizable(false);
        jFrame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT)); // YOU NEED THIS HERE FOR THE SIZE TO BE CORRECT (Sets size of game window, excluding the top windows bar)
        jFrame.pack();
        jFrame.setTitle("memerio: \t" + WIDTH + " x " + HEIGHT);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);

        GameMenu menu = new LauncherGUI();
        jFrame.add(menu);

        jFrame.setVisible(true);
    }
}
