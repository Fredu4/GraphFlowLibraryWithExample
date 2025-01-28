package View.Graphics;

import java.awt.*;

public class BoardPinGraphic implements Graphic{

    private final Point point;
    private final int size;
    private final Color color;

    public BoardPinGraphic(Point point, int size, boolean isHighlighted){
        this.point = point;
        this.size = size;
        if(isHighlighted) color = Color.GREEN;
        else color = Color.BLACK;
    }

    @Override
    public void paint(Graphics g){
        g.setColor(color);
        g.fillOval(point.x - (size/2), point.y - (size/2), size, size);
    }
}
