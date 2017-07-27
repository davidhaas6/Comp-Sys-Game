package Client;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by David on 11/16/2015.
 */
public interface Constants {
    int BLOCK_SIZE = Server.Constants.BLOCK_SIZE; // DO NOT CHANGE... The block size that the game receives its logic from the server in
    short DEFAULT_PORT = 7555;

    /* Program variables */
    int SCALE = GameClient.getScale();
    int[] SCALE_SIZES = {1, 2};
    int SCALED_BLOCK_SIZE = BLOCK_SIZE * SCALE;
    int TARGET_FPS = 45; //Number of different frames drawn per second
    String RESOURCE_PATH = "resources/"; // Path for general resources
    String IMG_FOLDER = RESOURCE_PATH + "scale " + SCALE + "/"; // Path for the picture files specific to the scale
    String FONT_FOLDER = "resources/fonts/";
    String SETTINGS_FILE_NAME = "settings.meme";

    /* Viewport size */
    int WIDTH = SCALED_BLOCK_SIZE * 12;
    int HEIGHT = SCALED_BLOCK_SIZE * 7;

    /* Side-scrolling camera settings */
    int OFFSET_MIN_X = 0;
    int OFFSET_MIN_Y = 0;

    /* Panel Keywords */
    String MAIN_MENU = "main menu";
    String OPTIONS_MENU = "options";
    String CHARACTER_SELECTION_MENU = "character selection";
    String MODE_SELECTION_MENU = "mode selection";
    String CONNECTION_MENU = "connection menu";
    String GAME_UI = "game ui";
    String LEVEL_SELECTION_MENU = "level selection";
    String LOBBY_MENU = "lobby menu";
    String ESCAPE_MENU = "escape menu";
    String END_GAME_MENU = "end game menu";

    /* Fonts */
    String SILKSCREEN = "slkscr.ttf";
    String IMPACT = "impact.ttf";
    String COMIC_SANS = "comic.ttf";
    String ROCKWELL_BOLD = "rockb.ttf";
    String SEGOE_UI_BOLD = "segoeuib.ttf";
    String RED_ALERT = "red_alert.ttf";
    String SEASIDE = "seaside.ttf";
    String GTA = "pricedown.ttf";
    Font DEFAULT_LARGE_BUTTON_FONT = GameClient.loadFont(IMPACT, 40);
    Font DEFAULT_SMALL_BUTTON_FONT = GameClient.loadFont(IMPACT, 24);

    /* Sprite Images */
    double ANIMATION_CYCLE_LENGTH = .05; // Animation length in seconds
    int ANIMATON_FRAME_LENGTH = (int) (TARGET_FPS * ANIMATION_CYCLE_LENGTH);
    Image[] PEPE_ANIMATIONS = {
            new ImageIcon(IMG_FOLDER + "pepe/pepe.png").getImage(),
            new ImageIcon(IMG_FOLDER + "pepe/pepe_right_1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "pepe/pepe_right_2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "pepe/pepe_right_1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "pepe/pepe.png").getImage(),
            new ImageIcon(IMG_FOLDER + "pepe/pepe_left_1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "pepe/pepe_left_2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "pepe/pepe_left_1.png").getImage()
    };
    Image[] MARIO_ANIMATIONS = {
            new ImageIcon(IMG_FOLDER + "mario/mario.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario3.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario3.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mario/mario2.png").getImage(),

    };
    Image[] TRUMP_ANIMATIONS = {
            new ImageIcon(IMG_FOLDER + "trump/trump.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump3.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump3.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "trump/trump.png").getImage(),

    };

    Image[] MUSHROOMBA_ANIMATIONS = {
            new ImageIcon(IMG_FOLDER + "mushroom_character/Mushroom_Character.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mushroom_character/Mushroom_Character_right_1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mushroom_character/Mushroom_Character_right_2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mushroom_character/Mushroom_Character_right_1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mushroom_character/Mushroom_Character.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mushroom_character/Mushroom_Character_left_1.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mushroom_character/Mushroom_Character_left_2.png").getImage(),
            new ImageIcon(IMG_FOLDER + "mushroom_character/Mushroom_Character_left_1.png").getImage()
    };

    HashMap<String, Image[]> CHARACTER_IMAGES = new HashMap<String, Image[]>() {{
        put("mario", MARIO_ANIMATIONS);
        put("pepe", PEPE_ANIMATIONS);
        put("trump", TRUMP_ANIMATIONS);
        put("Mushroomba", MUSHROOMBA_ANIMATIONS);
    }};

    Image[] MUSHROOM_AI_ANIMATIONS = {
            new ImageIcon(IMG_FOLDER + "mushroom_ai.png").getImage()
    };

    /* Sound */
    int DEFAULT_MUSIC_LOOP_COUNT = 3;
    float[] VOLUME_LEVELS = {-80, -12, -6, 0, 3, 6};
    String TWENTY_SECONDS = "twenty_seconds";
    String JUMP = "jump";
    String VICTORY = "victory";
    String MUSIC = "music";
    String BOSS_MUSIC = "boss music";
    String CRUSH = "crush";
    String DIE = "die";


    /* Strings */
    String DEATH_TEXT = "WASTED";


    /* Misc */
    ImageIcon[] BLOCK_IMAGES = { // Images for each block
            new ImageIcon(IMG_FOLDER + "air.png"), // 0
            new ImageIcon(IMG_FOLDER + "dirt_grass.png"), // 1
            new ImageIcon(IMG_FOLDER + "goal.png"), // 2
            new ImageIcon(IMG_FOLDER + "sand.png"), // 3
            new ImageIcon(IMG_FOLDER + "mushroom.png"), // 4
            new ImageIcon(IMG_FOLDER + "cloud.png"), // 5
            new ImageIcon(IMG_FOLDER + "spikes.png"), // 6
            new ImageIcon(IMG_FOLDER + "gas.png"), // 7
            new ImageIcon(IMG_FOLDER + "passthrough_dirt_grass.png"), // 8
            new ImageIcon(IMG_FOLDER + "brick_grey.png"), // 9
            new ImageIcon(IMG_FOLDER + "greystone.png"), // 10
            new ImageIcon(IMG_FOLDER + "lava.png"), // 11
            new ImageIcon(IMG_FOLDER + "trunk_bottom.png"), // 12
            new ImageIcon(IMG_FOLDER + "trunk_mid.png"), // 13
            new ImageIcon(IMG_FOLDER + "trunk_side.png"), // 14
            new ImageIcon(IMG_FOLDER + "leaves.png"), // 15
            new ImageIcon(IMG_FOLDER + "spikes_reverse.png"), // 16
            new ImageIcon(IMG_FOLDER + "air.png") // 17 - trigger block
    };
    int[][] BACKGROUND_LEVEL = Server.Server.levels[0];

}
