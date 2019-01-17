package topi.cuber.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import topi.cuber.Bluetooth;
import topi.cuber.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        Log.i(TAG, "maxMemory:" + Long.toString(maxMemory));

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        Log.i(TAG, "memoryClass:" + Integer.toString(memoryClass));
    }


    public void clickSolve(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},REQUEST_CODE);
        } else {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        }
    }

    public void clickRobot(View view) {
        Intent intent = new Intent(this, RobotActivity.class);
        startActivity(intent);
    }

    public void clickSettings(View view) {
        //Intent intent = new Intent(this, RobotActivity.class);
        //startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Camera permission granted");
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
            } else {
                Log.e(TAG, "Permission denied");
                Toast.makeText(this,"No camera permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Log.i(TAG, "BT is turned on by user");
                //try again now that Bluetooth is on
                Bluetooth.getInstance().connect(this);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "BT wasn't turned on by user");
            }
        }
    }
}

