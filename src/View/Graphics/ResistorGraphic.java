package View.Graphics;

import java.awt.*;

public class ResistorGraphic implements ComponentGraphic {

    private final Point point;
    private final int width;
    private final int height;
    private final String value;

    public ResistorGraphic(Point topLeftCorner, int width, int height, int value){
        this.point = topLeftCorner;
        this.width = width;
        this.height = height;
        this.value = String.valueOf(value) + "Ω";
    }

    @Override
    public void paint(Graphics g){
        g.setColor(Color.yellow);
        g.fillRect(point.x + 1, point.y + 1, width, height);
        
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(value, point.x + (width/2), point.y + (height/2));
    }


}
