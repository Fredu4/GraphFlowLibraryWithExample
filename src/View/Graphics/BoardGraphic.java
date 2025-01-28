package View.Graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BoardGraphic implements Graphic{

    private final BufferedImage graphic;

    public BoardGraphic(int boardWidth, int boardHeight, int pinRadius, ArrayList<Point> pinPositions){
        this.graphic = new BufferedImage(boardWidth, boardHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = graphic.createGraphics();
        g.setColor(Color.BLACK);
        for(Point pinPoint : pinPositions)
            g.fillRect(pinPoint.x, pinPoint.y, pinRadius, pinRadius);
        g.dispose();
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(this.graphic, 0, 0, null);
    }
}
