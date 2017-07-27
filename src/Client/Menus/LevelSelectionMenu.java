package Client.Menus;

import Client.Constants;
import Client.GameClient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by crystal on 4/11/16.
 */
public class LevelSelectionMenu extends GameMenu implements Constants {
    private ArrayList<MenuButton> levelButtons = new ArrayList<>();
    private Image buttonUpImg;
    private Image buttonDownImg;
    private MenuLabel levelSelection;
    private int numButtons;

    public LevelSelectionMenu() {
        buttonUpImg = new ImageIcon(RESOURCE_PATH + "button_up.png").getImage();
        buttonDownImg = new ImageIcon(RESOURCE_PATH + "button_down.png").getImage();
        numButtons = 12;
        initComponents();
    }

    private void initComponents() {
        int buttonWidth = 146 * SCALE;
        int buttonHeight = 92 * SCALE;
        for (int x = 1; x <= numButtons; x++) {
            levelButtons.add(new MenuButton(buttonUpImg, buttonDownImg, x + " ", DEFAULT_LARGE_BUTTON_FONT, (62 + 164 * ((x - 1) % 4)) * SCALE, (100 + 116 * ((x - 1) / 4)) * SCALE, buttonWidth, buttonHeight));
            final int levelNum = x;
            addButton(levelButtons.get(x - 1), new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    levelButtonReleased(levelNum);
                    return null;
                }
            });
        }

        // Text
        Font levelFont = GameClient.loadFont(RED_ALERT, 78 * SCALE);
        FontMetrics fm = getFontMetrics(levelFont);
        levelSelection = new MenuLabel(176 * SCALE, ((int)(30 / (SCALE*1.9)) + fm.getHeight() / 2) * SCALE, "Level Selection", levelFont, Color.white);
        addTextLabel(levelSelection);
    }

    private void levelButtonReleased(int x) {
        GameClient.sendLevelChange(x);
    }
}
