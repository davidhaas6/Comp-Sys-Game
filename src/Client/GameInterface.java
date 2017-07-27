package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by David on 1/21/2016.
 */
public class GameInterface extends JPanel implements Constants {
    private int camX, camY; // Reflects the players location relevant to the viewport
    private int worldSizeX, worldSizeY; // X and Y sizes of the world
    private int offsetMaxX, offsetMaxY;
    private int timeLeft;
    private boolean playersHaveWon; // True if any player has won the map
    private Level level; // The current level being played
    private HashMap<String, Boolean> previousSounds; // Sounds that were being played in the previous snapshot
    private HashMap<Player, Integer> winStatuses; // The ranks of each player who has won
    private HashMap<String, Integer> animationStates; // The frame which each player is at in their animation cycle
    private ArrayList<Mushroom> mushrooms;
    private Font timerFont, namesFont, winScreenFont, deathFont;
    private String[] ranks; // The positions of each player who has won in an easy-to-use array
    private Color[] nameColors; // The different colors for player names
    private int timerColorCounter; // For cycling between red and white once the timer reaches 15 seconds
    private boolean drawBackgroundImage;

    public GameInterface() {
        addKeyListener(new KAdapter());
        setBackground(new Color(169, 219, 219));
        setDoubleBuffered(true);
        setFocusable(true);
        // Red, green, orange, blue, purple,
        nameColors = new Color[]{new Color(231, 76, 60), new Color(46, 204, 113), new Color(243, 156, 18), new Color(52, 152, 219), new Color(155, 89, 182), Color.white};
        timerColorCounter = 0;
        playersHaveWon = false;
        drawBackgroundImage = true;
        level = null;
        mushrooms = new ArrayList<>();
        ranks = new String[4];
        winStatuses = new HashMap<>();
        animationStates = new HashMap<>();

        previousSounds = new HashMap<>();
        previousSounds.put(JUMP, false);
        previousSounds.put(MUSIC, false);
        previousSounds.put(VICTORY, false);
        previousSounds.put(TWENTY_SECONDS, false);
        previousSounds.put(CRUSH, false);
        previousSounds.put(DIE, false);
        previousSounds.put(BOSS_MUSIC, false);

        double scale = 1;
        if (SCALE == 2)
            scale = 1.5;

        timerFont = GameClient.loadFont(SILKSCREEN, (int) (48 * scale));
        namesFont = GameClient.loadFont(SILKSCREEN, (int) (24 * scale));
        winScreenFont = GameClient.loadFont(SILKSCREEN, (int) (24 * scale));
        deathFont = GameClient.loadFont(RED_ALERT, (int) (100 * scale));

        final int INITIAL_DELAY = 10;
        final int PERIOD_INTERVAL = 1000 / TARGET_FPS;
        new Timer().scheduleAtFixedRate(new RepaintTask(), INITIAL_DELAY, PERIOD_INTERVAL);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (level != null)
            doDrawing(g);
        Toolkit.getDefaultToolkit().sync();
    }

    // Does the drawing
    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Player player = GameClient.getClientPlayer();
        { // Side-scrolling operations
            camX = player.getLocation().x * SCALE - Constants.WIDTH / 2; // The center of the X viewport
            camY = player.getLocation().y * SCALE - Constants.HEIGHT / 2; // The center of the Y viewport

            if (camX > offsetMaxX) {
                camX = offsetMaxX;
            } else if (camX < OFFSET_MIN_X) {
                camX = OFFSET_MIN_X;
            }

            if (camY > offsetMaxY) {
                camY = offsetMaxY;
            }
            if (camY < OFFSET_MIN_Y) {
                camY = OFFSET_MIN_Y;
            }

            g2d.translate(-camX, -camY);
        }

        /*RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);*/

        // Sets rendering options
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHints(rh);

        drawMap(g2d);
        drawPlayers(g2d);
        drawAI(g2d);
        drawHUD(g2d);

        /*long currentTime = System.currentTimeMillis();
        if (currentTime > nextSecond) {
            nextSecond += 1000;
            framesInLastSecond = framesInCurrentSecond;
            framesInCurrentSecond = 0;
        }
        framesInCurrentSecond++;

        g.drawString(framesInLastSecond + " fps", 80 + camX, 80 + camY);*/
    }
    /*long nextSecond = System.currentTimeMillis() + 1000;
    int framesInLastSecond = 0;
    int framesInCurrentSecond = 0;*/

    // Draws the map
    private void drawMap(Graphics2D g2d) {
        if (level != null) {
            if (drawBackgroundImage)
                g2d.drawImage(new ImageIcon(IMG_FOLDER + "backdrop.png").getImage(), camX, camY, Constants.WIDTH, Constants.HEIGHT, this);
            g2d.setColor(Color.red);
            int terrain[][] = level.getTerrain();
            for (int r = 0; r < terrain.length; r++) {
                for (int c = 0; c < terrain[0].length; c++) {
                    g2d.drawImage(level.getTerrainImage(r, c), c * SCALED_BLOCK_SIZE, r * SCALED_BLOCK_SIZE, this);
                    //g2d.drawRect(c * SCALED_BLOCK_SIZE, r * SCALED_BLOCK_SIZE, SCALED_BLOCK_SIZE, SCALED_BLOCK_SIZE);
                }
            }
        }
    }

    // Draws each player and the names over their head
    private void drawPlayers(Graphics2D g2d) {

        int count = 0;
        for (Player player : GameClient.serverSnapshot.getPlayers()) {
            // Draw's the player
            if (player.isDead())
                g2d.drawImage(getImage(player.getCharacterName(), player),
                        player.getLocation().x * SCALE,
                        player.getLocation().y * SCALE + player.getDimensions().height * SCALE,
                        player.getDimensions().width * SCALE,
                        player.getDimensions().height * SCALE * -1,
                        this);
            else if (player.isFacingRight())
                g2d.drawImage(getImage(player.getCharacterName(), player), player.getLocation().x * SCALE, player.getLocation().y * SCALE, player.getDimensions().width * SCALE, player.getDimensions().height * SCALE, this);
            else
                g2d.drawImage(getImage(player.getCharacterName(), player), player.getLocation().x * SCALE - (player.getDimensions().width * SCALE * -1), player.getLocation().y * SCALE, player.getDimensions().width * SCALE * -1, player.getDimensions().height * SCALE, this);
            //g2d.setColor(Color.blue);
            //g2d.drawRect( player.getLocation().x * SCALE, player.getLocation().y * SCALE, player.getDimensions().width * SCALE, player.getDimensions().height * SCALE);

            // Draw's the player's name over their head
            g2d.setFont(namesFont);
            g2d.setColor(nameColors[count]);
            g2d.getFontMetrics().stringWidth(player.getUserName());
            int nameX = (player.getLocation().x + player.getDimensions().width / 2) * SCALE - (g2d.getFontMetrics().stringWidth(player.getUserName()) / 2);
            int nameY = player.getLocation().y * SCALE - g2d.getFontMetrics().getHeight();
            if (nameX < 0)
                nameX = 0;
            if (!player.isDead())
                g2d.drawString(player.getUserName(), nameX, nameY);
            count++;
        }
    }

    // Draws the mushrooms
    private void drawAI(Graphics2D g2d) {
        g2d.setColor(Color.pink);
        for (Mushroom mushroom : mushrooms) {
            g2d.drawImage(getImage("mushroom"), mushroom.getLocation().x * SCALE, mushroom.getLocation().y * SCALE, this);
            //g2d.drawRect(mushroom.getLocation().x * SCALE, mushroom.getLocation().y * SCALE, mushroom.getDimension().width * SCALE, mushroom.getDimension().height * SCALE);
        }
    }

    // Draws the timer, the player's icon, the win text, and the level-progress bar
    private void drawHUD(Graphics2D g2d) {
        Player player = GameClient.getClientPlayer();
        // Draw timer
        g2d.setColor(Color.WHITE);
        if (timeLeft <= 15) {
            if (timerColorCounter == 0)
                timerColorCounter = TARGET_FPS;

            if (timerColorCounter > TARGET_FPS / 2)
                g2d.setColor(Color.RED);
            else if (timerColorCounter <= TARGET_FPS / 2)
                g2d.setColor(new Color(196, 36, 0));
            timerColorCounter--;
        }

        g2d.setFont(timerFont);
        g2d.drawString(timeLeft + "", Constants.WIDTH - g2d.getFontMetrics().stringWidth(timeLeft + "") + camX, g2d.getFontMetrics().getHeight() - 10 + camY);

        // Draw progress bar
        g2d.setColor(Color.white);
        int boxWidth = Constants.WIDTH - 100;
        int boxHeight = 16;
        int cornerX = 50 + camX;
        int cornerY = Constants.HEIGHT - boxHeight - 4 + camY;

        g2d.drawRoundRect(cornerX, cornerY, boxWidth, boxHeight, 5, 5);
        g2d.drawLine(cornerX, cornerY + 8, Constants.WIDTH - (cornerX - camX) + camX, cornerY + 8);
        g2d.setColor(Color.RED);
        g2d.fillOval(cornerX + (int) (((double) player.getLocation().x * SCALE / (double) worldSizeX) * (boxWidth - 6)), cornerY + 2, 12, 12);

        // Draw character icon
        g2d.drawImage(getImage(player.getCharacterName()), 3 + camX, 3 + camY, SCALED_BLOCK_SIZE, SCALED_BLOCK_SIZE, this);

        // Game won text
        if (playersHaveWon) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(winScreenFont);
            int maxLength = g2d.getFontMetrics().stringWidth(longestString(new String[]{"1st -\t", "2nd -\t", "3rd -\t", "4th -\t"}) + longestString(ranks));
            int xPos = (Constants.WIDTH / 2) - (maxLength / 2) + camX;

            g2d.drawString("1st -\t" + ranks[0], xPos, g2d.getFontMetrics().getHeight() + camY);
            g2d.drawString("2nd -\t" + ranks[1], xPos, g2d.getFontMetrics().getHeight() * 2 + camY);
            g2d.drawString("3rd -\t" + ranks[2], xPos, g2d.getFontMetrics().getHeight() * 3 + camY);
            g2d.drawString("4th -\t" + ranks[3], xPos, g2d.getFontMetrics().getHeight() * 4 + camY);
        }

        if (player.isDead()) {
            g2d.setColor(Color.RED);
            g2d.setFont(deathFont);
            String deathText = DEATH_TEXT;

            if (player.getCharacterName().equals("trump"))
                deathText = "GET STUMPED";
            else if (player.getCharacterName().equals("pepe"))
                deathText = ":(";

            int textLength = g2d.getFontMetrics().stringWidth(deathText);
            int xPos = (Constants.WIDTH / 2) - (textLength / 2) + camX;
            int yPos = (Constants.HEIGHT / 2) + camY;
            g2d.drawString(deathText, xPos, yPos);
        }
    }

    // Translates the sounds HashMap received from the server into commands to SoundController to play sounds
    public void playSounds(HashMap<String, Boolean> sounds) {
        if (!sounds.equals(previousSounds)) {
            if (isSoundNew(sounds, MUSIC))
                SoundController.playMusic();
            else if (isSoundStopped(sounds, MUSIC))
                SoundController.stopMusic();
            if (isSoundNew(sounds, TWENTY_SECONDS))
                SoundController.play20SecondMusic();
            else if (isSoundStopped(sounds, TWENTY_SECONDS))
                SoundController.stop20SecondMusic();
            if (isSoundNew(sounds, BOSS_MUSIC))
                SoundController.playBossMusic();
            else if (isSoundStopped(sounds, BOSS_MUSIC))
                SoundController.stopBossMusic();


            if (isSoundNew(sounds, VICTORY))
                SoundController.playVictorySound();
            if (isSoundNew(sounds, JUMP))
                SoundController.playJumpSound();
            if (isSoundNew(sounds, CRUSH))
                SoundController.playCrushSound();
            if (isSoundNew(sounds, DIE))
                SoundController.playDeathSound();

            /*
            if (isSoundNew(sounds, MUSIC))
                SoundController.playMusic();
            else if (!isSoundNew(sounds, MUSIC))
                SoundController.stopMusic();
            if (isSoundNew(sounds, TWENTY_SECONDS))
                SoundController.play20SecondMusic();
            else if (!isSoundNew(sounds, TWENTY_SECONDS))
                SoundController.stop20SecondMusic();


            if (isSoundNew(sounds, VICTORY))
                SoundController.playVictorySound();
            if (isSoundNew(sounds, JUMP))
                SoundController.playJumpSound();
            if (isSoundNew(sounds, CRUSH))
                SoundController.playCrushSound();
            if (isSoundNew(sounds, DIE))
                SoundController.playDeathSound();

                else if (!sounds.get(TWENTY_SECONDS) && previousSounds.get(TWENTY_SECONDS))
                SoundController.stopMusic();
            if (sounds.get(TWENTY_SECONDS) && !previousSounds.get(TWENTY_SECONDS))
                SoundController.play20SecondMusic();
            else if (!sounds.get(TWENTY_SECONDS) && previousSounds.get(TWENTY_SECONDS))
                SoundController.stop20SecondMusic();
             */

            previousSounds.putAll(sounds);
        }
    }

    private boolean isSoundNew(HashMap<String, Boolean> sounds, String soundKey) {
        return sounds.get(soundKey) && !previousSounds.get(soundKey);
    }

    private boolean isSoundStopped(HashMap<String, Boolean> sounds, String soundKey) {
        return !sounds.get(soundKey) && previousSounds.get(soundKey);
    }

    public void initGameplayVariables(Snapshot snapshot) {
        winStatuses = snapshot.getPlayerWinStatus();
        for (Player player : snapshot.getPlayers())
            animationStates.put(player.getUserName(), 0);
        drawBackgroundImage = true;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void setLevel(int[][] levelTerrain) {
        initWorldVariables(levelTerrain);
        level = new Level(levelTerrain);
    }

    public void setMushrooms(ArrayList<Mushroom> shrooms) {
        mushrooms = shrooms;
    }

    public void updatePlayerWinStatus(HashMap<Player, Integer> newWinStatus) {
        if (!winStatusEqual(newWinStatus)) {
            playersHaveWon = newWinStatus.values().contains(1);
            winStatuses = new HashMap<>(newWinStatus);
            for (int i = 0; i < ranks.length; i++) {
                try {
                    if (getPlayerAtPosition(i + 1) != null)
                        ranks[i] = getPlayerAtPosition(i + 1).getUserName();
                    else
                        ranks[i] = "?????";

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initWorldVariables(int[][] levelTerrain) {
        worldSizeX = levelTerrain[0].length * SCALED_BLOCK_SIZE;
        worldSizeY = levelTerrain.length * SCALED_BLOCK_SIZE;
        offsetMaxX = worldSizeX - Constants.WIDTH;
        offsetMaxY = worldSizeY - Constants.HEIGHT;
    }

    // Gets the animated image for various sprites
    private Image getImage(String charName, Player player) {
        int frameNumber; // The animation frame the player will be on

        if (CHARACTER_IMAGES.keySet().contains(charName)) {
            if (!player.isMoving())
                frameNumber = 0;
            else {
                frameNumber = animationStates.get(player.getUserName()) + 1;
                if (frameNumber / ANIMATON_FRAME_LENGTH >= CHARACTER_IMAGES.get(charName).length)
                    frameNumber = 0;
            }
            animationStates.put(player.getUserName(), frameNumber);
            return CHARACTER_IMAGES.get(charName)[frameNumber / ANIMATON_FRAME_LENGTH];
        } else return new ImageIcon(IMG_FOLDER + "unknown_character.png").getImage();
    }

    // Gets the static image for varoius sprites
    private Image getImage(String charName) {
        if (CHARACTER_IMAGES.keySet().contains(charName))
            return CHARACTER_IMAGES.get(charName)[0];
        else if (charName.equals("mushroom"))
            return MUSHROOM_AI_ANIMATIONS[0];
        else
            return new ImageIcon(IMG_FOLDER + "unknown_character.png").getImage();
    }

    private Player getPlayerAtPosition(int position) {
        for (Player p : winStatuses.keySet())
            if (winStatuses.get(p) == position)
                return p;
        return null;
    }

    public void setBackgroundColor(Color bgColor) {
        setBackground(bgColor);
        drawBackgroundImage = false;
    }

    private String longestString(String[] arr) {
        String longest = "";
        int max = -1;
        for (String str : arr)
            if (str.length() > max) {
                longest = str;
                max = str.length();
            }
        return longest;
    }

    private boolean winStatusEqual(HashMap<Player, Integer> newWinStatus) {
        return winStatuses.values().containsAll(newWinStatus.values()) && newWinStatus.values().containsAll(winStatuses.values()) && winStatuses.size() == newWinStatus.size();
    }

    private class RepaintTask extends TimerTask {
        public void run() {
            if (isVisible())
                repaint();
        }
    }

    private class KAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            GameClient.keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            GameClient.keyReleased(e);
        }
    }

}
