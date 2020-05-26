package com.example.kaptair.database;

import androidx.room.*;

import com.example.kaptair.database.InterfacesMesures.MeteoMesure;

import java.util.Date;

@Entity
public class MesureMeteo implements MeteoMesure {

    @PrimaryKey
    public Date date;

    public double temperature;
    public double humidity;

    public double latitude;
    public double longitude;

    public MesureMeteo(Date date, double temperature, double humidity, double latitude, double longitude) {
        this.date = date;
        this.temperature = temperature;
        this.humidity = humidity;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    @Ignore
    @Override
    public float getFloatDate() {

        Date day = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), 0);
        return (date.getTime() - day.getTime()) / 1000; // On ne garde que le nombre de minutes de la mesure (independament du jour et de l'heure)
    }

    @Override
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
}
