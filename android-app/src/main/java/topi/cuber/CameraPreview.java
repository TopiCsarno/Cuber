package topi.cuber;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.i("ERROR", "Unable to load OpenCV");
        } else {
            Log.i("SUCCESS", "OpenCV loaded");
        }
    }

    private static final String TAG = "CameraPreview";
    private Camera mCamera;
    private int frameHeight;
    private int frameWidth;
    private int[] colors = new int[9];
    private char[] facelets = new char[9];
    private List<Rect> stickers = new ArrayList<>();
    private ImageView[] cubies;
    private Mat hue;
    private Mat sat;
    private Mat val;

    public CameraPreview(Context context, Camera camera, int frameWidth, int frameHeight, ImageView[] cubies) {
        super(context);
        this.mCamera = camera;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.cubies = cubies;
        hue = new Mat();
        sat = new Mat();
        val = new Mat();

        //install SurfaceHolder.Callback
        SurfaceHolder mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCamera.addCallbackBuffer(data);
        new ImageProcessTask(data).execute();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            //surface created -> tell the camera where to draw the preview
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.i(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        //TODO: properly create, destroy surface to handle pause/resume

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public char[] getFacelets() {
        return facelets;
    }

    private void setStickerAreas(Mat src) {
        int length = (src.width() < src.height()) ? src.width() : src.height();
        int offset = Math.abs(src.width() - src.height()) / 2;

        int x = length / 7;
        for (int i = 1; i <= 5; i += 2) {
            for (int j = 5; j >= 1; j -= 2) {
                stickers.add(new Rect(new Point(i * x, offset + j * x), new Point(i * x + x, offset + j * x + x)));
            }
        }
    }

    private double calcHistogramMax(Mat roi) {
        //hist setup
        MatOfInt histSize = new MatOfInt(180);
        List<Mat> hueList = new ArrayList<>();
        hueList.add(roi);

        // compute the histogram
        Imgproc.calcHist(hueList, new MatOfInt(0), new Mat(), roi, histSize, new MatOfFloat(0, 179), false);

        double max = 0.0;
        double maxLoc = 0;
        for (int i = 1; i < histSize.get(0, 0)[0]; i++) {
            double val = roi.get(i, 0)[0];
            if (val > max) {
                max = val;
                maxLoc = i;
            }
        }
        return maxLoc;
    }

    private void identifyColors(int index, double hue, double sat, double val) {

        if (sat < 70 && val > 70) {
            colors[index] = R.drawable.cubie_white;
            facelets[index] = 'U';
        } else {
            if (hue >= 0 && hue <= 5) {
                colors[index] = R.drawable.cubie_red;
                facelets[index] = 'R';

            } else if (hue > 5 && hue <= 15) {
                colors[index] = R.drawable.cubie_orange;
                facelets[index] = 'L';

            } else if (hue > 15 && hue <= 45) {
                colors[index] = R.drawable.cubie_yellow;
                facelets[index] = 'D';

            } else if (hue > 45 && hue <= 90) {
                colors[index] = R.drawable.cubie_green;
                facelets[index] = 'F';

            } else if (hue > 90 && hue <= 140) {
                colors[index] = R.drawable.cubie_blue;
                facelets[index] = 'B';

            } else if (hue > 140 && hue <= 180) {
                colors[index] = R.drawable.cubie_red;
                facelets[index] = 'R';
            }
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////

    private class ImageProcessTask extends AsyncTask<Void, Void, Void> {

        private byte[] data;

        ImageProcessTask(byte[] data) {
            this.data = data;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Source
            Mat src = new Mat(frameHeight + frameHeight / 2, frameWidth, CvType.CV_8UC1);
            src.put(0, 0, data);

            // get color channels
            Imgproc.cvtColor(src, src, Imgproc.COLOR_YUV2BGR_NV21); //should be 21 by default
            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);
            Core.extractChannel(src, hue, 0);
            Core.extractChannel(src, sat, 1);
            Core.extractChannel(src, val, 2);

            // set up sticker areas
            setStickerAreas(src);

            // Identify Colors
            for (int i = 0; i < colors.length; i++) {
                double d_hue = calcHistogramMax(new Mat(hue, stickers.get(i)));
                double d_sat = Core.mean(new Mat(sat, stickers.get(i))).val[0];
                double d_val = Core.mean(new Mat(val, stickers.get(i))).val[0];
                identifyColors(i, d_hue, d_sat, d_val);

                //Log.i(TAG, i + ". Sticker color stats: hue: " + d_hue + "   sat: " + d_sat + "   val: " + d_val);
                //Log.i(TAG, "cube side: " + Arrays.toString(facelets));
            }
            //Log.i(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            src.release();
            hue.release();
            sat.release();
            val.release();
            return null;
        }

        // this has access to UI thread
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (int i = 0; i < colors.length; i++) {
                cubies[i].setImageResource(colors[i]);
            }
        }
    }
}
