package Server;

import java.awt.*;

/**
 * Created by David on 11/16/2015.
 */
public interface Constants {
    /* Program variables */
    int BLOCK_SIZE = 64; // The block size in pixels that the server computes all of it's logic in

    /* Multiplayer variables */
    int TICK_RATE = 1000 / 60; // Number of times the gamestate is updated per second
    int MESSAGE_SEND_RATE = 1000 / 90; // Number of times per seconds a snapshot is sent to the client

    /* Gameplay variables */
    int HORIZONTAL_MOVEMENT_SPEED = 6;
    int AIR_STRAFE_SPEED = 4;
    double ACCELERATION_PERCENTAGE = .02; // n.b. A value of .01 is equivalent to 1%
    double DECELERATION_PERCENTAGE = .03;
    int JUMP_SPEED = 7; // Pixels per tick
    int EXTENDED_JUMP_SPEED = 4;
    int APEX_SPEED = 4;
    int GRAVITY_SPEED = 5;
    int MAXIMUM_JUMP_HEIGHT = (int) (BLOCK_SIZE * 3) + 20;
    int MINIMUM_JUMP_HEIGHT = (int) (BLOCK_SIZE * 2) + 20;
    Point DEFAULT_SPAWN_POINT = new Point(2, 2); // Where the player respawns when he dies
    int RESPAWN_TIME = 1500; // Respawn time in milliseconds
    int RESPAWN_NO_COLLIDE_TIME = 3; // In seconds
    int LEVEL_TIME = 100;
    int GAME_START_DELAY = 1500; // In milliseconds


    /* Level variables */
    int[] SOLID_BLOCKS = {1, 3, 9, 10, 12, 13, 14, 15}; // Blocks that can't be walked through
    int[] DEADLY_BLOCKS = {6, 7, 11, 16}; // Blocks that will kill you
    int LEVEL_WIN_BLOCK = 2; // The block that you win by reaching
    Color DEFAULT_BACKGROUND = new Color(169, 219, 219);
    Color DARK_BACKGROUND = new Color(73, 88, 93);
    int[] darkBgLevels = {4, 8, 12};
    int TRIGGER_BLOCK = 17;
    String LEVELS_PATH = "resources/levels.ser";

    /* Character variables */
    Dimension MARIO_DIMENSIONS = new Dimension(24, 32); //Hitboxes for Mario character
    Dimension PEPE_DIMENSIONS = new Dimension(20, 24);
    Dimension TRUMP_DIMENSIONS = new Dimension(23, 28);
    Dimension MUSHROOM_DIMENSIONS = new Dimension(26, 30);

    /* Mushroom variables */
    Dimension AI_MUSHROOM_DIMENSIONS = new Dimension(20, 20);
    int MUSHROOM_SPAWN_BLOCK = 4;
    int MUSHROOM_HORIZONTAL_MOVEMENT_SPEED = 2;
    int MUSHROOM_VERTICAL_MOVEMENT_SPEED = 2;
    int MUSHROOM_MOVEMENT_DISTANCE = BLOCK_SIZE * 4; // The total distance it traverses
    int MUSHROOM_JUMP_HEIGHT = (int) (BLOCK_SIZE * .5);

    /* Sound names */
    String TWENTY_SECONDS = Client.Constants.TWENTY_SECONDS;
    String JUMP = Client.Constants.JUMP;
    String VICTORY = Client.Constants.VICTORY;
    String MUSIC = Client.Constants.MUSIC;
    String CRUSH = Client.Constants.CRUSH;
    String DIE = Client.Constants.DIE;
    String BOSS_MUSIC = Client.Constants.BOSS_MUSIC;

    /* Other */
    String[] validKeyInputs = {"w", "a", "d"};
}
