package com.example.kaptair.database;

import androidx.room.*;

import com.example.kaptair.database.InterfacesMesures.PollutionMesure;

import java.util.Date;

@Entity
public class MesurePollution implements PollutionMesure {

    @PrimaryKey
    public Date date;

    public double pm1;
    public double pm25;
    public double pm10;
    public double co2;

    public MesurePollution(Date date, double pm1, double pm25, double pm10, double co2) {
        this.date = date;
        this.pm1 = pm1;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.co2 = co2;
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
    public double getPm1() {
        return pm1;
    }

    public void setPm1(double pm1) {
        this.pm1 = pm1;
    }

    @Override
    public double getPm25() {
        return pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    @Override
    public double getPm10() {
        return pm10;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    @Override
    public double getCo2() {
        return co2;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }


}
