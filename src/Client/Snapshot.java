package Client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by David on 1/23/2016.
 */

// An object that holds information relevant to the game being played, used to easily transmit that data to the client in one object
public class Snapshot implements Serializable {
    private static final long serialVersionUID = 1420672609912364060L;
    private final ArrayList<Player> players;
    private final ArrayList<Mushroom> mushrooms;
    private final HashMap<String, Boolean> sounds;
    private final HashMap<Player, Integer> playerWinStatus;
    private final int timeLeft;
    private final boolean gameWon;

    public Snapshot(ArrayList<Player> players, ArrayList<Mushroom> mushrooms, HashMap<Player, Integer> playerWinStatus, HashMap<String, Boolean> sounds, int timeLeft, boolean gameWon) {
        this.players = players;
        this.mushrooms = mushrooms;
        this.timeLeft = timeLeft;
        this.sounds = sounds;
        this.gameWon = gameWon;
        this.playerWinStatus = playerWinStatus;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Mushroom> getMushrooms() {
        return mushrooms;
    }

    public HashMap<String, Boolean> getSounds() {
        return sounds;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public HashMap<Player, Integer> getPlayerWinStatus() {
        return playerWinStatus;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Snapshot snapshot = (Snapshot) o;

        if (timeLeft != snapshot.timeLeft) return false;
        if (gameWon != snapshot.gameWon) return false;
        if (players != null ? !players.equals(snapshot.players) : snapshot.players != null) return false;
        if (mushrooms != null ? !mushrooms.equals(snapshot.mushrooms) : snapshot.mushrooms != null) return false;
        return !(sounds != null ? !sounds.equals(snapshot.sounds) : snapshot.sounds != null) && !(playerWinStatus != null ? !playerWinStatus.equals(snapshot.playerWinStatus) : snapshot.playerWinStatus != null);
    }

    @Override
    public int hashCode() {
        int result = players != null ? players.hashCode() : 0;
        result = 31 * result + (mushrooms != null ? mushrooms.hashCode() : 0);
        result = 31 * result + (sounds != null ? sounds.hashCode() : 0);
        result = 31 * result + (playerWinStatus != null ? playerWinStatus.hashCode() : 0);
        result = 31 * result + timeLeft;
        result = 31 * result + (gameWon ? 1 : 0);
        return result;
    }

    public String toString() {
        return "Snapshot{" +
                "players=" + players +
                ", sounds=" + sounds +
                ", timeLeft=" + timeLeft +
                ", mushrooms=" + mushrooms +
                "}";
    }
}
