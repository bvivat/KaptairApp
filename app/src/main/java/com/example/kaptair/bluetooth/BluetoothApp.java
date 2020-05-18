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

    ArrayList<Device> devices;
    ListeFragment liste;

    boolean hasBluetooth;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device;

    BroadcastReceiver receiver;
    boolean isRegisteringDone = false;

    ConnectThread connect;

    WeakReference<AppCompatActivity> act;

    public BluetoothApp(AppCompatActivity a) {
        act = new WeakReference<AppCompatActivity>(a);
        check();
    }


    public void check() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) { // On regarde si le support dispose de bluetooth
            hasBluetooth = false;
        } else {
            hasBluetooth = true;
            int REQUEST_ENABLE_BT = 1;
            if (!bluetoothAdapter.isEnabled()) { // On verifie que le bluetooth est active, sinon on demande a l'utilisateur de l'activer
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                act.get().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }


    public void rechercher() {
        if (!hasBluetooth) {
            Toast.makeText(act.get(), R.string.btNoBluetooth, Toast.LENGTH_SHORT).show();
        } else {

            //On recupere les appareils appaires
            devices = Device.fromSet(bluetoothAdapter.getBondedDevices());

            // Affichage des devices trouves
            liste = ListeFragment.newInstance(devices);
            liste.show(act.get().getSupportFragmentManager(), "dialog");

            // On recherche les appareils a proximite
            receiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {

                    String action = intent.getAction();

                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        //Lorsqu'un appareil est trouve a proximite
                        BluetoothDevice bDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Device device = new Device(bDevice);

                        //On l'ajoute a la liste
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

        if (!isRegisteringDone) {  // Si on a toujours besoin de discover
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            act.get().registerReceiver(receiver, filter);
        }
    }

    public void unregisterReceiver(boolean done) {

        if (!isRegisteringDone) // Si on a toujours besoin de discover
        {
            isRegisteringDone = done;
            act.get().unregisterReceiver(receiver);
        }

    }

    public void connecter(String adrMac) {

        Log.i(TAG, adrMac);
        bluetoothAdapter.cancelDiscovery(); // On arrete la recherche

        final UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //UUID for serial connection
        device = bluetoothAdapter.getRemoteDevice(adrMac); //get remote device by mac, we assume these two devices are already paired

        Toast.makeText(act.get() ,act.get().getString(R.string.ToastBTConnection,device.getName()), Toast.LENGTH_SHORT).show();

        connect = new ConnectThread(act,device, SERIAL_UUID); // On se connecte
        connect.start();
    }


    public void setAct(WeakReference<AppCompatActivity> act) {
        this.act = act;
    }

    public ConnectThread getConnect() {
        return connect;
    }
}
