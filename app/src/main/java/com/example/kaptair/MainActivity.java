package com.example.kaptair;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    public static final String SAVE_LOCATION_STATUS = "isLocationGranted";

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_GOOGLE_SERVICES = 2;
    private static final int REQUEST_CHECK_SETTINGS = 3;

    public static HandlerUITransfert handlerUI;
    static BluetoothApp bluetooth;
    OnConnectionChangeListener listener;

    static TrackerApp tracker;
    static boolean isGooglePlayServicesAvailable = false;

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

            // Localisation
            int locationResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            isGooglePlayServicesAvailable = (locationResult == ConnectionResult.SUCCESS);

            if (!isGooglePlayServicesAvailable) {
                GoogleApiAvailability.getInstance().getErrorDialog(this, locationResult, REQUEST_GOOGLE_SERVICES);
            }

        } else {
            // Localisation requise pour rechercher les appareils bluetooths
            isLocationGranted = savedInstanceState.getBoolean(SAVE_LOCATION_STATUS);
            if (isLocationGranted) {
                // On actualise les references externes vers cette activite
                handlerUI.setAct(new WeakReference<AppCompatActivity>(this));
                bluetooth.setAct(new WeakReference<AppCompatActivity>(this));
                bluetooth.registerBTReciever();

                tracker.setAct(new WeakReference<AppCompatActivity>(this));
                tracker.startLocationUpdates();
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
            bluetooth.unregisterReceiver(false); // Pour eviter les leaks de memoire
        }
        if (isFinishing()) {
            // Si l'activite se termine, on libere les variables statiques
            bluetooth = null;
            handlerUI = null;
        }
        if (tracker != null) {
            tracker.stopLocationUpdates(false);
        }

    }

    private void initBluetooth() {
        if (bluetooth == null) {
            isLocationGranted = true;
            handlerUI = new HandlerUITransfert(this);
            bluetooth = new BluetoothApp(this);
            bluetooth.setListener(listener); // listener du fragment param, si il existe
            bluetooth.checkIsBluetoothEnabled();
        } else {
            bluetooth.checkIsBluetoothEnabled();
        }

    }

    public void checkTrackingPermissions() {
        if (isGooglePlayServicesAvailable) {

            if (tracker==null){
                tracker = new TrackerApp(this);
            }

            // On verifie les autorisations associees a la requete
            LocationSettingsRequest.Builder locationSettingsBuilder = new LocationSettingsRequest.Builder().addLocationRequest(tracker.getLocationRequest());

            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(locationSettingsBuilder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // All location settings are satisfied
                    tracker.initTracking();
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.gglNoServices, Toast.LENGTH_LONG).show();
        }

    }

    public void endTracking(){
        if (isGooglePlayServicesAvailable){
            tracker.stopLocationUpdates(true);
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
                    args.putString(SimpleDialog.ARG_TITLE, getString(R.string.locationDialogTitle));
                    args.putString(SimpleDialog.ARG_MESSAGE, getString(R.string.locationDialogBody));
                    args.putInt(SimpleDialog.ARG_ICON, R.drawable.ic_warning);
                    args.putInt(SimpleDialog.ARG_TYPE, SimpleDialog.TYPE_OK);
                    dialog.setArguments(args);

                    dialog.show(getSupportFragmentManager(), "Location Dialog");

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
        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // Cas activation bluetooth
                if (resultCode == RESULT_OK) {
                    // Si le bluetooth est active
                    bluetooth.bluetoothEnabled();
                } else {
                    Toast.makeText(this, R.string.btBluetoothDisabled, Toast.LENGTH_LONG).show();
                }
            case REQUEST_GOOGLE_SERVICES:
                // Cas google services
                if (resultCode == RESULT_OK) {
                    // Si l'appareil possède les services google
                    isGooglePlayServicesAvailable = true;
                } else {
                    Toast.makeText(this, R.string.gglNoServices, Toast.LENGTH_LONG).show();
                }
            case REQUEST_CHECK_SETTINGS:
                // Cas localisation precise
                if (resultCode == RESULT_OK) {
                    // Si les autorisations necessaires a la localisation sont acceptees
                    tracker.initTracking();
                } else {
                    Toast.makeText(this, R.string.locNoRights, Toast.LENGTH_LONG).show();
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
        outState.putBoolean(SAVE_LOCATION_STATUS, isLocationGranted);

    }

    public void setListener(OnConnectionChangeListener listener) {
        this.listener = listener;
    }

    public static TrackerApp getTracker() {
        return tracker;
    }
}