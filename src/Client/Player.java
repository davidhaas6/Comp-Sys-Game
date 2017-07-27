package Client;

import java.awt.*;
import java.io.Serializable;


/**
 * Created by David on 1/23/2016.
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1420672609912364060L;
    private final String characterName;
    private final Dimension dimensions;
    private String userName;
    private Point location;
    private boolean dead;
    private boolean moving;
    private boolean facingRight;

    public Player(Point location, String userName, String characterName) {
        this.location = location;
        this.userName = userName;
        this.characterName = characterName;
        dimensions = new Dimension(12, 16);

        dead = false;
    }

    public Player(Point location, String userName, String characterName, Dimension dimensions, boolean moving, boolean facingRight, boolean dead) {
        this.location = location;
        this.userName = userName;
        this.characterName = characterName;
        this.dimensions = dimensions;
        this.dead = dead;
        this.moving = moving;
        this.facingRight = facingRight;
    }

    public Point getLocation() {
        return location;
    }

    public Rectangle getRectangle(){
        return new Rectangle(location.x,location.y,dimensions.width,dimensions.height);
    }

    public void setLocation(Point newLoc) {
        location = newLoc;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public Dimension getDimensions() {
        return dimensions;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDeathStatus(boolean dead) {
        this.dead = dead;
    }

    public void faceLeft() {
        facingRight = false;
    }

    public void faceRight() {
        facingRight = true;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean move) {
        moving = move;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public Player clonePlayer() {
        return new Player((Point) location.clone(), userName, characterName, (Dimension) dimensions.clone(), moving, facingRight, dead);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (dead != player.dead) return false;
        if (moving != player.moving) return false;
        if (facingRight != player.facingRight) return false;
        if (!userName.equals(player.userName)) return false;
        if (!characterName.equals(player.characterName)) return false;
        return dimensions.equals(player.dimensions) && location.equals(player.location);

    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "Player{" +
                "userName='" + userName + '\'' +
                ", characterName='" + characterName + '\'' +
                ", location=" + location +
                ", dead=" + dead +
                '}';
    }
}
