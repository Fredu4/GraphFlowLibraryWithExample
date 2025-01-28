package Model;


import Controller.GraphingProperties;

import java.awt.*;
import java.util.ArrayList;

public class Grid {

    private final int width;
    private final int height;

    private final int rowCount;
    private final int colCount;

    private final float xSpacing;
    private final float ySpacing;

    private final Point[][] grid;
    private final ArrayList<Point> allPinPositions;

    public Grid(GraphingProperties gp){
        this.width = gp.width;
        this.height = gp.height;
        this.rowCount = gp.pinCountAlongXAxis;
        this.colCount = gp.pinCountAlongYAxis;
        this.xSpacing = gp.horizontalPinSpacing;
        this.ySpacing = gp.verticalPinSpacing;

        this.grid = new Point[colCount][rowCount];
        this.allPinPositions = new ArrayList<>(rowCount * colCount);
        placeGridPoints();
    }

    private void placeGridPoints(){
        float xCord = xSpacing;
        float yCord = ySpacing;
        for(int i = 0; i < colCount; i++){
            for(int j = 0; j < rowCount; j++){
                Point point = new Point(Math.round(xCord), Math.round(yCord));
                grid[i][j] = point;
                allPinPositions.add(point);
                xCord += xSpacing;
            }
            xCord = xSpacing;
            yCord += ySpacing;
        }
    }

    private int xToIndex(int x){
        float ratioToWidth = (float) x / ( (float) width - xSpacing);
        return Math.round(ratioToWidth * rowCount) - 1;
    }

    private int yToIndex(int y){
        float ratioToHeight = (float) y / ( (float) height - ySpacing);
        return Math.round(ratioToHeight * colCount) - 1;
    }

    public Point toGrid(Point point){
        float xRatio = (float) point.x / ( (float) width - xSpacing);
        float yRatio = (float) point.y / ( (float) height - ySpacing);
        int xIndex = Math.round(xRatio * rowCount);
        int yIndex = Math.round(yRatio * colCount);

        xIndex = Math.max(0, xIndex);
        xIndex = Math.min(rowCount, xIndex);
        yIndex = Math.max(0, yIndex);
        yIndex = Math.min(colCount, yIndex);
        Point pin = grid[--yIndex < 0 ? 0 : yIndex][--xIndex < 0 ? 0 : xIndex];
        if(pin == null) throw new RuntimeException("No pin was found");
        return pin;
    }

    public Point shiftRow(Point point, int shift){
        int rowIndex = xToIndex(point.x) + shift;
        int colIndex = yToIndex(point.y);
        if(rowIndex < 0 || rowIndex >= rowCount) return null;
        return grid[colIndex][rowIndex];
    }

    public Point shiftCol(Point point, int shift){
        int rowIndex = xToIndex(point.x);
        int colIndex = yToIndex(point.y) + shift;
        if(colIndex < 0 || colIndex >= colCount) return null;
        return grid[colIndex][rowIndex];
    }

    public ArrayList<Point> gridPointsBetween(Point p1, Point p2){
        ArrayList<Point> pinsBetween = new ArrayList<>();
        if(p1.equals(p2)) return pinsBetween;

        int row1 = xToIndex(p1.x);
        int col1 = yToIndex(p1.y);
        int row2 = xToIndex(p2.x);
        int col2 = yToIndex(p2.y);

        if(row1 == row2){
            int index = Math.min(col1, col2) + 1;
            int endIndex = Math.max(col1, col2);
            while(index < endIndex)
                pinsBetween.add(grid[index++][row1]);
            return pinsBetween;
        }
        if(col1 == col2){
            int index = Math.min(row1, row2) + 1;
            int endIndex = Math.max(row1, row2);
            while(index < endIndex)
                pinsBetween.add(grid[col1][index++]);
            return pinsBetween;
        }
        return pinsBetween;
    }

    public ArrayList<Point> getGridPoints(){
        return this.allPinPositions;
    }

    public float[] getGridSpacings(){
        return new float[]{xSpacing, ySpacing};
    }

}

