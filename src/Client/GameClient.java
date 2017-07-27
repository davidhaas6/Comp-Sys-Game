package Client;

import Client.Menus.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by David on 1/21/2016.
 */
public class GameClient implements Constants {
    public static boolean isLobbyHost;
    public static String characterName = "pepe"; // Holds the player's user and character name
    public static Snapshot serverSnapshot; // Object to hold the data received from the server in the snapshot
    public static JFrame jFrame; // JFrame object that contains all of our UI
    private static String userName = System.getProperty("user.name");
    private static Thread localServerThread; // The thread that wraps the local server started in singleplayer mode
    private static String currentPanel; // The key of the panel which is currently displayed
    private static int lastLevel; // The last level played
    private static Socket socket = null;
    private static ObjectOutputStream objectOut; // Sends objects to the server
    private static ObjectInputStream objectIn; // Receives objects from the server
    private static Object fromServer; // Temporary object for holding data received from server
    private static CardLayout cardLayout; // This adds the ability for the JFrame to easily switch between menus ( main menu -> option menu -> game interface, etc)
    private static GameInterface gameInterface; // Handles drawing the data (sprites, map, etc) received from the server when in a game, as well as playing sounds received
    private static MainMenu mainMenu;
    private static OptionsMenu optionsMenu;
    private static ModeSelectionMenu modeSelectionMenu;
    private static ConnectionMenu connectionMenu;
    private static CharacterSelectionMenu characterSelectionMenu;
    private static LevelSelectionMenu levelSelectionMenu;
    private static LobbyMenu lobbyMenu;
    private static EscapeMenu escapeMenu;
    private static EndGameMenu endGameMenu;
    private static boolean connected; // Whether the client is connected to a server
    private static boolean a_down, d_down, w_down; // a_down, w_down, etc are true if that key is held down
    private static ArrayList<Integer> pressedKeys; // The keys that are currently being pressed/held down
    private static HashMap<String, Boolean> keyStatuses; // A map containing the key and it's status e.g {"a", true} would be in there if 'a' was being held down

    public static void main(String[] args) {
        a_down = false;
        d_down = false;
        w_down = false;
        connected = false;
        isLobbyHost = false;

        pressedKeys = new ArrayList<>();
        keyStatuses = new HashMap<>();
        keyStatuses.put("a", a_down);
        keyStatuses.put("d", d_down);
        keyStatuses.put("w", w_down);
        localServerThread = new Thread() {
            public void run() {
                Server.Server.main(new String[]{"no gui", "localhost"});
            }
        };

        /*while(!connected){
            try {
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        initGUI();
        while (true) {
            while (!connected) {
                try {
                    Thread.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                final Thread listenThread = new Thread() {
                    public void run() {
                        try {
                            while ((fromServer = objectIn.readObject()) != null) {
                                if (fromServer instanceof Snapshot) {
                                    if (serverSnapshot == null)
                                        gameInterface.initGameplayVariables((Snapshot) fromServer);
                                    serverSnapshot = (Snapshot) fromServer;
                                    gameInterface.setTimeLeft(serverSnapshot.getTimeLeft());
                                    //if(!System.getProperty("os.name").equals("Linux"))
                                    gameInterface.playSounds(serverSnapshot.getSounds());
                                    gameInterface.setMushrooms(serverSnapshot.getMushrooms());
                                    gameInterface.updatePlayerWinStatus(serverSnapshot.getPlayerWinStatus());
                                    if (serverSnapshot.isGameWon() && !(currentPanel.equals(END_GAME_MENU) || currentPanel.equals(LEVEL_SELECTION_MENU))) {
                                        new Timer().schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                if (!(currentPanel.equals(END_GAME_MENU) || currentPanel.equals(LEVEL_SELECTION_MENU))) {
                                                    switchPanels(END_GAME_MENU);
                                                    //System.out.println("switched to end game menu");
                                                }
                                            }
                                        }, 300);
                                    }
                                } else if (fromServer instanceof int[][]) {
                                    gameInterface.setLevel((int[][]) fromServer);
                                    gameInterface.initGameplayVariables(serverSnapshot);
                                    switchPanels(GAME_UI);
                                } else if (fromServer instanceof Boolean)
                                    isLobbyHost = (Boolean) fromServer;
                                else if (fromServer instanceof String)
                                    userName = (String) fromServer;
                                else if (fromServer instanceof Color) {
                                    gameInterface.setBackgroundColor((Color) fromServer);
                                } else
                                    System.err.println("Unknown data sent from server: \t" + fromServer);
                            }
                        } catch (Exception e) {
                            if (!(e instanceof SocketException))
                                e.printStackTrace();
                        }
                    }
                };
                listenThread.start();

                // Sends the character name and username the client chose to the server
                objectOut.writeObject(new

                        Player(new Point(0, 0), userName, characterName

                ));
                objectOut.reset();

                while (connected)

                { // TODO Find a way to prevent the outer while loop from re-running a billion times per second without this sleep loop
                    // Send stuff to server through here
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    // Sends the user's keyInputs to the server

    private static void sendUpdate(HashMap<String, Boolean> keyStatuses) {
        try {
            objectOut.writeObject(keyStatuses.clone()); // ObjectOutputStream keeps a cache of objects you send, so if it's the same pointer as a previous one the data wont get updated -- that's why i have to clone it
            objectOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    objectIn.close();
                    objectOut.close();
                    socket.close();
                    connected = false;
                    SoundController.stopAllSounds();
                    SoundController.stop20SecondMusic();
                    SoundController.stopMusic();
                    SoundController.stopBossMusic();
                    if (localServerThread.isAlive()) {
                        try {
                            localServerThread.interrupt();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    System.err.println("Disconnecting from server.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Initiates a server for the client to connect to hosted at the client's IP
    public static void startLocalServer() {
        localServerThread = new Thread() {
            public void run() {
                try {
                    Server.Server.main(new String[]{"no gui", "localhost"});
                } catch (Exception e) {
                    e.printStackTrace();
                    disconnect();
                    switchPanels(MODE_SELECTION_MENU);
                }
            }
        };
        localServerThread.start();
    }

    public static void sendLevelChange(int levelNumber) {
        try {
            objectOut.writeObject(levelNumber);
            objectOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastLevel = levelNumber;
    }

    public static void replay() {
        if (isLobbyHost)
            sendLevelChange(lastLevel);
    }

    // Gets the client's player object from the snapshot's ArrayList of players
    public static Player getClientPlayer() {
        for (Player p : serverSnapshot.getPlayers())
            if (p.getUserName().equals(userName))
                return p;
        return new Player(new Point(0, 0), userName, characterName);
    }

    public static void setUserName(String name) {
        userName = name;
    }

    public static void setCharacter(String character) {
        characterName = character;
    }

    // Called from GameInterface whenever a key is pressed -- updates keyInputs to reflect what keys the user is holding down
    public static void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (!pressedKeys.contains(keyCode)) {
            pressedKeys.add(keyCode);
            // System.out.println(e.getKeyChar() + " pressed ");
            for (Integer kCode : pressedKeys)
                switch (kCode) {
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_UP: case KeyEvent.VK_W:
                        keyStatuses.put("w", (w_down = true));
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        keyStatuses.put("a", (a_down = true));
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        keyStatuses.put("d", (d_down = true));
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (currentPanel.equals(GAME_UI))
                            GameClient.switchPanels(ESCAPE_MENU);
                }

            sendUpdate(keyStatuses);
        }
    }

    // Called from GameInterface whenever a key is released -- updates keyInputs to reflect what keys the user is no longer holding down
    public static void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (pressedKeys.contains(keyCode))
            pressedKeys.remove(pressedKeys.indexOf(keyCode));
        switch (keyCode) {
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                keyStatuses.put("w", (w_down = false));
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                keyStatuses.put("a", (a_down = false));
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                keyStatuses.put("d", (d_down = false));
                break;
        }
        sendUpdate(keyStatuses);
    }

    // Connects to the server and instantiates the objects needed to send and receive data
    public static boolean connectToServer(String IP) {
        try {
            socket = new Socket(IP, DEFAULT_PORT);
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.err.println("Couldn't connect to server at IP: " + IP);
            return false;
        }
        System.out.println("Connected to server at IP: " + IP);
        connected = true;
        return true;
    }

    // Initiates the JFrame object for the game, as well as all of its components. Adding new JPanels/UI should be done from here
    private static void initGUI() {
        jFrame = new JFrame();
        cardLayout = new CardLayout();

        jFrame.setLayout(cardLayout);
        jFrame.setResizable(false);
        jFrame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT)); // YOU NEED THIS HERE FOR THE SIZE TO BE CORRECT (Sets size of game window, excluding the top windows bar)
        jFrame.pack();
        jFrame.setTitle("Sharkboy and Lavagirl 3D");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);

        // Instantiates each menu
        gameInterface = new GameInterface();
        mainMenu = new MainMenu();
        optionsMenu = new OptionsMenu();
        characterSelectionMenu = new CharacterSelectionMenu();
        modeSelectionMenu = new ModeSelectionMenu();
        connectionMenu = new ConnectionMenu();
        levelSelectionMenu = new LevelSelectionMenu();
        lobbyMenu = new LobbyMenu();
        escapeMenu = new EscapeMenu();
        endGameMenu = new EndGameMenu();

        // Adds each menu with their key to the CardLayout so you can switch between them
        jFrame.add(gameInterface, GAME_UI);
        jFrame.add(mainMenu, MAIN_MENU);
        jFrame.add(optionsMenu, OPTIONS_MENU);
        jFrame.add(characterSelectionMenu, CHARACTER_SELECTION_MENU);
        jFrame.add(modeSelectionMenu, MODE_SELECTION_MENU);
        jFrame.add(connectionMenu, CONNECTION_MENU);
        jFrame.add(levelSelectionMenu, LEVEL_SELECTION_MENU);
        jFrame.add(lobbyMenu, LOBBY_MENU);
        jFrame.add(escapeMenu, ESCAPE_MENU);
        jFrame.add(endGameMenu, END_GAME_MENU);

        jFrame.setFocusable(true);
        switchPanels(MAIN_MENU);
        jFrame.setVisible(true);

        SoundController.init();
        //SoundController.playMusic();
    }

    // Facilitates switching between screens
    public static void switchPanels(String panelName) {
        cardLayout.show(jFrame.getContentPane(), panelName);
        currentPanel = panelName;
        switch (panelName) {
            case (GAME_UI):
                gameInterface.requestFocus();
                //SoundController.stopMusic();
                break;
            case (MAIN_MENU):
                mainMenu.requestFocus();
                break;
            case (OPTIONS_MENU):
                optionsMenu.requestFocus();
                break;
            case (CHARACTER_SELECTION_MENU):
                characterSelectionMenu.requestFocus();
                break;
            case (MODE_SELECTION_MENU):
                modeSelectionMenu.requestFocus();
                break;
            case (CONNECTION_MENU):
                connectionMenu.requestFocus();
                break;
            case (LEVEL_SELECTION_MENU):
                levelSelectionMenu.requestFocus();
                break;
            case (LOBBY_MENU):
                lobbyMenu.setPlayers();
                lobbyMenu.setLobbyPrivilege();
                lobbyMenu.requestFocus();
                break;
            case (ESCAPE_MENU):
                escapeMenu.updateLeaderStatus();
                escapeMenu.requestFocus();
                keyStatuses.put("w", (w_down = false));
                keyStatuses.put("a", (a_down = false));
                keyStatuses.put("d", (d_down = false));
                pressedKeys = new ArrayList<>();
                break;
            case (END_GAME_MENU):
                SoundController.stop20SecondMusic();
                SoundController.stopBossMusic();
                keyStatuses.put("w", (w_down = false));
                keyStatuses.put("a", (a_down = false));
                keyStatuses.put("d", (d_down = false));
                pressedKeys = new ArrayList<>();
                //SoundController.stopAllSounds();
                endGameMenu.setLobbyPrivilege();
                endGameMenu.setPlayers();
                endGameMenu.requestFocus();
                break;
        }
    }

    // Loads a TTF File as a usable font
    public static Font loadFont(String ttf_file, int fontSize) {
        Font f = null;
        try {
            f = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_FOLDER + ttf_file)).deriveFont((float) fontSize);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                f = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_FOLDER + COMIC_SANS)).deriveFont((float) fontSize);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return f;
    }

    public static int getScale() {
        try {
            File settings = new File(RESOURCE_PATH + SETTINGS_FILE_NAME);
            String scaleLine = Files.readAllLines(settings.toPath(), Charset.forName("UTF-8")).get(0);
            return Integer.parseInt(scaleLine.substring(scaleLine.indexOf(':') + 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
