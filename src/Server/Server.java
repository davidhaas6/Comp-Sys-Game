package Server;

import Client.Mushroom;
import Client.Player;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.*;

public class Server implements Constants {
    private static final int MAX_USERS = 4; // Max # of users allowed to connect
    static ServerGUI gui;
    static boolean serverOpen; // If the server is open and receiving connections
    static int userCount = 0; // Number of users connected
    static ArrayList<Player> players; // Server-wide ArrayList containing each client's Player object
    static HashMap<String, Boolean> sounds; // Contains the name and playing status (true/false) of each sound
    static HashMap<Mushroom, MushroomTimerTask> mushrooms; // Contains each Mushroom AI and their Timer Class
    static int mapTime; // Time left in the map
    private static int playersWon; // Number of players who have won
    static boolean gameWon; // Has the game been won yet
    static HashMap<Player, Integer> playerWinStatus; // The place of each player who has won (1st, 2nd, etc)
    static int[][] currentLevel; // The level that is currently loaded
    static boolean levelLoaded;
    static Rectangle[][] levelCollisionBoxes; // Rectangles representing the collision boxes for each block in the level
    static ArrayList<ServerThread> serverThreads;
    static boolean GUI_enabled;
    private static boolean localhost;
    private static Socket user;
    private static ServerThread serverThread;
    private static Thread shutdownMonitor, connectionAcceptor;
    private static Timer winCheckTimer, mushroomCollisionTimer, mapTimeTimer, triggerTimer;
    private static boolean hasTriggered;
    public static Point spawnPoint;
    public static Color backgroundColor;
    public static int[][][] levels = Server.retrieveLevels();


    public static void main(String[] args) {
        final ServerSocket serverSocket;
        players = new ArrayList<>();
        currentLevel = null;
        sounds = new HashMap<>();
        mushrooms = new HashMap<>();
        playerWinStatus = new HashMap<>(MAX_USERS);
        mapTimeTimer = new Timer();
        winCheckTimer = new Timer();
        triggerTimer = new Timer();
        mushroomCollisionTimer = new Timer();
        serverThreads = new ArrayList<>();
        playersWon = 0;
        mapTime = 0;
        gameWon = false;
        levelLoaded = false;
        GUI_enabled = true;
        localhost = false;
        hasTriggered = false;
        short port = 7555;
        spawnPoint = DEFAULT_SPAWN_POINT;
        backgroundColor = DEFAULT_BACKGROUND;
        levels = Server.retrieveLevels();

        if (args.length > 0) {
            GUI_enabled = !args[0].contains("no gui");
            if (args.length > 1)
                localhost = args[1].equals("localhost");
        }

        // Instantiates each sound as not being played
        sounds.put(JUMP, false);
        sounds.put(MUSIC, false);
        sounds.put(VICTORY, false);
        sounds.put(TWENTY_SECONDS, false);
        sounds.put(CRUSH, false);
        sounds.put(DIE, false);
        sounds.put(BOSS_MUSIC, false);

        try {
            serverSocket = new ServerSocket(port);

            // Loads the GUI for the server and displays the server's IP
            if (GUI_enabled) {
                gui = new ServerGUI(); //TODO Figure out why it sometimes hangs on this step (see ServerGUI initComponents()) *
                gui.setServerIP(getServerIP());
            }
            System.out.println("Server IP: " + getServerIP() + ":" + serverSocket.getLocalPort());
            serverOpen = true;

            shutdownMonitor = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean running = true;
                    while (running) {
                        if (userCount == 0) {
                            serverOpen = false;
                            try {
                                System.err.println("** No players connected... Shutting down server **");
                                serverSocket.close();
                                Thread.sleep(50);
                                connectionAcceptor.interrupt();
                                mushroomCollisionTimer.cancel();
                                mapTimeTimer.cancel();
                                for (Mushroom mush : mushrooms.keySet())
                                    mushrooms.get(mush).cancel();
                                running = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            });

            // The while loop that runs the whole time and accepts player's connections
            connectionAcceptor = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (serverOpen) {
                        if (userCount < MAX_USERS) {
                            // Creates a new ServerThread once a valid connection has been received and accepted
                            try {
                                serverThread = new ServerThread((user = serverSocket.accept()), "ServerThread " + (userCount + 1), userCount == 0);
                                serverThread.start();
                                serverThreads.add(serverThread);
                                userCount++;
                                if (localhost && !shutdownMonitor.isAlive())
                                    shutdownMonitor.start();
                                System.out.println("New user connected: " + user);
                            } catch (Exception e) {
                                if (!(e instanceof SocketException))
                                    e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {
                            if (!(e instanceof InterruptedException))
                                e.printStackTrace();
                        }
                    }
                }
            });
            connectionAcceptor.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("\nCould not listen on port: " + port);
            System.exit(-1);
        }
    }

    // Removes a player from it's relevant Server objects
    public static void removePlayer(Player player) {
        Server.players.remove(player);
        Server.playerWinStatus.remove(player);
    }

    private static void clearGameState() {
        stopAllAudio();

        playerWinStatus = new HashMap<>(MAX_USERS);
        winCheckTimer.cancel();
        playersWon = 0;
        gameWon = false;
        hasTriggered = false;
        triggerTimer.cancel();
        spawnPoint = DEFAULT_SPAWN_POINT;

        for (Player p : players)
            p.setLocation(spawnPoint); // Resets all player's location to the spawn point
        for (ServerThread st : serverThreads)
            st.resetCharacter();

        mushroomCollisionTimer.cancel(); // Stops checking for mushroom collision
        for (Mushroom m : mushrooms.keySet())
            mushrooms.get(m).stopTimer(); // stops each mushroom's movement timer
        mushrooms = new HashMap<>();

        mapTimeTimer.cancel(); // Stops counting down time
        mapTime = LEVEL_TIME;
    }

    public static void loadLevel(int levelNumber) {
        if (!levelLoaded) {
            if (levelNumber <= levels.length)
                currentLevel = levels[levelNumber - 1];
            else
                currentLevel = levels[0];
            clearGameState();

            // Creates the collision boxes for the current level
            levelCollisionBoxes = new Rectangle[currentLevel.length][currentLevel[0].length];
            for (int i = 0; i < currentLevel.length; i++)
                for (int j = 0; j < currentLevel[0].length; j++)
                    levelCollisionBoxes[i][j] = new Rectangle(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            for (int i = 0; i < currentLevel.length; i++)
                for (int j = 0; j < currentLevel[0].length; j++)
                    if (currentLevel[i][j] == MUSHROOM_SPAWN_BLOCK) // Adds Mushrooms to wherever mushroom spawner blocks are
                        addMushroom(new Mushroom(new Point(j * BLOCK_SIZE - BLOCK_SIZE / 2, i * BLOCK_SIZE), AI_MUSHROOM_DIMENSIONS));

            playMusic();
            startMapTimer();
            startWinCheckTimer(); // Starts checking for players' winning
            startMushroomCollisionTimer(); // Starts checking for player-mushroom collision
            if (contains(currentLevel, TRIGGER_BLOCK))
                startTriggerCheckerTimer(levelNumber); // Checks for player intersections with the trigger blocks
            for (ServerThread st : serverThreads) {
                if (contains(darkBgLevels, levelNumber)) {
                    backgroundColor = DARK_BACKGROUND;
                    st.sendLevel();
                } else {
                    backgroundColor = DEFAULT_BACKGROUND;
                    st.sendLevel();
                }
            }
            levelLoaded = true;
        }
    }

    private static boolean contains(int[] arr, int val) {
        for (int i : arr)
            if (val == i)
                return true;
        return false;
    }

    private static boolean contains(int[][] arr2d, int val) {
        for (int[] arr : arr2d)
            for (int c : arr)
                if (c == val)
                    return true;
        return false;
    }

    private static void addMushroom(Mushroom mushroom) {
        MushroomTimerTask timerTask = new MushroomTimerTask(mushroom);
        mushrooms.put(mushroom, timerTask);
        new Timer().scheduleAtFixedRate(timerTask, 1, TICK_RATE);
    }

    private static void removeMushroom(Mushroom mushroom) {
        mushrooms.get(mushroom).stopTimer();
        mushrooms.remove(mushroom);
        //System.out.println("KILLED A MUSHROOM");
    }

    private static void startMushroomCollisionTimer() {
        mushroomCollisionTimer = new Timer();
        mushroomCollisionTimer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        ArrayList<Mushroom> mushrooms = new ArrayList<>(Server.mushrooms.keySet());
                        for (Player player : players) {
                            if (!player.isDead()) {
                                int playerBoxHeight = GRAVITY_SPEED;
                                Rectangle playerRekt = new Rectangle(player.getLocation().x, player.getLocation().y + player.getDimensions().height - playerBoxHeight, player.getDimensions().width, playerBoxHeight);
                                for (Mushroom mushroom : mushrooms) {
                                    Rectangle rektMushroom = new Rectangle(mushroom.getLocation().x + 2, mushroom.getLocation().y - 1, mushroom.getDimension().width - 2, 1);
                                    if (rektMushroom.intersects(playerRekt)) {
                                        removeMushroom(mushroom);
                                        ServerThread playerThread = getPlayerThread(player);
                                        playCrushSound();
                                        if (playerThread != null)
                                            playerThread.doHop();
                                        // stopCrushSound();
                                    }
                                }
                            }
                        }
                    }
                }, 1, TICK_RATE);
    }

    private static void startTriggerCheckerTimer(int levelNum) {
        triggerTimer = new Timer();
        final int levelNumber = levelNum;
        triggerTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!gameWon && levelLoaded && !hasTriggered)
                    for (Player player : players) {
                        if (Server.levelCollisionBoxes != null) {
                            // Checks if the player intersects a trigger block
                            Rectangle playerRect = player.getRectangle();
                            for (int r = 0; r < Server.levelCollisionBoxes.length; r++)
                                for (int c = 0; c < Server.levelCollisionBoxes[0].length; c++)
                                    if (Server.currentLevel[r][c] == TRIGGER_BLOCK && !hasTriggered && playerRect.intersects(Server.levelCollisionBoxes[r][c]))
                                        activateTrigger(levelNumber);
                        }
                    }
            }
        }, GAME_START_DELAY, TICK_RATE);
    }

    private static void activateTrigger(int levelNum) {
        hasTriggered = true;
        switch (levelNum) {
            case 8:
                playBossMusic();
                stopMusic();
                spawnPoint = new Point(14 * BLOCK_SIZE, 14 * BLOCK_SIZE);
                mapTime = LEVEL_TIME;
                System.out.println("Trigger block activated!");
                break;
            case 12:
                playBossMusic();
                stopMusic();
                // TODO Put code here
                spawnPoint = new Point(3 * BLOCK_SIZE, 16 * BLOCK_SIZE);
                for (ServerThread thread : serverThreads)
                    thread.die();
                mapTime = LEVEL_TIME;
                System.out.println("Trigger block activated!");
        }
        triggerTimer.cancel();
    }

    // Timer to decrement mapTime
    private static void startMapTimer() {
        mapTime = LEVEL_TIME;
        mapTimeTimer = new Timer();
        mapTimeTimer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (mapTime > 0) {
                            mapTime--;
                            if (mapTime <= 23) {
                                stopMusic();
                                if (!sounds.get(BOSS_MUSIC))
                                    play20SecondMusic();
                            }
                        } else {
                            stop20SecondMusic();
                            stopBossMusic();
                            gameWon = true;
                            mapTimeTimer.cancel();
                        }
                    }
                }, GAME_START_DELAY, 1000);

    }

    private static void startWinCheckTimer() {
        winCheckTimer = new Timer();
        winCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkWinStatuses();
            }
        }, GAME_START_DELAY, TICK_RATE);
    }

    public static void checkWinStatuses() {
        if (!gameWon && levelLoaded)
            for (Player player : players) {
                if (Server.levelCollisionBoxes != null) {
                    // Checks if the player intersects the winning game block
                    Rectangle playerRect = player.getRectangle();
                    for (int r = 0; r < Server.levelCollisionBoxes.length; r++)
                        for (int c = 0; c < Server.levelCollisionBoxes[0].length; c++)
                            if (Server.currentLevel[r][c] == LEVEL_WIN_BLOCK && !hasPlayerWon(player) && !player.isDead() && playerRect.intersects(Server.levelCollisionBoxes[r][c]))
                                setGameWon(player);
                }
            }
    }

    public static void setGameWon(Player player) {
        System.out.println("** Player " + player.getUserName() + " has reached the end! **");
        if (playersWon == 0)
            mapTime = 17;
        Server.setPlayerWinStatus(player, Server.playersWon + 1);
        playersWon++;
        if (playersWon == userCount) {
            stopAllAudio();
            gameWon = true;
        }
        playVictorySound();
    }

    private static boolean hasPlayerWon(Player player) {
        for (Player p : Server.playerWinStatus.keySet())
            if (p.getUserName().equals(player.getUserName()))
                return true;
        return false;
    }

    private static void setPlayerWinStatus(Player player, int place) {
        playerWinStatus.put(players.get(getPlayerIndex(player)), place);
    }

    private static int getPlayerIndex(Player player) {
        for (int i = 0; i < players.size(); i++)
            if (players.get(i).getUserName().equals(player.getUserName()))
                return i;
        return -1;
    }

    private static ServerThread getPlayerThread(Player player) {
        for (ServerThread serverThread : serverThreads)
            if (serverThread.getPlayer().equals(player))
                return serverThread;
        return null;
    }

    public static int[][][] retrieveLevels() {
        try {
            FileInputStream fis = new FileInputStream(LEVELS_PATH);
            ObjectInputStream ois = new ObjectInputStream(fis);
            int[][][] lvls = (int[][][]) ois.readObject();
            ois.close();
            return lvls;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[][][]{};
    }

    private static String getServerIP() {
        try {
            String result = null;
            Enumeration<NetworkInterface> interfaces = null;
            try {
                interfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                // handle error
            }

            if (interfaces != null) {
                while (interfaces.hasMoreElements() && result == null) {
                    NetworkInterface i = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = i.getInetAddresses();
                    while (addresses.hasMoreElements() && (result == null || result.isEmpty())) {
                        InetAddress address = addresses.nextElement();
                        if (!address.isLoopbackAddress() &&
                                address.isSiteLocalAddress()) {
                            result = address.getHostAddress();
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "n/a";
    }

    public static void playJumpSound() {
        sounds.put(JUMP, true);
    }

    public static void stopJumpSound() {
        sounds.put(JUMP, false);
    }

    private static void playCrushSound() {
        sounds.put(CRUSH, true);
    }

    public static void stopCrushSound() {
        sounds.put(CRUSH, false);
    }

    public static void playDieSound() {
        sounds.put(DIE, true);
    }

    public static void stopDieSound() {
        sounds.put(DIE, false);
    }

    private static void playVictorySound() {
        sounds.put(VICTORY, true);
    }

    public static void stopVictorySound() {
        sounds.put(VICTORY, false);
    }

    private static void play20SecondMusic() {
        sounds.put(TWENTY_SECONDS, true);
    }

    private static void stop20SecondMusic() {
        sounds.put(TWENTY_SECONDS, false);
    }

    private static void playMusic() {
        sounds.put(MUSIC, true);
    }

    private static void stopMusic() {
        sounds.put(MUSIC, false);
    }

    private static void playBossMusic() {
        sounds.put(BOSS_MUSIC, true);
    }

    private static void stopBossMusic() {
        sounds.put(BOSS_MUSIC, false);
    }

    private static void stopAllAudio() {
        for (String key : sounds.keySet())
            sounds.put(key, false);
    }
}

