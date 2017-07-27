package Tools;

import Client.Constants;
import Server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.TimerTask;

/**
 * Created by David on 1/27/2016.
 */
class LevelEditor extends JPanel implements Constants_LevelEditor {
    private Rectangle[][] blockGrid;
    private int[][] levelGrid;
    private int camX, camY;
    private Level level;
    final int SCROLL_SPEED = 20;
    int currentBlock;
    int levelNumber;
    Font passthroughFont;

    public LevelEditor(Dimension levelSize) {
        init();

        levelNumber = -1;
        blockGrid = new Rectangle[levelSize.height][levelSize.width];
        for (int r = 0; r < blockGrid.length; r++)
            for (int c = 0; c < blockGrid[0].length; c++)
                blockGrid[r][c] = new Rectangle(c * BLOCK_SIZE, r * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

        levelGrid = new int[levelSize.height][levelSize.width];
        for (int r = 0; r < levelGrid.length; r++)
            for (int c = 0; c < levelGrid[0].length; c++)
                levelGrid[r][c] = 0;
        level = new Level(levelGrid);
    }

    public LevelEditor(int levelNumber) {
        init();

        levelGrid = loadLevel(levelNumber);
        this.levelNumber = levelNumber;
        blockGrid = new Rectangle[levelGrid.length][levelGrid[0].length];
        for (int r = 0; r < blockGrid.length; r++)
            for (int c = 0; c < blockGrid[0].length; c++)
                blockGrid[r][c] = new Rectangle(c * BLOCK_SIZE, r * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        level = new Level(levelGrid);
    }

    private void init() {
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(new KAdapter());
        addMouseListener(new MAdapter());
        addMouseMotionListener(new MAdapter());
        addMouseWheelListener(new MAdapter());
        //setVisible(true);

        currentBlock = 1;
        camX = camY = 0;
        passthroughFont = Client.GameClient.loadFont(Constants.RED_ALERT, 36);
        new java.util.Timer().scheduleAtFixedRate(new ScheduleTask(), 1, 1000 / 60);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
        Toolkit.getDefaultToolkit().sync();
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        g2d.translate(-camX, -camY);

        drawMap(g2d);
        drawGrid(g2d);
        drawHUD(g2d);
    }

    private void drawHUD(Graphics2D g2d) {
        int size = 50;
        int imgBorderSize = (size - BLOCK_SIZE) / 2;
        g2d.setColor(Color.lightGray);
        g2d.fillRect(getWidth() - size + camX, camY, size, size);
        g2d.drawImage(BLOCK_IMAGES[currentBlock].getImage(), getWidth() - size + imgBorderSize + camX, imgBorderSize + camY, BLOCK_SIZE, BLOCK_SIZE, this);
        if (currentBlock == TRIGGER_BLOCK || currentBlock == PASSTHROUGH_BLOCK) {
            //g2d.drawImage(BLOCK_IMAGES[currentBlock].getImage(), getWidth() - size + imgBorderSize + camX + 14, imgBorderSize + camY + getFontMetrics(passthroughFont).getHeight() + 10, BLOCK_SIZE, BLOCK_SIZE, this);
            g2d.setColor(Color.RED);
            g2d.setFont(passthroughFont);
            g2d.drawString("!", getWidth() - size + imgBorderSize + camX + 14, imgBorderSize + camY + 28);
        }
    }

    private void drawMap(Graphics2D g2d) {
        level = (level == null ? new Level(levelGrid) : level);

        for (int r = 0; r < levelGrid.length; r++)
            for (int c = 0; c < levelGrid[0].length; c++) {
                g2d.drawImage(level.getTerrainImage(r, c), c * SCALED_BLOCK_SIZE, r * SCALED_BLOCK_SIZE, SCALED_BLOCK_SIZE, SCALED_BLOCK_SIZE, this);
                if (levelGrid[r][c] == PASSTHROUGH_BLOCK || levelGrid[r][c] == TRIGGER_BLOCK) {
                    g2d.setColor(Color.RED);
                    g2d.setFont(passthroughFont);
                    g2d.drawString("!", c * SCALED_BLOCK_SIZE + 14, r * SCALED_BLOCK_SIZE + getFontMetrics(passthroughFont).getHeight() - 10);
                }
            }
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.black);
        for (Rectangle[] rectangles : blockGrid)
            for (Rectangle rect : rectangles)
                g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    private void cycleBlocks(int delta) {
        if (currentBlock + delta >= BLOCK_IMAGES.length)
            currentBlock = 0;
        else if (currentBlock + delta < 0)
            currentBlock = BLOCK_IMAGES.length - 1;
        else
            currentBlock += delta;
    }

    private void placeBlock(Point point) {
        for (int i = 0; i < blockGrid.length; i++)
            for (int j = 0; j < blockGrid[0].length; j++)
                if (blockGrid[i][j].contains(point))
                    levelGrid[i][j] = currentBlock;


        level.setTerrain(levelGrid);
    }

    private void removeBlock(Point point) {
        for (int i = 0; i < blockGrid.length; i++)
            for (int j = 0; j < blockGrid[0].length; j++)
                if (blockGrid[i][j].contains(point))
                    levelGrid[i][j] = 0;

        level.setTerrain(levelGrid);
    }

    public void saveLevel() {
        try {
            int[][][] levels = Server.retrieveLevels();
            int[][][] newLevels;
            if (levelNumber == -1) {
                newLevels = new int[levels.length + 1][][];
                levelNumber = levels.length + 1;
                for (int i = 0; i < levels.length; i++)
                    newLevels[i] = levels[i];
                newLevels[levelNumber - 1] = level.getTerrain();
            } else {
                newLevels = levels;
                newLevels[levelNumber - 1] = level.getTerrain();
            }
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEVELS_PATH));
                oos.writeObject(newLevels);
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println("** Saved Level **");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[][] loadLevel(int levelNumber) {
        try {
            return Server.retrieveLevels()[levelNumber - 1];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class MAdapter extends MouseAdapter implements MouseWheelListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (e.getButton() == MouseEvent.BUTTON1)
                placeBlock(new Point(e.getX() + camX, e.getY() + camY));
            else if (e.getButton() == MouseEvent.BUTTON3)
                removeBlock(new Point(e.getX() + camX, e.getY() + camY));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            if (SwingUtilities.isLeftMouseButton(e))
                placeBlock(new Point(e.getX() + camX, e.getY() + camY));
            else if (SwingUtilities.isRightMouseButton(e))
                removeBlock(new Point(e.getX() + camX, e.getY() + camY));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            super.mouseWheelMoved(e);
            cycleBlocks(-e.getWheelRotation());
        }
    }


    private class KAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case (KeyEvent.VK_RIGHT):
                    camX += SCROLL_SPEED;
                    break;
                case (KeyEvent.VK_LEFT):
                    camX -= SCROLL_SPEED;
                    break;
                case (KeyEvent.VK_UP):
                    camY -= SCROLL_SPEED;
                    break;
                case (KeyEvent.VK_DOWN):
                    camY += SCROLL_SPEED;
                    break;
                case (KeyEvent.VK_ENTER):
                    int reply = JOptionPane.showConfirmDialog(null, "Save level?", "Save Confirmation", JOptionPane.OK_CANCEL_OPTION);
                    if (reply == JOptionPane.YES_OPTION)
                        saveLevel();
                    break;
            }
        }
    }

    private class ScheduleTask extends TimerTask {
        public void run() {
            repaint();
        }
    }
}
