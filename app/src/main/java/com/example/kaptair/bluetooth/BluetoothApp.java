package com.example.kaptair.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.kaptair.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothApp {
    private static final String TAG = "Bluetooth class";

    private static final int REQUEST_COARSE_LOCATION = 0;

    ArrayList<Device> devices;
    ListeFragment liste;

    boolean hasBluetooth;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device;
    String adrMac = "98:3B:8F:78:2C:8C";

    BroadcastReceiver receiver;
    boolean isRegisteringDone = false;

    WeakReference<AppCompatActivity> act;

    public BluetoothApp(AppCompatActivity a) {
        act = new WeakReference<AppCompatActivity>(a);
        check();
    }


    public void check() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            hasBluetooth = false;
        } else {
            hasBluetooth = true;
            int REQUEST_ENABLE_BT = 1;
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                act.get().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }


    public void rechercher() {
        if (!hasBluetooth) {
            Toast.makeText(act.get(), R.string.btNoBluetooth, Toast.LENGTH_SHORT).show();
        } else {
            devices = Device.fromSet(bluetoothAdapter.getBondedDevices());

            liste = ListeFragment.newInstance(devices);
            liste.show(act.get().getSupportFragmentManager(), "dialog");

            checkLocationPermission();


            receiver = new BroadcastReceiver() {

                public void onReceive(Context context, Intent intent) {

                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                        BluetoothDevice bDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        Device device = new Device(bDevice);
                        if (!devices.contains(device)) {
                            liste.addItem(device);
                        }
                    }
                }
            };

            registerBTReciever();

            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();

        }


    }

    public void registerBTReciever() {

        if (!isRegisteringDone) {  // Si on a toujours besoin besoin de unregister
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            act.get().registerReceiver(receiver, filter);
        }
    }

    public void unregisterReceiver(boolean done) {

        if (!isRegisteringDone) // Si on a toujours besoin besoin de unregister
        {
            isRegisteringDone = done;
            act.get().unregisterReceiver(receiver);
        }

    }

    public void connecter(String adrMac) {

        Log.i(TAG, adrMac);
        bluetoothAdapter.cancelDiscovery();
        final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //UUID for serial connection
        device = bluetoothAdapter.getRemoteDevice(adrMac); //get remote device by mac, we assume these two devices are already paired

        ConnectThread connect = new ConnectThread(device, SERIAL_UUID);
        connect.start();

        /*
        // Récupération de la socket pour se connecter à l'appareil
        BluetoothSocket socket = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Echec creation socket");
        }

        try {
            socket.connect();

        } catch (IOException e) {
            Log.e(TAG, "Echec connection socket");
        }
        try {
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Echec recuperation des streams");
        }

        try {
            out.write("test".getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Echec écriture socket");
        }
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Echec fermeture socket");
        }

         */
    }


    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(act.get(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(act.get(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        }
    }

    public String getAdrMac() {
        return adrMac;
    }

    public void setAdrMac(String adrMac) {
        this.adrMac = adrMac;
    }

    public void TEST() {
        devices = Device.fromSet(bluetoothAdapter.getBondedDevices());
        //devices = new ArrayList<Device>();

        liste = ListeFragment.newInstance(devices);
        liste.show(act.get().getSupportFragmentManager(), "dialog");
    }

    public void setAct(WeakReference<AppCompatActivity> act) {
        this.act = act;
    }
}
