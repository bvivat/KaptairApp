package com.example.kaptair.database;

import androidx.room.*;

import com.example.kaptair.ui.main.graphiques.MeteoMesure;

import java.util.Date;

@Entity
public class MesureMeteo implements MeteoMesure {

    @PrimaryKey
    public Date date;

    public double temperature;
    public double humidity;

    public MesureMeteo(Date date, double temperature, double humidity) {
        this.date = date;
        this.temperature = temperature;
        this.humidity=humidity;
    }

    @Ignore
    @Override
    public float getFloatDate(){

        Date day = new Date(date.getYear(),date.getMonth(),date.getDate(),date.getHours(),0);
        return (date.getTime()-day.getTime())/1000;
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
