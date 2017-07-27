package Client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by David on 11/16/2015.
 */
public class Level implements Constants {
    public int height;
    public int width;
    private int[][] terrain;

    public Level(int[][] terrain) {
        this.terrain = terrain;
        height = terrain.length;
        width = terrain[0].length;
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
}
