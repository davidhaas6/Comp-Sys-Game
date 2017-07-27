package Client.Menus;

import Client.Constants;
import Client.GameClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.Callable;

/**
 * Created by david on 4/26/16.
 */
public class EscapeMenu extends GameMenu {
    private Image buttonUpImg;
    private Image buttonDownImg;
    MenuButton levelSelectionButton;

    public EscapeMenu() {
        buttonUpImg = new ImageIcon(RESOURCE_PATH + "button_up.png").getImage();
        buttonDownImg = new ImageIcon(RESOURCE_PATH + "button_down.png").getImage();
        init();
    }

    private void init() {
        int bwidth = 300;
        int bheight = 120;
        int gap = 40;
        int leftX = (Constants.WIDTH - (bwidth * 2 + gap)) / 2;
        int y = (Constants.HEIGHT - bheight - gap) / 2;
        MenuButton disconnectButton = new MenuButton(buttonUpImg, buttonDownImg, "Disconnect", DEFAULT_LARGE_BUTTON_FONT, leftX, y, bwidth, bheight);
        MenuButton resumeButton = new MenuButton(buttonUpImg, buttonDownImg, "Resume Game", DEFAULT_LARGE_BUTTON_FONT, leftX + bwidth + gap, y, bwidth, bheight);
        levelSelectionButton = new MenuButton(buttonUpImg, buttonDownImg, "Change Level", DEFAULT_LARGE_BUTTON_FONT,(leftX + bwidth + gap/2)-bwidth/2, y + bheight + gap, bwidth, bheight);

        addButton(disconnectButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                GameClient.switchPanels(MAIN_MENU);
                GameClient.disconnect();
                return null;
            }
        });

        addButton(resumeButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                GameClient.switchPanels(GAME_UI);
                return null;
            }
        });
    }

    public void updateLeaderStatus(){
        if(GameClient.isLobbyHost) {
            addButton(levelSelectionButton, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    GameClient.switchPanels(LEVEL_SELECTION_MENU);
                    return null;
                }
            });
        }
    }

    @Override
    public void john_roth_is_better_than_david_haas(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            GameClient.switchPanels(GAME_UI);
    }
}
