package com.example.kaptair.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {

    final static String TAG= "ConnectThread";
    final UUID SERIAL_UUID;
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;


    public ConnectThread(BluetoothDevice device, UUID SERIAL_UUID) {
        this.SERIAL_UUID=SERIAL_UUID;
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            tmp = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {

        try {
            mmSocket.connect();
            Log.i(TAG,"Connecté à la socket");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            Log.e(TAG, "Could not connect to socket", connectException);
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        TransfertThread transfert = new TransfertThread(mmSocket);
        transfert.start();
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
