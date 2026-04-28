package data;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class Facet {

    private final int x;  //x coord
    private final int y;  //y coord
    private final int w;  //width of Rect
    private final int thickness;  //border thickness
    private Color color;    //facet's color

    private final int thrshold = 60;  //detection threshold

    public Facet(int x, int y) {
        this.x = x;
        this.y = y;
        this.w = 25;
        this.thickness = 4;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean isDetected(Mat loopFrame) {
        //Facet's area is subMat of the whole frame
        Mat ROI = loopFrame.submat(new Rect(x, y, w, w));

        //Get mean color value of the facet's area
        Scalar meanScalar = Core.mean(ROI);
        double meanHUE = meanScalar.val[0];

        return (meanHUE > thrshold);
    }

    public void printBorder(Mat src) {
        Imgproc.rectangle(src, new Point(x, y), new Point(x + w, y + w), color.getBorderColor(), thickness);
    }


}
