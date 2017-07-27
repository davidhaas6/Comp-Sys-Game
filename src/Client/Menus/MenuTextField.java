package Client.Menus;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by David on 3/1/2016.
 */
public class MenuTextField {
    private Rectangle boundingRect;
    public int x, y, width, height;
    private boolean inFocus;
    private String text;
    private Font font;
    private int maxLength;

    public MenuTextField(Rectangle rectangle, Font font, int maxInputLength) {
        this.font = font;
        boundingRect = rectangle;
        x = rectangle.x;
        y = rectangle.y;
        width = rectangle.width;
        height = rectangle.height;
        text = "";
        maxLength = maxInputLength;
    }

    public void appendText(String add) {
        if ((add.length() + text.length()) <= maxLength)
            text += add;
    }

    public String getText() {
        return text;
    }

    public void setText(String inputText) {
        if (inputText.length() <= maxLength)
            text = inputText;
        else
            text = inputText.substring(0, maxLength);
    }

    public Rectangle getRectangle() {
        return boundingRect;
    }

    public void setRectangle(Rectangle rect) {
        boundingRect = rect;
    }

    public void setFocus(boolean focus) {
        inFocus = focus;
    }

    public boolean isInFocus() {
        return inFocus;
    }

    public void draw(Graphics2D g2d) {
        Font prevFont = g2d.getFont();
        float thickness = 4;
        Stroke oldStroke = g2d.getStroke();
        g2d.setColor(Color.white);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.drawRect(boundingRect.x, boundingRect.y, width, height);
        g2d.setStroke(oldStroke);
        g2d.setFont(font);

        Shape outline = font.createGlyphVector(g2d.getFontMetrics().getFontRenderContext(), text).getOutline();
        // the shape returned is located at the left side of the baseline, this means we need to re-align it to the top left corner. We also want to set it the the center of the screen while we are there
        AffineTransform transform = AffineTransform.getTranslateInstance(
                -outline.getBounds().getX() + width / 2 - outline.getBounds().width / 2,
                -outline.getBounds().getY() + height / 2 - outline.getBounds().height / 2);
        outline = transform.createTransformedShape(outline);
        //g2d.fill(outline);
        Rectangle outlineBounds = outline.getBounds();

        int strYPos = (height / 2) + (outlineBounds.height / 2) + boundingRect.y;
        int strXPos = (width - g2d.getFontMetrics().stringWidth(text)) / 2 + boundingRect.x;

        g2d.drawString(text, strXPos, strYPos);
        g2d.setFont(prevFont);
    }
}
