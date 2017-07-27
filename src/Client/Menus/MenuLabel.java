package Client.Menus;

import java.awt.*;

/**
 * Created by david on 3/2/16.
 */
public class MenuLabel {
    private int x;
    private int y;
    private Point originalLocation;
    private String text;
    private Font font;
    private Color textColor;

    public MenuLabel(int x, int y, String text, Font font) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.font = font;
        originalLocation = new Point(x, y);
        textColor = Color.WHITE;
    }

    public MenuLabel(int x, int y, String text, Font font, Color color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.font = font;
        originalLocation = new Point(x, y);
        textColor = color;
    }

    public Font getFont() {
        return font;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLocation(Point p) {
        x = p.x;
        y = p.y;
    }

    public Point getLocation(){
        return new Point(x,y);
    }

    public Point getOriginLoc() {
        return originalLocation;
    }

    public void setOriginLoc(Point p){
        originalLocation = p;
    }

    public void draw(Graphics2D g2d) {
        //GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //ge.registerFont(font);
        Color prevColor = g2d.getColor();
        Font prevFont = g2d.getFont();

        g2d.setColor(textColor);
        g2d.setFont(font);
        g2d.drawString(text, x, y);

        g2d.setColor(prevColor);
        g2d.setFont(prevFont);
    }

    @Override
    public String toString() {
        return "MenuLabel{" +
                "textColor=" + textColor +
                ", text='" + text + '\'' +
                ", originalLocation=" + originalLocation +
                '}';
    }
}
