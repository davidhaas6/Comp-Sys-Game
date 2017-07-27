package Client.Menus;

import Client.Constants;
import Client.GameClient;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Created by David on 3/1/2016.
 */
public class ModeSelectionMenu extends GameMenu implements Constants {
    private MenuButton singleplayerButton;
    private MenuButton multiplayerButton;
    private MenuButton backButton;
    private Image buttonUpImg;
    private Image buttonDownImg;

    public ModeSelectionMenu() {
        super();

        buttonUpImg = new ImageIcon(RESOURCE_PATH + "button_up.png").getImage();
        buttonDownImg = new ImageIcon(RESOURCE_PATH + "button_down.png").getImage();

        initComponents();
    }

    private void initComponents() {
        int buttonWidth = 300;
        int buttonHeight = 130;
        int buttonGap = 50;
        int buttonX = (Constants.WIDTH - (buttonWidth * 2 + buttonGap)) / 2;
        int buttonY = (Constants.HEIGHT / 2) - buttonHeight / 2;
        int connectButtonX = buttonX + buttonWidth + buttonGap;

        singleplayerButton = new MenuButton(buttonUpImg, buttonDownImg, "SINGLE-PLAYER", DEFAULT_LARGE_BUTTON_FONT, buttonX, buttonY, buttonWidth, buttonHeight);
        multiplayerButton = new MenuButton(buttonUpImg, buttonDownImg, "MULTIPLAYER", DEFAULT_LARGE_BUTTON_FONT, connectButtonX, buttonY, buttonWidth, buttonHeight);
        backButton = new MenuButton(buttonUpImg, buttonDownImg, "BACK", DEFAULT_SMALL_BUTTON_FONT, (buttonX + buttonWidth + (buttonGap - 180) / 2), (buttonY + buttonHeight + 40), 180, 80);

        addButton(singleplayerButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                singleplayerButtonReleased();
                return null;
            }
        });
        addButton(multiplayerButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                multiplayerButtonReleased();
                return null;
            }
        });
        addButton(backButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                backButtonReleased();
                return null;
            }
        });
    }

    private void multiplayerButtonReleased() {
        GameClient.switchPanels(CONNECTION_MENU);
    }

    private void singleplayerButtonReleased() {
        GameClient.startLocalServer();
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GameClient.connectToServer("127.0.0.1");
        GameClient.switchPanels(LEVEL_SELECTION_MENU);
    }

    private void backButtonReleased() {
        GameClient.switchPanels(CHARACTER_SELECTION_MENU);
    }
}
