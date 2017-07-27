package Client.Menus;

import Client.Constants;
import Client.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by David on 4/10/2016.
 */
public abstract class GameMenu extends JPanel implements Constants {
    public Level level;
    public int camX, camY;
    public boolean scrollForward;
    public ArrayList<MenuTextField> textFields;
    public ArrayList<MenuLabel> textLabels;
    public HashMap<MenuButton, Callable<Void>> buttons;
    public Callable<Void> keyPressedFunction;
    public boolean scrollingBackground;

    public GameMenu() {
        setDoubleBuffered(true);
        setFocusable(true);
        addMouseListener(new MAdapter()); // M'Adapter
        addKeyListener(new KAdapter());
        setBackground(new Color(169, 219, 219));

        textFields = new ArrayList<>();
        textLabels = new ArrayList<>();
        buttons = new HashMap<>();
        level = new Level(BACKGROUND_LEVEL);
        camX = 0;
        camY = 0;
        scrollForward = true;
        scrollingBackground = true;
        keyPressedFunction = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        };

        final int INITIAL_DELAY = 10;
        final int PERIOD_INTERVAL = 1000 / TARGET_FPS; // 60 FPS
        new java.util.Timer().scheduleAtFixedRate(new ScheduleTask(), INITIAL_DELAY, PERIOD_INTERVAL);
    }

    public void addButton(MenuButton button, Callable<Void> releaseFunction) {
        buttons.put(button, releaseFunction);
    }

    public void addTextLabel(MenuLabel label) {
        textLabels.add(label);
    }

    public void setTextLabels(ArrayList<MenuLabel> labels) {
        textLabels = labels;
    }

    public void addTextField(MenuTextField field) {
        textFields.add(field);
    }

    public void setKeyPressedFunction(Callable<Void> func) {
        keyPressedFunction = func;
    }

    private void unpressButtons() {
        for (MenuButton button : buttons.keySet())
            button.setPressed(false);
    }

    private void pressButtons(MouseEvent e) {
        Point clickPoint = new Point(e.getX() - camX, e.getY() - camY);
        for (MenuButton button : buttons.keySet())
            if (button.getBoundingRect().contains(clickPoint))
                button.setPressed(true);
    }

    private void callReleaseMethods(MouseEvent e) {
        Point clickPoint = new Point(e.getX() - camX, e.getY() - camY);
        for (MenuButton button : buttons.keySet())
            if (button.getBoundingRect().contains(clickPoint) && button.isPressed())
                try {
                    buttons.get(button).call();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
    }

    private void setTextFieldFocus(MouseEvent e) {
        Point clickPoint = new Point(e.getX() - camX, e.getY() - camY);
        for (MenuTextField field : textFields)
            if (field.getRectangle().contains(clickPoint))
                field.setFocus(true);
            else
                field.setFocus(false);
    }

    public void john_roth_is_better_than_david_haas(KeyEvent e) {
        String input = e.getKeyChar() + "";
        for (MenuTextField field : textFields)
            if (field.isInFocus())
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && field.getText().length() > 0)
                    field.setText(field.getText().substring(0, field.getText().length() - 1));
                else if (isValidInput(input))
                    field.appendText(input);
    }

    private boolean isValidInput(String input) {
        char[] validMiscChars = {'.'};
        if (input.equals("\uFFFF"))
            return false;
        for (char c : input.toCharArray())
            if (!(Character.isLetterOrDigit(c) || Arrays.binarySearch(validMiscChars, c) != -1))
                return false;
        return true;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            doDrawing(g);
        } catch (Exception e) {
            if(!(e instanceof ConcurrentModificationException))
            e.printStackTrace();
        }
        Toolkit.getDefaultToolkit().sync();
    }

    public void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.	KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHints(rh);

        if (scrollingBackground)
            drawBackgroundMap(g2d);
        drawButtons(g2d);
        drawTextFields(g2d);
        drawTextLabels(g2d);
    }

    public void drawBackgroundMap(Graphics2D g2d) {
        if (level.height * SCALED_BLOCK_SIZE != Constants.HEIGHT)
            camY = Constants.HEIGHT - (level.height * SCALED_BLOCK_SIZE);
        if (-camX == level.width * SCALED_BLOCK_SIZE - Constants.WIDTH)
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

        // g2d.drawImage(new ImageIcon(IMG_FOLDER + "backdrop.png").getImage(), 1, 1, Constants.WIDTH, Constants.HEIGHT, this);
        for (int r = 0; r < BACKGROUND_LEVEL.length; r++)
            for (int c = 0; c < BACKGROUND_LEVEL[0].length; c++)
                g2d.drawImage(level.getTerrainImage(r, c), c * SCALED_BLOCK_SIZE, r * SCALED_BLOCK_SIZE, this);
    }

    public void drawButtons(Graphics2D g2d) {
        for (MenuButton button : buttons.keySet())
            button.draw(g2d, getWidth(), getHeight());
    }

    public void drawTextFields(Graphics2D g2d) {
        for (MenuTextField textField : textFields)
            textField.draw(g2d);
    }

    public void drawTextLabels(Graphics2D g2d) {
        for (MenuLabel label : textLabels)
            label.draw(g2d);
    }

    private class ScheduleTask extends TimerTask {
        public void run() {
            repaint();
        }
    }

    private class MAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            setTextFieldFocus(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            pressButtons(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            callReleaseMethods(e);
            unpressButtons();
        }
    }

    private class KAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            john_roth_is_better_than_david_haas(e);
            try {
                keyPressedFunction.call();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
