package Client.Menus;

import Client.Constants;
import Client.GameClient;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Created by David on 2/8/2016.
 */

public class MainMenu extends GameMenu implements Constants {
    // The level shown in the background of the menu
    private MenuButton startButton;
    private MenuButton exitButton;
    private MenuButton settingsButton;
    private Image buttonUpImg;
    private Image buttonDownImg;

    public MainMenu() {
        //These have to be before initButtons()
        buttonUpImg = new ImageIcon(RESOURCE_PATH + "button_up.png").getImage();
        buttonDownImg = new ImageIcon(RESOURCE_PATH + "button_down.png").getImage();
        initButtons();
    }

    private void exitButtonReleased() {
        System.exit(0);
    }

    private void startButtonReleased() {
        GameClient.switchPanels(CHARACTER_SELECTION_MENU);
    }

    private void settingsButtonReleased() {
        GameClient.switchPanels(OPTIONS_MENU);
    }

    private void initButtons() {
        Font startButtonFont = GameClient.loadFont(IMPACT, 48);
        Font minorButtonFont = GameClient.loadFont(IMPACT, 30);

        int startButtonHeight = 130;
        int minorButtonWidth = 145;
        int minorButtonHeight = 70;
        int majorMinorVerticalMargin = 5;
        int buttonAreaWidth = 300;
        int buttonAreaHeight = startButtonHeight + minorButtonHeight + majorMinorVerticalMargin;

        int startButtonX = (Constants.WIDTH - buttonAreaWidth) / 2;
        int startButtonY = (Constants.HEIGHT - buttonAreaHeight) / 2;

        startButton = new MenuButton(buttonUpImg, buttonDownImg, "START GAME", startButtonFont, startButtonX + 10, startButtonY, buttonAreaWidth, startButtonHeight);

        int minorButtonY = startButton.y + startButton.height + majorMinorVerticalMargin;
        int gapBetweenMinorButtons = startButton.width - 2 * minorButtonWidth;
        settingsButton = new MenuButton(buttonUpImg, buttonDownImg, "SETTINGS", minorButtonFont, startButton.x, minorButtonY, minorButtonWidth, minorButtonHeight);

        int exitButtonX = settingsButton.x + settingsButton.width + gapBetweenMinorButtons;
        exitButton = new MenuButton(buttonUpImg, buttonDownImg, "EXIT GAME", minorButtonFont, exitButtonX, minorButtonY, minorButtonWidth, minorButtonHeight);

        addButton(startButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                startButtonReleased();
                return null;
            }
        });
        addButton(exitButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                exitButtonReleased();
                return null;
            }
        });
        addButton(settingsButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                settingsButtonReleased();
                return null;
            }
        });


    }
}
