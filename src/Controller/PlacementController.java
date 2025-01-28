package Controller;
import Controller.Exceptions.InvalidPlacement;
import Controller.Exceptions.NoSuchComponent;
import DataStructures.PointFieldMap;
import Model.Grid;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
/*
Responsibilities:
    -
 */
public class PlacementController {

    private final Grid grid;
    private final PointFieldMap wires;
    private final PointFieldMap components;

    private final int compSpansPins;
    private int initRotation;

    public PlacementController(GraphingProperties gProps){
        this.grid = new Grid(gProps);
        this.compSpansPins = gProps.ComponentGraphicPinLength;
        this.initRotation = gProps.ComponentGraphicInitialRotation;

        this.wires = new PointFieldMap();
        this.components = new PointFieldMap();
    }

    // Pin is a valid start of a new link
    public boolean isOpenWire(Point click){
        Point pin = grid.toGrid(click);
        boolean isCompBound = components.isBound(pin);
        boolean isLinkEnd = wires.isUniqueBound(pin) && ! wires.containsExcludingBounds(pin);
        if(isCompBound && isLinkEnd) return false;
        return isCompBound || isLinkEnd;
    }

    private boolean validWirePlacement(Point pin1, Point pin2){
        if(pin1.equals(pin2)) return false;
        if(components.hasOverlapClosed(pin1, pin2)) return false;
        if(wires.hasOverlapClosed(pin1, pin2)) return false;
        return true;
    }
    public void placeWire(Point click1, Point click2){
        Point pin1 = grid.toGrid(click1);
        Point pin2 = grid.toGrid(click2);
        if(! isOpenWire(pin1) || ! validWirePlacement(pin1, pin2)) throw new InvalidPlacement("Link placement failed");
        wires.put(pin1, pin2);
    }




    private boolean validComponentPlacement(Point inPoint, Point outPoint){
        if(outPoint == null){
            System.out.println("outPoint is null");
            return false;
        }
        if(components.hasOverlapOpen(inPoint, outPoint)) {
            System.out.println("Overlap with component");
            return false;
        }
        if(wires.contains(inPoint) || wires.contains(outPoint)){
            System.out.println("Overlap with wire");
            return false;
        }
        return true;
    }

    public Point placeComponent(Point click){
        Point inPoint = grid.toGrid(click);
        Point outPoint = getRotatedOutpoint(inPoint, initRotation);
        if(!validComponentPlacement(inPoint, outPoint)) throw new InvalidPlacement("Component cannot be placed at this point");
        components.put(inPoint, outPoint);
        return inPoint;
    }

    public void removeComponent(Point inPoint){
        if(!components.remove(inPoint)) throw new NoSuchComponent("No component corresponding to the key inPoint exists");
    }

    public boolean hasComponent(Point click){
        return components.contains(grid.toGrid(click));
    }
    public boolean hasWires(Point click){
        return wires.contains(click);
    }

    public void removeWires(Point click){
        Point gridPoint = grid.toGrid(click);
        ArrayList<Point[]> wireBounds = wires.getFieldsContaining(gridPoint);
        for(Point[] wire : wireBounds){wires.remove(wire[0]);}
    }


    /* Tries to rotate a component, and if it succeeds it sets the new rotation
    as the initial rotation for any components created after */
    public boolean rotateComponent(Point inPoint){
        if(!components.containsKey(inPoint)) throw new NoSuchComponent("The inPoint does not correspond to any component");
        Point outPoint = components.get(inPoint)[1];
        int nextRotation = nextRotation(getRotation(inPoint, outPoint));
        Point rotatedOutPoint = getRotatedOutpoint(inPoint, nextRotation);
        components.remove(inPoint);
        if(!validComponentPlacement(inPoint, rotatedOutPoint)){
            components.put(inPoint, outPoint);
            return false;}
        components.put(inPoint, rotatedOutPoint);
        this.initRotation = nextRotation;
        return true;
    }


    private Point getRotatedOutpoint(Point inPoint, int rotation){
        return switch(rotation){
            case 0 -> grid.shiftRow(inPoint, compSpansPins);
            case 1 -> grid.shiftCol(inPoint, compSpansPins);
            case 2 -> grid.shiftRow(inPoint, - compSpansPins);
            case 3 -> grid.shiftCol(inPoint, - compSpansPins);
            default -> null;};
    }

    //Mildly unoptimized for readabilities’ sake
    private int getRotation(Point inPoint, Point outPoint){
        if(inPoint.y == outPoint.y && inPoint.x < outPoint.x) return 0;
        else if(inPoint.x == outPoint.x && inPoint.y < outPoint.y) return  1;
        else if(inPoint.y == outPoint.y && inPoint.x > outPoint.x) return  2;
        else if(inPoint.x == outPoint.x && inPoint.y > outPoint.y) return  3;

        throw new RuntimeException("Points are not perpendicular to x = 0 nor y = 0");
    }

    private int nextRotation(int currentRotation){
        return (currentRotation + 1) <= 3 ? currentRotation + 1 : 0;
    }




    public HashSet<HashSet<Point>> getLinkedComponentBounds(){
        HashSet<HashSet<Point>> connectedComponentBounds = new HashSet<>();
        for(HashSet<Point> compositeField : wires.getCompositeFieldBounds()){
            compositeField.removeIf(p -> !components.isBound(p));
            connectedComponentBounds.add(new HashSet<>(compositeField));}
        return connectedComponentBounds;
    }


    public Point getComponentInput(Point click){
        Point pin = grid.toGrid(click);
        if(!components.contains(pin)) return null;
        return components.getFieldsContaining(pin).getFirst()[0];
    }

    public Point getComponentOutputWithInpoint(Point inPoint){
        if(!components.contains(inPoint)) return null;
        return components.get(inPoint)[1];
    }

    public ArrayList<Point[]> getAllComponentBounds(){
        return components.getFields();
    }

    public ArrayList<Point[]> getWireBounds(){
        return wires.getFields();
    }

    public ArrayList<Point> getGridPoints(){
        return this.grid.getGridPoints();
    }

    protected void clear(){
        this.components.clear();
        this.wires.clear();
    }



}
