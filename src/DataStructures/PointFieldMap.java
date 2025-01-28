package DataStructures;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/*
 Represents a network of open and closed fields on the cartesian plane
 Every InPoint is a unique key that corresponds to a field
 */

public class PointFieldMap{

    private final HashMap<Point, Field> fieldsAsOpen;
    private final HashMap<Point, Field> fieldAsClosed;
    private final HashMap<Point, Integer> boundOccurrences;

    public PointFieldMap(){
        this.fieldsAsOpen = new HashMap<>();
        this.fieldAsClosed = new HashMap<>();
        this.boundOccurrences = new HashMap<>();
    }


    //The inPoint functions as a key
    public void put(Point inPoint, Point outPoint){
        Field openField = Field.OpenField(inPoint, outPoint);
        Field closeField = Field.ClosedField(inPoint, outPoint);
        this.fieldsAsOpen.put(inPoint, openField);
        this.fieldAsClosed.put(inPoint, closeField);
        this.boundOccurrences.put(inPoint, boundOccurrences.getOrDefault(inPoint, 0) + 1);
        this.boundOccurrences.put(outPoint, boundOccurrences.getOrDefault(outPoint, 0) + 1);
    }


    public Point[] get(Point inPoint){
        Field field = this.fieldsAsOpen.get(inPoint);
        return new Point[]{field.p1, field.p2};
    }

    public boolean remove(Point inPoint){
        if(!fieldsAsOpen.containsKey(inPoint)) return false;
        Point outPoint = fieldsAsOpen.get(inPoint).p2;
        this.fieldsAsOpen.remove(inPoint);
        this.fieldAsClosed.remove(inPoint);
        int newInPointOccurrences = boundOccurrences.get(inPoint) - 1;
        int newOutPointOccurrences = boundOccurrences.get(outPoint) - 1;
        if(newInPointOccurrences <= 0) boundOccurrences.remove(inPoint);
        if(newOutPointOccurrences <= 0) boundOccurrences.remove(outPoint);
        return true;
    }

    public ArrayList<Point[]> getFieldsContaining(Point point){
        ArrayList<Point[]> fields = new ArrayList<>();
        for(Field f : this.fieldsAsOpen.values())
            if(f.contains(point)) fields.add(new Point[]{f.p1, f.p2});
        return fields;
    }



    public boolean hasOverlapOpen(Point p1, Point p2){
        Field temp = Field.OpenField(p1, p2);
        for(Field f : fieldsAsOpen.values())
            if(f.overlaps(temp)) return true;
        return false;
    }

    public boolean hasOverlapClosed(Point p1, Point p2){
        Field temp = Field.ClosedField(p1, p2);
        for(Field f : fieldAsClosed.values())
            if(f.overlaps(temp)) return true;
        return false;
    }

    public boolean isExtension(Point p1, Point p2){
        Field temp = Field.OpenField(p1, p2);
        for(Field f : fieldsAsOpen.values())
            if(f.isExtension(temp)) return true;
        return false;
    }


    public boolean contains(Point p){
        for(Field f : fieldsAsOpen.values()) if(f.contains(p)) return true;
        return false;
    }

    public boolean containsExcludingBounds(Point p){
        for(Field f : fieldAsClosed.values())
            if(f.contains(p)) return true;
        return false;
    }

    public boolean containsKey(Point point){
        return this.fieldsAsOpen.containsKey(point);
    }

    public boolean isBound(Point p){
        return boundOccurrences.containsKey(p);
    }

    public boolean isUniqueBound(Point p){
        if(!boundOccurrences.containsKey(p)) return false;
        return this.boundOccurrences.get(p) == 1;
    }


    public ArrayList<Point[]> getFields(){
        ArrayList<Point[]> bounds = new ArrayList<>();
        for(Field f : this.fieldsAsOpen.values())
            bounds.add(new Point[]{f.p1, f.p2});
        return bounds;
    }


    public ArrayList<Point> getBounds(){
        ArrayList<Point> boundingPoints = new ArrayList<>();
        for(Field f : this.fieldsAsOpen.values()){
            boundingPoints.add(f.p1);
            boundingPoints.add(f.p2);
        }
        return boundingPoints;
    }


    private HashSet<Point> composeSuperField(HashSet<Point> superField){
        int initialSize = superField.size();
        for(Field f : fieldsAsOpen.values())
            if(superField.contains(f.p1) || superField.contains(f.p2)){
                superField.add(f.p1);
                superField.add(f.p2);}

        if(superField.size() != initialSize) return composeSuperField(superField);
        return superField;
    }

    private HashSet<Point> composeSuperField(Field field){
        HashSet<Point> superField = new HashSet<>();
        superField.add(field.p1);
        superField.add(field.p2);
        return composeSuperField(superField);
    }

    public HashSet<HashSet<Point>> getCompositeFieldBounds(){
        HashSet<HashSet<Point>> fieldGroups = new HashSet<>();
        for(Field field : fieldsAsOpen.values())
            fieldGroups.add(composeSuperField(field));
        System.out.println("PointFieldMap found :" + fieldGroups.size() + " groups");
        return fieldGroups;
    }


    public void clear(){
        this.boundOccurrences.clear();
        this.fieldAsClosed.clear();
        this.boundOccurrences.clear();
    }

}