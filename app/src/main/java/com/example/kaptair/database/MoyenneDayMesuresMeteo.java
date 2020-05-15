package com.example.kaptair.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.kaptair.database.InterfacesMesures.MeteoMesure;

import java.util.Date;

@Entity
public class MoyenneDayMesuresMeteo implements MeteoMesure {

    @PrimaryKey
    public Date date;

    public double temperature;
    public double humidity;
    public int nbMesures;

    public MoyenneDayMesuresMeteo(Date date, double temperature, double humidity) {
        int minutes = date.getMinutes() - (date.getMinutes() % 5); //On garde aux 5 minutes près
        this.date = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), minutes);
        this.temperature = temperature;
        this.humidity = humidity;
        this.nbMesures = 1;
    }

    @Ignore
    public MoyenneDayMesuresMeteo(MesureMeteo m) {

        int minutes = m.date.getMinutes() - (m.date.getMinutes() % 5); //On garde aux 5 minutes près
        this.date = new Date(m.date.getYear(), m.date.getMonth(), m.date.getDate(), m.date.getHours(), minutes);
        this.temperature = m.temperature;
        this.humidity = m.humidity;
        this.nbMesures = 1;
    }

    @Ignore
    public MoyenneDayMesuresMeteo(MoyenneDayMesuresMeteo mOld, MoyenneDayMesuresMeteo mNew) {

        this.date = mOld.date;
        this.nbMesures = mOld.nbMesures + 1;
        this.temperature = (mOld.temperature * mOld.nbMesures + mNew.temperature) / this.nbMesures;
        this.humidity = (mOld.humidity * mOld.nbMesures + mNew.humidity) / this.nbMesures;

    }

    @Override
    @Ignore
    public float getFloatDate() {

        Date day = new Date(date.getYear(), date.getMonth(), date.getDate());
        return (date.getTime() - day.getTime()) / 1000; // On ne garde que le nombre d'heure de la mesure (independament du jour)
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

    public int getNbMesures() {
        return nbMesures;
    }

}
