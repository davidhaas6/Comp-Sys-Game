package Client;

import java.awt.*;
import java.io.Serializable;


/**
 * Created by Crystal on 2/9/2016.
 */
public class Mushroom implements Serializable {
    private static final long serialVersionUID = 1420672609912364060L;
    private final Dimension dimensions;
    private Point location;

    public Mushroom(Point location, Dimension x) {
        this.location = location;
        dimensions = x;
    }

    public Dimension getDimension() {
        return dimensions;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point p) {
        location = p;
    }

    public Mushroom cloneMushroom() {
        return new Mushroom((Point) location.clone(), (Dimension) dimensions.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mushroom mushroom = (Mushroom) o;

        return !(dimensions != null ? !dimensions.equals(mushroom.dimensions) : mushroom.dimensions != null) && !(location != null ? !location.equals(mushroom.location) : mushroom.location != null);
    }

    @Override
    public String toString() {
        return "Mushroom{" +
                "location=" + location +
                '}';
    }
}


