package View.Graphics;

import java.awt.*;

public class BatteryGraphic implements ComponentGraphic {

    private final Point point;
    private final int width;
    private final int height;
    private final String value;

    public BatteryGraphic(Point topLeftCorner, int width, int height, int value){
        this.point = topLeftCorner;
        this.width = width;
        this.height = height;
        this.value = String.valueOf(value) + "V";
    }

    public void paint(Graphics g){
        //Paint output side
        g.setColor(Color.red);
        g.fillRect(point.x + 1, point.y + 1, width, height);
        //Paint input side
        g.setColor(Color.blue);
        if(width > height) g.fillRect(point.x + 1, point.y + 1, width/2, height);
        else g.fillRect(point.x + 1, point.y + 1, width, height/2);
        //Paint voltage value
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(value, point.x + (width/2), point.y + (height/2));
    }


}
