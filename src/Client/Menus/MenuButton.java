package Client.Menus;

import Client.SoundController;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by David on 2/8/2016.
 */
public class MenuButton {
    private String text;
    public int x, y, width, height;
    private Rectangle rect;
    private Image buttonImage, buttonDownImg, buttonUpImage;
    private Font font;
    private boolean isButtonPressed;


    public MenuButton(Image buttonUpImage, Image buttonDownImage, String text, Font font, int x, int y, int width, int height) {
        this.buttonImage = buttonUpImage;
        this.buttonUpImage = buttonUpImage;
        this.buttonDownImg = buttonDownImage;
        this.font = font;
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        isButtonPressed = false;
        rect = new Rectangle(x, y, width, height);
    }

    private Image getButtonImage() {
        return buttonImage;
    }

    private void setButtonImage(Image buttonImage) {
        this.buttonImage = buttonImage;
    }

    private Font getFont() {
        return font;
    }

    public Rectangle getBoundingRect() {
        return rect;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRectangle(Rectangle rect) {
        this.rect = rect;
        width = rect.width;
        height = rect.height;
    }

    public boolean isPressed() {
        return isButtonPressed;
    }

    public void setPressed(boolean isPressed) {
        if (isButtonPressed && !isPressed)
            playClickSound();
        this.isButtonPressed = isPressed;
        if (isPressed)
            setButtonImage(buttonDownImg);
        else
            setButtonImage(buttonUpImage);

    }

    private void playClickSound() {
        SoundController.playButtonClickSound();
    }

    public void draw(Graphics2D g2d, int parentWidth, int parentHeight) {
        g2d.setColor(Color.WHITE);
        g2d.drawImage(getButtonImage(), rect.x, rect.y, width, height, null);
        Font prevFont = g2d.getFont();
        g2d.setFont(getFont());
        int strXPos = (width - g2d.getFontMetrics().stringWidth(text)) / 2 + rect.x;

        Shape outline = getFont().createGlyphVector(g2d.getFontMetrics().getFontRenderContext(), text).getOutline();
        // the shape returned is located at the left side of the baseline, this means we need to re-align it to the top left corner. We also want to set it the the center of the screen while we are there
        AffineTransform transform = AffineTransform.getTranslateInstance(
                -outline.getBounds().getX() + parentWidth / 2 - outline.getBounds().width / 2,
                -outline.getBounds().getY() + parentHeight / 2 - outline.getBounds().height / 2);
        outline = transform.createTransformedShape(outline);
        //g2d.fill(outline);
        Rectangle outlineBounds = outline.getBounds();
        int strYPos = (height / 2) + (outlineBounds.height / 2) + rect.y;

        //TODO Fix incorrect vertical placement for text
        g2d.drawString(text, strXPos, strYPos);

        g2d.setFont(prevFont);
    }

    @Override
    public String toString() {
        return "MenuButton{" +
                "text='" + text + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", isButtonPressed=" + isButtonPressed +
                '}';
    }
}

