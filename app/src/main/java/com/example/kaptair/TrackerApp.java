package com.example.kaptair;

import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;

public class TrackerApp {
    private static final String TAG = "TrackerApp";
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationClient;
    private boolean isTrackingDone = true;
    WeakReference<AppCompatActivity> act;
    Location lastLocation;

    public TrackerApp(AppCompatActivity a) {
        act = new WeakReference<AppCompatActivity>(a);

        // On cree la requete
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10 * 1000); // Requetes effectuees par l'application
        locationRequest.setFastestInterval(5 * 1000); // Requetes effectuees par d'autres apps
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
}
