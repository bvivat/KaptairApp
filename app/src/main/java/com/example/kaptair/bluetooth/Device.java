package com.example.kaptair.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Set;

//Classe device contenant seulement un nom et une adresse Mac. Implemente parcelable pour pouvoir le stocker dans des bundles
public class Device implements Parcelable {
    String nom;
    String adrMac;

    public Device(String nom, String adrMac) {
        this.nom = nom;
        this.adrMac = adrMac;
    }

    public Device(BluetoothDevice bDevice) {
        this.nom = bDevice.getName();
        this.adrMac = bDevice.getAddress();
    }

    protected Device(Parcel in) {
        nom = in.readString();
        adrMac = in.readString();
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    public String getNom() {
        return nom;
    }

    public String getAdrMac() {
        return adrMac;
    }

    public static ArrayList<Device> fromSet(Set<BluetoothDevice> set) {
        ArrayList<Device> devices = new ArrayList<>();
        for (BluetoothDevice b : set) {
            Device d = new Device(b.getName(), b.getAddress());
            devices.add(d);
        }
        return devices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;
        try {
            return nom.equals(device.nom) && adrMac.equals(device.adrMac);
        } catch (NullPointerException exception) {
            return false;
        }

    }

    @Override
    public int hashCode() {
        return nom.length() * 2 + adrMac.length() * 3;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeString(adrMac);

    }
}
