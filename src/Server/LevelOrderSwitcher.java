package Server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by David on 6/14/2016.
 */
public class LevelOrderSwitcher implements Constants {
    public static void main(String[] args) {
        try {
            int[][][] levels = Server.retrieveLevels();
            int[][][] newLevels = new int[levels.length][][];
            int lvlNum;
            Scanner scan = new Scanner(System.in);
            System.out.println("Hello! You are about to re-order your levels!");
            ArrayList<Integer> usedNumbers = new ArrayList<>();
            for (int i = 0; i < levels.length; i++) {
                System.out.println("Type the current level number of your desired level " + (i + 1));
                lvlNum = Integer.parseInt(scan.next());
                if (!usedNumbers.contains(lvlNum)) {
                    newLevels[i] = levels[lvlNum - 1];
                    usedNumbers.add(lvlNum);
                } else {
                    while (usedNumbers.contains(lvlNum)) {
                        System.out.println(usedNumbers);
                        System.out.println("You already used this level... please enter another number");
                        System.out.println("Type the current level number of your desired level " + (i + 1));
                        lvlNum = Integer.parseInt(scan.next());
                        if (!usedNumbers.contains(lvlNum)) {
                            newLevels[i] = levels[lvlNum - 1];
                            usedNumbers.add(lvlNum);
                        }
                    }
                }
                System.out.println(usedNumbers);
                System.out.println();
            }

                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEVELS_PATH));
                oos.writeObject(newLevels);
                oos.close();

                ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream("resources/levels_backup.ser"));
                oos2.writeObject(levels);
                oos2.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
