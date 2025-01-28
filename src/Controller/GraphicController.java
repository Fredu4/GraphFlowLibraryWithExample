package Controller;
import View.Graphics.*;

import java.awt.*;
import java.util.ArrayList;


public class GraphicController {

    private final int componentWidth;
    private ArrayList<ComponentGraphic> componentGraphics;

    public GraphicController(int componentWidth){
        this.componentWidth = componentWidth;
    }



    public int[] getConstructorArguments(Point inPoint, Point outPoint){
        int pointX, pointY, width, height;
        if(inPoint.y == outPoint.y){
            pointX = Math.min(inPoint.x, outPoint.x);
            pointY = inPoint.y - (componentWidth / 2);
            width = Math.abs(inPoint.x - outPoint.x);
            height = componentWidth;}
        else if(inPoint.x == outPoint.x){
            pointX = inPoint.x - (componentWidth / 2);
            pointY = Math.min(inPoint.y, outPoint.y);
            width = componentWidth;
            height = Math.abs(inPoint.y - outPoint.y);}
        else throw new IllegalArgumentException("points are not parallel to x nor y axis");
        return new int[]{pointX, pointY, width, height};
    }

    public BatteryGraphic getBatteryGraphic(Point inPoint, Point outPoint, double value){
        int[] constructor = getConstructorArguments(inPoint, outPoint);
        Point cornerPoint = new Point(constructor[0], constructor[1]);
        int integerValue = (int) Math.round(value);
        return new BatteryGraphic(cornerPoint, constructor[2], constructor[3], integerValue);
    }

    public ResistorGraphic getResistorGraphic(Point inPoint, Point outPoint, double value){
        int[] constructor = getConstructorArguments(inPoint, outPoint);
        Point cornerPoint = new Point(constructor[0], constructor[1]);
        int integerValue = (int) Math.round(value);
        return new ResistorGraphic(cornerPoint, constructor[2], constructor[3], integerValue);
    }


    public ArrayList<ComponentGraphic> getComponentGraphics(){
        return this.componentGraphics;
    }


    public WireGraphic getWireGraphic(Point p1, Point p2, boolean isLinker1, boolean isLinker2){
        return new WireGraphic(p1, p2,5, isLinker1, isLinker2);
    }

    public BoardPinGraphic getPinGraphic(Point p, boolean highlight){
        return new BoardPinGraphic(p, 3, highlight);
    }



}
