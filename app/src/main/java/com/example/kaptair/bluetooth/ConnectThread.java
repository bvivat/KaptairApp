package com.example.kaptair.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kaptair.MainActivity;
import com.example.kaptair.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;

import static com.example.kaptair.MainActivity.PREF_ADDRMAC;


public class ConnectThread extends Thread {

    final static String TAG = "ConnectThread";

    WeakReference<AppCompatActivity> act;
    final UUID SERIAL_UUID;
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    TransfertThread transfert;

    private boolean isConnected = false;
    OnConnectionChangeListener listener;


    public ConnectThread(WeakReference<AppCompatActivity> act, BluetoothDevice device, UUID SERIAL_UUID) {
        this.act = act;
        this.SERIAL_UUID = SERIAL_UUID;
        BluetoothSocket tmp = null;
        mmDevice = device;

        //Initialisation de la socket
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
            Log.i(TAG, "Connecté à la socket");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            isConnected = false;
            act.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act.get(), act.get().getString(R.string.ToastBTConnectError, mmDevice.getName()), Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "Could not connect to socket", connectException);
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            result();
            return;
        }

        // Connexion reussie
        isConnected = true;
        act.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act.get(), act.get().getString(R.string.ToastBTConnecte, mmDevice.getName()), Toast.LENGTH_SHORT).show();
            }
        });

        // On enregistre l'adresse du dernier appareil connecte
        SharedPreferences sharedPref = act.get().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_ADDRMAC, mmDevice.getAddress());
        editor.commit();

        // On active le tracking des mesures
        ((MainActivity) act.get()).checkTrackingPermissions();

        // On lance le transfert
        transfert = new TransfertThread(mmSocket);
        transfert.setListener(new OnConnectionChangeListener() {
            @Override
            public void onConnectionResult() {
                cancel();
            }
        });
        transfert.start();
        result();
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        if(transfert!=null){
            transfert.cancel();
        }
        isConnected=false;
        result();

        // On arrete le tracking GPS
        ((MainActivity) act.get()).endTracking();

        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getDeviceName(){
        return mmDevice.getName();
    }

    private void result(){
        if (listener!=null){
            // On gere le callback du listener
            act.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onConnectionResult();
                }
            });

        }
    }

    public void setAct(WeakReference<AppCompatActivity> act) {
        this.act = act;
    }

    public void setListener(OnConnectionChangeListener listener) {
        this.listener = listener;
    }
}
