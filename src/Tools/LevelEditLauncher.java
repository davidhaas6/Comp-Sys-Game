package Tools;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

/**
 * Created by David on 1/27/2016.
 */
class LevelEditLauncher implements Constants_LevelEditor{
    static JFrame jFrame = new JFrame();
    public static void main(String[] args){
        /*
        int levelWidth;
        int levelHeight;
        int levelToEdit;
        LevelEditor editor = null;
        String temp;

        Scanner input = new Scanner(System.in);
        System.out.println("Would you like to make a new(N) level  or edit(E) an existing one?");
        System.out.print("(N) / (E): ");
        temp = input.next();
        if(temp.toLowerCase().equals("n")){
            try {
                System.out.print("Enter the level's width: ");
                levelWidth = Integer.parseInt(input.next());
                System.out.print("Enter the level's height: ");
                levelHeight = Integer.parseInt(input.next());
                editor = new LevelEditor(new Dimension(levelWidth, levelHeight));
            } catch (NumberFormatException e) {
                System.err.println("Not an integer.");
                System.exit(0);
            }
        }
        else{
            try {
                System.out.print("\nEnter the level number which you would like to edit: ");
                levelToEdit = Integer.parseInt(input.next());
                editor = new LevelEditor(levelToEdit);
            } catch (NumberFormatException e) {
                System.err.println("Not an integer.");
                System.exit(0);
            }
        }*/

        jFrame.setLayout(new BorderLayout());
        jFrame.setResizable(true);
        jFrame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT)); // YOU NEED THIS HERE FOR THE SIZE TO BE CORRECT (Sets size of game window, excluding the top windows bar)
        jFrame.pack();
        jFrame.setTitle("Mario Game Level Editor");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.getContentPane().add(new LauncherGUI());
        //jFrame.add("editor",)
        jFrame.setVisible(true);
        jFrame.toFront();
    }
}
