package application;

import data.Color;
import data.Facet;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import solver.CubeExplorer;
import tools.SerialConnection;
import tools.Utils;

import java.util.*;

public class Controller {

    @FXML
    private ImageView mainView;
    @FXML
    private ImageView debugView;
    @FXML
    private Label labelSolution;
    @FXML
    private ToggleGroup colorSelector;
    @FXML
    private Pane pane;

    private VideoCapture capture;
    private Timer timer;

    private List<Facet> facets;
    private List<Color> colors;
    private CubeExplorer cubeExplorer;
    private SerialConnection sc;
    private final Rectangle[][] uiCube = new Rectangle[6][9];
    private final int[] XOFF = { 3, 6, 3, 3, 0, 9 }; // Offsets for uiCube
    private final int[] YOFF = { 0, 3, 3, 6, 3, 3 };

    // Facet location coords
    private final int x = 200;  //x coord
    private final int y = 80;   //y coord
    private final int s = 135;  //spacing
    private final int t = 2;    //border thickness



    @FXML
    private void initialize() {
        cubeExplorer = new CubeExplorer();
        timer = new Timer();
        capture = new VideoCapture();

        // Initialize facet locations
        facets = new ArrayList<>();
        createFacets();

        createUiCube();

        // Define color ranges for detection   - maskMin -                 - maskMax -                 - borderColor -
        colors = new ArrayList<>();
        colors.add(new Color("White",   new Scalar(0, 0, 0),        new Scalar(180, 80, 255),   new Scalar(180, 180, 180)));
        colors.add(new Color("Blue",    new Scalar(100, 128, 50),   new Scalar(130, 255, 255),  new Scalar(255, 100, 50)));
        colors.add(new Color("Green",   new Scalar(50, 128, 50),    new Scalar(90, 255, 255),   new Scalar(50, 255, 57)));
        colors.add(new Color("Yellow",  new Scalar(20, 128, 50),    new Scalar(40, 255, 255),   new Scalar(50, 255, 255)));
        colors.add(new Color("Orange",  new Scalar(6, 128, 50),     new Scalar(16, 255, 255),   new Scalar(0, 150, 255)));
        colors.add(new Color("Red",     new Scalar(160, 128, 50),   new Scalar(180, 255, 255),
                                        new Scalar(0, 128, 50),     new Scalar(5, 255, 255),    new Scalar(0, 0, 255)));
    }

    @FXML
    private void handleScan(){
        StringBuilder sb = new StringBuilder("");
        for (Facet facet: facets) {
            if (facet != null) {
                if (facet.getColor() != null) {
                    sb.append(facet.getColor().nameToChar());
                }
            } else System.out.println("facet is null!");
        }
        cubeExplorer.addScannedSide(sb.toString());
        updateUiCube();
    }

    @FXML
    private void handleClear() {
        cubeExplorer.clear();
        labelSolution.setText("");
        for( int i=0; i < 6; i++) {
            for( int j=0; j < 9; j++) {
                uiCube[i][j].setFill(javafx.scene.paint.Color.GRAY);
            }
        }
    }

    @FXML
    private void handleSolve() {
        String solution = cubeExplorer.solveCube();
        if (solution != null) {
            labelSolution.setText(solution);
            sc.writeMessage(solution);
        } else {
            labelSolution.setText("Invalid cube");
        }

    }

    @FXML
    private void handleStart() {
        if (sc != null) {
            sc.writeMessage("start");
        }
    }

    @FXML
    private void handleNext() {
        if (sc != null) {
            sc.writeMessage("next");
        }
    }

    @FXML
    private void handleFinish() {
        if (sc != null) {
            sc.writeMessage("finish");
        }
    }

    @FXML
    private void handleConnect() {
        if (sc == null) {
            sc = new SerialConnection();
        }
        sc.connect();
    }

    @FXML
    private void startCamera() {
        //Start video capture
        if (!capture.isOpened()) {
            capture.open(0);
        }

        //Background thread: captures frame every 33 ms and displays in the ImageView
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Mat frame = grabFrame();
                Image tmp = Utils.mat2Image(frame);
                updateImageView(mainView, tmp);
            }
        };

        //Timer for the Runnable for the task above
        timer.schedule(task, 0, 33);
    }

    private Mat grabFrame() {

        Mat mainFrame = new Mat();
        Mat hvsFrame = new Mat();
        Mat loopFrame = new Mat();

        //Capture frame from webcam
        capture.read(mainFrame);

        //Image processing for better result
        Imgproc.blur(mainFrame, hvsFrame, new Size(7, 7));
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size((2*2) + 1, (2*2)+1));
        Imgproc.dilate(hvsFrame, hvsFrame, kernel);
        Imgproc.erode(hvsFrame, hvsFrame, kernel);
        Imgproc.cvtColor(hvsFrame, hvsFrame, Imgproc.COLOR_BGR2HSV);

        //Identify colors of the 9 facets on screen
        for (Color color : colors) {
            color.applyFilter(hvsFrame, loopFrame);

            //show debug window with selected color
            showInDebugWindow(color, loopFrame);

            for (Facet facet : facets) {
                //if color detected on facet
                if (facet.isDetected(loopFrame)) {
                    //set the color of facet, and print on UI
                    facet.setColor(color);
                    facet.printBorder(mainFrame);
                }
            }
        }

        //pass frame to ImageView
        Core.copyMakeBorder(mainFrame, mainFrame, t,t,t,t,Core.BORDER_CONSTANT);
        return mainFrame;
    }

    private void createFacets() {
        int x = this.x;
        int y = this.y;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                facets.add(new Facet(x, y));
                x += s;
            }
            x = this.x;
            y += s;
        }
    }

    private void createUiCube() {
        int size = 20;
        for( int i=0; i < 6; i++) {
            for( int j=0; j < 9; j++) {
                uiCube[i][j] = new Rectangle(size,size,javafx.scene.paint.Color.GRAY);
                uiCube[i][j].setLayoutX(size * XOFF[i] + size * (j % 3));
                uiCube[i][j].setLayoutY(size * YOFF[i] + size * (j / 3));
                uiCube[i][j].setArcHeight(5);
                uiCube[i][j].setArcWidth(5);
                uiCube[i][j].setStroke(javafx.scene.paint.Color.BLACK);
                pane.getChildren().add(uiCube[i][j]);
            }
        }
    }

    private void updateUiCube(){
        String[] side = cubeExplorer.getCubeSides();
        int n = 0;

        for( int i=0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                if (side[n] != null) {
                    String s = side[n];
                    Color.charToColor(uiCube[i][j], s.charAt(j));
                }
            }
            n++;
        }
    }

    private void showInDebugWindow(Color color, Mat src) {
        String selectedColor = ((RadioButton) colorSelector.getSelectedToggle()).getText();
        if (color.getName().equals(selectedColor)) {
            Core.copyMakeBorder(src, src, t,t,t,t,Core.BORDER_CONSTANT);
            updateImageView(debugView, Utils.mat2Image(src));
        }
    }

    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }
}
