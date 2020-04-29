package com.example.kaptair;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.kaptair.bluetooth.BluetoothApp;
import com.example.kaptair.bluetooth.HandlerUITransfert;
import com.example.kaptair.database.AppDatabase;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kaptair.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_COARSE_LOCATION = 0;
    public static HandlerUITransfert handlerUI ;

    BluetoothApp bluetooth;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //Bluetooth
        handlerUI = new HandlerUITransfert(this);
        bluetooth = new BluetoothApp(this);
        bluetooth.rechercher();


    }

    public BluetoothApp getBluetooth() {
        return bluetooth;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetooth.unregisterReceiver();
        db.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}