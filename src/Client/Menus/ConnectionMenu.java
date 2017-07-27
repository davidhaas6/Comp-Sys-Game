package Client.Menus;

import Client.Constants;
import Client.GameClient;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Created by David on 3/1/2016.
 */
public class ConnectionMenu extends GameMenu implements Constants {
    private MenuButton backButton;
    private MenuButton connectButton;
    private MenuTextField ipField;
    private MenuLabel ipLabel;
    private MenuLabel serverNotFoundLabel;
    private MenuLabel duplicateUserNameLabel;
    private Image buttonUpImg;
    private Image buttonDownImg;
    private String IP;

    public ConnectionMenu() {
        setKeyPressedFunction(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                IP = ipField.getText();
                return null;
            }
        });

        IP = "";
        buttonUpImg = new ImageIcon(Constants.RESOURCE_PATH + "button_up.png").getImage();
        buttonDownImg = new ImageIcon(Constants.RESOURCE_PATH + "button_down.png").getImage();

        initComponents();
    }

    private void initComponents() {
        // Buttons
        int buttonWidth = 145;
        int buttonHeight = 70;
        int buttonGap = 60;
        int buttonX = (Constants.WIDTH - (buttonWidth * 2 + buttonGap)) / 2;
        int buttonY = (int) (Constants.HEIGHT * 0.66);
        final int connectButtonX = buttonX + buttonWidth + buttonGap;

        backButton = new MenuButton(buttonUpImg, buttonDownImg, "BACK", Constants.DEFAULT_SMALL_BUTTON_FONT, buttonX, buttonY, buttonWidth, buttonHeight);
        connectButton = new MenuButton(buttonUpImg, buttonDownImg, "CONNECT", Constants.DEFAULT_SMALL_BUTTON_FONT, connectButtonX, buttonY, buttonWidth, buttonHeight);

        addButton(backButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                backButtonReleased();
                return null;
            }
        });
        addButton(connectButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                connectButtonReleased();
                return null;
            }
        });


        // Text fields
        int fieldX = (Constants.WIDTH - (buttonWidth * 2 + buttonGap)) / 2;
        int fieldY = (int) (Constants.HEIGHT * 0.25);
        int fieldWidth = buttonWidth * 2 + buttonGap;
        int fieldHeight = 45;
        Font textFieldFont = GameClient.loadFont(Constants.RED_ALERT, 35);
        ipField = new MenuTextField(new Rectangle(fieldX, fieldY, fieldWidth, fieldHeight), textFieldFont, 20);
        addTextField(ipField);

        // Text labels
        Font textLabelFont = GameClient.loadFont(Constants.SILKSCREEN, 16);
        ipLabel = new MenuLabel(fieldX, fieldY - 5, "Server IP Address:", textLabelFont);
        Font serverNotFoundFont = GameClient.loadFont(Constants.SILKSCREEN, 36);
        serverNotFoundLabel = new MenuLabel(fieldX + (fieldWidth / 2) - getFontMetrics(serverNotFoundFont).stringWidth("SERVER NOT FOUND") / 2, Constants.HEIGHT / 2, "", serverNotFoundFont, Color.RED);
        duplicateUserNameLabel = new MenuLabel(fieldX + (fieldWidth / 2) - getFontMetrics(serverNotFoundFont).stringWidth("PLEASE CHANGE YOUR USERNAME") / 2, Constants.HEIGHT / 2 + getHeight(), "", serverNotFoundFont, Color.RED);
        addTextLabel(ipLabel);
        addTextLabel(serverNotFoundLabel);
        addTextLabel(duplicateUserNameLabel);
    }

    private void connectButtonReleased() {
        if (IP.length() > 0 && GameClient.connectToServer(IP)) {
            GameClient.switchPanels(Constants.LOBBY_MENU);
        } else
            serverNotFoundLabel.setText("SERVER NOT FOUND");
    }

    private void backButtonReleased() {
        GameClient.switchPanels(Constants.MODE_SELECTION_MENU);
        serverNotFoundLabel.setText("");
        duplicateUserNameLabel.setText("");
    }
}
