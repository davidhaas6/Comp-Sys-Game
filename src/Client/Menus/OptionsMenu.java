package Client.Menus;

import Client.Constants;
import Client.GameClient;
import Client.SoundController;
import Tools.LauncherGUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * Created by david on 2/23/16.
 */
public class OptionsMenu extends GameMenu implements Constants {
    // The level shown in the background of the menu
    private MenuButton volumeButton;
    private MenuButton graphicsScaleButton;
    private MenuButton backButton;
    private Image buttonUpImg;
    private Image buttonDownImg;
    private int volumeIndex;
    private int scaleIndex;

    public OptionsMenu() {
        //These have to be before initButtons()
        buttonUpImg = new ImageIcon(RESOURCE_PATH + "button_up.png").getImage();
        buttonDownImg = new ImageIcon(RESOURCE_PATH + "button_down.png").getImage();
        volumeIndex = 2;
        scaleIndex = Arrays.binarySearch(SCALE_SIZES, SCALE);

        initButtons();
    }

    private void initButtons() {
        int buttonHeight = 90;
        int buttonWidth = 270;
        int backButtonWidth = 200;
        int buttonGap = 15;
        int buttonY = (Constants.HEIGHT - (int) (buttonHeight * 1.33)) / 2;
        int buttonX = (Constants.WIDTH - buttonGap - buttonWidth * 2) / 2;

        volumeButton = new MenuButton(buttonUpImg, buttonDownImg, "VOLUME: " + (volumeIndex + 1), DEFAULT_SMALL_BUTTON_FONT, buttonX, buttonY, buttonWidth, buttonHeight);
        graphicsScaleButton = new MenuButton(buttonUpImg, buttonDownImg, "GRAPHICS SCALE: " + SCALE_SIZES[scaleIndex], DEFAULT_SMALL_BUTTON_FONT, buttonX + buttonGap + buttonWidth, buttonY, buttonWidth, buttonHeight);
        backButton = new MenuButton(buttonUpImg, buttonDownImg, "BACK", DEFAULT_SMALL_BUTTON_FONT, ((buttonX + buttonWidth + buttonGap) - backButtonWidth / 2 - 10), (buttonY + buttonHeight + buttonGap), backButtonWidth, buttonHeight);

        addButton(volumeButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                volumeButtonReleased();
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
        addButton(graphicsScaleButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                graphicsScaleButtonReleased();
                return null;
            }
        });

        int bwidth = 120;
        int bheight = 60;
        int margins = 10;
        MenuButton levelEditorButton = new MenuButton(buttonUpImg, buttonDownImg, "Level Editor", GameClient.loadFont(IMPACT, 20), Constants.WIDTH - bwidth - margins, Constants.HEIGHT - bheight - margins, bwidth, bheight);
        addButton(levelEditorButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LauncherGUI.main(new String[]{});
                    }
                }).start();

                return null;
            }
        });
    }

    private void volumeButtonReleased() {
        volumeIndex++;
        if (volumeIndex >= VOLUME_LEVELS.length)
            volumeIndex = 0;

        SoundController.setGain(VOLUME_LEVELS[volumeIndex]);
        volumeButton.setText("VOLUME: " + (volumeIndex + 1));
    }

    private void graphicsScaleButtonReleased() {
        File settings = new File(RESOURCE_PATH + SETTINGS_FILE_NAME);
        scaleIndex++;
        if (scaleIndex >= SCALE_SIZES.length)
            scaleIndex = 0;

        try {
            Scanner scanner = new Scanner(settings);
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.contains("SCALE:"))
                    line = line.substring(0, line.indexOf(":") + 1) + SCALE_SIZES[scaleIndex];
                lines.add(line);
                //System.out.println(line);
            }
            Files.write(settings.toPath(), lines, Charset.forName("UTF-8"));
            graphicsScaleButton.setText("GRAPHICS SCALE: " + SCALE_SIZES[scaleIndex]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backButtonReleased() {
        GameClient.switchPanels(MAIN_MENU);
    }
}
