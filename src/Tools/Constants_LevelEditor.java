package Tools;
import Client.Constants;

import javax.swing.*;

/**
 * Created by David on 11/16/2015.
 */
interface Constants_LevelEditor {
    int BLOCK_SIZE = 32; // DO NOT CHANGE

    //Program variables
    int SCALE = 1; // If you want to change the scale, change this variable. DON'T CHANGE BLOCK SIZE.
    int SCALED_BLOCK_SIZE = BLOCK_SIZE * SCALE;

    String RESOURCE_PATH = "resources/";
    String IMG_FOLDER = RESOURCE_PATH + "scale " + SCALE + "/";
    String LEVELS_PATH = "resources/levels.ser";
    int WIDTH = 800;
    int HEIGHT = 600;

    ImageIcon[] BLOCK_IMAGES = Constants.BLOCK_IMAGES;
    int PASSTHROUGH_BLOCK = 8;
    int TRIGGER_BLOCK = Server.Constants.TRIGGER_BLOCK;
}
