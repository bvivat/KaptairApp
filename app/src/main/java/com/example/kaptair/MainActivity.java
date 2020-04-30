package com.example.kaptair;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.kaptair.bluetooth.BluetoothApp;
import com.example.kaptair.bluetooth.HandlerUITransfert;
import com.example.kaptair.database.AppDatabase;
import com.example.kaptair.ui.main.HistoriqueFrag;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kaptair.ui.main.SectionsPagerAdapter;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_COARSE_LOCATION = 0;
    public static HandlerUITransfert handlerUI ;

    BluetoothApp bluetooth;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MesuresFrag fragMesures = new MesuresFrag();
        final ParamFrag fragParam = new ParamFrag();
        final CarteFrag fragCarte = new CarteFrag();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frag,fragMesures);
        transaction.commit();

        Toolbar t = findViewById(R.id.toolbar);
        final PrimaryDrawerItem mesures = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.mesures).withIcon(GoogleMaterial.Icon.gmd_straighten);
        final PrimaryDrawerItem carte = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.carte).withIcon(GoogleMaterial.Icon.gmd_map);
        final PrimaryDrawerItem param = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.param).withIcon(GoogleMaterial.Icon.gmd_settings);



        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(t)
                .addDrawerItems(
                        mesures,
                        carte,
                        new DividerDrawerItem(),
                        param
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem==param){

                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frag,fragParam);
                            transaction.commit();
                        } else if(drawerItem==mesures){
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frag,fragMesures);
                            transaction.commit();
                        }else if(drawerItem==carte){
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frag,fragCarte);
                            transaction.commit();
                        }
                        return false;
                    }
                }).withHeader(R.layout.nav_header)
                .build();



        db = AppDatabase.getInstance(this);



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