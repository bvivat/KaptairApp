package com.example.kaptair.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import com.example.kaptair.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

interface TypeMessage {
    int PARTICULES = 0;
    int ATMOSPHERE = 1;
    int LOCALISATION = 2;
    HashMap<String, Integer> type = new HashMap<String, Integer>() {{
        put("PM", PARTICULES);
        put("AT", ATMOSPHERE);
        put("GP", LOCALISATION);

    }};

}

public class TransfertThread extends Thread {

    Integer getOrDef(HashMap<String, Integer> map, String key) {
        if (map.get(key) != null) {
            return map.get(key);
        }
        return -1;
    }

    private static final String TAG = "TransfertThread";

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    public TransfertThread(BluetoothSocket socket) {
        mmSocket = socket;

        //On initialise les streams
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);

                String msg = new String(mmBuffer, 0, numBytes);
                Log.i(TAG, "Message recu : " + msg);

                String id = "";
                if (numBytes >= 2) {
                    id = msg.substring(0, 2);
                    Log.i(TAG, "id recu : " + id);
                }

                // Send the obtained bytes to the UI activity.
                Message readMsg = MainActivity.handlerUI.obtainMessage(
                        getOrDef(TypeMessage.type, id), numBytes, -1,
                        msg);
                readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);

        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}
