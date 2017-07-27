package Tools;

import Client.Constants;
import Client.GameClient;
import Client.Menus.GameMenu;
import Client.Menus.MenuButton;
import Client.Menus.MenuLabel;
import Client.Menus.MenuTextField;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Created by David on 6/13/2016.
 */
public class LauncherGUI extends GameMenu implements Constants_LevelEditor {
    private Image buttonUpImg;
    private Image buttonDownImg;
    int width = Constants_LevelEditor.WIDTH;
    int height = Constants_LevelEditor.HEIGHT;
    int[][] BACKGROUND_LEVEL;
    MenuTextField levelWidth, levelHeight, levelNumber;

    static JFrame jFrame = new JFrame();
    static LauncherGUI GUI = new LauncherGUI();

    public static void main(String[] args) {
        jFrame.setLayout(new BorderLayout());
        jFrame.setResizable(false);
        jFrame.getContentPane().setPreferredSize(new Dimension(Constants_LevelEditor.WIDTH, Constants_LevelEditor.HEIGHT)); // YOU NEED THIS HERE FOR THE SIZE TO BE CORRECT (Sets size of game window, excluding the top windows bar)
        jFrame.pack();
        jFrame.setTitle("Level Editor Launcher");
        jFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.getContentPane().add(GUI);
        //jFrame.add("editor",)
        jFrame.setVisible(true);
        jFrame.toFront();
    }

    public LauncherGUI() {
        buttonUpImg = new ImageIcon(GameMenu.RESOURCE_PATH + "button_up.png").getImage();
        buttonDownImg = new ImageIcon(GameMenu.RESOURCE_PATH + "button_down.png").getImage();
        BACKGROUND_LEVEL = Server.Server.levels[(int) (Math.random() * Server.Server.levels.length)];
        init();
    }

    private void init() {
        MenuLabel widthLabel, heightLabel, lvlNumberLabel, createLevelLabel, editLevelLabel, headerLabel;

        MenuButton toEditorButton;

        // Labels
        Font f = GameClient.loadFont(SILKSCREEN, 64);
        FontMetrics fm = getFontMetrics(f);
        headerLabel = new MenuLabel((width - fm.stringWidth("Level Editor")) / 2, fm.getHeight() + 10, "Level Editor", f);

        f = GameClient.loadFont(SILKSCREEN, 30);
        fm = getFontMetrics(f);
        int sectionTitleYPos = headerLabel.getOriginLoc().y + fm.getHeight() + 50;
        createLevelLabel = new MenuLabel((width / 2 - fm.stringWidth("Create new level")) / 2, sectionTitleYPos, "Create new level", f);
        editLevelLabel = new MenuLabel(width / 2 + (width / 2 - fm.stringWidth("Edit existing level")) / 2, sectionTitleYPos, "Edit existing level", f);

        f = GameClient.loadFont(SILKSCREEN, 22);
        fm = getFontMetrics(f);
        int variableYValue = sectionTitleYPos + 60;
        int margin = 30;
        lvlNumberLabel = new MenuLabel(width / 2 + margin, variableYValue, "Level number:", f);
        widthLabel = new MenuLabel(margin, variableYValue, "Level width:", f);
        heightLabel = new MenuLabel(margin, variableYValue + 80, "Level height:", f);

        addTextLabel(headerLabel);
        addTextLabel(createLevelLabel);
        addTextLabel(editLevelLabel);
        addTextLabel(lvlNumberLabel);
        addTextLabel(heightLabel);
        addTextLabel(widthLabel);

        // Text Fields
        int rectWidth = 80;
        int rectHeight = 35;
        int spaceBetweenText = 10;
        levelNumber = new MenuTextField(new Rectangle(width / 2 + margin + 3, variableYValue + spaceBetweenText, rectWidth, rectHeight), f, 2);
        levelWidth = new MenuTextField(new Rectangle(margin + 3, variableYValue + spaceBetweenText, rectWidth, rectHeight), f, 3);
        levelHeight = new MenuTextField(new Rectangle(margin + 3, variableYValue + 80 + spaceBetweenText, rectWidth, rectHeight), f, 3);

        addTextField(levelNumber);
        addTextField(levelHeight);
        addTextField(levelWidth);

        // Button
        int bWidth = 110;
        int bHeight = 60;
        toEditorButton = new MenuButton(buttonUpImg, buttonDownImg, "Go!", GameClient.loadFont(SILKSCREEN, 48), (width - bWidth) / 2, height - 80, bWidth, bHeight);
        addButton(toEditorButton, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                //TODO fill this shit
                if (levelNumber.getText().length() > 0) {
                    openEditorWindow(new LevelEditor(Integer.parseInt(levelNumber.getText())));
                    jFrame.dispose();
                } else if (levelWidth.getText().length() > 0 && levelHeight.getText().length() > 0) {
                    int lvlWidth = Integer.parseInt(levelWidth.getText());
                    int lvlHeight = Integer.parseInt(levelHeight.getText());
                    openEditorWindow(new LevelEditor(new Dimension(lvlWidth, lvlHeight)));
                    jFrame.dispose();
                }
                return null;
            }
        });
    }

    public void openEditorWindow(LevelEditor editor) {
        levelWidth.setText("");
        levelHeight.setText("");
        levelNumber.setText("");
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setResizable(true);
        frame.getContentPane().setPreferredSize(new Dimension(Constants_LevelEditor.WIDTH, Constants_LevelEditor.HEIGHT));
        frame.pack();
        frame.setTitle("Level Editor");
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(editor);
        frame.setVisible(true);
        frame.toFront();
    }

    public void drawBackgroundMap(Graphics2D g2d) {
        int[][] BACKGROUND_LEVEL = this.BACKGROUND_LEVEL;
        Client.Level level = new Client.Level(BACKGROUND_LEVEL);

        if (level.height * Constants.SCALED_BLOCK_SIZE != Constants_LevelEditor.HEIGHT)
            camY = Constants_LevelEditor.HEIGHT - (level.height * Constants.SCALED_BLOCK_SIZE);
        if (-camX == level.width * Constants.SCALED_BLOCK_SIZE - Constants_LevelEditor.WIDTH)
            scrollForward = false;
        else if (camX == 0)
            scrollForward = true;
        if (scrollForward)
            g2d.translate(camX--, camY);
        else
            g2d.translate(camX++, camY);

        for (MenuButton button : buttons.keySet())
            button.setRectangle(new Rectangle(button.x - camX, button.y - camY, button.width, button.height));
        for (MenuLabel label : textLabels)
            label.setLocation(new Point(label.getOriginLoc().x - camX, label.getOriginLoc().y - camY));
        for (MenuTextField field : textFields)
            field.setRectangle(new Rectangle(field.x - camX, field.y - camY, field.width, field.height));

        // g2d.drawImage(new ImageIcon(IMG_FOLDER + "backdrop.png").getImage(), 1, 1, Constants_LevelEditor.WIDTH, Constants_LevelEditor.HEIGHT, this);
        for (int r = 0; r < BACKGROUND_LEVEL.length; r++)
            for (int c = 0; c < BACKGROUND_LEVEL[0].length; c++)
                g2d.drawImage(level.getTerrainImage(r, c), c * Constants.SCALED_BLOCK_SIZE, r * Constants.SCALED_BLOCK_SIZE, this);
    }

    @Override
    public void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHints(rh);

        drawBackgroundMap(g2d);
        drawButtons(g2d);
        drawTextFields(g2d);
        drawTextLabels(g2d);

        g2d.setColor(Color.white);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(width / 2 - camX, 120 - camY, width / 2 - camX, height - 100 - camY);
    }
}
