package Client.Menus;


import Client.Constants;
import Client.GameClient;
import Client.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Created by crystal on 4/26/16.
 */
public class EndGameMenu extends GameMenu implements Constants {
    private MenuButton replay;
    private MenuButton levelSelection;
    private MenuButton mainMenu;
    private HashMap<Player, Integer> players;
    private ArrayList<MenuLabel> list;
    private Image buttonUpImg;
    private Image buttonDownImg;

    public EndGameMenu() {
        buttonUpImg = new ImageIcon(RESOURCE_PATH + "button_up.png").getImage();
        buttonDownImg = new ImageIcon(RESOURCE_PATH + "button_down.png").getImage();
        list = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        replay = new MenuButton(buttonUpImg, buttonDownImg, "Replay", DEFAULT_LARGE_BUTTON_FONT, 493*SCALE, 50*SCALE, 150*SCALE, 100*SCALE);
        levelSelection = new MenuButton(buttonUpImg, buttonDownImg, "Level Selection", DEFAULT_LARGE_BUTTON_FONT, 428*SCALE, 160*SCALE, 280*SCALE, 100*SCALE);
        mainMenu = new MenuButton(buttonUpImg, buttonDownImg, "Main Menu", DEFAULT_LARGE_BUTTON_FONT, 458*SCALE, 270*SCALE, 220*SCALE, 100*SCALE);


        addButton(mainMenu, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                mainMenuPressed();
                return null;
            }
        });


    }

    // Displays the replay and level selection button only to the lobby host... Needs to be called when connected to the server
    public void setLobbyPrivilege() {
        if (GameClient.isLobbyHost) {
            addButton(replay, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    replayPressed();
                    return null;
                }
            });

            addButton(levelSelection, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    levelSelectionPressed();
                    return null;
                }


            });
        }
    }

    public void setPlayers() {
        setTextLabels(new ArrayList<MenuLabel>());
        list = new ArrayList<>();
        Font levelFont = GameClient.loadFont(RED_ALERT, 78 * SCALE);
        try {
            players = GameClient.serverSnapshot.getPlayerWinStatus();
            int count = 1;
            for (Player player : players.keySet()) {
                for (Player p : players.keySet()) {
                    String y;
                    if (players.get(p).equals(count)) {
                        y = count + ". " + p.getUserName();
                        list.add(new MenuLabel(50, count * 100, y, levelFont));
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            addTextLabel(list.get(i));
        }
    }

    private void replayPressed() {
        if (GameClient.isLobbyHost)
            GameClient.replay();
    }

    private void levelSelectionPressed() {
        if (GameClient.isLobbyHost)
            GameClient.switchPanels(LEVEL_SELECTION_MENU);
    }

    private void mainMenuPressed() {
        GameClient.disconnect();
        GameClient.switchPanels(MAIN_MENU);
    }
}
