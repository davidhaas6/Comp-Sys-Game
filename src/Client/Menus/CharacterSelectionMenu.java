package Client.Menus;

import Client.Constants;
import Client.GameClient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by David on 3/26/2016.
 */
public class CharacterSelectionMenu extends GameMenu implements Constants {
    private MenuButton leftCharacterSelectButton;
    private MenuButton rightCharacterSelectButton;
    private MenuButton backButton;
    private MenuButton goButton;
    private MenuLabel usernameLabel;
    private MenuLabel characterLabel;
    private MenuLabel chooseYourCharacterLabel;
    private MenuTextField nameField;
    private int characterImageWidth;
    private int characterButtonWidth;
    private int characterButtonHeight;
    private int characterButtonX;
    private int characterButtonY;
    private Image buttonUpImg;
    private Image buttonDownImg;
    private String userName;
    private String[] characterChoices;
    private int characterIndex = 0;
    private Font characterFont;

    public CharacterSelectionMenu() {
        setKeyPressedFunction(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                userName = nameField.getText();
                //System.out.println(userName);
                if (userName.length() > 0)
                    GameClient.setUserName(userName);
                return null;
            }
        });

        buttonUpImg = new ImageIcon(RESOURCE_PATH + "button_up.png").getImage();
        buttonDownImg = new ImageIcon(RESOURCE_PATH + "button_down.png").getImage();

        characterChoices = new String[CHARACTER_IMAGES.size()];
        for (String character : CHARACTER_IMAGES.keySet()) {
            characterChoices[characterIndex] = character;
            characterIndex++;
        }
        characterIndex = indexOf(characterChoices, GameClient.characterName);

        initComponents();
    }

    private void initComponents() {

        characterImageWidth = 150;
        characterButtonWidth = 50;
        characterButtonHeight = 150;
        characterButtonX = (Constants.WIDTH - characterButtonWidth) / 2;
        characterButtonY = (int) ((Constants.HEIGHT - characterButtonHeight) / 2.33);

        leftCharacterSelectButton = new MenuButton(buttonUpImg, buttonDownImg, "<", DEFAULT_LARGE_BUTTON_FONT, characterButtonX - characterImageWidth, characterButtonY, characterButtonWidth, characterButtonHeight);
        rightCharacterSelectButton = new MenuButton(buttonUpImg, buttonDownImg, ">", DEFAULT_LARGE_BUTTON_FONT, characterButtonX + characterImageWidth, characterButtonY, characterButtonWidth, characterButtonHeight);

        int buttonWidth = 130;
        int buttonHeight = 70;
        int cornerMargins = 30;

        backButton = new MenuButton(buttonUpImg, buttonDownImg, "Back", DEFAULT_SMALL_BUTTON_FONT, cornerMargins, Constants.HEIGHT - cornerMargins - buttonHeight, buttonWidth, buttonHeight);
        goButton = new MenuButton(buttonUpImg, buttonDownImg, "Go", DEFAULT_SMALL_BUTTON_FONT, Constants.WIDTH - cornerMargins - buttonWidth, Constants.HEIGHT - cornerMargins - buttonHeight, buttonWidth, buttonHeight);

        addButton(leftCharacterSelectButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                leftCharacterSelectButtonReleased();
                return null;
            }
        });
        addButton(rightCharacterSelectButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                rightCharacterSelectButtonReleased();
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
        addButton(goButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                goButtonReleased();
                return null;
            }
        });

        // Text fields
        Font textFieldFont = GameClient.loadFont(SILKSCREEN, 24);
        int fieldVerticalGap = 80;
        int fieldWidth = (int) (characterImageWidth * 1.5);
        int fieldHeight = 45;
        int fieldX = characterButtonX + characterButtonWidth / 2 - (fieldWidth / 2);
        int fieldY = characterButtonY + characterButtonHeight + fieldVerticalGap;

        nameField = new MenuTextField(new Rectangle(fieldX, fieldY, fieldWidth, fieldHeight), textFieldFont, 20);
        addTextField(nameField);

        // Text labels
        Font unameFont = GameClient.loadFont(RED_ALERT, 35);
        usernameLabel = new MenuLabel(fieldX + (fieldWidth - getFontMetrics(unameFont).stringWidth("Player name")) / 2, fieldY - unameFont.getSize() / 5, "Player name", unameFont);
        Font chooseFont = GameClient.loadFont(SILKSCREEN, 50);
        chooseYourCharacterLabel = new MenuLabel((Constants.WIDTH - getFontMetrics(chooseFont).stringWidth("CHOOSE YOUR CHARACTER")) / 2, getFontMetrics(chooseFont).getHeight(), "CHOOSE YOUR CHARACTER", chooseFont);
        characterFont = GameClient.loadFont(SILKSCREEN, 40);
        characterLabel = new MenuLabel(characterButtonX + characterButtonWidth / 2 - (getFontMetrics(characterFont).stringWidth(characterChoices[characterIndex]) / 2), characterButtonY - 10, characterChoices[characterIndex], characterFont);

        addTextLabel(usernameLabel);
        addTextLabel(characterLabel);
        addTextLabel(chooseYourCharacterLabel);
    }

    private void leftCharacterSelectButtonReleased() {
        characterIndex--;
        if (characterIndex == -1)
            characterIndex = characterChoices.length - 1;
        changeCharacter();
    }

    private void rightCharacterSelectButtonReleased() {
        characterIndex++;
        if (characterIndex == characterChoices.length)
            characterIndex = 0;
        changeCharacter();
    }

    private void changeCharacter(){
        GameClient.setCharacter(characterChoices[characterIndex]);
        characterLabel.setOriginLoc(new Point(characterButtonX + characterButtonWidth / 2 - (getFontMetrics(characterFont).stringWidth(characterChoices[characterIndex]) / 2), characterButtonY - 10));
        characterLabel.setText(characterChoices[characterIndex]);
        ArrayList<MenuLabel> labels = new ArrayList<>();
        labels.add(characterLabel);
        labels.add(chooseYourCharacterLabel);
        labels.add(usernameLabel);
        setTextLabels(labels);
    }

    private void backButtonReleased() {
        GameClient.switchPanels(MAIN_MENU);
    }

    private void goButtonReleased() {
        GameClient.switchPanels(MODE_SELECTION_MENU);
    }

    @Override
    public void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        drawBackgroundMap(g2d);
        drawButtons(g2d);
        drawTextFields(g2d);

        g2d.drawImage(CHARACTER_IMAGES.get(characterChoices[characterIndex])[0],
                characterButtonX - characterButtonWidth - camX,
                characterButtonY - camY,
                characterImageWidth,
                characterButtonHeight,
                this);
        drawTextLabels(g2d);
    }

    private int indexOf(String[] arr, String key) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i].equals(key))
                return i;
        return -1;
    }
}
