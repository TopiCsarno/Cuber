package topi.cuber.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cs.min2phase.Search;
import topi.cuber.CameraPreview;
import topi.cuber.CubeExplorer;
import topi.cuber.R;


@SuppressWarnings("deprecation")
public class CameraActivity extends AppCompatActivity {


    private static final String TAG = "CameraActivity";
    private static final int CAMERA_ID = 0;

    private static final int previewWidth = 480;
    private static final int previewHeight = 640;
    private ImageView[] cubies = new ImageView[9];
    private Camera mCamera;
    private CameraPreview mPreview;
    private CubeExplorer cubeExplorer;
    private TextView tv_scanner;
    private ProgressDialog progress;
    private FrameLayout previewFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        tv_scanner = findViewById(R.id.tv_scanner);
        for (int i = 0; i < 9; i++) {
            try {
                cubies[i] = findViewById(R.id.class.getField("cubie" + i).getInt(0));
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }
        }

        cubeExplorer = new CubeExplorer();

        mCamera = getCameraInstance(this);

        mPreview = new CameraPreview(this, mCamera, previewWidth, previewHeight, cubies);

        // add overlay
        ImageView overlay = new ImageView(this);
        overlay.setImageResource(R.drawable.grid);

        // attach views to frame
        previewFrame = findViewById(R.id.frame);
        previewFrame.addView(mPreview);
        previewFrame.addView(overlay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        previewFrame.removeView(mPreview);
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        previewFrame.removeView(mPreview);
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCameraInstance(this);
        }
    }

    public void clickScan(View view) {
        cubeExplorer.addScannedSide(new String(mPreview.getFacelets()));

        int count = cubeExplorer.getScannedCount();
        tv_scanner.setText(count + "/6");
        if (count >= 6) {
            //run solver
            new SolverTask(this).execute();
        }
    }

    public static Camera getCameraInstance(Context context) {
        Camera camera = null;
        try {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                camera = Camera.open(CAMERA_ID);
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(previewWidth, previewHeight);
                camera.setParameters(parameters);
                Log.i(TAG, "Camera OK");
            }
        } catch (Exception e) {
            Log.i(TAG, "Error getting camera instance: " + e.getMessage());
        }
        return camera;
    }

    private class SolverTask extends AsyncTask<Void, Void, Void> {

        private Activity activity;
        private String solution;

        SolverTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(activity, "Connecting...", "Please Wait!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            Log.i(TAG, "SolverTask called");
            solution = cubeExplorer.solveCube();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.dismiss();
            mCamera.stopPreview();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Solution");
            builder.setMessage(solution);
            builder.setOnDismissListener(dialog -> {
                cubeExplorer.clear();
                mCamera.startPreview();
                tv_scanner.setText("0/6");
            });
            builder.show();

        }
    }

}


