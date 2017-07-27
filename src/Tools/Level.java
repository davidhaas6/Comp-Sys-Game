package Tools;

import javax.swing.*;
import java.awt.*;

/**
 * Created by David on 11/16/2015.
 */
class Level implements Constants_LevelEditor {
    private int[][] terrain;
    public int height;
    public int width;

    public Level(int[][] terrain) {
        this.terrain = terrain;
        height = terrain.length;
        width = terrain[0].length;
        printWorld();
    }

    public int[][] getTerrain() {
        return terrain;
    }

    public Image getTerrainImage(int row, int column) {
        int blockNumber = terrain[row][column];
        if (row > 0)
            if (blockNumber == 1) { // If it's dirt_grass
                if (terrain[row - 1][column] != 0)
                    return new ImageIcon(IMG_FOLDER + "dirt.png").getImage();
            } else if (blockNumber == 8) // If it's empty grass (you can move through it)
                if (terrain[row - 1][column] != 0)
                    return new ImageIcon(IMG_FOLDER + "passthrough_dirt.png").getImage();
        return BLOCK_IMAGES[blockNumber].getImage();
    }

    public void setTerrain(int[][] terrain) {
        this.terrain = terrain;
    }

    public void printWorld() {
        for (int r = 0; r < terrain.length; r++) {
            for (int c = 0; c < terrain[0].length; c++)
                if (terrain[r][c] == 0)
                    System.out.print(" \t");
                else
                    System.out.print(terrain[r][c] + "\t");
            System.out.println();
        }
    }
}
