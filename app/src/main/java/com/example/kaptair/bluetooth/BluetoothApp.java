package com.example.kaptair.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kaptair.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.kaptair.MainActivity.PREF_ADDRMAC;

public class BluetoothApp {
    private static final String TAG = "Bluetooth class";
    public static final int REQUEST_ENABLE_BT = 1;

    ArrayList<Device> devices;
    ListeFragment liste;

    boolean hasBluetooth;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device;

    BroadcastReceiver receiver;
    boolean isRegisteringDone = true;
    private boolean launch = true; // Check si il s'agit du lancement de l'application

    ConnectThread connect;
    private OnConnectionChangeListener listener;

    WeakReference<AppCompatActivity> act;


    public BluetoothApp(AppCompatActivity a) {
        act = new WeakReference<AppCompatActivity>(a);
        checkHasBluetooth();
    }


    public void checkHasBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) { // On regarde si le support dispose de bluetooth
            hasBluetooth = false;
        } else {
            hasBluetooth = true;
        }
    }

    public void checkIsBluetoothEnabled() {

        if (!hasBluetooth) {
            Toast.makeText(act.get(), R.string.btNoBluetooth, Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) { // On verifie que le bluetooth est active, sinon on demande a l'utilisateur de l'activer
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                act.get().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                bluetoothEnabled();
            }
        }
    }

    public void bluetoothEnabled(){
        // On enregistre l'adresse du dernier appareil connecte
        SharedPreferences sharedPref = act.get().getPreferences(Context.MODE_PRIVATE);
        String adresse = sharedPref.getString(PREF_ADDRMAC,null);

        if (adresse != null && launch){
            connecter(adresse);
        }else{
            rechercher();
        }
        launch = false;

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
            isRegisteringDone=false;
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

        Toast.makeText(act.get(), act.get().getString(R.string.ToastBTConnection, device.getName()), Toast.LENGTH_SHORT).show();

        if (connect != null){
            // On ferme les connexions existantes
            connect.cancel();
        }
        connect = new ConnectThread(act, device, SERIAL_UUID); // On se connecte
        connect.setListener(listener);
        connect.start();
    }


    public void setAct(WeakReference<AppCompatActivity> act) {
        this.act = act;
    }

    public ConnectThread getConnect() {
        return connect;
    }

    public void setListener(OnConnectionChangeListener listener) {
        this.listener = listener;
    }

    public void setRegisteringDone(boolean registeringDone) {
        isRegisteringDone = registeringDone;
    }

}
