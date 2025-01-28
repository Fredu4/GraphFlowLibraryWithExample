package DataStructures;

import java.awt.*;
import DataStructures.Field.Open;
import DataStructures.Field.Closed;

public abstract sealed class Field permits Closed, Open {

    public static final class Closed extends Field {
        public Closed(Point p1, Point p2){
            super(p1, p2);
        }
        @Override
        public boolean contains(Point p){
            return isVertical ?
                    (p.x == dimension) && (p.y > l) && (p.y < u) :
                    (p.y == dimension) && (p.x > l) && (p.x < u);
        }
    }

    public static final class Open extends Field {
        public Open(Point p1, Point p2){
            super(p1, p2);
        }
        @Override
        public boolean contains(Point p){
            return isVertical ?
                    (p.x == dimension) && (p.y >= l) && (p.y <= u) :
                    (p.y == dimension) && (p.x >= l) && (p.x <= u);
        }
    }

    public Point p1, p2;
    protected int l;
    protected int u;
    protected final int dimension;
    protected final boolean isVertical;
    protected int length;

    private Field(Point p1, Point p2){
        if(p1.x != p2.x && p1.y != p2.y) throw new IllegalArgumentException("points do not share dimension");
        this.p1 = p1;
        this.p2 = p2;
        this.isVertical = p1.x == p2.x;
        this.l = isVertical ? Math.min(p1.y, p2.y) : Math.min(p1.x, p2.x);
        this.u = isVertical ? Math.max(p1.y, p2.y) : Math.max(p1.x, p2.x);
        this.length = this.u - this.l;
        this.dimension = isVertical ? p1.x : p1.y;
    }


    public static Field OpenField(Point p1, Point p2){
        return new Open(p1, p2);
    }

    public static Field ClosedField(Point p1, Point p2){
        return new Closed(p1, p2);
    }

    public abstract boolean contains(Point p);

    // returns true if this and field inBoundsAsOpen at least one common Point
    public boolean overlaps(Field field){
        if(this.isVertical == field.isVertical) return field.contains(p1) || field.contains(p2);
        return this.isVertical ?
                this.contains(new Point(this.dimension, field.dimension)) &&
                field.contains(new Point(this.dimension, field.dimension))
                :
                this.contains(new Point(field.dimension, this.dimension)) &&
                field.contains(new Point(field.dimension, this.dimension));
    }

    public final boolean isExtension(Field field){
        if(this.isVertical != field.isVertical) return false;
        if(this.dimension != field.dimension) return false;
        return this.u == field.l || this.l == field.u;
    }

    public final Field union(Field field){
        if(this.isVertical != field.isVertical || this.dimension != field.dimension) return null;
        if(this.l >= field.u || this.u <= field.l) return null;
        Point unionLowerBound, unionUpperBound;
        if(isVertical){
            unionLowerBound = new Point(dimension, Math.max(this.l, field.l));
            unionUpperBound = new Point(dimension, Math.min(this.u, field.u));}
        else{
            unionLowerBound = new Point(Math.max(this.l, field.l), dimension);
            unionUpperBound = new Point(Math.min(this.u, field.u), dimension);}
        return new Open(unionLowerBound, unionUpperBound);
    }

    public final boolean extend(Field b){
        if(!isExtension(b)) return false;
        this.p1 = l < b.l ? p1 : b.p1;
        this.p2 = u > b.u ? p2 : b.p2;

        this.l = Math.min(l, b.l);
        this.u = Math.max(u, b.u);
        return true;
    }

}