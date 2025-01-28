package View.Graphics;

import java.awt.*;

public class WireGraphic implements Graphic {

    Point origin;
    Point end;
    int thickness;
    boolean originIsJunction;
    boolean endIsJunction;

    public WireGraphic(Point origin, Point end, int thickness, boolean originIsJunction, boolean endIsJunction) {
        this.origin = origin;
        this.end = end;
        this.originIsJunction = originIsJunction;
        this.endIsJunction = endIsJunction;
        this.thickness = thickness;
    }


    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.drawLine(origin.x + 1, origin.y + 1, end.x + 1 , end.y + 1);
        int offset = thickness/2;
        if(originIsJunction) g2d.drawOval(origin.x, origin.y, thickness, thickness);
        if(endIsJunction) g2d.drawOval(end.x, end.y, thickness, thickness);
    }
}
