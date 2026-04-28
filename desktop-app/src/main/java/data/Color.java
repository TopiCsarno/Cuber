package data;

import javafx.scene.shape.Rectangle;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class Color {

    private Scalar maskMin;
    private Scalar maskMax;
    private Scalar maskMin2;
    private Scalar maskMax2;
    private Scalar borderColor;
    private String name;

    //single range
    public Color(String name, Scalar maskMin, Scalar maskMax, Scalar borderColor) {
        this(name, maskMin, maskMax, null, null, borderColor);
    }

    //two ranges (for red)
    public Color(String name, Scalar maskMin, Scalar maskMax, Scalar maskMin2, Scalar maskMax2, Scalar borderColor) {
        this.maskMin = maskMin;
        this.maskMax = maskMax;
        this.maskMin2 = maskMin2;
        this.maskMax2 = maskMax2;
        this.borderColor = borderColor;
        this.name = name;
    }

    public void applyFilter(Mat src, Mat dst) {
        if (maskMin2 == null) {
            //single range
            Core.inRange(src, maskMin, maskMax, dst);
        } else {
            //two ranges combined together
            Mat mask1 = new Mat();
            Mat mask2 = new Mat();
            Core.inRange(src, maskMin, maskMax, mask1);
            Core.inRange(src, maskMin2, maskMax2, mask2);
            Core.bitwise_or(mask1, mask2, dst);
        }
    }

    public Scalar getBorderColor() {
        return borderColor;
    }

    public String getName() {
        return name;
    }

    public char nameToChar() {
        if (name == null) return '0';
        switch (name) {
            case "White":
                return 'U';
            case "Yellow":
                return 'D';
            case "Green":
                return 'F';
            case "Blue":
                return 'B';
            case "Red":
                return 'R';
            case "Orange":
                return 'L';
        }
        return '?';
    }

    public static void charToColor(Rectangle r, char c) {
        switch (c) {
            case 'U':
                r.setFill(javafx.scene.paint.Color.WHITE);
                break;
            case 'D':
                r.setFill(javafx.scene.paint.Color.YELLOW);
                break;
            case 'F':
                r.setFill(javafx.scene.paint.Color.GREEN);
                break;
            case 'B':
                r.setFill(javafx.scene.paint.Color.BLUE);
                break;
            case 'R':
                r.setFill(javafx.scene.paint.Color.RED);
                break;
            case 'L':
                r.setFill(javafx.scene.paint.Color.ORANGE);
                break;
            default:
                r.setFill(javafx.scene.paint.Color.BLACK);
                break;
        }
    }
}
