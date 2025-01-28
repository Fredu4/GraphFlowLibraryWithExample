package DataStructures;

import java.awt.*;


public abstract sealed class TrueBound {


    public static final class ClosedBound extends TrueBound{
        public ClosedBound(Point p1, Point p2){
            super(p1, p2);
        }
        @Override
        public boolean contains(Point p){
            return false;
        }
    }

    public static final class OpenBound extends TrueBound{
        public OpenBound(Point p1, Point p2){
            super(p1, p2);
        }
        @Override
        public boolean contains(Point p){
            return false;
        }
    }

    private Point p1, p2;

    private int lower;
    private int upper;

    private int dy;
    private int dx;

    private int[] base; //as rational number


    public TrueBound(Point p1, Point p2){
        if(p1.x != p2.x && p1.y != p2.y) throw new IllegalArgumentException("points do not share dimension");
        this.p1 = p1;
        this.p2 = p2;
        this.dx = Math.abs(p2.x - p1.x);
        this.dy = Math.abs(p2.y - p1.y);
    }

    public abstract boolean contains(Point p);

    // returns true if this and field inBoundsAsOpen at least one common Point
    public boolean overlaps(Field field){
        return false;
    }

    public final ClosedBound toClosedBound(){
        return new ClosedBound(p1, p2);
    }

    public final OpenBound toOpenBound(){
        return new OpenBound(p1, p2);
    }


}
