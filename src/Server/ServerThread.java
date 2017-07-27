package Server;

import Client.Mushroom;
import Client.Player;
import Client.Snapshot;

import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class ServerThread extends Thread implements Constants {
    private final Timer movementTimer;
    private Socket socket = null;
    private ObjectInputStream objectIn; // Stream of incoming objects from client
    private ObjectOutputStream objectOut; // Stream of object being sent to the client
    private Object fromUser; // Object that temporarily holds the client's sent data
    private boolean connected; // If the server is connected
    private boolean lobbyHost; // Decides whether the player can select maps or not
    private Player player; // The player object for the client of this thread
    private HashMap<String, Boolean> keyInputs; // Map of the the keys that the user is currently holding down
    private Timer deathTimer;
    private MovementTimer movementTimerTask; // Timer thread that converts keyInputs to moving the player's location
    private boolean sendLevel; // If the level is able to be loaded
    private boolean jumping; // If the player is in the middle of a jump
    private int jumpCount; // Count denoting how many times the player has moved up during their jump
    private Color backgroundColor;
    private int lastDeathTime;
    private boolean canIntersectPlayers;

    public ServerThread(Socket socket, String threadName, boolean lobbyHost) {
        super(threadName);
        this.socket = socket;
        player = new Player(new Point(0, 0), "", "");
        keyInputs = new HashMap<>();
        this.lobbyHost = lobbyHost;
        for (String validKey : validKeyInputs)
            keyInputs.put(validKey, false);
        movementTimer = new Timer();
        deathTimer = new Timer();
        sendLevel = Server.levelLoaded;
        connected = true;
        jumping = false;
        movementTimerTask = new MovementTimer();
        lastDeathTime = LEVEL_TIME;
        canIntersectPlayers = true;
        backgroundColor = DEFAULT_BACKGROUND;
    }

    public void run() {
        try {
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectIn = new ObjectInputStream(socket.getInputStream());

            try {
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Thread for listening for packets from the client (if you need to listen to multiple things simultaneously, you can make multiple threads
            final Thread listenThread = new Thread() {
                @SuppressWarnings("unchecked")
                public void run() {
                    try {
                        Player tempPlayer;
                        while ((fromUser = objectIn.readObject()) != null) {
                            if (fromUser instanceof HashMap) { // If the input is a HashMap, it's the client sending key inputs
                                keyInputs = (HashMap<String, Boolean>) fromUser;
                            } else if (fromUser instanceof Player) { // Takes the Player object that the client specifies, containing their name and character name
                                tempPlayer = (Player) fromUser;
                                if (Server.GUI_enabled)
                                    Server.gui.addUser(socket.getInetAddress().getHostAddress(), tempPlayer.getUserName());
                                switch (tempPlayer.getCharacterName()) { // So if you choose a character, you get the correct dimensions for that character
                                    case ("mario"):
                                        player = new Player(Server.spawnPoint, tempPlayer.getUserName(), tempPlayer.getCharacterName(), MARIO_DIMENSIONS, false, true, false);
                                        break;
                                    case ("pepe"):
                                        player = new Player(Server.spawnPoint, tempPlayer.getUserName(), tempPlayer.getCharacterName(), PEPE_DIMENSIONS, false, true, false);
                                        break;
                                    case ("trump"):
                                        player = new Player(Server.spawnPoint, tempPlayer.getUserName(), tempPlayer.getCharacterName(), TRUMP_DIMENSIONS, false, true, false);
                                        break;
                                    case ("Mushroomba"):
                                        player = new Player(Server.spawnPoint, tempPlayer.getUserName(), tempPlayer.getCharacterName(), MUSHROOM_DIMENSIONS, false, true, false);
                                        break;
                                    default:
                                        player = (Player) fromUser;
                                        break;
                                }
                                player.setUserName(makeUserName());
                                Server.players.add(player); // Add's the player to the Server-wide ArrayList containing all players
                                movementTimer.scheduleAtFixedRate(movementTimerTask, 1, TICK_RATE); // Starts the movementTimer so it's called TICKRATE times every second

                                sendLevel = Server.levelLoaded;
                            } else if (fromUser instanceof Integer) {
                                if (lobbyHost) {
                                    loadLevel((Integer) fromUser);
                                    System.out.println("Loading level " + fromUser);
                                }
                            } else
                                System.err.println("Unknown data sent to server: \t" + fromUser);

                        }
                        connected = false;
                    } catch (Exception e) {
                        if (e instanceof EOFException || e instanceof SocketException)
                            endPlayerSession();
                        else {
                            e.printStackTrace();
                            endPlayerSession();
                        }
                    }
                }
            };
            listenThread.start();

            //Sends info to client
            HashMap<String, Boolean> tempSounds;
            HashMap<Player, Integer> tempRanks;
            Snapshot oldSnapshot = new Snapshot(new ArrayList<Player>(), new ArrayList<Mushroom>(), new HashMap<Player, Integer>(), new HashMap<String, Boolean>(), -1, false); // Holds the previous snapshot
            Snapshot currentSnapshot;

            objectOut.writeObject(lobbyHost);
            objectOut.reset();

            while (connected) {
                currentSnapshot = new Snapshot(Server.players, new ArrayList<>(Server.mushrooms.keySet()), Server.playerWinStatus, Server.sounds, Server.mapTime, Server.gameWon);
                if (!currentSnapshot.equals(oldSnapshot)) { // If there's a difference between the current snapshot and the previous one sent, it sends a new snapshot
                    writeObject(currentSnapshot);
                    // Sets oldSnapshot equal to currentSnapshot
                    // Making duplicates of HashMap objects is difficult, so you need these loops to individually copyPlayerArrayList each index in the various HashMaps
                    tempSounds = new HashMap<>();
                    for (String key : Server.sounds.keySet())
                        tempSounds.put(key, Server.sounds.get(key));
                    tempRanks = new HashMap<>();
                    for (Player key : Server.playerWinStatus.keySet())
                        tempRanks.put(key, Server.playerWinStatus.get(key));

                    oldSnapshot = new Snapshot(copyPlayerArrayList(Server.players), copyMushroomArrayList(new ArrayList<>(Server.mushrooms.keySet())), tempRanks, tempSounds, currentSnapshot.getTimeLeft(), Server.gameWon);
                    Server.stopVictorySound(); // Here so that VICTORY in the sounds HashMap gets reset to false so that it can be played again (set to true again)
                    Server.stopCrushSound(); // Same as above.
                }
                if (sendLevel) { // TODO this code segment is activating before the player's username can get set sometimes... hence, the client doesn't know who the player is
                    try {
                        writeObject(player.getUserName());
                        writeObject(Server.currentLevel);
                        if (!Server.backgroundColor.equals(DEFAULT_BACKGROUND))
                            writeObject(Server.backgroundColor);
                        sendLevel = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //Sleeps thread for x milliseconds so that it sends a snapshot TICKRATE times every second
                try {
                    Thread.sleep(MESSAGE_SEND_RATE);
                } catch (Exception e) {
                    if (!(e instanceof InterruptedException))
                        e.printStackTrace();
                }
            }
            endPlayerSession();
        } catch (Exception e) {
            if (!(e instanceof SocketException)) // If it's not a SocketException (user disconnects), it prints out an error
                e.printStackTrace();
            else
                endPlayerSession();
        }
    }

    private void writeObject(Object o) {
        try {
            objectOut.writeObject(o);
            objectOut.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLevel(int levelNumber) {
        Server.levelLoaded = false;
        Server.loadLevel(levelNumber);
        sendLevel = true;
    }

    public void sendLevel() {
        sendLevel = true;
    }

    public void resetCharacter() {
        jumping = false;
        for (String key : keyInputs.keySet())
            keyInputs.put(key, false);
        player.faceRight();
        player.setMoving(false);
        player.setDeathStatus(false);
    }

    private String makeUserName() {
        for (Player p : Server.players)
            if (p.getUserName().equals(player.getUserName()))
                return makeUserName(1);
        return player.getUserName();
    }

    private String makeUserName(int num) {
        String uname = player.getUserName() + " (" + num + ")";
        //System.out.println("trying " + uname + "...");
        for (Player p : Server.players)
            if (p.getUserName().equals(uname))
                return makeUserName(num + 1);
        return uname;
    }

    public void die() {
        if (!player.isDead()) {
            Server.playDieSound();
            player.setDeathStatus(true);
            player.setMoving(false);
            deathTimer.schedule(new DeathTimerTask(), RESPAWN_TIME);
            jumping = false;
        }
    }

    private boolean isSolidBlock(int val) {
        for (int i : SOLID_BLOCKS)
            if (val == i)
                return true;
        return false;
    }

    private boolean isDeadlyBlock(int val) {
        for (int i : DEADLY_BLOCKS)
            if (val == i) {
                return true;
            }
        return false;
    }

    private boolean canMove(int dx, int dy) {
        if (Server.levelCollisionBoxes != null) {
            // Checks if the user is trying to move out of the bounds of the map
            if (player.getLocation().x + dx < 0 || player.getLocation().x + player.getDimensions().width + dx >= Server.currentLevel[0].length * BLOCK_SIZE)
                return false;
            if (player.getLocation().y + dy < 0 || player.getLocation().y + dy >= Server.currentLevel.length * BLOCK_SIZE)
                return false;

            // Checks if the player's hitbox will intersect with any of the blocks' hitboxes
            Rectangle playerRect_new = new Rectangle(player.getLocation().x + dx, player.getLocation().y + dy, player.getDimensions().width, player.getDimensions().height);
            for (int r = 0; r < Server.levelCollisionBoxes.length; r++)
                for (int c = 0; c < Server.levelCollisionBoxes[0].length; c++)
                    if (playerRect_new.intersects(Server.levelCollisionBoxes[r][c]) && isSolidBlock(Server.currentLevel[r][c]))
                        return false;

            if (!canIntersectPlayers)
                for (Player p : Server.players)
                    if (!p.getUserName().equals(player.getUserName()) && playerRect_new.intersects(p.getRectangle()))
                        return false;

            return true;
        } else
            return false;
    }

    // Checks if the player is on the ground
    private boolean onGround() {

        int pixelBelowPlayer = player.getLocation().y + player.getDimensions().height + 1;
        int playerX = player.getLocation().x;
        for (Player p : Server.players)
            if (p.getRectangle().contains(new Point(playerX, pixelBelowPlayer)) ||
                    p.getRectangle().contains(new Point(playerX + player.getDimensions().width - 1, pixelBelowPlayer)) ||
                    p.getRectangle().contains(new Point(playerX + player.getDimensions().width / 2, pixelBelowPlayer)))
                return true; // Return true if the player is directly on top of another player


        int playerFootLevel = (player.getLocation().y + player.getDimensions().height) / BLOCK_SIZE;
        int playerRightSide = (player.getLocation().x + player.getDimensions().width - 1) / BLOCK_SIZE;
        int playerLeftSide = player.getLocation().x / BLOCK_SIZE;

        // Makes sure there is a solid block on both the left or right side of the player
        return isSolidBlock(Server.currentLevel[playerFootLevel][playerRightSide]) || isSolidBlock(Server.currentLevel[playerFootLevel][playerLeftSide]);
    }

    // Checks if the player is in a position where they should be dead (e.g. colliding w/ a deadly block)
    private boolean willDie() {
        int playerFootLocation = (player.getLocation().y + player.getDimensions().height);
        if (playerFootLocation >= Server.currentLevel.length * BLOCK_SIZE)
            return true;

        int playerFootLevel = (player.getLocation().y + player.getDimensions().height - 1) / BLOCK_SIZE;
        int playerRightSide = (player.getLocation().x + player.getDimensions().width - 1) / BLOCK_SIZE;
        int playerLeftSide = player.getLocation().x / BLOCK_SIZE;
        if (touchingMushroom())
            return true;


        // Checks if there is a deadly block on the left or right side of the player
        return isDeadlyBlock(Server.currentLevel[playerFootLevel][playerRightSide]) || isDeadlyBlock(Server.currentLevel[playerFootLevel][playerLeftSide]);
    }

    private boolean touchingMushroom() {
        try {
            ArrayList<Mushroom> mushrooms = new ArrayList<>(Server.mushrooms.keySet());
            int playerBoxHeight = player.getDimensions().height - GRAVITY_SPEED;
            Rectangle playerRect = new Rectangle(player.getLocation().x, player.getLocation().y, player.getDimensions().width, playerBoxHeight);
            for (Mushroom mushroom : mushrooms) {
                Rectangle rectMushroom = new Rectangle(mushroom.getLocation().x, mushroom.getLocation().y, mushroom.getDimension().width, mushroom.getDimension().height);
                if (rectMushroom.intersects(playerRect))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void doHop() {
        movementTimerTask.hop();
    }

    // Gets the index of the thread's player object in the server-wide player ArrayList
    private int getPlayerIndex() {
        for (int i = 0; i < Server.players.size(); i++)
            if (Server.players.get(i).getUserName().equals(player.getUserName()))
                return i;
        return -1;

    }

    public Player getPlayer() {
        return player;
    }

    private ArrayList<Player> copyPlayerArrayList(ArrayList<Player> players) {
        ArrayList<Player> clonedPlayers = new ArrayList<>();
        for (Player p : players)
            clonedPlayers.add(p.clonePlayer());
        return clonedPlayers;
    }

    private ArrayList<Mushroom> copyMushroomArrayList(ArrayList<Mushroom> shrooms) {
        ArrayList<Mushroom> clonedShrooms = new ArrayList<>();
        for (Mushroom m : shrooms) {
            clonedShrooms.add(m.cloneMushroom());
        }
        return clonedShrooms;
    }

    // Provides a clean exit for the player... all loose ends are tied up
    private void endPlayerSession() {
        if (connected) {
            connected = false;
            System.err.println(player.getUserName() + " @ " + socket.getInetAddress().getHostAddress() + " disconnected.");
            Server.userCount--;
            movementTimer.cancel();
            if (Server.GUI_enabled)
                Server.gui.removeUser(socket.getInetAddress().getHostAddress());
            Server.removePlayer(player);
            Server.serverThreads.remove(this);
            try {
                objectOut.close();
                objectIn.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        interrupt();
    }

    // Timer that converts the client's keyInputs to moving the player's location
    private class MovementTimer extends TimerTask {
        final double horizontal_accel_constant = HORIZONTAL_MOVEMENT_SPEED * ACCELERATION_PERCENTAGE;
        final double horizontal_decel_constant = HORIZONTAL_MOVEMENT_SPEED * DECELERATION_PERCENTAGE;
        final int minimum_jump_height = MINIMUM_JUMP_HEIGHT + player.getDimensions().height;
        final int maximum_jump_height = MAXIMUM_JUMP_HEIGHT + player.getDimensions().height;
        boolean falling;
        boolean inOriginalJump = true;
        int jumpSpeed;
        double speed = 0;
        int roundedSpeed;

        public void run() {
            falling = false; // If the player is currently falling due to gravity
            player.setMoving(false);
            //System.out.println(keyInputs);
            if (keyInputs != null && keyInputs.keySet().containsAll(Arrays.asList(validKeyInputs)) && Server.levelLoaded) {
                // Gravity still applies to a player, even if he's dead.
                if (!jumping)
                    // Moves the player down the maximum amount <= GRAVITY_SPEED, if the player can't move down GRAVITY_SPEED units, then it decrements GRAVITY_SPEED by 1 and tries again
                    for (int g = GRAVITY_SPEED; g > 0; g--)
                        // If the player can still move down g pixels then move them down
                        if (canMove(0, g)) {
                            player.setLocation(new Point(player.getLocation().x, player.getLocation().y + g));
                            falling = true;
                            break;
                        } else
                            falling = false;

                if (!player.isDead() && !Server.gameWon) {
                    if (willDie())
                        die();

                    // The player won't have player intersection after the no-collide time from their spawn
                    if (Server.mapTime < lastDeathTime - RESPAWN_NO_COLLIDE_TIME)
                        canIntersectPlayers = false;

                    // Teleports the player out of an intersection if they somehow are intersecting another player
                    if (!canIntersectPlayers)
                        for (Player p : Server.players)
                            if (!p.getUserName().equals(player.getUserName()) && player.getRectangle().intersects(p.getRectangle()))
                                if (canMove(p.getDimensions().width, 0))
                                    player.setLocation(new Point(player.getLocation().x + p.getDimensions().width, player.getLocation().y));
                                else if (canMove(-p.getDimensions().width, 0))
                                    player.setLocation(new Point(player.getLocation().x - p.getDimensions().width, player.getLocation().y));
                                else if (canMove(0, -p.getDimensions().height - 1))
                                    player.setLocation(new Point(player.getLocation().x, player.getLocation().y - p.getDimensions().height - 1));

                    if (jumping) {
                        player.setMoving(false);
                        if (canMove(0, -jumpSpeed)) { // jumpSpeed has to be negative because you're moving up on the screen, thus decreasing your y-pixel value
                            Server.stopJumpSound();
                            player.setLocation(new Point(player.getLocation().x, player.getLocation().y - jumpSpeed));
                            jumpCount--;
                            /*if (jumpSpeed > APEX_SPEED && jumpCount % (JUMP_SPEED - APEX_SPEED) == 0) {
                                jumpSpeed--;
                                System.out.println("jumpCount = " + jumpCount);
                                if (jumpCount > 0)
                                    if (inOriginalJump)
                                        jumpCount = (minimum_jump_height / jumpSpeed) - jumpCount;
                                    else {
                                        jumpCount = (maximum_jump_height - minimum_jump_height) / jumpSpeed - jumpCount;
                                    }
                            }*/
                            if (jumpCount == 0 && keyInputs.get("w") && inOriginalJump) { // Starts extended jump
                                jumpSpeed = EXTENDED_JUMP_SPEED;
                                jumpCount = (maximum_jump_height - minimum_jump_height) / jumpSpeed;
                                inOriginalJump = false;
                            } else if (!inOriginalJump && (!keyInputs.get("w") || jumpCount == 0)) { // Ends extended jump
                                jumping = false;
                                jumpCount = 0;
                            } else if (jumpCount == 0) { // Ends original jump if not doing an extended jump
                                jumping = false;
                                falling = true;
                            }
                        } else {
                            jumpCount = 0;
                            jumping = false;
                        }
                    }

                    // If the player is jumping or falling, then set their horizontal speed to AIR_STRAFE_SPEED, which is less than HORIZONTAL_MOVEMENT SPEED
                    // This is done because it doesn't feel 'right' if the player can move left and right super fast while in mid-air
                    if (jumping || falling) {
                        if (keyInputs.get("a")) {
                            if (speed > -AIR_STRAFE_SPEED)
                                speed -= horizontal_accel_constant;
                            else
                                speed = -AIR_STRAFE_SPEED;
                            player.setMoving(true);
                            player.faceLeft();
                        }
                        if (keyInputs.get("d")) {
                            if (speed < AIR_STRAFE_SPEED)
                                speed += horizontal_accel_constant;
                            else
                                speed = AIR_STRAFE_SPEED;
                            player.setMoving(true);
                            player.faceRight();
                        }
                    } else {
                        if (keyInputs.get("w")) // Initiates jumping... can't do this while jumping or falling
                            if (!jumping && canMove(0, -JUMP_SPEED) && onGround()) { // You have to be not jumping and on the ground
                                jumpSpeed = JUMP_SPEED;
                                //player.setLocation(new Point(player.getLocation().x, player.getLocation().y - JUMP_SPEED));
                                jumping = true; // Sets this to true, so the if(jumping) statement above is activated, and thus the jumping movement script is used instead of just flying upwards
                                inOriginalJump = true;
                                jumpCount = minimum_jump_height / JUMP_SPEED; // Sets jumpCount so that you move up JUMP_SPEED times to reach minimum_jump_height
                                Server.playJumpSound();
                            }
                        // Regular horizontal movement
                        if (keyInputs.get("a")) {
                            if (speed > -HORIZONTAL_MOVEMENT_SPEED)
                                speed -= horizontal_accel_constant;
                            player.setMoving(true);
                            player.faceLeft();
                        }
                        if (keyInputs.get("d")) {
                            if (speed < HORIZONTAL_MOVEMENT_SPEED)
                                speed += horizontal_accel_constant;
                            player.setMoving(true);
                            player.faceRight();
                        }
                    }
                }
                // Decelerates the player
                if (!player.isMoving())
                    if (speed > -horizontal_decel_constant && speed < horizontal_decel_constant)
                        speed = 0;
                    else if (speed > 0)
                        speed -= horizontal_decel_constant;
                    else if (speed < 0)
                        speed += horizontal_decel_constant;

                // Moves the player using the rounded speed, since you can't move him .5 pixels
                // (even if you did use decimal coordinates for the player, the Graphics class just rounds up or down when drawing it on the screen so there's no point)
                roundedSpeed = (int) Math.round(speed);
                if (roundedSpeed != 0 && canMove(roundedSpeed, 0))
                    player.setLocation(new Point(player.getLocation().x + roundedSpeed, player.getLocation().y));
                else if (!canMove(roundedSpeed, 0))
                    speed = 0;
            }
            Server.players.set(getPlayerIndex(), player);
        }

        public void hop() {
            falling = false;
            //player.setLocation(new Point(player.getLocation().x, player.getLocation().y - JUMP_SPEED));
            jumping = true; // Sets this to true, so the if(jumping) statement above is activated, and thus the jumping movement script is used instead of just flying upwards
            jumpSpeed = JUMP_SPEED;
            inOriginalJump = true;
            jumpCount = (int) ((minimum_jump_height / JUMP_SPEED) / 1.5); // A portion of the regular jump-height
        }
    }

    private class DeathTimerTask extends TimerTask {
        public void run() {
            player.setLocation(Server.spawnPoint);
            Server.stopDieSound();
            player.setDeathStatus(false);
            lastDeathTime = Server.mapTime;
            for (Player p : Server.players)
                if (!p.getUserName().equals(player.getUserName()))
                    canIntersectPlayers = player.getRectangle().intersects(p.getRectangle());
        }
    }
}