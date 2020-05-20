package com.example.kaptair;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.kaptair.bluetooth.BluetoothApp;
import com.example.kaptair.bluetooth.HandlerUITransfert;
import com.example.kaptair.bluetooth.OnConnectionChangeListener;
import com.example.kaptair.database.AppDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_COARSE_LOCATION = 0;
    public static final String PREF_ADDRMAC = "LastConnectedDevice";
    private static final String TAG = "MainActivity";

    public static HandlerUITransfert handlerUI;
    static BluetoothApp bluetooth;
    OnConnectionChangeListener listener;

    boolean isLocationGranted = false;
    AppDatabase db;

    Drawer result;

    MesuresFrag fragMesures = new MesuresFrag();
    ParamFrag fragParam = new ParamFrag();
    CarteFrag fragCarte = new CarteFrag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragParam = new ParamFrag();
        fragCarte = new CarteFrag();

        if (savedInstanceState == null) {
            // On met le fragment mesure
            fragMesures = new MesuresFrag();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frag, fragMesures);
            transaction.commit();

            //Bluetooth
            checkLocationPermission();

        } else {
            // Localisation requise pour rechercher les appareils bluetooths
            isLocationGranted = savedInstanceState.getBoolean("isLocationGranted");
            if (isLocationGranted) {
                // On actualise les references externes vers cette activite
                handlerUI.setAct(new WeakReference<AppCompatActivity>(this));
                bluetooth.setAct(new WeakReference<AppCompatActivity>(this));
                bluetooth.registerBTReciever();
            }

        }


        Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Material Drawer \\

        // Les items du Material Drawer
        final PrimaryDrawerItem mesures = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.mesures).withIcon(GoogleMaterial.Icon.gmd_straighten);
        final PrimaryDrawerItem carte = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.carte).withIcon(GoogleMaterial.Icon.gmd_map);
        final PrimaryDrawerItem param = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.param).withIcon(GoogleMaterial.Icon.gmd_settings);

        // On construit le menu
        result = new DrawerBuilder()
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
                        // On change de fragment quand un item est clique
                        if (drawerItem == param) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frag, fragParam);
                            transaction.commit();
                        } else if (drawerItem == mesures) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frag, fragMesures);
                            transaction.commit();
                        } else if (drawerItem == carte) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frag, fragCarte);
                            transaction.commit();
                        }
                        return false;
                    }
                }).withHeader(R.layout.nav_header).withSavedInstance(savedInstanceState)
                .build();


        db = AppDatabase.getInstance(this);


    }

    public BluetoothApp getBluetooth() {
        return bluetooth;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetooth != null) {
            bluetooth.unregisterReceiver(false); // Pour eviter les leak de memoire
        }
        if (isFinishing()){
            // Si l'activite se termine, on libere les variables statiques
            bluetooth=null;
            handlerUI=null;
        }

    }

    private void initBluetooth() {
        if (bluetooth==null){
            isLocationGranted = true;
            handlerUI = new HandlerUITransfert(this);
            bluetooth = new BluetoothApp(this);
            bluetooth.setListener(listener); // listener du fragment param, si il existe
            bluetooth.checkIsBluetoothEnabled();
        }else{
            bluetooth.setRegisteringDone(false);
            bluetooth.checkIsBluetoothEnabled();
        }

    }

    protected void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission pas encore acceptee/ deja refusee
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);

        } else {
            // Permission deja accordee
            initBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //TODO Bluetooth related
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    initBluetooth();
                } else {
                    // permission denied, boo!
                    isLocationGranted = false;
                    // On cree un dialog pour prevenir l'utilisateur que le bluetooth sera inutilisable
                    DialogFragment dialog = new SimpleDialog();

                    Bundle args = new Bundle();
                    args.putString(SimpleDialog.ARG_TITLE, getString( R.string.locationDialogTitle));
                    args.putString(SimpleDialog.ARG_MESSAGE, getString(R.string.locationDialogBody));
                    args.putInt(SimpleDialog.ARG_ICON, R.drawable.ic_warning);
                    args.putInt(SimpleDialog.ARG_TYPE, SimpleDialog.TYPE_OK);
                    dialog.setArguments(args);

                    dialog.show(getSupportFragmentManager(),"Location Dialog");

                }
            }

            // other 'case' lines to checkHasBluetooth for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Ajouter si necessaire differentes cases au switch.
        switch (requestCode){

            case BluetoothApp.REQUEST_ENABLE_BT:
                // Cas activation bluetooth
                if(resultCode == RESULT_OK){
                    // Si le bluetooth est active
                    bluetooth.bluetoothEnabled();
                }else{
                    Toast.makeText(this, R.string.btBluetoothDisabled, Toast.LENGTH_LONG).show();
                }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // On initialise le menu de la toolbar
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menu.findItem(R.id.action_synchro).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // On reagit au click sur un des items du menu de la toolbar
        switch (item.getItemId()) {
            case R.id.action_synchro: {
                // do your sign-out stuff
                break;
            }
            // case blocks for other MenuItems (if any)
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        result.saveInstanceState(outState); //On sauvegarde la position actuelle du drawer menu
        outState.putBoolean("isLocationGranted", isLocationGranted);

    }
    @Override
    public void onBackPressed() {
       super.onBackPressed();
    }
    public void setListener(OnConnectionChangeListener listener) {
        this.listener = listener;
    }
}