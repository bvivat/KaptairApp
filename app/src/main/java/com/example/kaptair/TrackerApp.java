package com.example.kaptair;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;

import static com.example.kaptair.MainActivity.REQUEST_FINE_LOCATION;

/**
 * Created by Benjamin Vivat on 06/23/2020.
 */
public class TrackerApp {
    private static final String TAG = "TrackerApp";
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationClient;
    private boolean isTrackingDone = true;
    WeakReference<AppCompatActivity> act;
    Location lastLocation;

    private int intervalle; // En secondes

    public TrackerApp(AppCompatActivity a) {
        act = new WeakReference<AppCompatActivity>(a);

        // On verifie l'acces a la localisation
        if (ContextCompat.checkSelfPermission(act.get(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                    act.get(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }

        // On recupere l'intervalle choisi par l'utilisateur
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(act.get());
        intervalle = Integer.valueOf(settings.getString("freqGps", "20"));
        Log.d(TAG,""+intervalle);

        // On cree la requete
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(intervalle * 1000); // Requetes effectuees par l'application
        locationRequest.setFastestInterval(intervalle/2 * 1000); // Requetes effectuees par d'autres apps
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(act.get());
    }

    public void startLocationUpdates() {
        if(!this.isTrackingDone){
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }

    }

    public void stopLocationUpdates(boolean isTrackingDone) {
        if (!this.isTrackingDone) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            this.isTrackingDone = isTrackingDone;
        }

    }

    public void initTracking() {

        if (intervalle==0){
            isTrackingDone =true;
            return;
        }
        // On cree le callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "update result");
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    Log.d(TAG, location.toString());
                    lastLocation=location;
                }
            }
        };


        // On commence a ecouter les updates de position
        isTrackingDone=false;
        startLocationUpdates();
    }

    public void restartTracking(){
        stopLocationUpdates(false);
        initTracking();
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    public LocationCallback getLocationCallback() {
        return locationCallback;
    }

    public FusedLocationProviderClient getFusedLocationClient() {
        return fusedLocationClient;
    }

    public void setAct(WeakReference<AppCompatActivity> act) {
        this.act = act;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(act.get());
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public int getIntervalle() {
        return intervalle;
    }

    public void setIntervalle(int intervalle) {
        this.intervalle = intervalle;
        // On recree la requete
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(intervalle * 1000); // Requetes effectuees par l'application
        locationRequest.setFastestInterval(intervalle/2 * 1000); // Requetes effectuees par d'autres apps
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(act.get());

        restartTracking();
    }
}
