package topi.cuber.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.util.Timer;
import java.util.TimerTask;
import topi.cuber.Bluetooth;
import topi.cuber.R;

public class RobotActivity extends AppCompatActivity {
    public static final String TAG = RobotActivity.class.getName();
    private ImageView circle;
    private Timer timer;
    final Handler handler = new Handler();

    final  Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            if (Bluetooth.getInstance().isConnected()) {
                circle.setImageResource(R.drawable.circle_green);
            } else {
                circle.setImageResource(R.drawable.circle_red);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);

        //status circle
        circle = findViewById(R.id.statusCircle);

        //schedule check
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {handler.post(updateUI);}
        }, 0, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public void clickBluetooth(View view) {
        Bluetooth.getInstance().connect(this);
    }

    public void clickButton(View view) {
        String message = "";
        switch (view.getId()) {
            case R.id.LO: message = "1OPEN"; break;
            case R.id.LC: message = "1CLOSE"; break;
            case R.id.LL: message = "1LEFT"; break;
            case R.id.LM: message = "1MID"; break;
            case R.id.LR: message = "1RIGHT"; break;
            case R.id.RO: message = "2OPEN"; break;
            case R.id.RC: message = "2CLOSE"; break;
            case R.id.RL: message = "2LEFT"; break;
            case R.id.RM: message = "2MID"; break;
            case R.id.RR: message = "2RIGHT"; break;
        }
        Bluetooth.getInstance().sendMessage(message);
    }

    //for Bluetooth
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
