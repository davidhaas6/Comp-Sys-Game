package Client.Menus;

import Client.Constants;
import Client.GameClient;
import Client.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

/**
 * Created by David on 4/16/2016.
 */
public class LobbyMenu extends GameMenu implements Constants {
    private ArrayList<MenuLabel> playerLabels;
    private MenuButton disconnectButton;
    private MenuButton levelSelectionButton;
    private ArrayList<Player> players;
    private MenuLabel titleLabel;
    private Image buttonUpImg;
    private Image buttonDownImg;

    public LobbyMenu() {
        buttonUpImg = new ImageIcon(RESOURCE_PATH + "button_up.png").getImage();
        buttonDownImg = new ImageIcon(RESOURCE_PATH + "button_down.png").getImage();
        players = new ArrayList<>();
        initComponents();
        new Timer().scheduleAtFixedRate(new UpdatePlayerLabelsTask(), 500, 500);
    }

    private void initComponents() {
        // Buttons
        int buttonWidth = 130;
        int buttonHeight = 70;
        int cornerMargins = 30;

        disconnectButton = new MenuButton(buttonUpImg, buttonDownImg, "Disconnect", DEFAULT_SMALL_BUTTON_FONT, cornerMargins, Constants.HEIGHT - cornerMargins - buttonHeight, buttonWidth, buttonHeight);
        levelSelectionButton = new MenuButton(buttonUpImg, buttonDownImg, "Select a Level", DEFAULT_SMALL_BUTTON_FONT, Constants.WIDTH - cornerMargins - buttonWidth, Constants.HEIGHT - cornerMargins - buttonHeight, buttonWidth, buttonHeight);
        addButton(disconnectButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                disconnectButtonPressed();
                return null;
            }
        });

        // TextLabels
        Font titleFont = GameClient.loadFont(RED_ALERT, 78);
        FontMetrics fm = getFontMetrics(titleFont);
        titleLabel = new MenuLabel((Constants.WIDTH - fm.stringWidth("LOBBY")) / 2, fm.getHeight() / 2 + 20, "LOBBY", titleFont);
        addTextLabel(titleLabel);

        Font playerFont = GameClient.loadFont(RED_ALERT, 36);
        fm = getFontMetrics(playerFont);
        for (int i = 0; i < players.size(); i++)
            playerLabels.add(new MenuLabel((Constants.WIDTH - fm.stringWidth(players.get(i).getUserName())) / 2, 100 + getHeight() * i, players.get(i).getUserName(), playerFont));
        setTextLabels(playerLabels);
    }

    private void disconnectButtonPressed() {
        GameClient.disconnect();
        GameClient.switchPanels(MODE_SELECTION_MENU);
    }

    private void levelSelectionButtonPressed() {
        GameClient.switchPanels(LEVEL_SELECTION_MENU);
    }

    public void setPlayers() {
        try {
            players = GameClient.serverSnapshot.getPlayers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLobbyPrivilege() {
        if (GameClient.isLobbyHost)
            addButton(levelSelectionButton, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    levelSelectionButtonPressed();
                    return null;
                }
            });
    }

    private class UpdatePlayerLabelsTask extends TimerTask {
        public void run() {
            if (GameClient.serverSnapshot != null)
                players = GameClient.serverSnapshot.getPlayers();

            ArrayList<MenuLabel> tempPlayerLabels = new ArrayList<>();
            Font playerFont = GameClient.loadFont(RED_ALERT, 36);
            FontMetrics fm = getFontMetrics(playerFont);
            for (int i = 0; i < players.size(); i++)
                tempPlayerLabels.add(new MenuLabel((Constants.WIDTH - fm.stringWidth(players.get(i).getUserName())) / 2, 100 + fm.getHeight() * i, players.get(i).getUserName(), playerFont));
            playerLabels = tempPlayerLabels;
            setTextLabels(playerLabels);
            addTextLabel(titleLabel);
        }
    }
}
