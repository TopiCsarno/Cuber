package topi.cuber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public final class Bluetooth {

    private static final String TAG = Bluetooth.class.getName();
    private static final int REQUEST_CODE = 1;
    private static Bluetooth instance;
    private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;
    private ProgressDialog progress;
    private BluetoothDevice[] devices;
    private String[] deviceNames;
    private String deviceAddress;

    private Bluetooth() {
    }

    public static synchronized Bluetooth getInstance() {
        if (instance == null) {
            instance = new Bluetooth();
        }
        return instance;
    }

    private boolean isSupported() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Log.e(TAG, "Bluetooth not supported on device");
            return false;
        } else {
            Log.i(TAG, "Bluetooth is supported");
            return true;
        }
    }

    private boolean isEnabled() {
        try {
            if (adapter.isEnabled()) {
                Log.i(TAG, "Bluetooth is ON");
                return true;
            } else {
                Log.i(TAG, "Bluetooth is OFF, ask user to enable it");
                return false;
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "adapter is null");
            return false;
        }
    }

    public boolean isConnected() {
        if (socket == null) {
            return false;
        } else {
            return socket.isConnected();
        }
    }

    private void getPairedDevices() {
        try {
            devices = adapter.getBondedDevices().toArray(new BluetoothDevice[0]);
            deviceNames = new String[devices.length];
            for (int i = 0; i < devices.length; i++) {
                deviceNames[i] = devices[i].getName() + "\n" + devices[i].getAddress();
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "adapter is null");
        }
    }

    public void sendMessage(String message) {
        try {
            if (isConnected()) {
                message = "<" + message + ">";
                socket.getOutputStream().write(message.getBytes());
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception is send message");
        }
    }

    public void connect(final Activity activity) {
        disconnect();
        if (Bluetooth.getInstance().isSupported()) {
            if (!Bluetooth.getInstance().isEnabled()) {
                // ask user to enable BT
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(turnBTon, REQUEST_CODE);
                Log.i(TAG, "Asking user to turn on BT");
                return;
                // wait for activity result
            }
            Log.i(TAG, "Bluetooth is OK, choosing device now");
            getPairedDevices();

            //choose device
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Paired Devices");
            if (deviceNames.length > 0) {
                builder.setItems(deviceNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deviceAddress = devices[which].getAddress();
                        Log.i(TAG, "Starting new thread to connect");
                        new myTask(activity).execute();
                    }
                });
            } else {
                builder.setMessage("No paired devices found");
            }
            builder.show();
        }
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.getInputStream().close();
                socket.getOutputStream().close();
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Could not disconnect socket");
        }
    }

    private class myTask extends AsyncTask<Void, Void, Void> {

        private Activity activity;

        myTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(activity, "Connecting...", "Please Wait!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (socket == null) {
                    adapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = adapter.getRemoteDevice(deviceAddress);
                    socket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    socket.connect();
                }
            } catch (IOException e) {
                Log.e(TAG, "IO exception when connecting");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (socket.isConnected()) {
                Toast.makeText(activity, "Connected.", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Bluetooth socket is Connected");

            } else {
                Toast.makeText(activity, "Connection Failed.", Toast.LENGTH_LONG).show();
                Log.i(TAG, "Connection Failed");
            }
            progress.dismiss();
        }
    }
}
