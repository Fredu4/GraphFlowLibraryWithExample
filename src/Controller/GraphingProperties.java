package Controller;

public class GraphingProperties{

    public int width;
    public int height;

    public final int pinCountAlongXAxis;
    public final int pinCountAlongYAxis;
    public final float horizontalPinSpacing;
    public final float verticalPinSpacing;

    public final int ComponentGraphicPinLength;
    public final int ComponentGraphicInitialRotation;

    public GraphingProperties(int width, int height, int preferredBoardDensity,
                              int ComponentGraphicPinLength, int ComponentGraphicInitialRotation) {
        this.width = width;
        this.height = height;

        this.pinCountAlongXAxis = width / preferredBoardDensity;
        this.pinCountAlongYAxis = height / preferredBoardDensity;

        this.horizontalPinSpacing = (float) width / (pinCountAlongXAxis + 1);
        this.verticalPinSpacing = (float) height / (pinCountAlongYAxis + 1);

        this.ComponentGraphicPinLength = ComponentGraphicPinLength;
        this.ComponentGraphicInitialRotation = ComponentGraphicInitialRotation;
    }

}

