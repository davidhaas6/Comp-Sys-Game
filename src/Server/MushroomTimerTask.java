package Server;

import Client.Mushroom;

import java.awt.*;
import java.util.TimerTask;

/**
 * Created by Crystal on 3/2/2016.
 */
class MushroomTimerTask extends TimerTask implements Constants {
    private final int MAX_DISTANCE_RIGHT = (MUSHROOM_MOVEMENT_DISTANCE / MUSHROOM_HORIZONTAL_MOVEMENT_SPEED) / 2;
    private final int MAX_DISTANCE_LEFT = -(MUSHROOM_MOVEMENT_DISTANCE / MUSHROOM_HORIZONTAL_MOVEMENT_SPEED) / 2;
    private Mushroom mushroom;
    private Point spawnPoint;
    private int moveCounter = 0;
    private boolean moveRight = true;
    private boolean falling = false;
    private boolean jumping = false;
    private int jumpCount = MUSHROOM_JUMP_HEIGHT / MUSHROOM_VERTICAL_MOVEMENT_SPEED * 2;
    private int jumpSpeed = MUSHROOM_VERTICAL_MOVEMENT_SPEED;

    MushroomTimerTask(Mushroom mushroom) {
        this.mushroom = mushroom;
        this.spawnPoint = mushroom.getLocation();
        mushroom.setLocation(new Point(spawnPoint.x + (BLOCK_SIZE / 2), spawnPoint.y));
    }

    public void run() {
        int deltaX, deltaY;
        // Horizontal movement
        if (moveRight && !jumping && !falling) {
            deltaX = MUSHROOM_HORIZONTAL_MOVEMENT_SPEED;
            deltaY = 0;
            if (!willFallOffEdge(deltaX, deltaY) && canMove(deltaX, deltaY)) {
                mushroom.setLocation(new Point(mushroom.getLocation().x + deltaX, mushroom.getLocation().y + deltaY));
                moveCounter++;
            } else {
                moveRight = false;
                moveCounter = MAX_DISTANCE_RIGHT;
            }
            if (moveCounter == MAX_DISTANCE_RIGHT) {
                moveRight = false;
                jumping = true;
            }
        } else if (!moveRight && !jumping && !falling) {
            deltaX = -MUSHROOM_HORIZONTAL_MOVEMENT_SPEED;
            deltaY = 0;
            if (!willFallOffEdge(deltaX, deltaY) && canMove(deltaX, deltaY)) {
                mushroom.setLocation(new Point(mushroom.getLocation().x + deltaX, mushroom.getLocation().y + deltaY));
                moveCounter--;
            } else {
                moveRight = true;
                moveCounter = MAX_DISTANCE_LEFT;
            }
            if (moveCounter == MAX_DISTANCE_LEFT) {
                moveRight = true;
                jumping = true;
            }
        } else if (jumping && !falling) {
            if (jumpCount <= (MUSHROOM_JUMP_HEIGHT / MUSHROOM_VERTICAL_MOVEMENT_SPEED) / 2) // Slows the velocity of the jump by 1 if they're halfway through their jump
                jumpSpeed = MUSHROOM_VERTICAL_MOVEMENT_SPEED - 1;
            if (canMove(0, -jumpSpeed)) { // jumpSpeed has to be negative because you're moving up on the screen, thus decreasing your y-pixel value
                mushroom.setLocation(new Point(mushroom.getLocation().x, mushroom.getLocation().y - jumpSpeed));
                jumpCount--;
                if (jumpCount == 0) { // Stops jumping
                    falling = true;
                    jumping = false;
                    jumpCount = MUSHROOM_JUMP_HEIGHT / MUSHROOM_VERTICAL_MOVEMENT_SPEED;
                }
            } else {
                jumpCount = MUSHROOM_JUMP_HEIGHT / MUSHROOM_VERTICAL_MOVEMENT_SPEED;
                falling = true;
                jumping = false;
            }
        } else {
            for (int g = GRAVITY_SPEED; g > 0; g--)
                if (canMove(0, g))
                    mushroom.setLocation(new Point(mushroom.getLocation().x, mushroom.getLocation().y + g));
                else {
                    falling = false;
                }
        }
    }


    private boolean canMove(int dx, int dy) {
        if (Server.levelCollisionBoxes != null) {
            // Checks if the user is trying to move out of the bounds of the map
            if (mushroom.getLocation().x + dx < 0 ||
                    mushroom.getLocation().x + AI_MUSHROOM_DIMENSIONS.width + dx >= Server.currentLevel[0].length * BLOCK_SIZE ||
                    mushroom.getLocation().y + dy < 0 ||
                    mushroom.getLocation().y + dy >= Server.currentLevel.length * BLOCK_SIZE) {
                return false;
            }


            // Checks if the player's hitbox will intersect with any of the blocks' hitboxes
            Rectangle mushroomRect_new = new Rectangle(mushroom.getLocation().x + dx, mushroom.getLocation().y + dy, AI_MUSHROOM_DIMENSIONS.width, AI_MUSHROOM_DIMENSIONS.height);
            for (int r = 0; r < Server.levelCollisionBoxes.length; r++)
                for (int c = 0; c < Server.levelCollisionBoxes[0].length; c++)
                    if (mushroomRect_new.intersects(Server.levelCollisionBoxes[r][c]) && isSolidBlock(Server.currentLevel[r][c]))
                        return false;
            return true;
        } else
            return false;
    }

    private boolean willFallOffEdge(int dx, int dy) {
        try {
            int mushroomFootLevel = (mushroom.getLocation().y + dy + AI_MUSHROOM_DIMENSIONS.height) / BLOCK_SIZE;
            int mushroomRightSide = (mushroom.getLocation().x + dx + AI_MUSHROOM_DIMENSIONS.width - 1) / BLOCK_SIZE;
            int mushroomLeftSide = (mushroom.getLocation().x + dx) / BLOCK_SIZE;

            return !isSolidBlock(Server.currentLevel[mushroomFootLevel][mushroomRightSide]) || !isSolidBlock(Server.currentLevel[mushroomFootLevel][mushroomLeftSide]);
        } catch (Exception e) {
            stopTimer();
        }
        return true;
    }

    private boolean onGround() {
        int mushroomFootLevel = (mushroom.getLocation().y + AI_MUSHROOM_DIMENSIONS.height) / BLOCK_SIZE;
        int mushroomRightSide = (mushroom.getLocation().x + AI_MUSHROOM_DIMENSIONS.width - 1) / BLOCK_SIZE;
        int mushroomLeftSide = mushroom.getLocation().x / BLOCK_SIZE;

        // Makes sure there is a solid block on both the left or right side of the player
        return isSolidBlock(Server.currentLevel[mushroomFootLevel][mushroomRightSide]) || isSolidBlock(Server.currentLevel[mushroomFootLevel][mushroomLeftSide]);
    }

    private boolean isSolidBlock(int val) {
        for (int i : SOLID_BLOCKS)
            if (val == i)
                return true;
        return false;
    }

    public void stopTimer() {
        cancel();
    }

    @Override
    public String toString() {
        return "MushroomTimerTask{" +
                "spawnPoint=" + spawnPoint +
                ", mushroom=" + mushroom +
                '}';
    }
}